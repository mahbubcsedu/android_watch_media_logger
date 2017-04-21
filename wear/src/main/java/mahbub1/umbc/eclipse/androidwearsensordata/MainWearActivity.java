package mahbub1.umbc.eclipse.androidwearsensordata;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * This is main activity at wearable side. If, development media is emulator, it needs to install
 * mobile and wear app separately and need at least an activity on both side
 */
public class MainWearActivity extends Activity {

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextView = (TextView) findViewById(R.id.text);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //startService(new Intent(this, SensorDataCollectionServices.class));
    }

    @Override
    protected void onPause() {
        super.onPause();
        //stopService(new Intent(this, SensorDataCollectionServices.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //stopService(new Intent(this, SensorDataCollectionServices.class));
    }
}

