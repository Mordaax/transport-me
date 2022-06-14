package sg.edu.np.mad.transportme;

import java.util.ArrayList;

public class User {

    public String name,email;
    public ArrayList<BusStop> savedBusStops = new ArrayList<>();

    public User(){}

    public User(String name, String email, ArrayList<BusStop> busStops){
        this.name = name;
        this.email = email;
        this.savedBusStops = busStops;
    }
    public void updateBusStop(BusStop busStop){
        this.savedBusStops.add(busStop);

    }
    public ArrayList<BusStop> getBusStops(){
        return this.savedBusStops;
    }


}
