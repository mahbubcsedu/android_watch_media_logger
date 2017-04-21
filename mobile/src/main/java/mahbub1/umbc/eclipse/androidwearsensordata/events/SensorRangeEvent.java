package mahbub1.umbc.eclipse.androidwearsensordata.events;

import mahbub1.umbc.eclipse.androidwearsensordata.data.Sensor;

/**
 * Created by mahbub on 1/25/17.
 */

public class SensorRangeEvent {
    private Sensor sensor;

    public SensorRangeEvent(Sensor sensor) {
        this.sensor = sensor;
    }

    public Sensor getSensor() {
        return sensor;
    }
}
