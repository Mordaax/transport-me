package sg.edu.np.mad.transportme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class CarparkAdapter
        extends RecyclerView.Adapter<CarparkViewHolder> {

    ArrayList<Carpark> data;
    Context c;

    public CarparkAdapter(ArrayList<Carpark> data, Context c){
        this.data = data;
        this.c = c;
    }

    @NonNull
    @Override
    public CarparkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_carpark_adapter, null, false);

        return new CarparkViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull CarparkViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}