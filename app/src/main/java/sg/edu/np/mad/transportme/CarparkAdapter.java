package sg.edu.np.mad.transportme;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import sg.edu.np.mad.transportme.api.MySingleton;

public class CarparkAdapter
        extends RecyclerView.Adapter<CarparkViewHolder> {

    ArrayList<Carpark> data = getCarparkAvailability();
    Context c;
    JsonObjectRequest jsonObjectRequestCarPark;

    public CarparkAdapter(Context c){
        this.c = c;
    }

    @NonNull
    @Override
    public CarparkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_carpark_layout, null, false);
        return new CarparkViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull CarparkViewHolder holder, int position) {
        //getCarparkAvailability();
        Carpark carpark_content = data.get(position);

        holder.CarparkDescription.setText(carpark_content.Development);

        if (carpark_content.LotType == "C"){
            holder.CarLotsAvailable.setText(carpark_content.AvailableLots);
            holder.MotorLotsAvailable.setText("--");
            holder.TruckLotsAvailable.setText("--");
        }
        else if (carpark_content.LotType == "Y"){
            holder.CarLotsAvailable.setText("--");
            holder.MotorLotsAvailable.setText(carpark_content.AvailableLots);
            holder.TruckLotsAvailable.setText("--");
        }
        else{
            holder.CarLotsAvailable.setText("--");
            holder.MotorLotsAvailable.setText("--");
            holder.TruckLotsAvailable.setText(carpark_content.AvailableLots);
        }


        /**
        BusServiceAdapter adapterMember = new BusServiceAdapter(content.getBusServices(), c);  //Create the RecyclerView for BusServices
        LinearLayoutManager layout = new LinearLayoutManager(c);                            //LayoutManager tells RecyclerView how to draw the list

        holder.RecyclerView2.setLayoutManager(layout);          //Pass in layout and adapter
        holder.RecyclerView2.setAdapter(adapterMember);
         **/
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public ArrayList<Carpark> getCarparkAvailability(){
        String url = "http://datamall2.mytransport.sg/ltaodataservice/CarParkAvailabilityv2";
        //Get carparks using api
        jsonObjectRequestCarPark = new JsonObjectRequest( Request.Method.GET, url,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("value");
                            for (int i = 0; i < jsonArray.length(); i++){
                                //converting JSONArray to JSONObject
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                int carparkID =  Integer.parseInt(jsonObject.get("CarParkID").toString());
                                String area = jsonObject.get("Area").toString();
                                String dev = jsonObject.get("Development").toString();
                                String[] coordinates = jsonObject.get("Location").toString().split(" ");
                                Location location = new Location(dev);
                                location.setLatitude(Double.parseDouble(coordinates[0]));
                                location.setLongitude(Double.parseDouble(coordinates[1]));
                                int available = Integer.parseInt(jsonObject.get("AvailableLots").toString());
                                String lotType = jsonObject.get("LotType").toString();
                                String agency = jsonObject.get("Agency").toString();
                                //Adding object to array
                                data.add(new Carpark(carparkID, area, dev, location, available, lotType, agency));

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d("TAG", response.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", error.getMessage(), error);
            }
        }) { //no semicolon or coma
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("AccountKey", "MlHNjn00RVKWi4Z1R5zR+A==");
                params.put("accept", "application/json");
                return params;
            }
        };
        MySingleton.getInstance(c).addToRequestQueue(jsonObjectRequestCarPark);
        return data;
    }


}