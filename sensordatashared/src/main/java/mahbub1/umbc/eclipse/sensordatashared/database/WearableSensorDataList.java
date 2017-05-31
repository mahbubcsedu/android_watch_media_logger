package mahbub1.umbc.eclipse.sensordatashared.database;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by mahbub on 2/8/17.
 */

public class WearableSensorDataList extends RealmObject{

    @PrimaryKey
    long id;
    private String status;
    private String jsonAsString;
    private String androidDevice;
    private String source;
    private int type;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public String getAndroidDevice() {
        return androidDevice;
    }

    public void setAndroidDevice(String androidDevice) {
        this.androidDevice = androidDevice;
    }

    public String getJsonAsString() {
        return jsonAsString;
    }

    public void setJsonAsString(String jsonAsString) {
        this.jsonAsString = jsonAsString;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
