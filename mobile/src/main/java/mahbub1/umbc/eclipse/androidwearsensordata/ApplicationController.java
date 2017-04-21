package mahbub1.umbc.eclipse.androidwearsensordata;

import android.app.Application;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.Volley;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by mahbub on 2/7/17.
 */

public class ApplicationController extends Application {
    public static final String TAG=ApplicationController.class.getSimpleName();

    private RequestQueue mRequestQueue;

    private static ApplicationController applicationInstance;



    @Override
    public void onCreate(){
        super.onCreate();
        Realm.init(this);
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder().build();
        Realm.deleteRealm(realmConfiguration); // Clean slate
        Realm.setDefaultConfiguration(realmConfiguration);
        applicationInstance = this;

    }


    public static synchronized ApplicationController getInstance(){
        return applicationInstance;
    }


    public RequestQueue getmRequestQueue(){
        if(mRequestQueue == null){
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> request, String tag){
        request.setTag(TextUtils.isEmpty(tag)? TAG : tag);
        VolleyLog.d("Adding request to queue %s", request.getUrl());
        getmRequestQueue().add(request);
    }

    public <T> void addToRequestQueue(Request<T> request){
        request.setTag(TAG);
        getmRequestQueue().add(request);
    }

    public void cancelPendingRequest(Object tag){
        if(mRequestQueue!=null){
            mRequestQueue.cancelAll(tag);
        }
    }
}
