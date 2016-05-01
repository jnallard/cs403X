package cs403x.crowdcade;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

/**
 * Created by Joshua on 2/23/2016.
 */
public class MoreDetailsDialog {
    private ArcadeEntry entry;

    public MoreDetailsDialog(final MainActivity mainActivity, final ArcadeEntry entry){
        this.entry = entry;


        LayoutInflater inflater = mainActivity.getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_more_details, null);

        TextView gameName = (TextView) view.findViewById(R.id.gameName);
        TextView locationName = (TextView) view.findViewById(R.id.gameName);
        TextView address = (TextView) view.findViewById(R.id.address);
        RatingBar condition = (RatingBar) view.findViewById(R.id.condition);
        final TextView visits = (TextView) view.findViewById(R.id.visits);
        final Button visitsButton = (Button) view.findViewById(R.id.visitedButton);
        final ImageView picture = (ImageView) view.findViewById(R.id.cabinetPicture);

        gameName.setText(entry.getName());
        locationName.setText(entry.getLocationName());
        address.setText(entry.getAddress());
        condition.setRating((float) entry.getCondition());
        condition.setIsIndicator(true);
        visits.setText(entry.getVisits() + "");
        picture.setImageDrawable(mainActivity.getResources().getDrawable(R.drawable.download));

        visitsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                visitsButton.setEnabled(false);
                NetworkManager.getInstance().reportArcadeEntryVisited(entry, new ResponseRunnable(mainActivity) {
                    @Override
                    public void runOnMainThread() {
                        visits.setText((entry.visits + 1) + "");
                    }
                });
            }
        });

        ResponseRunnable runnable = new ResponseRunnable(mainActivity) {
            @Override
            public void runOnMainThread() {
                picture.setImageBitmap(entry.getPhoto());
            }
        };

        entry.loadImage(runnable, mainActivity);



        new AlertDialog.Builder(mainActivity)
                .setTitle("More Details")
                .setView(view)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();
    }
}
