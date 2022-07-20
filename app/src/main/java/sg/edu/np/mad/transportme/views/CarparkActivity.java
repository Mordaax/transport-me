package sg.edu.np.mad.transportme.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toolbar;

import java.util.ArrayList;

import sg.edu.np.mad.transportme.Carpark;
import sg.edu.np.mad.transportme.CarparkAdapter;
import sg.edu.np.mad.transportme.R;

public class CarparkActivity extends AppCompatActivity {

    private ArrayList<Carpark> carparkArrayList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carpark);
        RecyclerView recyclerView = findViewById(R.id.carparkRecyclerView);
        CarparkAdapter adapter = new CarparkAdapter(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }
}