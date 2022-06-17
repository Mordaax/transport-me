package sg.edu.np.mad.transportme;

import java.util.ArrayList;

public class BusStop {
    String BusStopCode;
    String RoadName;
    String Description;
    Double Latitude;
    Double Longitude;
    Boolean Favourited;
    ArrayList<BusService> busServices;

    public BusStop(String BusStopCode, String RoadName, String Description, Double Latitude, Double Longitude){
        this.BusStopCode = BusStopCode;
        this.RoadName = RoadName;
        this.Description = Description;
        this.Latitude = Latitude;
        this.Longitude = Longitude;
    }
}
