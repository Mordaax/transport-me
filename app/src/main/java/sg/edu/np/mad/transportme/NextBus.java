package sg.edu.np.mad.transportme;

public class NextBus {
    String EstimatedArrival;
    String Latitude;
    String Longitude;
    String Load;
    String Feature;
    String Type;

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