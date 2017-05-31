package mahbub1.umbc.eclipse.androidwearsensordata;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.BoxInsetLayout;
import android.view.View;
import android.widget.TextView;

import mahbub1.umbc.eclipse.sensordatashared.status.GoogleApiStatus;

/**
 * This is main activity at wearable side. If, development media is emulator, it needs to install
 * mobile and wear app separately and need at least an activity on both side
 */
public class MainWearActivity extends WearableActivity {

    private static final String TAG = MainWearActivity.class.getSimpleName();
    private BoxInsetLayout mContainerView;
    private TextView mainTv;
    private TextView preTv;
    private TextView postTv;
    private TextView logTv;
    private TextView mTextView;
    private GoogleApiStatus appStatus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_main_wear);
        //mTextView = (TextView) findViewById(R.id.text);
        /*Realm.init(this);
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder().build();
        Realm.deleteRealm(realmConfiguration); // Clean slate
        Realm.setDefaultConfiguration(realmConfiguration);*/
        setupUi();
    }


    private void setupUi() {
        setContentView(R.layout.activity_main_wear);
        //setAmbientEnabled();
        this.appStatus = new GoogleApiStatus();
        mContainerView = (BoxInsetLayout) findViewById(R.id.container);
        mainTv = (TextView) findViewById(R.id.mainText);
        // preTv = (TextView) findViewById(R.id.preText);
        //postTv = (TextView) findViewById(R.id.postText);
        //logTv = (TextView) findViewById(logText);

       /* mContainerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateStatusTexts();
            }
        });*/

        //updateDisplay();
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


    private void updateDisplay() {
        if (isAmbient()) {
            mContainerView.setBackgroundColor(Color.BLACK);

            preTv.setVisibility(View.GONE);
            postTv.setVisibility(View.GONE);
            logTv.setVisibility(View.GONE);
        } else {
            mContainerView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));

            preTv.setVisibility(View.VISIBLE);
            postTv.setVisibility(View.VISIBLE);
            logTv.setVisibility(View.VISIBLE);
        }
        // updateStatusTexts();
    }


    private void updateStatusTexts() {
        // String connectedDeviceName = getConnectedDeviceName();
        // int availableSensorsCount = getAvailableSensorsCount();
        //  int registeredSensorsCount = getRegisteredSensorsCount();

        String preText;
        String mainText;
        String postText;
        String logText = String.valueOf(this.appStatus.getLastUpdateTime());

        /*if (isSendingRequestResponses()) {
            preText = getString(R.string.status_connected_pre);
            preText = preText.replace("[REGISTERED_SENSORS]", String.valueOf(registeredSensorsCount));
            preText = preText.replace("[AVAILABLE_SENSORS]", String.valueOf(availableSensorsCount));
            mainText = getString(R.string.status_connected_main);
            postText = getString(R.string.status_connected_post);
            postText = postText.replace("[DEVICENAME]", connectedDeviceName);
        } else {*/
        preText = getString(R.string.status_disconnected_pre);
        //preText = preText.replace("[AVAILABLE_SENSORS]", String.valueOf(availableSensorsCount));
        mainText = getString(R.string.status_disconnected_main);
        postText = (this.appStatus.isConnected() ? "connected" : "disconnected");
        //}

        preTv.setText(preText);
        mainTv.setText(mainText);
        postTv.setText(postText);
        logTv.setText(logText);
    }
}

