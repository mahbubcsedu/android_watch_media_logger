package mahbub1.umbc.eclipse.androidwearsensordata;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by mahbub on 3/28/17.
 */

public class PowerBroadCastReceiver extends BroadcastReceiver {
    public static final String TAG = PowerBroadCastReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (action.equals(Intent.ACTION_POWER_CONNECTED)) {
            Log.d(TAG, "power connected");
            Intent i = new Intent();
            i.setClassName("mahbub1.umbc.eclipse.androidwearsensordata", "mahbub1.umbc.eclipse.androidwearsensordata.ExportActivity");
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        } else if (action.equals(Intent.ACTION_POWER_DISCONNECTED)) {
            // Do something when power disconnected
            Log.d(TAG, "power disconnected");
        }


        //Log.d(TAG, "power connected");

    }


}
