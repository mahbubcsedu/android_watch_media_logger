package mahbub1.umbc.eclipse.androidwearsensordata;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.SparseArray;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import mahbub1.umbc.eclipse.androidwearsensordata.data.Sensor;
import mahbub1.umbc.eclipse.androidwearsensordata.data.SensorDataPoint;
import mahbub1.umbc.eclipse.androidwearsensordata.data.SensorWithMappedIndex;
import mahbub1.umbc.eclipse.androidwearsensordata.data.TagData;
import mahbub1.umbc.eclipse.androidwearsensordata.events.BusProvider;
import mahbub1.umbc.eclipse.androidwearsensordata.events.NewSensorEvent;
import mahbub1.umbc.eclipse.androidwearsensordata.events.SensorUpdateEvent;
import mahbub1.umbc.eclipse.androidwearsensordata.events.TagAddEvent;
import mahbub1.umbc.eclipse.sensordatashared.data.PreferenceData;
import mahbub1.umbc.eclipse.sensordatashared.sensors.SensorNames;
import mahbub1.umbc.eclipse.sensordatashared.sensors.SourceSensorTypeMap;
import mahbub1.umbc.eclipse.sensordatashared.utils.DataMapFields;
import mahbub1.umbc.eclipse.sensordatashared.utils.DataTransferUtils;
import mahbub1.umbc.eclipse.sensordatashared.utils.MessagePath;

/**
 * Created by mahbub on 1/24/17.
 */

public class RemoteWearSensorManager {
    private static final String TAG=RemoteWearSensorManager.class.getSimpleName();
    private static final int WEAR_CONNECTION_TIMEOUT= 10000;

    private Context mContext;
    private ExecutorService mExecutorService;
    private GoogleApiClient mGoogleApiClient;
    private SparseArray<Sensor> sensorMapping;
    private ArrayList<Sensor> sensors;
    private SensorNames sensorNames;
    private Resources resources;
    private static RemoteWearSensorManager instance;
    private LinkedList<TagData> tags = new LinkedList<>();
    private SourceSensorTypeMap sourceSensorMapping;
    private ArrayList<SensorWithMappedIndex> mappedSensors;

    public static synchronized  RemoteWearSensorManager getInstance(Context context){
        if(instance == null){
            instance = new RemoteWearSensorManager(context.getApplicationContext());
        }
        return instance;
    }

    public RemoteWearSensorManager(Context mContext) {
        this.mContext = mContext;
        this.sensorMapping = new SparseArray<Sensor>();
        this.sourceSensorMapping = new SourceSensorTypeMap();
        this.sensors = new ArrayList<Sensor>();
        this.mappedSensors = new ArrayList<SensorWithMappedIndex>();

        this.sensorNames = new SensorNames();
        this.mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addApi(Wearable.API)
                .build();
        this.mExecutorService = Executors.newCachedThreadPool();
    }

    public List<Sensor> getSensors(){
        return (List<Sensor>) sensors.clone();
    }

    public List<SensorWithMappedIndex> getMappedSensors(){
        return (List<SensorWithMappedIndex>)mappedSensors.clone();
    }
    public Sensor getSensor(long id){
        return sensorMapping.get((int) id);
    }


    private Sensor createSensor(int id,String sourceNodeId){
        Sensor sensor = new Sensor(id,sourceNodeId,sensorNames.getName(id));
        sensors.add(sensor);
        int mappingId=this.sourceSensorMapping.addNewSensor(sensor.getCompositeID());
        SensorWithMappedIndex sensorWithMappedIndex = new SensorWithMappedIndex(sensor,mappingId);
        mappedSensors.add(sensorWithMappedIndex);
        //sensorMapping.append(id,sensor);
        sensorMapping.append(mappingId,sensor);

        BusProvider.postOnMainThread(new NewSensorEvent(sensorWithMappedIndex));
        return sensor;
    }

    private Sensor getOrCreateSensor(int id, String sourceNodeId){
        String compositId=id+"-"+sourceNodeId;
        int mappedId=this.sourceSensorMapping.addNewSensor(compositId);
        //Sensor sensor = sensorMapping.get(id);
        Sensor sensor =sensorMapping.get(mappedId);
        //if the sensor not present already, a new type of sensor is added, so generate events for that
        if(sensor == null){
            sensor = createSensor(id,sourceNodeId);
        }
        return sensor;
    }

    public synchronized void addSensorData(int sensorType, String sourceNodeId, int accuracy, long timeStamp, float[] values){
       Sensor sensor = getOrCreateSensor(sensorType,sourceNodeId);
        SensorDataPoint dataPoint = new SensorDataPoint(timeStamp,values,accuracy);
        sensor.addDataPoints(dataPoint);
        BusProvider.postOnMainThread(new SensorUpdateEvent(sensor,dataPoint));

    }
    public synchronized void addTag(String pTagName) {
        TagData tag = new TagData(pTagName, System.currentTimeMillis());
        this.tags.add(tag);


        BusProvider.postOnMainThread(new TagAddEvent(tag));
    }

    public LinkedList<TagData> getTags() {
        return (LinkedList<TagData>) tags.clone();
    }

    public boolean validateConnection(){
        if(mGoogleApiClient.isConnected()){
            return true;
        }

        ConnectionResult result =mGoogleApiClient.blockingConnect(WEAR_CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS);
       return result.isSuccess();
    }

    public void filterBySensorId(final int sensorId) {
        mExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                filterBySensorIdInBackground(sensorId);
            }
        });
    }

    ;

    private void filterBySensorIdInBackground(final int sensorId) {
        Log.d(TAG, "filterBySensorId(" + sensorId + ")");

        if (validateConnection()) {
            PutDataMapRequest dataMap = PutDataMapRequest.create(MessagePath.PATH_FILTER);//"/filter");

            dataMap.getDataMap().putInt(DataMapFields.FILTER, sensorId);
            dataMap.getDataMap().putLong(DataMapFields.TIMESTAMP, System.currentTimeMillis());

            PutDataRequest putDataRequest = dataMap.asPutDataRequest();
            Wearable.DataApi.putDataItem(mGoogleApiClient, putDataRequest).setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                @Override
                public void onResult(DataApi.DataItemResult dataItemResult) {
                    Log.d(TAG, "Filter by sensor " + sensorId + ": " + dataItemResult.getStatus().isSuccess());
                }
            });
        }
    }

    public void startMeasurement() {
        mExecutorService.submit(new Runnable() {
            @Override
            public void run() {
                controlMeasurementInBackground(MessagePath.START_MEASUREMENT);
            }
        });
    }

    public void stopMeasurement() {
        mExecutorService.submit(new Runnable() {
            @Override
            public void run() {
                controlMeasurementInBackground(MessagePath.STOP_MEASUREMENT);
            }
        });
    }

    public void getNodes(ResultCallback<NodeApi.GetConnectedNodesResult> pCallback) {
        Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).setResultCallback(pCallback);
    }

    private String getPreferenceData(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        String frequency=prefs.getString("frequency_key","-1");
        boolean storageLocation=prefs.getBoolean("storage_switch",false);

        PreferenceData preferenceData=new PreferenceData(Integer.parseInt(frequency),storageLocation);
        String preferenceString =preferenceData.toString();
        return preferenceString;

    }
    private void controlMeasurementInBackground(final String path) {
        if (validateConnection()) {
            List<Node> nodes = Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await().getNodes();

            Log.d(TAG, "Sending to nodes: " + nodes.size());

            for (Node node : nodes) {
                Log.i(TAG, "add node " + node.getDisplayName());
                Wearable.MessageApi.sendMessage(
                        mGoogleApiClient, node.getId(), path, getPreferenceData().getBytes(DataTransferUtils.DEFAULT_CHARSET)
                ).setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
                    @Override
                    public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                        Log.d(TAG, "controlMeasurementInBackground(" + path + "): " + sendMessageResult.getStatus().getStatusCode());
                    }
                });
            }
        } else {
            Log.w(TAG, "No connection possible");
        }
    }
}
