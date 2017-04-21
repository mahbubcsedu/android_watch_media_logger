package mahbub1.umbc.eclipse.androidwearsensordata.data;

/**
 * Created by mahbub on 1/25/17.
 */

public class SensorDataPoint {

    private long timeStamp;
    private float[] values;
    private int accuracy;

    public SensorDataPoint(long timeStamp, float[] values, int accuracy) {
        this.timeStamp = timeStamp;
        this.values = values;
        this.accuracy = accuracy;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public float[] getValues() {
        return values;
    }

    public int getAccuracy() {
        return accuracy;
    }
}
