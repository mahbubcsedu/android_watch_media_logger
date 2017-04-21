package mahbub1.umbc.eclipse.androidwearsensordata.data;

import android.util.Log;

import java.util.LinkedList;

import mahbub1.umbc.eclipse.androidwearsensordata.events.BusProvider;
import mahbub1.umbc.eclipse.androidwearsensordata.events.SensorRangeEvent;

/**
 * Created by mahbub on 1/25/17.
 */

public class Sensor {
    private static final String TAG = Sensor.class.getSimpleName();
    private static final int QUEUE_SIZE = 1000;


    private long id;
    private String sourceNodeId;
    private String compositeID;
    private String sensorName;
    private float minValue=Integer.MAX_VALUE;
    private float maxValue = Integer.MIN_VALUE;

    private LinkedList<SensorDataPoint> dataPoints = new LinkedList<SensorDataPoint>();

    public Sensor(long id,String sourceNodeId,String sensorName) {
        this.sensorName = sensorName;
        this.id = id;
        this.sourceNodeId=sourceNodeId;
        this.compositeID=id+"-"+sourceNodeId;
    }

    public void setSensorName(String sensorName) {
        this.sensorName = sensorName;
    }

    public String getSensorName() {
        return sensorName;
    }

    public float getMinValue() {
        return minValue;
    }

    public float getMaxValue() {
        return maxValue;
    }

    public long getId() {
        return id;
    }

    public synchronized LinkedList<SensorDataPoint> getDataPoints(){
        return (LinkedList<SensorDataPoint>) dataPoints.clone();
    }

    public synchronized void addDataPoints(SensorDataPoint sensorDataPoint){
        this.dataPoints.addLast(sensorDataPoint);

        if(this.dataPoints.size()>QUEUE_SIZE)
        {
            this.dataPoints.removeFirst();
        }

        boolean changeLimits = false;

        for(float value: sensorDataPoint.getValues()){
            if(value >maxValue){
                maxValue = value;
                changeLimits = true;
                }
            if(value < minValue){
                minValue = value;
                changeLimits = true;
            }
        }


        if(changeLimits){
            Log.d(TAG, "Changed range for sensor :"+id+":"+minValue + "-"+maxValue);
            BusProvider.postOnMainThread(new SensorRangeEvent(this));
        }
    }
    public String getCompositeID(){

        return this.compositeID;
    }
    public String getSourceNodeId(){
        return this.sourceNodeId;
    }

}