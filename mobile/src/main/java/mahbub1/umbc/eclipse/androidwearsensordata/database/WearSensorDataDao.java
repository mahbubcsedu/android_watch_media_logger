package mahbub1.umbc.eclipse.androidwearsensordata.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.text.TextUtils;
import android.util.Log;

import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by mahbub on 2/3/17.
 */


public class WearSensorDataDao {

    /** The Constant TAG. */
    private static final String TAG = "WearSensorDataDao";

    /** The m db helper. */
    private DbHelper dbHelper;

    /** The s sensor data projection map. */
    private static HashMap<String, String> sensorHashMap;

   /**
     * Instantiates CalendarUtils new aps dao.
     *
     * @param context
     *            the context
     */
    public WearSensorDataDao(Context context) {
        dbHelper = new DbHelper(context);
    }

    /**
     * Instantiates CalendarUtils new aps dao.
     *
     * @param context
     *            the context
     * @param testMode
     *            the test mode
     */
    protected WearSensorDataDao(Context context, boolean testMode) {
        if (testMode) {
            dbHelper = new DbHelper(context, testMode);
        } else {
            dbHelper = new DbHelper(context);
        }
    }

    /**
     * Delete by id.
     *
     * @param id
     *            the id
     * @param idColumnName
     *            the id column name
     * @param tableName
     *            the table name
     * @return the int
     */
    public int deleteById(int id, String idColumnName, String tableName) {
        String finalWhere = idColumnName + " = " + id;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // Performs the delete.
        int count = db.delete(tableName, // The database table name.
                finalWhere, // The final WHERE clause
                null // The incoming where clause values.
        );
        return count;
    }

    /**
     * Delete.
     *
     * @param where
     *            the where
     * @param whereArgs
     *            the where args
     * @param tableName
     *            the table name
     * @return the int
     */
    public int delete(String where, String[] whereArgs, String tableName) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int count = db.delete(tableName, // The database table name
                where, // The incoming where clause column names
                whereArgs // The incoming where clause values
        );
        // Returns the number of rows deleted.
        return count;
    }

    /**
     * Insert.
     *
     * @param values
     *            the values
     * @param nullHackColumnName
     *            the null hack column name
     * @param tableName
     *            the table name
     * @return the long
     */
    public long insert(ContentValues values, String nullHackColumnName,
                       String tableName) {

        // If the incoming values map is not null, uses it for the new values.
        if (values != null) {
            values = new ContentValues(values);

        } else {
            // Otherwise, create CalendarUtils new value map
            values = new ContentValues();
        }

        // Gets the current system time in milliseconds
        // Long now = Long.valueOf(System.currentTimeMillis());

		/*
		 * if
		 * (values.containsKey(MedicationList.MedicationItem.COLUMN_NAME_LASTTAKEN
		 * ) == false) {
		 * values.put(MedicationList.MedicationItem.COLUMN_NAME_LASTTAKEN, now);
		 * }
		 */

        // Opens the database object in "write" mode.
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        long rowId = db.insert(tableName, // The table to insert into.
                nullHackColumnName, // A hack, SQLite sets this column value to
                // null
                // if values is empty.
                values // A map of column names, and the values to insert
                // into the columns.
        );
        // Log.d(TAG, "Insert ROWID= " + rowId);
        if (rowId < 0) {
            throw new SQLiteException("Failed to insert diary.");
        }

        // ContentValues formValues = new ContentValues();
        // formValues.put("last_collection",
        // Calendar.getInstance().getTime().toString());
        // db.update("form_content",formValues,"form_name='diary'",null);

        Log.d(TAG,"values:" +values.toString()+"row id:"+rowId);
        return rowId;
    }

    /**
     * Query by id.
     *
     * @param id
     *            the id
     * @param projection
     *            the projection
     * @param tableName
     *            the table name
     * @param idColumnName
     *            the id column name
     * @param defaultSortOrder
     *            the default sort order
     * @return the cursor
     */
    public Cursor queryById(int id, String[] projection, String tableName,
                            String idColumnName, String defaultSortOrder) {
        // Constructs CalendarUtils new query builder and sets its table name
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(tableName);
        qb.appendWhere(idColumnName + // the name of the ID column
                "=" + id);
        return performQuery(qb, projection, null, null, null, defaultSortOrder);
    }

    /**
     * Query.
     *
     * @param projection
     *            the projection
     * @param selection
     *            the selection
     * @param selectionArgs
     *            the selection args
     * @param sortOrder
     *            the sort order
     * @param tableName
     *            the table name
     * @param defaultSortOrder
     *            the default sort order
     * @return the cursor
     */
    public Cursor query(String[] projection, String selection,
                        String[] selectionArgs, String sortOrder, String tableName,
                        String defaultSortOrder) {
        // Constructs CalendarUtils new query builder and sets its table name
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(tableName);

        return performQuery(qb, projection, selection, selectionArgs,
                sortOrder, defaultSortOrder);
    }

    /**
     * Perform query.
     *
     * @param qb
     *            the qb
     * @param projection
     *            the projection
     * @param selection
     *            the selection
     * @param selectionArgs
     *            the selection args
     * @param sortOrder
     *            the sort order
     * @param defaultSortOrder
     *            the default sort order
     * @return the cursor
     */
    private Cursor performQuery(SQLiteQueryBuilder qb, String[] projection,
                                String selection, String[] selectionArgs, String sortOrder,
                                String defaultSortOrder) {

        // Opens the database object in "read" mode, since no writes need to be
        // done.
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // qb.setProjectionMap(sDiarysProjectionMap);

        String orderBy;
        // If no sort order is specified, uses the default
        if (TextUtils.isEmpty(sortOrder)) {
            orderBy = defaultSortOrder;
        } else {
            // otherwise, uses the incoming sort order
            orderBy = sortOrder;
        }

        Log.i(TAG,
                "performQuery- Projection: " + Arrays.toString(projection) + " selection: "
                        + selection + " selection arguments: "
                        + Arrays.toString(selectionArgs));
		/*
		 * Performs the query. If no problems occur trying to read the database,
		 * then CalendarUtils Cursor object is returned; otherwise, the cursor variable
		 * contains null. If no records were selected, then the Cursor object is
		 * empty, and Cursor.getCount() returns 0.
		 */
        Cursor c = qb.query(db, // The database to query
                projection, // The columns to return from the query
                selection, // The columns for the where clause
                selectionArgs, // The values for the where clause
                null, // don't group the rows
                null, // don't filter by row groups
                orderBy // The sort order
        );
        return c;
    }

    /**
     * Update by id.
     *
     * @param Id
     *            the id
     * @param values
     *            the values
     * @param tableName
     *            the table name
     * @param idColumnName
     *            the id column name
     * @return the int
     */
    public int updateById(long Id, ContentValues values, String tableName,
                          String idColumnName) {

        // Opens the database object in "write" mode.
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int count;
        String where = idColumnName + // The ID column name
                " = " + Id; // test for equality

        // Does the update and returns the number of rows updated.
        count = db.update(tableName, // The database table name.
                values, // A map of column names and new values to use.
                where, // The final WHERE clause to use
                // placeholders for whereArgs
                null // The where clause column values to select on, or
                // null if the values are in the where argument.
        );

        // Returns the number of rows updated.
        return count;
    }

    /**
     * Update.
     *
     * @param values
     *            the values
     * @param where
     *            the where
     * @param whereArgs
     *            the where args
     * @param tableName
     *            the table name
     * @return the int
     */
    public int update(ContentValues values, String where, String[] whereArgs,
                      String tableName) {

        // Opens the database object in "write" mode.
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int count;

        // Does the update and returns the number of rows updated.
        count = db.update(tableName, // The database table name.
                values, // A map of column names and new values to use.
                where, // The where clause column names.
                whereArgs // The where clause column values to select on.
        );

        return count;
    }

    /**
     * Gets the db helper for test.
     *
     * @return the db helper for test
     */
    public DbHelper getDbHelperForTest() {
        return dbHelper;
    }

}