package sg.edu.np.mad.transportme;

import android.location.Location;

public class Carpark {

    public int CarparkID;
    public String Development;
    public Location location;
    public int AvailableLots;
    public char LotType;
    public String Agency;

    public Carpark(){}

    public Carpark(int carparkID, String development, Location location, int availableLots, char lotType, String agency){
        CarparkID = carparkID;
        Development = development;
        this.location = location;
        AvailableLots = availableLots;
        LotType = lotType;
        Agency = agency;
    }
}
