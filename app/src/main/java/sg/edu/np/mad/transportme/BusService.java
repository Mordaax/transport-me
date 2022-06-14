package sg.edu.np.mad.transportme;

import java.util.ArrayList;

public class BusService {
    String ServiceNumber;
    ArrayList<NextBus> NextBuses;
    public BusService(String serviceNumber, ArrayList<NextBus> nextBuses){
        this.ServiceNumber = serviceNumber;
        this.NextBuses = nextBuses;
    }
}
