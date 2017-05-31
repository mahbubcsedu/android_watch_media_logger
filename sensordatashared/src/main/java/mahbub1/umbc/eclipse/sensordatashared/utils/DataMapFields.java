package mahbub1.umbc.eclipse.sensordatashared.utils;

import android.hardware.SensorManager;

/**
 * Created by mahbub on 3/16/17.
 */

public class DataMapFields {

    enum sampling_rate {
        DELAY_FASTEST, DELAY_GAME, DELAY_NORMAL, DELAY_UI, DELAY_HZ_60, DELAY_HZ_100, GAME_DELAY, FASTEST_DELAY, NORMAL_DELAY, UI_DELAY;
    }
    public static final String ACCURACY = "accuracy";
    public static final String TIMESTAMP = "timestamp";
    public static final String VALUES = "values";
    public static final String FILTER = "filter";
    public static final String RESPONSE = "response";
    public static final String KEY_SOURCE_NODE_ID="source_node_id";
    public static final String DEVICE_NAME="device_name";


    public static int getDelay(int rate) {
        sampling_rate which_rate = sampling_rate.values()[rate];
        int delay = -1;
        switch (which_rate) {
            case DELAY_FASTEST://0
                delay = 0;
                break;
            case DELAY_GAME://1//30hz
                delay = 20000;
                break;
            case DELAY_NORMAL://2 // 15 hz
                delay = 66667;
                break;
            case DELAY_UI://3 //10 hz
                delay = 200000;
                break;
            case DELAY_HZ_60:
                delay = 16666;
                break;
            case DELAY_HZ_100:
                delay = 10000;
                break;
            case GAME_DELAY:
                delay = SensorManager.SENSOR_DELAY_GAME;
                break;
            case FASTEST_DELAY:
                delay = SensorManager.SENSOR_DELAY_FASTEST;
                break;
            case NORMAL_DELAY:
                delay = SensorManager.SENSOR_DELAY_NORMAL;
                break;
            case UI_DELAY:
                delay = SensorManager.SENSOR_DELAY_UI;
                break;
            default:
                delay = rate;
                break;
        }
        return delay;
    }

}
