package sg.edu.np.mad.transportme;

import com.google.android.gms.maps.model.LatLng;

public class RouteStep {
    LatLng Latlongstart;
    LatLng Latlongend;
    String TravelMode;
    String Instructions;
    Integer NumStops; //
    String Distance;
    String Duration;
    String LineName;  //
    String PreviousLocation;
    String NextLocation;
    String LineColor;
    public RouteStep(){}
    public RouteStep(LatLng latlongstart, LatLng latlongend, String travelMode, String instructions, String distance, String duration, String previousLocation, String nextLocation) {
        Latlongstart = latlongstart;
        Latlongend = latlongend;
        TravelMode = travelMode;
        Instructions = instructions;
        Distance = distance;
        Duration = duration;
        PreviousLocation = previousLocation;
        NextLocation = nextLocation;
    }



}
