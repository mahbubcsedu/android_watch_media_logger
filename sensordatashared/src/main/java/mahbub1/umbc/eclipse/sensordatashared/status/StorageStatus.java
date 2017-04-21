package mahbub1.umbc.eclipse.sensordatashared.status;

/**
 * Created by mahbub on 4/18/17.
 */

public class StorageStatus extends Status{
    private boolean storageOnLocalWatch = false;

    public StorageStatus() {
        this.storageOnLocalWatch = false;
    }

    public boolean isStorageOnLocalWatch() {
        return storageOnLocalWatch;
    }

    public void setStorageOnLocalWatch(boolean storageOnLocalWatch) {
        this.storageOnLocalWatch = storageOnLocalWatch;
    }
}