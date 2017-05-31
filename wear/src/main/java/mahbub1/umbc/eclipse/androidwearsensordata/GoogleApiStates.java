package mahbub1.umbc.eclipse.androidwearsensordata;

import android.content.Context;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Wearable;

import java.util.concurrent.TimeUnit;

/**
 * Created by mahbub on 3/28/17.
 */

public class GoogleApiStates {
    private GoogleApiClient mGoogleApiClient;
    private static final int CLIENT_CONNECTION_TIMEOUT = 10000;// its in miliseconds

    public GoogleApiStates(Context context) {
        mGoogleApiClient = mGoogleApiClient = new GoogleApiClient.Builder(context).addApi(Wearable.API).build();
    }

    public boolean validateConnectionWithHost() {
        if (mGoogleApiClient.isConnected())
            return true;

        ConnectionResult connectionResult = mGoogleApiClient.blockingConnect(CLIENT_CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS);
        return connectionResult.isSuccess();
    }
}
