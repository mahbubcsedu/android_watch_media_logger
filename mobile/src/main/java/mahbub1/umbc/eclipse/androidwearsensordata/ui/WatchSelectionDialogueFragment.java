package mahbub1.umbc.eclipse.androidwearsensordata.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.util.Log;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ListView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mahbub1.umbc.eclipse.androidwearsensordata.R;
import mahbub1.umbc.eclipse.sensordatashared.sensors.Wears;
import mahbub1.umbc.eclipse.sensordatashared.ui.UnitHelper;
import mahbub1.umbc.eclipse.sensordatashared.wears.WearDevices;

//import mahbub1.umbc.eclipse.sensordatashared.sensors.Wears;

/**
 * Created by mahbub on 4/20/17.
 */

public class WatchSelectionDialogueFragment extends DialogFragment {
    private static final int WEAR_CONNECTION_TIMEOUT = 10000;
    public static final String TAG = WatchSelectionDialogueFragment.class.getSimpleName();
    private Context mContext;

    public interface AvailableSensorsUpdatedListener {
        void onAvailableSensorsUpdated(String nodeId, List<Wears> deviceSensors);
    }

    public interface SelectedSensorsUpdatedListener {
        void onSensorsFromAllNodesSelected(Map<String, List<Wears>> selectedSensors);

        void onSensorsFromNodeSelected(String nodeId, List<Wears> sensors);

        void onSensorSelectionClosed(DialogFragment dialog);
    }

    //private MobileApp app;
    //private SelectedSensorsUpdatedListener listener;

    private List<Node> availableNodes = new ArrayList<>();
    private List<WearDevices> availableWears = new ArrayList<>();
    private List<WearDevices> selectedWears = new ArrayList<>();
    private List<WearDevices> previouslySelectedSensors = new ArrayList<>();
    private WearListAdapter multiChoiceAdapter;
    private GoogleApiClient mGoogleApiClient;
    //private MessageHandler setSensorsMessageHandler = getSetSensorsMessageHandler();

    //private List<AvailableSensorsUpdatedListener> availableSensorsUpdatedListeners = new ArrayList<>();

    /**
     * Creates the basic alert dialog with a multi-choice list.
     * It will be customized and filled with data from connected device
     * once it's shown
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.loading_available_wears);

        final CharSequence[] availableSensors = new CharSequence[0];
        builder.setMultiChoiceItems(availableSensors, null, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                // this will be overwritten to prevent default behaviour
            }
        });
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // this will be overwritten to prevent default behaviour
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // dismiss dialog

            }
        });
        AlertDialog dialog = builder.create();
        dialog.requestWindowFeature(Window.FEATURE_LEFT_ICON);
        return dialog;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mContext = activity;
        this.mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addApi(Wearable.API)
                .build();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //app.unregisterMessageHandler(setSensorsMessageHandler);
    }

    @Override
    public void onStart() {
        super.onStart();
        showAndroidWears();
        // override positive button click listener in order to prevent it from
        // closing the dialog if not all sensors have been selected yet


    }

    @Override
    public void onStop() {
        //listener.onSensorSelectionClosed(this);
        super.onStop();
    }

    /**
     * Updates the existing dialog to show a list of available sensors
     * from the specified node. List items will be set when they are
     * available through an @AvailableSensorsUpdatedListener.
     */
    private void showAndroidWears() {
        // String nodeName = app.getGoogleApiMessenger().getNodeName(nodeId);
        //    Log.d(TAG, "Showing sensor selection for node: " + nodeName + " - " + nodeId);

        // prepare dialog for new sensor selection
        String title = getString(R.string.loading_available_wears);
        getDialog().setTitle(title);

        setDialogIcon(R.drawable.ic_watch_black_48dp);


        // create & apply new list adapter
        multiChoiceAdapter = new WearListAdapter(new ArrayList<WearDevices>(), getActivity());
        ListView listView = ((AlertDialog) getDialog()).getListView();
        listView.setAdapter(multiChoiceAdapter);

        // update layout params & invalidate list view
        ((ViewGroup.MarginLayoutParams) listView.getLayoutParams()).setMargins((int) UnitHelper.convertDpToPixel(16, getActivity()), 0, 0, 0);
        listView.invalidate();

        if (multiChoiceAdapter != null) {
            // update adapter with sensors
            findConnectedNodes();
            multiChoiceAdapter.setAvailableSensors(this.availableWears);

            // restore previously selected sensors
            List<WearDevices> selectedWears = previouslySelectedSensors;
            if (selectedWears != null) {
                multiChoiceAdapter.setPreviouslySelectedSensors(selectedWears);
            }
            multiChoiceAdapter.notifyDataSetChanged();

            // update dialog title
            //getDialog().setTitle(getDialogTitleForAvailableSensors(nodeId));
        }

        // track analytics event
       /* Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, "Device Sensors");
        bundle.putString(FirebaseAnalytics.Param.VALUE, nodeName);
        app.getAnalytics().logEvent(FirebaseAnalytics.Event.VIEW_ITEM_LIST, bundle);*/
    }


    /**
     * Writes the selected sensors for the specified node id
     * into the @selectedSensors map
     */
    private void saveCurrentlySelectedSensors() {
        Log.d(TAG, "Saving currently selected wears");
        List<WearDevices> currentlySelectedWears;
        if (multiChoiceAdapter != null) {
            currentlySelectedWears = multiChoiceAdapter.getSelectedWears();
        } else {
            currentlySelectedWears = new ArrayList<>();
        }
        selectedWears = currentlySelectedWears;
    }


    public boolean validateConnection() {
        if (mGoogleApiClient.isConnected()) {
            return true;
        }
        //return false;
      return connect();
        //ConnectionResult result =mGoogleApiClient.blockingConnect(WEAR_CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS);
        //return result.isSuccess();
    }
    public boolean connect() {
        Log.d(TAG, "Connecting Google API client");
        if (mGoogleApiClient != null && !mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
            return true;
        }
        return false;
    }

    private void findConnectedNodes() {

        if (validateConnection()) {
            List<Node> nodes = Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await().getNodes();

            Log.d(TAG, "Sending to nodes: " + nodes.size());
            WearDevices wearDevice;
            for (Node node : nodes) {
                wearDevice = new WearDevices(node.getDisplayName(), node.getId());
                this.availableWears.add(wearDevice);

            }
            //this.availableNodes = node;


        } else {
            Log.w(TAG, "No connection possible");
        }
    }

    private void setDialogIcon(@DrawableRes int resId) {
        getDialog().setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, resId);
    }


    public List<WearDevices> getAvailableWears() {
        return availableWears;
    }

    public void setAvailableWears(List<WearDevices> availableWears) {
        this.availableWears = availableWears;
    }


    public List<WearDevices> getPreviouslySelectedSensors() {
        return previouslySelectedSensors;
    }

    public void setPreviouslySelectedSensors(List<WearDevices> previouslySelectedSensors) {
        this.previouslySelectedSensors = previouslySelectedSensors;
    }
}