package sg.edu.np.mad.transportme;

public class NextBus {
    public String getEstimatedArrival() {
        return EstimatedArrival;
    }

    public void setEstimatedArrival(String estimatedArrival) {
        EstimatedArrival = estimatedArrival;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }

    public String getLoad() {
        return Load;
    }

    public void setLoad(String load) {
        Load = load;
    }

    public String getFeature() {
        return Feature;
    }

    public void setFeature(String feature) {
        Feature = feature;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    private String EstimatedArrival;
    private String Latitude;
    private String Longitude;
    private String Load;
    private String Feature;
    private String Type;

    public NextBus(String estimatedArrival, String latitude, String longitude, String load, String feature, String type){

        this.EstimatedArrival = estimatedArrival;
        this.Latitude = latitude;
        this.Longitude = longitude;
        this.Load = load;
        this.Feature = feature;
        this.Type = type;
    }

    /*
    Load: SEA (Seats available), SDA (Standing available), LSD (Limited Standing)
    Feature: WAB (wheel Chair accessible), none (Not wheel chat accessible)
    Type: SD (Single Deck), DD (Double Deck), BD (Bendy) */

}