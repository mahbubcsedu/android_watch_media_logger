package mahbub1.umbc.eclipse.androidwearsensordata;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.BoxInsetLayout;
import android.support.wearable.view.CircledImageView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.List;

import io.realm.RealmResults;
import mahbub1.umbc.eclipse.sensordatashared.data.DataBatch;
import mahbub1.umbc.eclipse.sensordatashared.data.Data;
import mahbub1.umbc.eclipse.sensordatashared.data.DataRequestResponse;
import mahbub1.umbc.eclipse.sensordatashared.database.WearableSensorDataList;
import mahbub1.umbc.eclipse.sensordatashared.serversync.LocalToServerSync;

/**
 * Created by mahbub on 4/10/17.
 */

public class ExportActivity extends WearableActivity {
    public static final String TAG = ExportActivity.class.getSimpleName();
    private BoxInsetLayout mContainerView;
    private TextView textView;
    private LocalToServerSync localToServerSync;
    private Context mContext;
    ProgressBar mProgress;
    CircledImageView cancle_btn, ok_btn;
    private WearApp wearAppInstance;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupUi();
        mContext = this;
        wearAppInstance = WearApp.getInstance();

        Resources res = getResources();
        Drawable drawable = res.getDrawable(R.drawable.circular);
        mProgress = (ProgressBar) findViewById(R.id.circularProgressbar);
        mProgress.setProgress(0);   // Main Progress
        mProgress.setSecondaryProgress(100); // Secondary Progress
        mProgress.setMax(100); // Maximum Progress
        mProgress.setVisibility(View.GONE);
        mProgress.setProgressDrawable(drawable);


        cancle_btn = (CircledImageView) findViewById(R.id.cancel_btn);
        ok_btn = (CircledImageView) findViewById(R.id.ok_btn);
        textView = (TextView) findViewById(R.id.description);


        cancle_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ExportActivity.this, ListViewActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("EXIT", true);
                startActivity(intent);
            }
        });


        ok_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setVisibility(View.GONE);
                cancle_btn.setVisibility(View.GONE);
                textView.setVisibility(View.GONE);
                localToServerSync = new LocalToServerSync(mContext);
                if (isOnline(mContext)) {

                    new Thread() {
                        @Override
                        public void run() {
                            exportDirectlyToServer();
                            Log.d(TAG, "Transferring to server.....");
                        }
                    }.start();
                } else {

                    Toast.makeText(mContext, "network or connection failed", Toast.LENGTH_LONG).show();
                }
            }

        });
    }

    private void setupUi() {
        setContentView(R.layout.activity_exp);
        //setAmbientEnabled();

        mContainerView = (BoxInsetLayout) findViewById(R.id.container);

    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
        //updateDisplay();
        //status.setAmbientMode(true);
        //status.updated(statusUpdateHandler);
    }

    @Override
    public void onUpdateAmbient() {
        super.onUpdateAmbient();
        //updateDisplay();
    }

    @Override
    public void onExitAmbient() {
        //updateDisplay();
        // status.setAmbientMode(false);
        //status.updated(statusUpdateHandler);
        super.onExitAmbient();
    }


    private void exportDirectlyToServer() {
        //make the buttons invisible and progress circle visible


        final RealmResults<WearableSensorDataList> result = localToServerSync.retrieveDatatoTransfer();
        final int dataSize = result.size();
        Log.d(TAG, "data size to transfer: " + dataSize);

        final Gson gson = new Gson();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgress.setMax(dataSize);
                mProgress.setVisibility(View.VISIBLE);
                mProgress.setProgress(0);
            }
        });

        int i = 0;
        for (WearableSensorDataList dataListJson : result) {
            Log.d(TAG, "retrieved data:" + dataListJson.getJsonAsString().toString());

            //int range = Math.min(i, unsyncData.size());
            final int progress = i;
            DataRequestResponse dataRequestResponse = DataRequestResponse.fromJson(dataListJson.getJsonAsString());
            /*Type listOfTestObject = new TypeToken<List<Data>>(){}.getType();
            List<Data> listOfData = gson.fromJson(dataListJson.getJsonAsString(), listOfTestObject);
            String sourcNodeId = dataListJson.getAndroidDevice();
            long id = dataListJson.getId();

            Log.d(TAG, "data as list: "+listOfData.size()+"data"+listOfData.toArray().toString());*/

            //localToServerSync.syncDataToServerAsJson(listOfData,sourcNodeId,id);
            List<DataBatch> dataBatchesList = dataRequestResponse.getDataBatches();
            List<Data> dataList = dataBatchesList.get(0).getDataList();
            localToServerSync.syncDataToServerAsJson(dataList, dataListJson.getAndroidDevice(), dataListJson.getId());


            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //mProgress.setMax(dataSize);
                    //mProgress.setVisibility(View.VISIBLE);
                    mProgress.setProgress(progress);
                }
            });
            i = i + 1;


        }


        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mProgress.setVisibility(View.GONE);
                        textView.setVisibility(View.VISIBLE);
                        textView.setText(localToServerSync.getDataCounter() + " data points transfered");
                    }
                }, 1000);

            }
        });
    }

    public boolean isOnline(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        //should check null because in airplane mode it will be null
        return (netInfo != null && netInfo.isConnected());
    }

   /* private boolean validateConnectionWithHost() {
        if (mGoogleApiClient.isConnected()) {
            return true;
        }

        ConnectionResult connectionResult = mGoogleApiClient.blockingConnect(CLIENT_CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS);
        return connectionResult.isSuccess();
    }*/
}
