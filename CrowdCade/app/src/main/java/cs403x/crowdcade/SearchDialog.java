package cs403x.crowdcade;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Joshua on 2/23/2016.
 */
public class SearchDialog {

    private String query;

    ArrayAdapter<ArcadeEntry> adapter;

    List<ArcadeEntry> searchResults;

    AlertDialog alertDialog;

    public SearchDialog(final MainActivity mainActivity, final String query){
        this.query = query;

        LayoutInflater inflater = mainActivity.getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_search, null);

        ListView listView = (ListView) view.findViewById(R.id.searchListView);
        adapter = new ArrayAdapter<ArcadeEntry>(mainActivity, R.layout.search_row, new ArrayList<ArcadeEntry>()){

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                LayoutInflater inflater = (LayoutInflater) mainActivity
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View rowView = inflater.inflate(R.layout.search_row, parent, false);

                TextView gameName = (TextView) rowView.findViewById(R.id.gameName);
                TextView address = (TextView) rowView.findViewById(R.id.address);
                TextView locationName = (TextView) rowView.findViewById(R.id.locationName);
                TextView distance = (TextView) rowView.findViewById(R.id.distance);

                final ArcadeEntry currentEntry = getItem(position);
                gameName.setText(currentEntry.getName());
                address.setText(currentEntry.getAddress());
                locationName.setText(currentEntry.getLocationName());

                rowView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        mainActivity.selectSearchResults(currentEntry);
                        alertDialog.dismiss();
                    }

                });

                return rowView;
            }
        };

        listView.setAdapter(adapter);

        ResponseRunnable search = new ResponseRunnable(mainActivity) {
            @Override
            public void runOnMainThread() {
                searchResults = ArcadeEntry.fromJSONArray(data);
                adapter.clear();
                adapter.addAll(searchResults);
            }
        };

        NetworkManager.getInstance().searchArcadeEntries(search, query);

        alertDialog = new AlertDialog.Builder(mainActivity)
                .setTitle("Search")
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
