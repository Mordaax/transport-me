package sg.edu.np.mad.transportme;

import android.location.Location;

public class Carpark {

    public String CarparkID;
    public String Area;
    public String Development;
    public Location location;
    public String AvailableLots;
    public String LotType;
    public String Agency;

    public Carpark(){}

    public Carpark(String carparkID,String area, String development, Location location, String availableLots, String lotType, String agency){
        CarparkID = carparkID;
        Area = area;
        Development = development;
        this.location = location;
        AvailableLots = availableLots;
        LotType = lotType;
        Agency = agency;
    }
}
