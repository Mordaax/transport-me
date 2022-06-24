package sg.edu.np.mad.transportme;

import java.util.ArrayList;

public class User {

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ArrayList<BusStop> getSavedBusStops() {
        return savedBusStops;
    }

    public void setSavedBusStops(ArrayList<BusStop> savedBusStops) {
        this.savedBusStops = savedBusStops;
    }

    private String name,email, password;
    private ArrayList<BusStop> savedBusStops = new ArrayList<>();

    public User(){}

    public User(String name, String email, String password, ArrayList<BusStop> busStops){
        this.name = name;
        this.email = email;
        this.password = password;
        this.savedBusStops = busStops;
    }
    public void updateBusStop(BusStop busStop){
        this.savedBusStops.add(busStop);

    }
    public ArrayList<BusStop> getBusStops(){
        return this.savedBusStops;
    }


}
