package mahbub1.umbc.eclipse.androidwearsensordata.events;


import android.os.Handler;
import android.os.Looper;

import com.squareup.otto.Bus;

/**
 * Created by mahbub on 1/25/17.
 */
//try to learn bus provider eventbus, rxbusx and otto
public class BusProvider {
    private static final Bus BUS = new Bus();

     public static Bus getInstance(){
         return BUS;
     }

    public static void postOnMainThread(final Object event){
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                BUS.post(event);
            }
        });
    }

    private BusProvider(){}
}
