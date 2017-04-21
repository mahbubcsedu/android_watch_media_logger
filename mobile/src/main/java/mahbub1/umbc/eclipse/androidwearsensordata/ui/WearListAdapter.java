package mahbub1.umbc.eclipse.androidwearsensordata.ui;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import mahbub1.umbc.eclipse.sensordatashared.wears.WearDevices;

/**
 * Created by mahbub on 4/20/17.
 */

public class WearListAdapter extends BaseAdapter {

    private Context context;
    private List<WearDevices> availableWears;
    private List<WearDevices> selectedWears;
    private List<WearDevices> previouslySelectedWears;

    public WearListAdapter(List<WearDevices> availableSensors, Context context) {
        this.availableWears = availableSensors;
        this.selectedWears = new ArrayList<>();
        this.previouslySelectedWears = new ArrayList<>();
        this.context = context;
    }

    @Override
    public int getCount() {
        return availableWears.size();
    }

    @Override
    public Object getItem(int position) {
        return availableWears.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = new CheckBox(context);
        }

        final WearDevices wearDevices = availableWears.get(position);
        boolean previouslySelected = wasPreviouslySelected(wearDevices);

        ((CheckBox) convertView).setText(wearDevices.getWearDevicName());

        ((CheckBox) convertView).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                WearDevices selectedWear = availableWears.get(position);
                if (selectedWears.contains(selectedWear) && !isChecked) {
                    Log.d("Adapter", "Removing " + wearDevices.getWearDevicName() + " from selected sensors");
                    selectedWears.remove(selectedWear);
                } else if (!selectedWears.contains(selectedWear) && isChecked) {
                    Log.d("Adapter", "Adding " + wearDevices.getWearDevicName() + " to selected sensors");
                    selectedWears.add(selectedWear);
                }
            }
        });

        ((CheckBox) convertView).setChecked(previouslySelected);
        ((ListView) parent).setItemChecked(position, previouslySelected);
        return convertView;
    }

    private boolean wasPreviouslySelected(WearDevices wearDevices) {
        for (WearDevices previouslySelectedSWearDevices : previouslySelectedWears) {

            if (!previouslySelectedSWearDevices.getWearDevicName().equals(wearDevices.getWearDevicName())) {
                continue;
            }
            return true;
        }
        return false;
    }

    /**
     * Getter & Setter
     */
    public List<WearDevices> getAvailableSensors() {
        return availableWears;
    }

    public void setAvailableSensors(List<WearDevices> availableWears) {
        this.availableWears = availableWears;
    }

    public List<WearDevices> getSelectedWears() {
        return selectedWears;
    }

    public List<WearDevices> getPreviouslySelectedWearss() {
        return previouslySelectedWears;
    }

    public void setPreviouslySelectedSensors(List<WearDevices> previouslySelectedWears) {
        this.previouslySelectedWears = previouslySelectedWears;
    }
}
