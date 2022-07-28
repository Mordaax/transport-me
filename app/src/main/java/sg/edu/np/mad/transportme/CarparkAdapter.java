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

    ArrayList<Carpark> data;
    Context c;
    //JsonObjectRequest jsonObjectRequestCarPark;

    public CarparkAdapter(Context c, ArrayList<Carpark> data){
        this.c = c;
        this.data = data;
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

        if (carpark_content.LotType.equals("C")){
            holder.CarLotsAvailable.setText(carpark_content.AvailableLots);
            holder.MotorLotsAvailable.setText("---");
            holder.TruckLotsAvailable.setText("---");
        }
        else if (carpark_content.LotType.equals("Y")){
            holder.CarLotsAvailable.setText("---");
            holder.MotorLotsAvailable.setText(carpark_content.AvailableLots);
            holder.TruckLotsAvailable.setText("---");
        }
        else{
            holder.CarLotsAvailable.setText("---");
            holder.MotorLotsAvailable.setText("---");
            holder.TruckLotsAvailable.setText(carpark_content.AvailableLots);
        }

    }

    @Override
    public int getItemCount() {
        return data.size();
    }


}