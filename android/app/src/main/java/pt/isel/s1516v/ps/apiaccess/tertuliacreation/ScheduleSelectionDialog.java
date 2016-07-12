package pt.isel.s1516v.ps.apiaccess.tertuliacreation;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import pt.isel.s1516v.ps.apiaccess.R;

public class ScheduleSelectionDialog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.new_tertulia_schedule_hint)
                .setItems(R.array.new_tertulia_dialog_schedules, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String[] schedules = getResources().getStringArray(R.array.new_tertulia_dialog_schedules);
                        switch (which) {
                            case 1:
                                break;
                            case 2:
                                break;
                            case 3:
                                break;
                            case 4:
                                break;
                            case 5:
                                break;
                            default:
                                throw new IllegalArgumentException("Unnexpected schedule selection");
                        }
                    }
                })
                .setPositiveButton(R.string.new_tertulia_dialog_next_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String[] schedules = getResources().getStringArray(R.array.new_tertulia_dialog_schedules);
                    }
                })
                .setNegativeButton(R.string.new_tertulia_dialog_cancel_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dismiss();                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
