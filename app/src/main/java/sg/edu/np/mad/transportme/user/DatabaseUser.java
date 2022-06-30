package sg.edu.np.mad.transportme.user;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import sg.edu.np.mad.transportme.User;

public class DatabaseUser {
    private DatabaseReference dr;
    public DatabaseUser()
    {
        FirebaseDatabase db = FirebaseDatabase.getInstance("https://transportme-c607f-default-rtdb.asia-southeast1.firebasedatabase.app/"); //Initialise Database
        dr = db.getReference(User.class.getSimpleName()); //Get reference to User
    }
    public Task<Void> add(User u)
    {
        return dr.child(u.getName()).setValue(u);
    } //Adding User to the database
}
