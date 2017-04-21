package mahbub1.umbc.eclipse.androidwearsensordata.events;

import mahbub1.umbc.eclipse.sensordatashared.data.DataBatch;

/**
 * Created by mahbub on 4/4/17.
 */

public class DataBatchChangedEvent {
    private DataBatch dataBatch;
    private String sourceNodeId;
    private String deviceName;

    public DataBatchChangedEvent(DataBatch dataBatch,String sourceNodeId,String deviceName){
       this.dataBatch=dataBatch;
        this.sourceNodeId=sourceNodeId;
        this.deviceName=deviceName;
    }
    public DataBatch getDataBatch(){
        return this.dataBatch;

    }

    public String getSourceNodeId(){return sourceNodeId;}
    public String getDeviceName(){return this.deviceName;}
}
