package sg.edu.np.mad.transportme;

import android.location.Location;

public class Carpark {

    public int CarparkID;
    public String Area;
    public String Development;
    public Location location;
    public int AvailableLots;
    public String LotType;
    public String Agency;

    public Carpark(){}

    public Carpark(int carparkID,String area, String development, Location location, int availableLots, String lotType, String agency){
        CarparkID = carparkID;
        Area = area;
        Development = development;
        this.location = location;
        AvailableLots = availableLots;
        LotType = lotType;
        Agency = agency;
    }
}
