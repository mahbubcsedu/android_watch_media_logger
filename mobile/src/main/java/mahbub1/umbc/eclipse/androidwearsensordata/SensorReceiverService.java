package mahbub1.umbc.eclipse.androidwearsensordata;

import android.content.Context;
import android.net.Uri;
import android.os.StrictMode;
import android.util.Log;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.WearableListenerService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

import io.realm.Realm;
import mahbub1.umbc.eclipse.androidwearsensordata.events.BusProvider;
import mahbub1.umbc.eclipse.androidwearsensordata.events.DataBatchChangedEvent;
import mahbub1.umbc.eclipse.sensordatashared.data.Data;
import mahbub1.umbc.eclipse.sensordatashared.data.DataBatch;
import mahbub1.umbc.eclipse.sensordatashared.data.DataRequestResponse;
import mahbub1.umbc.eclipse.sensordatashared.database.WearableSensorDataList;
import mahbub1.umbc.eclipse.sensordatashared.utils.DataMapFields;
import mahbub1.umbc.eclipse.sensordatashared.utils.DataTransferUtils;
import mahbub1.umbc.eclipse.sensordatashared.utils.MessagePath;

/**
 * Created by mahbub on 1/25/17.
 */

public class SensorReceiverService extends WearableListenerService {
    private static final String TAG = "SensorReceiverService";

    private RemoteWearSensorManager mSensorManager;
    private Context mContext;
    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
    private List<Data> mSensorDataToStore;
    private ExecutorService mExecutorService;
    private long dataTransferedSoFar = 0;
    private long realmThread = 0;

    @Override
    public void onCreate() {
        super.onCreate();

        mSensorDataToStore = new ArrayList<>();
        this.mExecutorService = Executors.newCachedThreadPool();
        mSensorManager = RemoteWearSensorManager.getInstance(this);
        mContext = getApplication();

        if (BuildConfig.DEBUG) {
            StrictMode.VmPolicy policy = new StrictMode.VmPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build();
            StrictMode.setVmPolicy(policy);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onPeerConnected(Node node) {
        super.onPeerConnected(node);
        Log.i(TAG, "Connected: " + node.getDisplayName() + "(" + node.getId() + ")");
    }

    @Override
    public void onPeerDisconnected(Node node) {
        super.onPeerDisconnected(node);

        Log.i(TAG, "Disconnected: " + node.getDisplayName() + "(" + node.getId() + ")");
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {
        Log.d(TAG, "DataChanged notified");
        super.onDataChanged(dataEventBuffer);

        for (DataEvent dataEvent : dataEventBuffer) {
            if (dataEvent.getType() == DataEvent.TYPE_CHANGED) {
                DataItem dataItem = dataEvent.getDataItem();
                Uri uri = dataItem.getUri();
                String path = uri.getPath();
                String node = dataEvent.getDataItem().getUri().getHost();
                Log.d(TAG, "host node: " + node);

                if (path.startsWith(MessagePath.PATH_WEARABLE_DATA)) {//"/wearesensors/")){
                    unpackSensorData(Integer.parseInt(uri.getLastPathSegment()), DataMapItem.fromDataItem(dataItem).getDataMap());
                }
            }
        }
    }

    private void unpackSensorData(int sensorType, DataMap dataMap) {
        //int accuracy = dataMap.getInt(DataMapFields.ACCURACY);
        //long timestamp = dataMap.getLong(DataMapFields.TIMESTAMP);
        //float[] values = dataMap.getFloatArray(DataMapFields.VALUES);
        Log.d(TAG, "data received size " + dataMap.getByteArray(DataMapFields.RESPONSE).length + " bytes");
        String responseJson = getDataFromMessageAsString(dataMap.getByteArray(DataMapFields.RESPONSE));
        DataRequestResponse response = DataRequestResponse.fromJson(responseJson);
        //response.getDataBatches().size();
        // Log.d(TAG, responseJson);
        //for the time being put device name in place for sourceNodeId. Later need to fix
        // final String sourceNodeId = dataMap.getString(DataMapFields.KEY_SOURCE_NODE_ID);
        final String deviceName = dataMap.get(DataMapFields.DEVICE_NAME);
        final String sourceNodeId = deviceName;
        //Log.d(TAG, sourceNodeId + "->" + deviceName);
        //final DataRequestResponse response = DataRequestResponse.fromJson(responseJson);
        /**
         * store the data here if it available
         */

/*        Gson gson = new Gson();
        for (DataBatch dataBatch : response.getDataBatches()) {
            this.dataTransferedSoFar += dataBatch.getDataList().size();
            Log.d(TAG, "data transfared so far"+this.dataTransferedSoFar);

            BusProvider.postOnMainThread(new DataBatchChangedEvent(dataBatch,sourceNodeId,deviceName));

            mSensorDataToStore.addAll(dataBatch.getDataList());


            if (mSensorDataToStore.size() > 500) {
                final List<Data> dataToWrite = new ArrayList<>(mSensorDataToStore);
                mSensorDataToStore = new ArrayList<>();

                ///for (Data dataObj : dataToWrite) {
                Log.d(TAG, "datasize to write" + dataToWrite.size());

                //}

                Type listOfTestObject = new TypeToken<List<Data>>() {
                }.getType();
                final String s = gson.toJson(dataToWrite, listOfTestObject);

                Runnable newTask = new Runnable() {
                    @Override
                    public void run() {
                        // android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
                        final Realm realm = Realm.getDefaultInstance();
                        realm.beginTransaction();

                        AtomicLong productPrimaryKey = new AtomicLong(realm.where(WearableSensorDataList.class).max("id") == null ? 0 : realm.where(WearableSensorDataList.class).max("id").longValue() + 1);

                        long id = productPrimaryKey.getAndIncrement();
                        WearableSensorDataList entry = realm.createObject(WearableSensorDataList.class, id);

                        entry.setJsonAsString(s);
                        entry.setStatus(DataTransferUtils.STATUS_DATA_TRANSFER_INCOMPLETE);
                        entry.setAndroidDevice(sourceNodeId);
                        //entry.setAndroidDeviceName(deviceName);
                        Log.d(TAG, "source id: "+sourceNodeId);
                        //   }

                        realm.commitTransaction();


                        *//*realm.beginTransaction();
                        AtomicLong recordingId= new AtomicLong(realm.where(SensorRecordingList.class).max("id")== null ? 0 : realm.where(SensorRecordingList.class).max("id").longValue()+1);

                        realm.commitTransaction();
                    *//*
                        realm.close();
                        Thread t = Thread.currentThread();
                        Log.d(TAG, "tname realm:"+t.getName()+"datasize: "+dataToWrite.size());
                        //});



                    }
                };

                mExecutorService.execute(newTask);
            }
        }*/

        //try to store each batch in a new thread

        Gson gson = new Gson();
        for (DataBatch dataBatch : response.getDataBatches()) {
            this.dataTransferedSoFar += dataBatch.getDataList().size();

            Log.d(TAG, "data transfared so far" + this.dataTransferedSoFar);

            BusProvider.postOnMainThread(new DataBatchChangedEvent(dataBatch, sourceNodeId, deviceName));


            Type listOfTestObject = new TypeToken<List<Data>>() {
            }.getType();

            final String s = gson.toJson(dataBatch.getDataList(), listOfTestObject);
            final int type = dataBatch.getType();
            final String source = dataBatch.getSource();
            Runnable newTask = new Runnable() {
                @Override
                public void run() {

                    Realm realm = null;
                    try { // I could use try-with-resources here
                        realm = Realm.getDefaultInstance();
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                AtomicLong productPrimaryKey = new AtomicLong(realm.where(WearableSensorDataList.class).max("id") == null ? 0 : realm.where(WearableSensorDataList.class).max("id").longValue() + 1);

                                long id = productPrimaryKey.getAndIncrement();
                                WearableSensorDataList entry = realm.createObject(WearableSensorDataList.class, id);

                                entry.setJsonAsString(s);
                                entry.setStatus(DataTransferUtils.STATUS_DATA_TRANSFER_INCOMPLETE);
                                entry.setAndroidDevice(sourceNodeId);
                                entry.setType(type);
                                entry.setSource(source);
                                Thread t = Thread.currentThread();
                                Log.d(TAG, "realmdb: file open with thread:" + t.getName());
                                realmThread++;
                            }
                        });
                    } finally {
                        if (realm != null) {
                            realm.close();
                            Log.d(TAG, "realmdb: closed :");
                            realmThread--;
                        }
                    }
                }
            };
            mExecutorService.execute(newTask);
            Log.d(TAG, "realmdb: total active thread:" + realmThread);
            /* Runnable newTask = new Runnable() {
                @Override
                public void run() {
                    // android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
                    final Realm realm = Realm.getDefaultInstance();
                    realm.beginTransaction();

                    AtomicLong productPrimaryKey = new AtomicLong(realm.where(WearableSensorDataList.class).max("id") == null ? 0 : realm.where(WearableSensorDataList.class).max("id").longValue() + 1);

                    long id = productPrimaryKey.getAndIncrement();
                    WearableSensorDataList entry = realm.createObject(WearableSensorDataList.class, id);

                    entry.setJsonAsString(s);
                    entry.setStatus(DataTransferUtils.STATUS_DATA_TRANSFER_INCOMPLETE);
                    entry.setAndroidDevice(sourceNodeId);
                    entry.setType(type);
                    entry.setSource(source);
                    Thread t = Thread.currentThread();
                    Log.d(TAG, "realmdb: file open with thread:"+t.getName());
                    //entry.setAndroidDeviceName(deviceName);
                    //Log.d(TAG, "source id: " + sourceNodeId);
                    //   }

                    realm.commitTransaction();
                    Log.d(TAG, "realmdb: closed :"+t.getName());
                    realm.close();

                    Log.d(TAG, "realmdb: closed :"+t.getName());
                    //});


                }
            };

            mExecutorService.execute(newTask);*/
        }


        // Log.d(TAG, "Received s data " + sensorType + " = " + Arrays.toString(values)+"datavalue"+responseJson);
        /* this is required if we want to show to previous version of ploting
        */


        // mSensorManager.addSensorData(sensorType, sourceNodeId,accuracy, timestamp, values);
    }

    public static String getDataFromMessageAsString(byte[] data) {
        //byte[] data = getDataFromMessage(message);
        if (data != null) {
            return new String(data, DEFAULT_CHARSET);
        }
        return null;
    }


}
