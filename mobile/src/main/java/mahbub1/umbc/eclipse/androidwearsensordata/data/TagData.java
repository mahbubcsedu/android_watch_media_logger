package mahbub1.umbc.eclipse.androidwearsensordata.data;

/**
 * Created by mahbub on 2/1/17.
 */

public class TagData {
    private String tagName;
    private long timestamp;

    public TagData(String pTagName, long pTimestamp) {
        tagName = pTagName;
        timestamp = pTimestamp;
    }

    public String getTagName() {
        return tagName;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
