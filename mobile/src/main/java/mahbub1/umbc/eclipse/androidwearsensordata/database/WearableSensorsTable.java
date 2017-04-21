package mahbub1.umbc.eclipse.androidwearsensordata.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;
import android.util.SparseArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import mahbub1.umbc.eclipse.sensordatashared.sensors.SensorNames;

/**
 * Created by mahbub on 2/3/17.
 */




public final class WearableSensorsTable implements java.io.Serializable{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The Constant AUTHORITY. */
    public static final String AUTHORITY = "mahbub1.umbc.eclipse.androidwearsensordata";	// The Content Provider Authority

    /** The sdf. */
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy:MM:dd", Locale.US);

    /** The entry_time. */
    public long entry_time;

    /**
     * Instantiates CalendarUtils new available medication info in db.
     *
     * @param values the values
     */
    public WearableSensorsTable(ContentValues values){
        entry_time = values.getAsLong(WearableSensorDataTable.SensorDataPoints.COLUMN_NAME_ENTRYTIME);
    }

    /**
     * Instantiates CalendarUtils new available medication info in db.
     *
     * @param date the date
     */
    public WearableSensorsTable(long date ){
        this.entry_time = date;
    }

    /*
     * Returns CalendarUtils ContentValues instance (CalendarUtils map) for this diaryInfo instance. This is useful for
     * inserting CalendarUtils diaryInfo into CalendarUtils database.
     */
    /**
     * Gets the content values.
     *
     * @return the content values
     */
    public ContentValues getContentValues() {
        // Gets CalendarUtils new ContentValues object
        ContentValues v = new ContentValues();

        // Adds map entries for the user-controlled fields in the map
        v.put(WearableSensorDataTable.SensorDataPoints.COLUMN_NAME_ENTRYTIME, entry_time);
        return v;

    }



    /**
     * Constant definition to define the mapping of CalendarUtils Diary to the underlying database
     * Also provides constants to help define the Content Provider.
     */
    public static final class SensorInfoColumns implements BaseColumns {

        // This class cannot be instantiated
        /**
         * Instantiates CalendarUtils new DB medication list item.
         */
        private SensorInfoColumns() {}

        /** The table name offered by this provider. */
        public static final String TABLE_NAME = "tb_sensor";

        /*
         * URI definitions
         */

        /** The scheme part for this provider's URI. */
        private static final String SCHEME = "content://";

        /** Path parts for the URIs. */

        /**
         * Path part for the diary URI
         */
        private static final String PATH_WEARABLE_SENSOR = "/tb_sensor";

        /** Path part for the diary ID URI. */
        private static final String PATH_WEAR_SENSOR_ID = "/tb_sensor/";

        /** 0-relative position of CalendarUtils diary item ID segment in the path part of CalendarUtils diary item ID URI. */
        public static final int WEARABLE_SENSOR_ID_PATH_POSITION = 1;


        /** The content:// style URL for this table. */
        public static final Uri CONTENT_URI =  Uri.parse(SCHEME + AUTHORITY + PATH_WEARABLE_SENSOR);

        /**
         * The content URI base for CalendarUtils single diary item. Callers must
         * append CalendarUtils numeric diary id to this Uri to retrieve an diary item
         */
        public static final Uri CONTENT_ID_URI_BASE
                = Uri.parse(SCHEME + AUTHORITY + PATH_WEAR_SENSOR_ID);

        /** The default sort order for this table. */
        public static final String DEFAULT_SORT_ORDER = WearableSensorsTable.SensorInfoColumns.COLUMN_NAME_ENTRYTIME + " ASC";


        /*
         * MIME type definitions
         */

        /**
         * The MIME type of {@link #CONTENT_URI} providing CalendarUtils directory of diaries.
         */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.wearsensor.tb_sensor";

        /** The MIME type of CalendarUtils {@link #CONTENT_URI} sub-directory of CalendarUtils single diary item. */
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.wearsensor.tb_sensor";

        /*
         * Column definitions
         */

        /**
         * Column name for the creation timestamp
         * <P>Type: INTEGER (long from System.curentTimeMillis())</P>
         */
        public static final String COLUMN_NAME_ENTRYTIME= "entry_time";

        /** The Constant COLUMN_NAME_MEDICATION_NAME. */
        //public static final String COLUMN_NAME_SENSOR_CONTENT = "sensor_content";
        /** The Constant COLUMN_NAME_MEDICATION_NAME. */
        //public static final String COLUMN_NAME_STATUS = "status";

        public static final String COLUMN_NAME_SENSOR_TYPE = "sensor_type";


        /** The Constant COLUMN_NAME_ID. */
        public static final String COLUMN_NAME_ID = BaseColumns._ID;


        public  static final String COLUMN_NAME_SENSOR_NAME="sensor_name";
        //public  static final String COLUMN_NAME_Y="y";
        //public  static final String COLUMN_NAME_Z="z";

        //public  static final String COLUMN_NAME_ACCURACY="accuracy";
        //public  static final String COLUMN_NAME_DATA_TYPE="datatype";



        //public static final String COLUMN_NAME_STATUS="status";
        /**  Projection holding all the columns required to populate and Diary item. */
        public static final String[] FULL_PROJECTION = {
                COLUMN_NAME_ENTRYTIME,
                COLUMN_NAME_SENSOR_NAME

        };

        /** The Constant LIST_PROJECTION. */
        public static final String[] LIST_PROJECTION =
                new String[] {
                        WearableSensorDataTable.SensorDataPoints._ID
                };

    }

    /**
     * The Class LocationDurationDataTableManage.
     */
    public static final class WearableSensorsTableManage {
        // Database creation SQL statement
        /** The Constant DATABASE_CREATE. */
        private static final String DATABASE_CREATE = "create table "
                + SensorInfoColumns.TABLE_NAME
                + " ("
                + SensorInfoColumns._ID + " INTEGER primary key autoincrement, "
                + SensorInfoColumns.COLUMN_NAME_ENTRYTIME + " text not null,"
                + SensorInfoColumns.COLUMN_NAME_SENSOR_NAME + " text not null ,"
                + SensorInfoColumns.COLUMN_NAME_SENSOR_TYPE + " integer not null"
                + ");";

        /**
         * On create.
         *
         * @param database the database
         */
        public static void onCreate(SQLiteDatabase database) {
            database.execSQL(DATABASE_CREATE);
        }

        /**
         * On upgrade.
         *
         * @param database the database
         * @param oldVersion the old version
         * @param newVersion the new version
         */
        public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                     int newVersion) {
            Log.w(WearableSensorsTableManage.class.getName(), "Upgrading database from version "
                    + oldVersion + " to " + newVersion
                    + ", which will destroy all old data");
            database.execSQL("DROP TABLE IF EXISTS " + SensorInfoColumns.TABLE_NAME);
            onCreate(database);
        }
    }


    /**
     * Insert available medication list.
     *
     * @return the string[]
     */
    public static ArrayList<String> InsertIntialSensorInfo()
    {
        Long now = Long.valueOf(System.currentTimeMillis());
        SensorNames sensorNames = new SensorNames();
        SparseArray<String> sensors = sensorNames.getNames();


        ArrayList<String> insertQuery =new ArrayList<>();
        String query="", sensorName;
        int key=-1;

        for(int i =0; i < sensors.size();i++){

            key = sensors.keyAt(i);
            sensorName = sensors.get(key);

            query= "INSERT INTO " + WearableSensorsTable.SensorInfoColumns.TABLE_NAME + "('"
                    + WearableSensorsTable.SensorInfoColumns.COLUMN_NAME_SENSOR_NAME + "','"
                    + WearableSensorsTable.SensorInfoColumns.COLUMN_NAME_SENSOR_TYPE + "','"
                    + WearableSensorsTable.SensorInfoColumns.COLUMN_NAME_ENTRYTIME
                    + "') values('"+ sensorName+"',"+key +"," + now + ")";

            insertQuery.add(query);
        }




        return insertQuery;
    }
    /**
     * The Class Helper.
     */
    public static final class Helper {
        /**
         * Converts CalendarUtils cursor to an array of Diary
         * Note that this method is "mean and lean" with little error checking.
         * It assumes that the projection used is DiaryItem.FULL_PROJECTION
         * @param cursor A cursor loaded with Diary data
         * @return populated array of Diaries
         */
        public static final WearableSensorsTable[] getSensorFromCursor(Cursor cursor){
            WearableSensorsTable[] diaries = null;
            int rows = cursor.getCount();
            if(rows > 0){
                diaries = new WearableSensorsTable[rows];
                int i=0;
                while(cursor.moveToNext()){
                    diaries[i++] = new WearableSensorsTable( cursor.getLong(0));
                }
            }
            return diaries;
        }

        /**
         * Dbmedicationlist to json.
         *
         * @param e the e
         * @return the JSON object
         * @throws JSONException the JSON exception
         */
        public static final JSONObject sensorToJSON(WearableSensorsTable e) throws JSONException{
            JSONObject jObj = new JSONObject();
            jObj.put("lasttaken", e.entry_time);
            return jObj;
        }

        /**
         * Dbmedicationlist array to json.
         *
         * @param duration the duration
         * @return the JSON array
         * @throws JSONException the JSON exception
         */
        public static final JSONArray sensorArrayToJSON(WearableSensorsTable[] duration)  throws JSONException {
            JSONArray jArray = new JSONArray();
            System.out.println("Converting  " + duration.length + " dbmedicationlist to JSON");
            for(WearableSensorsTable e: duration){
                jArray.put(sensorToJSON(e));
            }
            return jArray;
        }

        /**
         * Dblist array to csv.
         *
         * @param duration the duration
         * @return the string builder
         */
        public static final StringBuilder sensorArrayToCsv(WearableSensorsTable[] duration){
            StringBuilder result= new StringBuilder();
            for(WearableSensorsTable e: duration){
                result.append(sensorToCsv(e));
            }
            return result;
        }

        /**
         * Dblist to csv.
         *
         * @param e the e
         * @return the string builder
         */
        public static final StringBuilder sensorToCsv(WearableSensorsTable e){
            StringBuilder builder = new StringBuilder();
            builder.append(sdf.format(new Date(e.entry_time)));
            builder.append('\n');
            return builder;
        }

    }



}