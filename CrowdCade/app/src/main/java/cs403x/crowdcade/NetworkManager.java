package cs403x.crowdcade;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Joshua on 4/25/2016.
 */
public class NetworkManager {

    private NetworkManager instance = new NetworkManager();

    private NetworkManager(){

    }

    public NetworkManager getInstance(){
        return instance;
    }

    public List<ArcadeEntry> getArcadeEntries(){
        return new ArrayList<>();
    }

    public void reportArcadeEntry(ArcadeEntry entry){

    }

    public void reportArcadeEntryVisited(ArcadeEntry entry){

    }

}
