package pt.isel.pdm.g04.pf.syncadapter.thoth;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.SyncResult;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import pt.isel.pdm.g04.pf.data.thoth.database.Schema;
import pt.isel.pdm.g04.pf.data.thoth.models.BaseTeacher;
import pt.isel.pdm.g04.pf.data.thoth.models.Student;
import pt.isel.pdm.g04.pf.data.thoth.models.Students;
import pt.isel.pdm.g04.pf.data.thoth.models.Teacher;
import pt.isel.pdm.g04.pf.data.thoth.models.Teachers;
import pt.isel.pdm.g04.pf.data.thoth.provider.ThothContract;
import pt.isel.pdm.g04.pf.helpers.Logger;
import pt.isel.pdm.g04.pf.helpers.Preferences;
import pt.isel.pdm.g04.pf.helpers.WebRequest;

public class ThothSyncAdapter extends AbstractThreadedSyncAdapter {

    public ThothSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Logger.d("onPerformSync for account[" + account.name + "]");
        try {
            // if (!Utils.canSync(intent, this))
            //    return;

            ContentResolver contentResolver = getContext().getContentResolver();

            ArrayList<ContentProviderOperation> contentProviderOperations = syncStudents(contentResolver);
            if (contentProviderOperations != null) {
                provider.applyBatch(contentProviderOperations);
            }
            contentProviderOperations = syncTeachers(contentResolver);
            if (contentProviderOperations != null) {
                provider.applyBatch(contentProviderOperations);
            }
        } catch (RemoteException | OperationApplicationException | ParseException e) {
            Logger.e(e);
        }
    }

    @Nullable
    private ArrayList<ContentProviderOperation> syncStudents(ContentResolver contentResolver) throws ParseException, RemoteException {
        Students students;
        ArrayList<ContentProviderOperation> contentProviderOperations = new ArrayList<>();
        Map<Integer, Student> existingItems = new HashMap<>();
        Collection<Student> itemsToInsert = new LinkedList<>();
        Collection<Student> itemsToUpdate = new LinkedList<>();
        for (Student item : Schema.Students.selectAll(contentResolver)) {
            existingItems.put(Integer.valueOf(item.getId()), item);
        }
        students = WebRequest.connect(Preferences.getStudentsUrl(getContext()))
                .download()
                .to(Students.class);
        if (students == null) {
            return null;
        }
        for (Student t : students.getStudents()) {
            if (!existingItems.containsKey(t.getId())) {
                itemsToInsert.add(t);
            } else {
                Student existingTeacher = existingItems.get(t.getId());
                //compare only base attributes to avoid overriding extended Teacher
                if (t.hashCode() != existingTeacher.hashCode()) {
                    itemsToUpdate.add(t);
                }
                existingItems.remove(t.getId());
            }
        }


        Logger.i("Students loaded: " + students.getStudents().size() + " (new: " + itemsToInsert.size() + ", updated: " + itemsToUpdate.size() + ", deleted: " + existingItems.values().size() + ")");

        Schema.Students.prepareUpdateBatch(itemsToUpdate, contentProviderOperations);
        Schema.Students.prepareDeleteBatch(existingItems.values(), contentProviderOperations);
        Schema.Students.prepareInsertBatch(itemsToInsert, contentProviderOperations);
        if (itemsToInsert.size() > 0) {
            getContext().getContentResolver().notifyChange(ThothContract.Students.CONTENT_URI, null);
        }
        return contentProviderOperations;
    }

    @Nullable
    private ArrayList<ContentProviderOperation> syncTeachers(ContentResolver contentResolver) throws ParseException, RemoteException {
        Teachers teachers;
        ArrayList<ContentProviderOperation> contentProviderOperations = new ArrayList<>();
        Map<Integer, Teacher> existingTeachers = new HashMap<>();
        Collection<Teacher> itemsToInsert = new LinkedList<>();
        Collection<Teacher> teachersToUpdate = new LinkedList<>();
        for (Teacher t : Schema.Teachers.selectAll(contentResolver)) {
            existingTeachers.put(Integer.valueOf(t.getId()), t);
        }
        teachers = WebRequest.connect(Preferences.getTeachersUrl(getContext()))
                .download()
                .to(Teachers.class);
        if (teachers == null) {
            return null;
        }
        for (BaseTeacher bt : teachers.getTeachers()) {
            Teacher t = new Teacher(bt);
            if (!existingTeachers.containsKey(t.getId())) {
                itemsToInsert.add(t);
            } else {
                Teacher existingTeacher = existingTeachers.get(t.getId());
                //compare only base attributes to avoid overriding extended Teacher
                if (t.hashCode() != existingTeacher.hashCode()) {
                    teachersToUpdate.add(t);
                }
                existingTeachers.remove(t.getId());
            }
        }
        Logger.i("Teachers loaded: " + teachers.getTeachers().size() + " (new: " + itemsToInsert.size() + ", updated: " + teachersToUpdate.size() + ", deleted: " + existingTeachers.values().size() + ")");
        Schema.Teachers.prepareUpdateBatch(teachersToUpdate, contentProviderOperations);
        Schema.Teachers.prepareDeleteBatch(existingTeachers.values(), contentProviderOperations);
        Schema.Teachers.prepareInsertBatch(itemsToInsert, contentProviderOperations);
        if (itemsToInsert.size() > 0) {
            contentResolver.notifyChange(ThothContract.Teachers.CONTENT_URI, null);
        }
        return contentProviderOperations;
    }
}
