package mahbub1.umbc.eclipse.androidwearsensordata.ui;

/**
 * Created by mahbub on 5/14/17.
 */

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Daniel on 12/30/2014.
 */
public class RecordingListItem implements Parcelable {
    private String recordingName; // file name
    private String startTime; //file path
    private long mId; //id in database
    private long previousId;
    private String preferences;
    private long startId;
    private long endId;
    //private int mLength; // length of recording in seconds
    //private long mTime; // date/time of the recording

    public RecordingListItem() {
    }

    public RecordingListItem(Parcel in) {
        recordingName = in.readString();
        startTime = in.readString();
        mId = in.readInt();
        previousId = in.readLong();
        preferences = in.readString();
        startId = in.readLong();
        endId = in.readLong();
        //mLength = in.readInt();
        //mTime = in.readLong();
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    public long getPreviousId() {
        return previousId;
    }

    public void setPreviousId(long previousId) {
        this.previousId = previousId;
    }

    public String getPreferences() {
        return preferences;
    }

    public void setPreferences(String preferences) {
        this.preferences = preferences;
    }

    public String getName() {
        return recordingName;
    }

    public void setName(String name) {
        recordingName = name;
    }

    public String getTime() {
        return startTime;
    }

    public void setTime(String startTime) {
        this.startTime = startTime;
    }

    public long getEndId() {
        return endId;
    }

    public void setEndId(long endId) {
        this.endId = endId;
    }

    public long getStartId() {
        return startId;
    }

    public void setStartId(long startId) {
        this.startId = startId;
    }

    public static final Parcelable.Creator<RecordingListItem> CREATOR = new Parcelable.Creator<RecordingListItem>() {
        public RecordingListItem createFromParcel(Parcel in) {
            return new RecordingListItem(in);
        }

        public RecordingListItem[] newArray(int size) {
            return new RecordingListItem[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mId);

        dest.writeString(startTime);
        dest.writeString(recordingName);
        dest.writeLong(previousId);
        dest.writeString(preferences);
        dest.writeLong(startId);
        dest.writeLong(endId);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
