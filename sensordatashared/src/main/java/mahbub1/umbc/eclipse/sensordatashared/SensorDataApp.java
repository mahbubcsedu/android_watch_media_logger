package mahbub1.umbc.eclipse.sensordatashared;

import android.app.Activity;
import android.support.multidex.MultiDexApplication;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import mahbub1.umbc.eclipse.sensordatashared.status.AppStatus;

/**
 * Created by mahbub on 4/14/17.
 */

public class SensorDataApp extends MultiDexApplication {
public static final String TAG=SensorDataApp.class.getSimpleName();


    private AppStatus status = new AppStatus();
    private Activity activityContext;
    private static SensorDataApp sensorDataAppInstance;

    public void initialized(Activity activityContext){
        this.activityContext= activityContext;


    }


    public static SensorDataApp getInstance(){
        return sensorDataAppInstance;
    }
    public void setupDatabase(){
        Realm.init(this);
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder().build();
        Realm.deleteRealm(realmConfiguration); // Clean slate
        Realm.setDefaultConfiguration(realmConfiguration); // Make this Realm the default

    }
}
