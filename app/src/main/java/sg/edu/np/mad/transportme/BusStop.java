package sg.edu.np.mad.transportme;

import java.util.ArrayList;

public class BusStop implements Comparable<BusStop> {
    String BusStopCode;
    String RoadName;
    String Description;
    Double Latitude;
    Double Longitude;
    Boolean Favourited;
    ArrayList<BusService> busServices;
    Double distanceToLocation;

    public BusStop(String BusStopCode, String RoadName, String Description, Double Latitude, Double Longitude){
        this.BusStopCode = BusStopCode;
        this.RoadName = RoadName;
        this.Description = Description;
        this.Latitude = Latitude;
        this.Longitude = Longitude;
    }
    public int compareTo(BusStop bs){
        if(distanceToLocation == bs.distanceToLocation){
            return 0;
        }
        else if (distanceToLocation > bs.distanceToLocation){
            return 1;
        }
        else{
            return -1;
        }
    }
}
