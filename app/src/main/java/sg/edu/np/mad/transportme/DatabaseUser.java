package sg.edu.np.mad.transportme;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DatabaseUser {
    private DatabaseReference dr;
    public DatabaseUser()
    {
        FirebaseDatabase db = FirebaseDatabase.getInstance("https://transportme-c607f-default-rtdb.asia-southeast1.firebasedatabase.app/");
        dr = db.getReference(User.class.getSimpleName());
    }
    public Task<Void> add(User u)
    {
        return dr.child(u.name).setValue(u);
    }
}
