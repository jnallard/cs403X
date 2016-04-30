package cs403x.crowdcade;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;

/**
 * Created by Joshua on 2/23/2016.
 */
public class MoreDetailsDialog {
    private ArcadeEntry entry;

    public MoreDetailsDialog(MainActivity mainActivity, ArcadeEntry entry){
        this.entry = entry;


        LayoutInflater inflater = mainActivity.getLayoutInflater();

        new AlertDialog.Builder(mainActivity)
                .setTitle(entry.getName())
                .setView(inflater.inflate(R.layout.fragment_more_details, null))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();
    }
}
