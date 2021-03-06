package mahbub1.umbc.eclipse.androidwearsensordata.ui.visualization;

/**
 * Created by mahbub on 4/4/17.
 */

import mahbub1.umbc.eclipse.sensordatashared.data.DataBatch;

public class VisualizationCardData {

    private String key;
    private String heading;
    private String subHeading;
    private DataBatch dataBatch;

    public VisualizationCardData(String key) {
        this.key = key;
    }

    public VisualizationCardData(String nodeId, String deviceName, String source) {
        this(generateKey(nodeId, source));
        this.heading = source;
        this.subHeading = deviceName;
    }

    public static String generateKey(String nodeId, String source) {
        return nodeId + " - " + source;
        //return source;
    }

    /**
     * Getter & Setter
     */
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSubHeading() {
        return subHeading;
    }

    public void setSubHeading(String subHeading) {
        this.subHeading = subHeading;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public DataBatch getDataBatch() {
        return dataBatch;
    }

    public void setDataBatch(DataBatch dataBatch) {
        this.dataBatch = dataBatch;
    }
}

