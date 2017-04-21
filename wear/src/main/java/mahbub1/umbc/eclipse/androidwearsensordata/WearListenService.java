package mahbub1.umbc.eclipse.androidwearsensordata;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import mahbub1.umbc.eclipse.sensordatashared.utils.DataMapFields;
import mahbub1.umbc.eclipse.sensordatashared.utils.MessagePath;


/**
 * Created by mahbub on 1/24/17.
 */
@SuppressLint("LongLogTag")
public class WearableMessageReceiveService extends WearableListenerService {
    private static final String TAG = WearableMessageReceiveService.class.getSimpleName();

    private AndroidWearClient androidWearClient;

    @Override
    public void onCreate(){
        super.onCreate();

        androidWearClient = AndroidWearClient.getInstance(this);


        //startForeground(1, builder.build());
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer){
        Log.d(TAG, "Sensor data changed");
        //super.onDataChanged(dataEventBuffer);

        for(DataEvent dataEvent: dataEventBuffer){
            if(dataEvent.getType()==DataEvent.TYPE_CHANGED){
                DataItem dataItem = dataEvent.getDataItem();
                Uri uri = dataItem.getUri();
                String path = uri.getPath();



                if(path.startsWith(MessagePath.PATH_FILTER)){
                    DataMap dataMap = DataMapItem.fromDataItem(dataItem).getDataMap();
                    int filterById = dataMap.getInt(DataMapFields.FILTER);
                    androidWearClient.setSensorToFilter(filterById);
                }
            }

        }


    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent){
        //super.onMessageReceived(messageEvent);
        Log.d(TAG, "Received Message: "+messageEvent.getPath());


        if(messageEvent.getPath().equals(MessagePath.START_MEASUREMENT)){//getApplicationContext().getResources().getString(R.string.START_RECORDING_PATH))){
            startService(new Intent(this, SensorDataCollectionServices.class));
        }
        if(messageEvent.getPath().equals(MessagePath.STOP_MEASUREMENT)){//getApplicationContext().getResources().getString(R.string.STOP_RECORDING_PATH))){
            stopService(new Intent(this, SensorDataCollectionServices.class));
        }
    }



}
