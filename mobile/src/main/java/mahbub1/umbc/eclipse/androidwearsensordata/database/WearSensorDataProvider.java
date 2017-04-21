package mahbub1.umbc.eclipse.androidwearsensordata.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;

import java.util.Arrays;
import java.util.HashSet;

/**
 * Created by mahbub on 2/3/17.
 */


// TODO: Auto-generated Javadoc
/**
 * The Class DiaryProvider.
 */
public class WearSensorDataProvider extends ContentProvider {

    /** The m dao. */
    //private DAO mDAO;

    /** The aps dao. */
    private WearSensorDataDao wearSensorDao;

    /** The Constant sUriMatcher. */
    private static final UriMatcher sUriMatcher;

    /** The Constant DIARYS. */
    private static final int WEARSENSOR_DATA = 1;

    /** The Constant DIARY_ID. */
    private static final int WEARSENSOR_DATA_ID = 2;


    private static  final int WEARABLE_SENSOR = 3;
    private static final int WEARABLE_SENSOR_ID = 4;


    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        sUriMatcher.addURI(WearableSensorDataTable.AUTHORITY, "tb_sensordata", WEARSENSOR_DATA);
        sUriMatcher.addURI(WearableSensorDataTable.AUTHORITY, "tb_sensordata/#", WEARSENSOR_DATA_ID);

        sUriMatcher.addURI(WearableSensorDataTable.AUTHORITY, "tb_sensor", WEARABLE_SENSOR);
        sUriMatcher.addURI(WearableSensorDataTable.AUTHORITY, "tb_sensor/#", WEARABLE_SENSOR_ID);

    }

    /* (non-Javadoc)
     * @see android.content.ContentProvider#delete(android.net.Uri, java.lang.String, java.lang.String[])
     */
    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        int count;
        int id;
        switch (sUriMatcher.match(uri)) {

            case WEARSENSOR_DATA:
                count = wearSensorDao.delete(where, whereArgs, WearableSensorDataTable.SensorDataPoints.TABLE_NAME);
                break;
            case WEARSENSOR_DATA_ID:
                id = Integer.parseInt(uri.getPathSegments().get(WearableSensorDataTable.SensorDataPoints.WEAR_SENSOR_DATA_ID_PATH_POSITION));
                count = wearSensorDao.deleteById(id, WearableSensorDataTable.SensorDataPoints._ID, WearableSensorDataTable.SensorDataPoints.TABLE_NAME);
                break;
            case WEARABLE_SENSOR:
                count = wearSensorDao.delete(where, whereArgs, WearableSensorsTable.SensorInfoColumns.TABLE_NAME);
                break;
            case WEARABLE_SENSOR_ID:
                id = Integer.parseInt(uri.getPathSegments().get(WearableSensorsTable.SensorInfoColumns.WEARABLE_SENSOR_ID_PATH_POSITION));
                count = wearSensorDao.deleteById(id, WearableSensorsTable.SensorInfoColumns._ID, WearableSensorsTable.SensorInfoColumns.TABLE_NAME);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return count;
    }

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#getType(android.net.Uri)
	 */

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
		/*case DIARYS:
			return Diary.DiaryItem.CONTENT_TYPE;
		case DIARY_ID:
			return Diary.DiaryItem.CONTENT_ITEM_TYPE;*/
            case WEARSENSOR_DATA:
                return WearableSensorDataTable.SensorDataPoints.CONTENT_TYPE;
            case WEARSENSOR_DATA_ID:
                return WearableSensorDataTable.SensorDataPoints.CONTENT_ITEM_TYPE;
            case WEARABLE_SENSOR:
                return WearableSensorsTable.SensorInfoColumns.CONTENT_TYPE;
            case WEARABLE_SENSOR_ID:
                return WearableSensorsTable.SensorInfoColumns.CONTENT_ITEM_TYPE;


            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    /* (non-Javadoc)
     * @see android.content.ContentProvider#insert(android.net.Uri, android.content.ContentValues)
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {


        Uri insertUri=null;
        long rowId;

        switch (sUriMatcher.match(uri)) {

            case WEARSENSOR_DATA_ID:
            case WEARSENSOR_DATA:
                rowId = wearSensorDao.insert(values, WearableSensorDataTable.SensorDataPoints.COLUMN_NAME_ENTRYTIME, WearableSensorDataTable.SensorDataPoints.TABLE_NAME);
                if (rowId > 0) {
                    insertUri = ContentUris.withAppendedId(WearableSensorDataTable.SensorDataPoints.CONTENT_ID_URI_BASE, rowId);
                    getContext().getContentResolver().notifyChange(insertUri, null);
                    return insertUri;
                }
            case WEARABLE_SENSOR_ID:
            case WEARABLE_SENSOR:
                rowId = wearSensorDao.insert(values, WearableSensorsTable.SensorInfoColumns.COLUMN_NAME_ENTRYTIME, WearableSensorsTable.SensorInfoColumns.TABLE_NAME);
                if (rowId > 0) {
                    insertUri = ContentUris.withAppendedId(WearableSensorsTable.SensorInfoColumns.CONTENT_ID_URI_BASE, rowId);
                    getContext().getContentResolver().notifyChange(insertUri, null);
                    return insertUri;
                }
            default:
                throw new SQLException("Failed to insert row into " + uri);
        }
    }

    /* (non-Javadoc)
     * @see android.content.ContentProvider#onCreate()
     */
    @Override
    public boolean onCreate() {
        //mDAO = new DAO(getContext());
        wearSensorDao=new WearSensorDataDao(getContext());
        return true;
    }


    /* (non-Javadoc)
     * @see android.content.ContentProvider#query(android.net.Uri, java.lang.String[], java.lang.String, java.lang.String[], java.lang.String)
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
		/*Cursor cursor = null;
       	   cursor = mDAO.queryDiarys(projection, selection, selectionArgs, sortOrder);
	       return cursor;*/
        Cursor cursor = null;
        switch (sUriMatcher.match(uri)) {
		/*case DIARYS:
		case DIARY_ID:
			cursor = mDAO.queryDiarys(projection, selection, selectionArgs, sortOrder);
			return cursor;*/
            case WEARSENSOR_DATA_ID:
            case WEARSENSOR_DATA:

                cursor = wearSensorDao.query(projection, selection, selectionArgs, sortOrder, WearableSensorDataTable.SensorDataPoints.TABLE_NAME, WearableSensorDataTable.SensorDataPoints.DEFAULT_SORT_ORDER);
                return cursor;
            case WEARABLE_SENSOR_ID:
            case WEARABLE_SENSOR:

                cursor = wearSensorDao.query(projection, selection, selectionArgs, sortOrder, WearableSensorsTable.SensorInfoColumns.TABLE_NAME, WearableSensorsTable.SensorInfoColumns.DEFAULT_SORT_ORDER);
                return cursor;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

    }

    /* (non-Javadoc)
     * @see android.content.ContentProvider#update(android.net.Uri, android.content.ContentValues, java.lang.String, java.lang.String[])
     */
    @Override
    public int update(Uri uri, ContentValues values, String where,
                      String[] whereArgs) {
        int count;
        long id;

        switch (sUriMatcher.match(uri)) {
		/*case DIARYS:
			count = mDAO.updateDiarys(values, where, whereArgs);
			break;
		case DIARY_ID:
			id= ContentUris.parseId(uri);
			count = mDAO.updateDiaryById(id, values);
			break;*/
            case WEARSENSOR_DATA:
                count = wearSensorDao.update(values, where, whereArgs, WearableSensorDataTable.SensorDataPoints.TABLE_NAME);
                break;
            case WEARSENSOR_DATA_ID:
                id = ContentUris.parseId(uri);
                count = wearSensorDao.updateById(id, values, WearableSensorDataTable.SensorDataPoints.TABLE_NAME, WearableSensorDataTable.SensorDataPoints._ID);
                break;
            case WEARABLE_SENSOR:
                count = wearSensorDao.update(values, where, whereArgs, WearableSensorsTable.SensorInfoColumns.TABLE_NAME);
                break;
            case WEARABLE_SENSOR_ID:
                id = ContentUris.parseId(uri);
                count = wearSensorDao.updateById(id, values, WearableSensorsTable.SensorInfoColumns.TABLE_NAME, WearableSensorsTable.SensorInfoColumns._ID);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return count;
    }

    /**
     * Gets the db helper for test.
     *
     * @return the db helper for test
     */
    public DbHelper getDbHelperForTest() {
        return wearSensorDao.getDbHelperForTest();
    }

    /**
     * Check columns.
     *
     * @param projection the projection
     * @param available the available
     */
    private void checkColumns(String[] projection,String[] available) {
		/*String[] available = { LocTable.COLUMN_TIME,
				LocTable.COLUMN_LONGITUDE, LocTable.COLUMN_LATITUDE,
				LocTable.COLUMN_ID };*/
        if (projection != null) {
            HashSet<String> requestedColumns = new HashSet<String>(
                    Arrays.asList(projection));
            HashSet<String> availableColumns = new HashSet<String>(
                    Arrays.asList(available));
            // Check if all columns which are requested are available
            if (!availableColumns.containsAll(requestedColumns)) {
                throw new IllegalArgumentException(
                        "Unknown columns in projection");
            }
        }
    }
}
