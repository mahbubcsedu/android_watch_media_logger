package mahbub1.umbc.eclipse.androidwearsensordata.data;

/**
 * Created by mahbub on 4/4/17.
 */

public class SensorWithMappedIndex {
    public static final String TAG = SensorWithMappedIndex.class.getSimpleName();

    private Sensor sensor;
    private int sensorIdex;

    public SensorWithMappedIndex(Sensor sensor, int sensorIdex){
        this.sensor =sensor;
        this.sensorIdex=sensorIdex;
    }
    public int getSensorIdex(){return sensorIdex;}
    public Sensor getSensor(){return sensor;}
}
