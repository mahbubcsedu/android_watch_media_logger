package mahbub1.umbc.eclipse.androidwearsensordata.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import static com.google.android.gms.wearable.DataMap.TAG;
import static mahbub1.umbc.eclipse.androidwearsensordata.database.WearableSensorsTable.InsertIntialSensorInfo;

/**
 * Created by mahbub on 2/3/17.
 */



// TODO: Auto-generated Javadoc
/**
 * The Class DatabaseHelper.
 */

public class DbHelper extends SQLiteOpenHelper
{

    /** The Constant DATABASE_NAME. */
    private static final String DATABASE_NAME = "wearablesensors.db";

    /** The Constant DATABASE_VERSION. */
    private static final int DATABASE_VERSION = 1;



    /**
     * Instantiates CalendarUtils new database helper.
     *
     * @param context
     *            the context
     */
    public DbHelper(Context context) {
        // If the 2nd parameter is null then the database is held in memory --
        // this form creates CalendarUtils file backed database
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        // TODO Auto-generated constructor stub
    }

    /**
     * Alternative constructor for test mode.
     *
     * @param context
     *            the context
     * @param testMode
     *            state of flag is irrelevant. The presence of the 2nd argument
     *            causes the in-memory db to be created
     */
    public DbHelper(Context context, boolean testMode) {
        // If the 2nd parameter is null then the database is held in memory --
        // this form creates an in memory database
        super(context, null, null, DATABASE_VERSION);
    }

    /* (non-Javadoc)
     *
     * @see
     * android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite
     * .SQLiteDatabase) */
    @Override
    public void onCreate(SQLiteDatabase db)
    {

        Log.d(TAG, "database created");
        WearableSensorDataTable.WearableSensorDataTableManage.onCreate(db);
        WearableSensorsTable.WearableSensorsTableManage.onCreate(db);

        db.beginTransaction();
        // insert lots of stuff...
        ArrayList<String> sensorInfo = InsertIntialSensorInfo();
        for (String sensor : sensorInfo)
        {
            db.execSQL(sensor);
        }
        db.setTransactionSuccessful();
        db.endTransaction();

    }



    /**
     * Not sure what to do with this. Could ignore for the course...
     *
     * @param db
     *            the db
     * @param oldVersion
     *            the old version
     * @param newVersion
     *            the new version
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        // TODO Auto-generated method stub
        WearableSensorDataTable.WearableSensorDataTableManage.onUpgrade(db, oldVersion, newVersion);

    }



}