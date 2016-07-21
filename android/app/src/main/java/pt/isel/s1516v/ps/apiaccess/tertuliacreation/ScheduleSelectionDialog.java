package pt.isel.s1516v.ps.apiaccess.tertuliacreation;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import pt.isel.s1516v.ps.apiaccess.R;
import pt.isel.s1516v.ps.apiaccess.helpers.Util;
import pt.isel.s1516v.ps.apiaccess.support.domain.Schedule;

public class ScheduleSelectionDialog extends DialogFragment {
    private Schedule schedule;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.new_tertulia_schedule_hint)
                .setItems(R.array.new_tertulia_dialog_schedules, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ((DialogFragmentResult) getActivity()).onSelection(which);
                        ScheduleSelectionDialog.this.dismiss();
                    }
                });
        return builder.create();
    }
}
