package mahbub1.umbc.eclipse.androidwearsensordata;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.google.android.gms.wearable.Node;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import mahbub1.umbc.eclipse.androidwearsensordata.events.BusProvider;
import mahbub1.umbc.eclipse.androidwearsensordata.events.DataBatchChangedEvent;
import mahbub1.umbc.eclipse.androidwearsensordata.ui.ExportActivity;
import mahbub1.umbc.eclipse.androidwearsensordata.ui.WatchSelectionDialogueFragment;
import mahbub1.umbc.eclipse.androidwearsensordata.ui.visualization.VisualizationCardData;
import mahbub1.umbc.eclipse.androidwearsensordata.ui.visualization.VisualizationCardListAdapter;
import mahbub1.umbc.eclipse.sensordatashared.data.DataBatch;
/**
 * Created by mahbub on 4/4/17.
 */

public class SecondaryActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    public static final String TAG = MainActivity.class.getSimpleName();

    private RemoteWearSensorManager mRemoteSensorManager;
    Toolbar mToolbar;

    private ViewPager viewPager;
    private View initialState;
    //private NavigationView mNavigationView;
    private Menu mNavigationViewMenu;
    private List<Node> mNodes;
    private Context mContext;
    private VisualizationCardListAdapter cardListAdapter;
    private GridView gridView;
    private FloatingActionButton floatingActionButton;
    private WatchSelectionDialogueFragment sensorSelectionDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secondary);

        mToolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        //initialState = findViewById(R.id.empty_state);
        mRemoteSensorManager = RemoteWearSensorManager.getInstance(this);

        gridView = (GridView) findViewById(R.id.gridView);
        List<VisualizationCardData> visualizationCardData = new ArrayList<>();
        cardListAdapter = new VisualizationCardListAdapter(this, R.id.gridView, visualizationCardData);
        gridView.setAdapter(cardListAdapter);

        // mNavigationView = (NavigationView) findViewById(R.id.navView);
        //mNavigationView.setNavigationItemSelectedListener(this);
        //mNavigationViewMenu = mNavigationView.getMenu();

        /*Realm.init(this);
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder().build();
        Realm.deleteRealm(realmConfiguration); // Clean slate
        Realm.setDefaultConfiguration(realmConfiguration);
*/
        initToolbar();
        //initViewPager();
        //keep cpu awake while this app is running.
        keepCPUAwake();

        //final EditText tagname = (EditText) findViewById(R.id.tagname);

        Button btnStart = (Button) findViewById(R.id.start_button);
        Button btnStop = (Button) findViewById(R.id.stop_button);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRemoteSensorManager.startMeasurement();
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRemoteSensorManager.stopMeasurement();
            }
        });

        /*floatingActionButton = (FloatingActionButton) findViewById(R.id.floadtingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Runnable optionDialogue=new Runnable() {
                    @Override
                    public void run() {
                        showSensorSelectionDialog();
                    }
                };
                Thread t = new Thread(optionDialogue);
                t.start();

            }

        });*/


        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//keep this activiy awake

    }


    private void initToolbar() {
        setSupportActionBar(mToolbar);

        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(false);
            ab.setTitle(R.string.app_name);
            mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {

                        case R.id.action_export:
                            startActivity(new Intent(SecondaryActivity.this, ExportActivity.class));
                            return true;
                        case R.id.action_settings:
                            startActivity(new Intent(SecondaryActivity.this,SettingsActivity.class));
                            return true;
                        case R.id.action_export_list:
                            startActivity(new Intent(SecondaryActivity.this, RecordingListActivity.class));
                    }

                    return true;
                }
            });
        }
    }

    private void initViewPager() {
        viewPager = (ViewPager) findViewById(R.id.pager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(final MenuItem pMenuItem) {
        Toast.makeText(this, "Device: " + pMenuItem.getTitle(), Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
        mRemoteSensorManager.stopMeasurement();
    }



    @Subscribe
    public void onDataBatchChangedEvent(final DataBatchChangedEvent event) {
        Log.d(TAG, "databach changes");
        renderDataBatch(event.getDataBatch(),event.getSourceNodeId(),event.getDeviceName());
    }

    /**
     * Creates or updates a visualization card and notifies the @cardListAdapter
     * in order to update the @ChartView with the provided @DataBatch
     */
    private void renderDataBatch(DataBatch dataBatch, String sourceNodeId,String dataSourcedeviceName) {
        try {
            // Don't render if the data isn't requested anymore.
            // This can happen if the request has been updated but data
            // has already been sent by the request receiver
            //if (!isRequestingSensorEventData(sourceNodeId, dataBatch.getSource())) {
             //   return;
            //}

            // get the visualization card
            String key = VisualizationCardData.generateKey(sourceNodeId, dataBatch.getSource());
            //Log.i(TAG, "key:"+key);
            VisualizationCardData visualizationCardData = cardListAdapter.getVisualizationCard(key);

            // create a new card if not yet avaialable
            if (visualizationCardData == null) {
                String deviceName = dataSourcedeviceName;//app.getGoogleApiMessenger().getNodeName(sourceNodeId);
                visualizationCardData = new VisualizationCardData(key);
                visualizationCardData.setHeading(dataBatch.getSource());
                visualizationCardData.setSubHeading(deviceName);
                cardListAdapter.add(visualizationCardData);
                cardListAdapter.notifyDataSetChanged();
            }

            // update the card data
            DataBatch visualizationDataBatch = visualizationCardData.getDataBatch();
            if (visualizationDataBatch == null) {
                visualizationDataBatch = dataBatch;
                //visualizationDataBatch.setCapacity(DataBatch.CAPACITY_UNLIMITED);
                visualizationDataBatch.setCapacity(DataBatch.CAPACITY_DEFAULT);
                visualizationCardData.setDataBatch(visualizationDataBatch);
            } else {
                visualizationDataBatch.addData(dataBatch.getDataList());
            }

            //Log.v(TAG, "batch data:"+dataBatch.toJson());



            /*AsyncTask.execute(new Runnable(){
                @Override
                public void run(){
                    sensorDataUpdateToDb(processDataList,sourceId);
                }
            });
*/
            cardListAdapter.invalidateVisualization(visualizationCardData.getKey());
        } catch (Exception ex) {
            Log.w(TAG, "Unable to render data batch: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void keepCPUAwake(){
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                TAG);
        wakeLock.acquire();
    }



    private void showSensorSelectionDialog() {
        try {
            if (sensorSelectionDialog != null) {
                Log.w(TAG, "Not showing sensor selection dialog, previous dialog is still set");
                return;
            }
            Log.d(TAG, "Showing sensor selection dialog");
            sensorSelectionDialog = new WatchSelectionDialogueFragment();
            //sensorSelectionDialog.setPreviouslySelectedSensors(selectedSensors);
            sensorSelectionDialog.show(getFragmentManager(), WatchSelectionDialogueFragment.class.getSimpleName());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
