package sg.edu.np.mad.transportme;

import java.util.ArrayList;

public class BusService {
    public String getServiceNumber() {
        return ServiceNumber;
    }

    public void setServiceNumber(String serviceNumber) {
        ServiceNumber = serviceNumber;
    }

    public ArrayList<NextBus> getNextBuses() {
        return NextBuses;
    }

    public void setNextBuses(ArrayList<NextBus> nextBuses) {
        NextBuses = nextBuses;
    }

    public ArrayList<String> busRoute;
    private String ServiceNumber;
    private ArrayList<NextBus> NextBuses;
    public BusService(String serviceNumber, ArrayList<NextBus> nextBuses){
        this.ServiceNumber = serviceNumber;
        this.NextBuses = nextBuses;
    }
}
