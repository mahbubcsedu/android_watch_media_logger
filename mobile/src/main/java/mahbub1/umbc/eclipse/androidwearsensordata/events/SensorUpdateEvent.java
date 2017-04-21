package mahbub1.umbc.eclipse.androidwearsensordata.events;

import mahbub1.umbc.eclipse.androidwearsensordata.data.Sensor;
import mahbub1.umbc.eclipse.androidwearsensordata.data.SensorDataPoint;

/**
 * Created by mahbub on 1/25/17.
 */

public class SensorUpdateEvent {
    private Sensor sensor;
    private SensorDataPoint sensorDataPoint;

    public SensorUpdateEvent(Sensor sensor, SensorDataPoint sensorDataPoint) {
        this.sensor = sensor;
        this.sensorDataPoint = sensorDataPoint;
    }

    public Sensor getSensor() {
        return sensor;
    }

    public SensorDataPoint getSensorDataPoint() {
        return sensorDataPoint;
    }
}
