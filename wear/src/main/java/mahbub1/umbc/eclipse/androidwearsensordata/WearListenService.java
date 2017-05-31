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

import mahbub1.umbc.eclipse.sensordatashared.database.PreferenceData;
import mahbub1.umbc.eclipse.sensordatashared.utils.DataMapFields;
import mahbub1.umbc.eclipse.sensordatashared.utils.MessagePath;

import static mahbub1.umbc.eclipse.sensordatashared.utils.DataTransferUtils.getDataFromMessageAsString;


/**
 * Created by mahbub on 1/24/17.
 */
@SuppressLint("LongLogTag")
public class WearListenService extends WearableListenerService {
    private static final String TAG = WearListenService.class.getSimpleName();

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
        final Intent intent = new Intent(this, SensorServices.class);



        if(messageEvent.getPath().equals(MessagePath.START_MEASUREMENT)){//getApplicationContext().getResources().getString(R.string.START_RECORDING_PATH))){

            String prefData = getDataFromMessageAsString(messageEvent.getData());
            PreferenceData preferenceData = PreferenceData.fromJson(prefData);
            int frequency = preferenceData.getSensor_frequency();
            int delay = DataMapFields.getDelay(frequency);
            preferenceData.isStorageLocationIsWatch();
            intent.putExtra("isStorageLocal", preferenceData.isStorageLocationIsWatch());
            intent.putExtra("frequency", delay);

            startService(intent);
        }
        if(messageEvent.getPath().equals(MessagePath.STOP_MEASUREMENT)){//getApplicationContext().getResources().getString(R.string.STOP_RECORDING_PATH))){

            Thread serviceStopThread = new Thread() {

                @Override
                public void run() {
                    stopService(intent);
                }
            };
            serviceStopThread.setPriority(10);
            serviceStopThread.start();
            Log.d(TAG, "service stop requested");

        }

    }


}
