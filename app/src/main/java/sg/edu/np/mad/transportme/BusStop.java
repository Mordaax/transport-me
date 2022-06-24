package sg.edu.np.mad.transportme;

import java.util.ArrayList;

public class BusStop implements Comparable<BusStop> {
    private String BusStopCode;
    private String RoadName;
    private String Description;
    private Double Latitude;
    private Double Longitude;
    private Boolean Favourited;
    private ArrayList<BusService> busServices;
    private Double distanceToLocation;

    public String getBusStopCode() {
        return BusStopCode;
    }

    public void setBusStopCode(String busStopCode) {
        BusStopCode = busStopCode;
    }

    public String getRoadName() {
        return RoadName;
    }

    public void setRoadName(String roadName) {
        RoadName = roadName;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public Double getLatitude() {
        return Latitude;
    }

    public void setLatitude(Double latitude) {
        Latitude = latitude;
    }

    public Double getLongitude() {
        return Longitude;
    }

    public void setLongitude(Double longitude) {
        Longitude = longitude;
    }

    public Boolean getFavourited() {
        return Favourited;
    }

    public void setFavourited(Boolean favourited) {
        Favourited = favourited;
    }

    public ArrayList<BusService> getBusServices() {
        return busServices;
    }

    public void setBusServices(ArrayList<BusService> busServices) {
        this.busServices = busServices;
    }

    public Double getDistanceToLocation() {
        return distanceToLocation;
    }

    public void setDistanceToLocation(Double distanceToLocation) {
        this.distanceToLocation = distanceToLocation;
    }

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
