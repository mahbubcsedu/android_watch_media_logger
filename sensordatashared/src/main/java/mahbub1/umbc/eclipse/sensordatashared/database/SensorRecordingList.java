package mahbub1.umbc.eclipse.sensordatashared.database;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by mahbub on 5/12/17.
 */

public class SensorRecordingList extends RealmObject {
    @PrimaryKey
    private long id;
    private String recording;
    private String preferences;
    //this is the last id of previous recording. for first item, it will be -1, so the id greater than this and less than or equal to next recording entry will the list of a single recording
    private long previousRecordingLastId;
    //private long endRow;
    private String startTime;
    //private String endTime;


    /*public SensorRecordingList(){
        //startRow=0;
        //endRow=0;
        recording="Recording serial";
        Date cureDate= new Date();
        SimpleDateFormat dataFormate = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
        startTime =dataFormate.format(cureDate);

        //endTime = dataFormate.format(cureDate);
    }*/

    public long getPreviousRecordingLastId() {
        return previousRecordingLastId;
    }

    public void setPreviousRecordingLastId(long previousRecordingLastId) {
        this.previousRecordingLastId = previousRecordingLastId;
    }

    public String getPreferences() {
        return preferences;
    }

    public void setPreferences(String preferences) {
        this.preferences = preferences;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getRecording() {
        return recording;
    }

    public void setRecording(String recording) {
        this.recording = recording;
    }


    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }


}
