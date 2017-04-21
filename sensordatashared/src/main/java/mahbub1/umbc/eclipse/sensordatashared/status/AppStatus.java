package mahbub1.umbc.eclipse.sensordatashared.status;

import android.os.Build;

/**
 * Created by mahbub on 4/17/17.
 */

public class AppStatus extends Status {


    private boolean appInitialized = false;
    private String deviceName = Build.MODEL;
    private ActivityStatus activityStatus = new ActivityStatus();
    private GoogleApiStatus googleApiStatus = new GoogleApiStatus();

    public boolean isAppInitialized() {
        return appInitialized;
    }

    public void setAppInitialized(boolean appInitialized) {
        this.appInitialized = appInitialized;
    }

    public ActivityStatus getActivityStatus() {
        return activityStatus;
    }

    public void setActivityStatus(ActivityStatus activityStatus) {
        this.activityStatus = activityStatus;
    }

    public GoogleApiStatus getGoogleApiStatus() {
        return googleApiStatus;
    }

    public void setGoogleApiStatus(GoogleApiStatus googleApiStatus) {
        this.googleApiStatus = googleApiStatus;
    }

}
