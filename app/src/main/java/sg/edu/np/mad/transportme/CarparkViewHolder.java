package sg.edu.np.mad.transportme;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CarparkViewHolder extends RecyclerView.ViewHolder {
    TextView CarparkDescription;
    TextView CarLotsAvailable;
    TextView MotorLotsAvailable;
    TextView TruckLotsAvailable;
    ImageView CarImageView;
    ImageView MotorImageView;
    ImageView TruckImageView;

    public CarparkViewHolder(@NonNull View item) {
        super(item);
        CarparkDescription = item.findViewById(R.id.carparkDesc);
        CarLotsAvailable = item.findViewById(R.id.carAvailable);
        MotorLotsAvailable = item.findViewById(R.id.motorAvailable);
        TruckLotsAvailable = item.findViewById(R.id.truckAvailable);
        CarImageView = item.findViewById(R.id.carImageView);
        MotorImageView = item.findViewById(R.id.motorImageView);
        TruckImageView = item.findViewById(R.id.truckImageView);
    }


}
