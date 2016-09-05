package pt.isel.pdm.g04.pf.data.thoth.database;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import pt.isel.pdm.g04.pf.data.thoth.models.core.Attributes;
import pt.isel.pdm.g04.pf.data.thoth.models.core.IHasId;
import pt.isel.pdm.g04.pf.data.thoth.provider.ThothContract;
import pt.isel.pdm.g04.pf.helpers.Logger;

public abstract class Table<T extends IHasId> {

    private Class<T> type;
    private String table;
    private String[] mColumns;

    public Table(Class<T> type) {
        this.type = type;
        table = type.getSimpleName();
    }

    public String getDropTableDDL() {
        return "DROP TABLE IF EXISTS " + table;
    }

    private static String[] getColumns(Class<?> type, String prefix) {

        Iterable<Field> fields = getFieldsUpTo(type, Object.class);

        List<String> columns = new ArrayList<>();
        for (Field field : fields) {
            Annotation annotation = field.getAnnotation(Attributes.class);
            Attributes attr = (Attributes) annotation;

            if (attr != null && attr.notMapped()) {
                continue;
            }

            String columnName = prefix + (attr != null && attr.primaryKey() ? "_" : "") + field.getName();

            if (!String.class.isAssignableFrom(field.getType()) && field.getType() != Integer.TYPE) {
                Collections.addAll(columns, getColumns(field.getType(), field.getName()));
            } else {
                columns.add(columnName);
            }
        }
        return columns.toArray(new String[columns.size()]);
    }

    public String getCreateTableDDL() {

        StringBuilder queryBuilder = new StringBuilder();

        try {
            Iterable<Field> fields = getFieldsUpTo(type, Object.class);
            queryBuilder.append("CREATE TABLE ").append(table).append(" (");

            queryBuilder.append(getColumnsDDL(fields));

            queryBuilder.append(");");
        } catch (Exception e) {
            Logger.e(e);
        }

        return queryBuilder.toString();
    }

    public String[] getColumns() {

        if (mColumns != null) {
            return mColumns;
        }

        mColumns = getColumns(type, "");
        return mColumns;
    }

    public String getColumnsDDL(Iterable<Field> fields) {
        return getColumnsDDL(fields, "");
    }

    private String getColumnsDDL(Iterable<Field> fields, String prefix) {
        boolean firstField = true;
        StringBuilder queryBuilder = new StringBuilder();

        for (Field field : fields) {
            Annotation annotation = field.getAnnotation(Attributes.class);
            Attributes attr = (Attributes) annotation;

            if (attr != null && attr.notMapped()) {
                continue;
            }

            if (!firstField) {
                queryBuilder.append(", ");
            }

            String columnName = (attr != null && attr.primaryKey() ? "_" : "") + field.getName();

            if (String.class.isAssignableFrom(field.getType())) {
                queryBuilder.append(prefix);
                queryBuilder.append(columnName).append(" ");
                queryBuilder.append("TEXT");
            } else if (field.getType() == Integer.TYPE || field.getType() == Long.TYPE) {
                queryBuilder.append(prefix);
                queryBuilder.append(columnName).append(" ");
                queryBuilder.append("INTEGER");
            } else {
                Iterable<Field> fieldFields = getFieldsUpTo(field.getType(), Object.class);
                queryBuilder.append(getColumnsDDL(fieldFields, field.getName()));
                continue;
            }

            if (annotation != null && attr != null) {
                if (attr.primaryKey()) {
                    queryBuilder.append(" PRIMARY KEY");
                } else if (attr.unique()) {
                    queryBuilder.append(" UNIQUE");
                }
            }

            firstField = false;
        }
        return queryBuilder.toString();
    }

    // region Abstract Methods

    protected abstract Uri getUri();

    protected String[] getDefaultProjection() {
        return getColumns();
    }

    protected int[] getColumnIds(Cursor cursor) {
        String[] cols = getColumns();
        int[] indexes = new int[cols.length];
        for (int i = 0; i < cols.length; i++) {
            indexes[i] = cursor.getColumnIndex(cols[i]);
        }
        return indexes;
    }

    protected abstract T buildItem(Cursor cursor, int[] columnIds) throws ParseException;

    // endregion Abstract Methods

    // region Template


    public ContentValues toContentValues(T item) {
        return getContentValues(item);
    }


    public void prepareInsertBatch(Collection<T> items, ArrayList<ContentProviderOperation> ops) throws ParseException {
        for (T item : items) {
            ops.add(ContentProviderOperation
                    .newInsert(getUri())
                    .withValues(toContentValues(item))
                    .build());
        }
    }

    public void prepareUpdateBatch(Collection<T> items, ArrayList<ContentProviderOperation> ops) throws ParseException {
        for (T item : items) {
            ops.add(ContentProviderOperation
                    .newUpdate(Uri.withAppendedPath(getUri(), String.valueOf(item.getId())))
                    .withValues(toContentValues(item))
                    .build());
        }
    }


    public void prepareDeleteBatch(Collection<T> items, ArrayList<ContentProviderOperation> ops) throws ParseException {
        for (T item : items) {
            ContentProviderOperation
                    .newDelete(Uri.withAppendedPath(getUri(), String.valueOf(item.getId())));
        }
    }

    public int update(ContentResolver contentResolver, T item) throws ParseException, RemoteException {
        Uri uri = Uri.withAppendedPath(getUri(), String.valueOf(item.getId()));

        ContentValues values = toContentValues(item);
        return contentResolver.update(uri,
                values,
                null,
                null);
    }

    public T select(ContentResolver contentResolver, int id) {
        return select(contentResolver, ThothContract.Users._ID, String.valueOf(id));
    }

    public T selectByEmail(ContentResolver contentResolver, String email) {
        return select(contentResolver, ThothContract.Users.ACADEMIC_EMAIL, email);
    }

    public int delete(ContentResolver contentResolver, T item)  {
        Uri _uri = Uri.withAppendedPath(getUri(), String.valueOf(item.getId()));
        return contentResolver.delete(_uri,
                ThothContract.Users._ID + " = ?",
                new String[]{String.valueOf(item.getId())});
    }

    public Collection<T> selectAll(ContentResolver contentResolver) throws ParseException, RemoteException {
        Uri uri = getUri();
        Cursor cursor = contentResolver.query(uri, getDefaultProjection(), null, null, null);
        Collection<T> collection = new ArrayList<>();
        int[] columnIds = getColumnIds(cursor);
        while (cursor.moveToNext()) {
            collection.add(buildItem(cursor, columnIds));
        }
        cursor.close();
        return collection;
    }

    private T select(ContentResolver contentResolver, String column, String value) {
        Uri uri = getUri();
        Cursor cursor = contentResolver.query(uri, getDefaultProjection(),
                column + " = ?",
                new String[]{value},
                null);
        T item = null;
        int[] columnIds = getColumnIds(cursor);
        if (cursor.moveToFirst())
            try {
                item = buildItem(cursor, columnIds);
            } catch (ParseException e) {
                Logger.w(e.getMessage());
            }
        cursor.close();
        return item;
    }


    // endregion Template

    // region Implementation Helpers


    public ContentValues getContentValues(T startClass) {
        return getContentValues("", startClass, new ContentValues());
    }

    protected ContentValues getContentValues(String prefix, Object obj, ContentValues values) {
        Iterable<Field> fields = getFieldsUpTo(obj.getClass(), Object.class);
        for (Field field : fields) {

            try {
                Annotation annotation = field.getAnnotation(Attributes.class);
                Attributes attr = (Attributes) annotation;

                if (attr != null && attr.notMapped()) {
                    continue;
                }

                String name = field.getName();
                Method method = getMethod(obj.getClass(), "get" + name.substring(0, 1).toUpperCase() + name.substring(1));
                Object value = method.invoke(obj);
                String columnName = prefix + (attr != null && attr.primaryKey() ? "_" : "") + name;

                if (field.getType() == Integer.TYPE) {
                    values.put(columnName, (Integer) value);
                } else if (field.getType() == Long.TYPE) {
                    values.put(columnName, (long) value);
                } else if (String.class.isAssignableFrom(field.getType())) {
                    values.put(columnName, (String) value);
                } else {
                    getContentValues(columnName, value, values);
                }

            } catch (IllegalAccessException e) {
                Logger.e(e);
            } catch (NoSuchMethodException e) {
                Logger.e(e);
            } catch (InvocationTargetException e) {
                Logger.e(e);
            } catch (NoSuchFieldException e) {
                Logger.e(e);
            }
        }
        return values;
    }

    private static Method getMethod(Class<?> clazz, String name) throws NoSuchFieldException, NoSuchMethodException {
        try {
            return clazz.getDeclaredMethod(name);
        } catch (NoSuchMethodException ex) {
            if (clazz == Object.class) {
                throw ex;
            }
            return getMethod(clazz.getSuperclass(), name);
        }
    }

    private static Iterable<Field> getFieldsUpTo(@NonNull Class<?> startClass,
                                                 @Nullable Class<?> exclusiveParent) {

        List<Field> currentClassFields = new ArrayList<>();
        currentClassFields.addAll(Arrays.asList(startClass.getDeclaredFields()));
        Class<?> parentClass = startClass.getSuperclass();

        if (parentClass != null &&
                (exclusiveParent == null || !(parentClass.equals(exclusiveParent)))) {
            List<Field> parentClassFields =
                    (List<Field>) getFieldsUpTo(parentClass, exclusiveParent);
            currentClassFields.addAll(parentClassFields);
        }

        return currentClassFields;
    }


    // endregion Implementation Helpers
}
