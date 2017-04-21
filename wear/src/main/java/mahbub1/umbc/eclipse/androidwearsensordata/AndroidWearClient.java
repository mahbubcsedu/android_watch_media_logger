package mahbub1.umbc.eclipse.androidwearsensordata;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.util.SparseLongArray;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by mahbub on 1/24/17.
 */

public class AndroidWearClient {

    private static final String TAG="AndroidWearClient";
    private static final int CLIENT_CONNECTION_TIMEOUT = 10000;// its in miliseconds

    public static AndroidWearClient instance;
    private Context mContext;
    private GoogleApiClient mGoogleApiClient;
    private ExecutorService mExecutorService;
    private int filterSensorId;
    private SparseLongArray mLastSensorData;
    private Resources resources;

    // singleton class to get a static instance of the current wear instance
    public static AndroidWearClient getInstance(Context context){

        if(instance==null){

            instance = new AndroidWearClient(context.getApplicationContext());
        }
        return instance;
    }

    private AndroidWearClient(Context context){
        this.mContext=context;
        mGoogleApiClient = new GoogleApiClient.Builder(context).addApi(Wearable.API).build();

        mExecutorService = Executors.newCachedThreadPool(); //create new multiple thread and executer can take any one of them
        mLastSensorData = new SparseLongArray();
    }


public void setSensorToFilter(int filterSensorId){
    this.filterSensorId = filterSensorId;
}


public void sendSensorData(final int sensorType, final int accuracy, final long timestamp, final float[] values){
    long currentTimeStamp = System.currentTimeMillis();

    long lastTimeStamp = mLastSensorData.get(sensorType);
    long timeGap = currentTimeStamp - lastTimeStamp;


    if(lastTimeStamp !=0) {
        //filterid is the specific sensor which the application is interested and will record if 100 milliseconds passed
        if (filterSensorId == sensorType && timeGap < 100) {
            return;
        }
        // if we are not interested right now, we can record the sensor data at 3000 interval
        if (filterSensorId != sensorType && timeGap < 3000) {
            return;
        }
    }
        mLastSensorData.put(sensorType,currentTimeStamp);

        mExecutorService.submit(new Runnable() {
            @Override
            public void run() {
                sendSensorDataToHostInBackGround(sensorType,accuracy,timestamp,values);
            }
        });


   }


    public void sendSensorDataToHostInBackGround(int sensorType, int accuracy, long timeStamp,float[] values){
    if(sensorType == filterSensorId){
        //print as information to logcat
        Log.i(TAG, "sensor: "+sensorType +"=" + Arrays.toString(values));
    }
        else {
        Log.d(TAG, "sensor:" +sensorType+"="+Arrays.toString(values));
    }

        PutDataMapRequest dataMapRequest = PutDataMapRequest.create("/wearesensors/"+sensorType);
        dataMapRequest.getDataMap().putInt(mContext.getResources().getString(R.string.ACCURACY),accuracy);
        dataMapRequest.getDataMap().putLong(mContext.getResources().getString(R.string.TIMESTAMP),timeStamp);
        dataMapRequest.getDataMap().putFloatArray(mContext.getResources().getString(R.string.VALUES),values);
        dataMapRequest.getDataMap().putInt(mContext.getResources().getString(R.string.FILTER),sensorType);


        PutDataRequest putDataRequest = dataMapRequest.asPutDataRequest();
        sendToHost(putDataRequest);
    }

    private boolean validateConnectionWithHost(){
        if(mGoogleApiClient.isConnected())
            return true;

        ConnectionResult connectionResult = mGoogleApiClient.blockingConnect(CLIENT_CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS);
        return  connectionResult.isSuccess();
    }

    private void sendToHost(PutDataRequest putDataRequest){
        if(validateConnectionWithHost()){
            Wearable.DataApi.putDataItem(mGoogleApiClient,putDataRequest)
                    .setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                        @Override
                        public void onResult(DataApi.DataItemResult dataItemResult) {
                            Log.v(TAG, "sending sensor data" + dataItemResult.getStatus().isSuccess());
                        }
                    });
        }
    }
}
