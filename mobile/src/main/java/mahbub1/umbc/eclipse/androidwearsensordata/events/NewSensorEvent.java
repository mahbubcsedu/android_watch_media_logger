package mahbub1.umbc.eclipse.androidwearsensordata.events;

import mahbub1.umbc.eclipse.androidwearsensordata.data.SensorWithMappedIndex;

/**
 * Created by mahbub on 1/25/17.
 */

public class NewSensorEvent {
   // private Sensor sensor;
    private SensorWithMappedIndex sensorWithMappedIndex;
   // private int mappedIndex;

    public NewSensorEvent(SensorWithMappedIndex sensorWithMappedIndex) {
        this.sensorWithMappedIndex = sensorWithMappedIndex;
        //this.mappedIndex=mappedIndex;
    }

    public SensorWithMappedIndex getSensorWithMappedIndex() {
        return sensorWithMappedIndex;
    }
    public int getMappedIndex(){return this.sensorWithMappedIndex.getSensorIdex();}
}
