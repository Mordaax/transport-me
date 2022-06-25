package sg.edu.np.mad.transportme;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class BusStopDBHandler extends SQLiteOpenHelper {

    public BusStopDBHandler(Context c, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(c,"BusStops.db", factory, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {   //Create new database for bus stops
        String Create_Bus_Stops = "CREATE TABLE busStops(BusStopCode INTEGER PRIMARY KEY, RoadName TEXT, Description TEXT, Latitude DOUBLE,Longitude DOUBLE)";
        db.execSQL(Create_Bus_Stops);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS busStops"); //Drop table, can help with refreshing bus stop data
        onCreate(db);
    }

    public ArrayList<BusStop> getBusStops(){ //Returns a list of busStops loaded from the database, uses cursor
        ArrayList<BusStop> busStops = new ArrayList<BusStop>();
        String query = "SELECT * FROM busStops";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        while(cursor.moveToNext()){
            String busStopCode = cursor.getString(0);
            String roadName = cursor.getString(1);
            String description = cursor.getString(2);
            Double latitude = cursor.getDouble(3);
            Double longitude = cursor.getDouble(4);
            busStops.add(new BusStop(busStopCode,roadName,description,latitude,longitude));
        }
        db.close();
        return busStops;
    }

    public void addBusStops(ArrayList<BusStop> busStops){ //Add bus stops into database
        SQLiteDatabase db = this.getWritableDatabase();
        for (BusStop busStop : busStops){
            String busStopCode = busStop.getBusStopCode();
            String roadName = busStop.getRoadName();
            String description = busStop.getDescription();
            Double latitude = busStop.getLatitude();
            Double longitude = busStop.getLongitude();
/*
            String Create_Bus_Stops = "CREATE TABLE busStops(BusStopCode INTEGER PRIMARY KEY, RoadName TEXT, Description TEXT, Latitude DOUBLE,Longitude DOUBLE)";
*/
            String query = "INSERT INTO busStops(BusStopCode, RoadName,Description,Latitude,Longitude) VALUES (\""+busStopCode+"\""+","+"\""+roadName+"\""+","+"\""+description+"\""+","+latitude+","+longitude+")";
/*
            String query2= "INSERT INTO busStops (BusStopCode=\""+busStopCode+"\""+","+"RoadName=\""+roadName+"\""+","+"Description=\""+description+"\""+","+"Latitude=\""+latitude+"\""+","+"Longitude=\""+longitude+"\""+")";
*/

            db.execSQL(query);
        }
        db.close();


    }
}
