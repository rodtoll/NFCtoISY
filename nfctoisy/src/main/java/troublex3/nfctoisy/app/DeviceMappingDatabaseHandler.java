package troublex3.nfctoisy.app;

import android.database.sqlite.*;
import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import java.util.List;
import java.util.ArrayList;


/**
 * Created by rodtoll on 2/19/14.
 */
public class DeviceMappingDatabaseHandler extends SQLiteOpenHelper
{
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "device_map";

    // Contacts table name
    private static final String DEVICE_MAP_TABLE_NAME = "tags";

    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_DEVICE_MAP_DEVICE_ALIAS = "device_name";
    private static final String KEY_DEVICE_MAP_DEVICE_ADDRESS = "device_address";

    public DeviceMappingDatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_DEVICE_MAP_TABLE = "CREATE TABLE " + DEVICE_MAP_TABLE_NAME + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_DEVICE_MAP_DEVICE_ALIAS + " TEXT,"
                + KEY_DEVICE_MAP_DEVICE_ADDRESS + " TEXT" + ")";
        db.execSQL(CREATE_DEVICE_MAP_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + DEVICE_MAP_TABLE_NAME);

        // Create tables again
        onCreate(db);
    }

    public void addDeviceMapEntry(DeviceMapEntry entry)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_DEVICE_MAP_DEVICE_ALIAS, entry.getAlias());
        values.put(KEY_DEVICE_MAP_DEVICE_ADDRESS, entry.getAddress());

        // Inserting Row
        db.insert(DEVICE_MAP_TABLE_NAME, null, values);
        db.close(); // Closing database connection
    }

    public DeviceMapEntry getDeviceMapEntry(String alias)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(DEVICE_MAP_TABLE_NAME, new String[] { KEY_ID,
                KEY_DEVICE_MAP_DEVICE_ALIAS, KEY_DEVICE_MAP_DEVICE_ADDRESS }, KEY_DEVICE_MAP_DEVICE_ALIAS + "=?",
                new String[] { alias }, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        DeviceMapEntry entry = new DeviceMapEntry();
        entry.setID(Integer.parseInt(cursor.getString(0)));
        entry.setAlias(cursor.getString(1));
        entry.setAddress(cursor.getString(2));
        return entry;
    }

    public void updateDeviceMapEntry(DeviceMapEntry entry)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_DEVICE_MAP_DEVICE_ALIAS, entry.getAlias());
        values.put(KEY_DEVICE_MAP_DEVICE_ADDRESS, entry.getAddress());

        // updating row
        db.update(DEVICE_MAP_TABLE_NAME, values, KEY_ID + " = ?",
                new String[] { String.valueOf(entry.getID()) });
    }

    public void deleteDeviceMapEntry(int id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DEVICE_MAP_TABLE_NAME, KEY_ID + " = ?",
                new String[] { String.valueOf(id) });
        db.close();
    }

    public List<DeviceMapEntry> getAllDeviceMapEntries()
    {
        List<DeviceMapEntry> deviceMapList = new ArrayList<DeviceMapEntry>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + DEVICE_MAP_TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                DeviceMapEntry entry = new DeviceMapEntry();
                entry.setID(Integer.parseInt(cursor.getString(0)));
                entry.setAlias(cursor.getString(1));
                entry.setAddress(cursor.getString(2));
                deviceMapList.add(entry);
            } while (cursor.moveToNext());
        }

        // return contact list
        return deviceMapList;
    }

}
