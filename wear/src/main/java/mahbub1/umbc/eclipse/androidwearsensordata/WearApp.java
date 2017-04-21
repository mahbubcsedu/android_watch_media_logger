package mahbub1.umbc.eclipse.androidwearsensordata;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by mahbub on 3/30/17.
 */

public class WearSingleTon extends Application {

    private static WearSingleTon wearapplicationInstance;



    @Override
    public void onCreate(){
        super.onCreate();

        //getApplicationContext();
        Realm.init(this);
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder().build();
        Realm.deleteRealm(realmConfiguration); // Clean slate
        Realm.setDefaultConfiguration(realmConfiguration);
        wearapplicationInstance = this;

    }


    public static synchronized WearSingleTon getInstance(){
        return wearapplicationInstance;
    }

}
