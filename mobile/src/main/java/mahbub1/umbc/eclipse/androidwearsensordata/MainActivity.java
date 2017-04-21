package mahbub1.umbc.eclipse.androidwearsensordata;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.wearable.Node;
import com.squareup.otto.Subscribe;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import mahbub1.umbc.eclipse.androidwearsensordata.data.SensorWithMappedIndex;
import mahbub1.umbc.eclipse.androidwearsensordata.events.BusProvider;
import mahbub1.umbc.eclipse.androidwearsensordata.events.DataBatchChangedEvent;
import mahbub1.umbc.eclipse.androidwearsensordata.events.NewSensorEvent;
import mahbub1.umbc.eclipse.androidwearsensordata.ui.ExportActivity;

/**
 * This activity is the main activity of this data collection process.
 */
public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{
    public static final String TAG = MainActivity.class.getSimpleName();

    private RemoteWearSensorManager mRemoteSensorManager;
    Toolbar mToolbar;

    private ViewPager viewPager;
    private View initialState;
    //private NavigationView mNavigationView;
    private Menu mNavigationViewMenu;
    private List<Node> mNodes;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        initialState = findViewById(R.id.empty_state);
        mRemoteSensorManager = RemoteWearSensorManager.getInstance(this);

       // mNavigationView = (NavigationView) findViewById(R.id.navView);
        //mNavigationView.setNavigationItemSelectedListener(this);
        //mNavigationViewMenu = mNavigationView.getMenu();

        Realm.init(this);
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder().build();
        Realm.deleteRealm(realmConfiguration); // Clean slate
        Realm.setDefaultConfiguration(realmConfiguration);

        initToolbar();
        initViewPager();




        final EditText tagname = (EditText) findViewById(R.id.tagname);

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



        findViewById(R.id.tag_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tagnameText = "EMPTY";
                if (!tagname.getText().toString().isEmpty()) {
                    tagnameText = tagname.getText().toString();
                }

                RemoteWearSensorManager.getInstance(MainActivity.this).addTag(tagnameText);
            }
        });

        tagname.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

                    in.hideSoftInputFromWindow(tagname
                                    .getApplicationWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);


                    return true;

                }
                return false;
            }
        });




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
                            startActivity(new Intent(MainActivity.this, ExportActivity.class));
                            return true;
                    }

                    return true;
                }
            });
        }
    }
    private void initViewPager(){
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
        //List<Sensor> sensors = RemoteWearSensorManager.getInstance(this).getSensors();
        List<SensorWithMappedIndex> sensors = RemoteWearSensorManager.getInstance(this).getMappedSensors();
        viewPager.setAdapter(new ScreenSlidePagerAdapter(getSupportFragmentManager(), sensors));


        if (sensors.size() > 0) {
            initialState.setVisibility(View.GONE);
        } else {
            initialState.setVisibility(View.VISIBLE);
        }

        //mRemoteSensorManager.startMeasurement();

        //this was the original position to start recording




       // mNavigationViewMenu.clear();
        /*mRemoteSensorManager.getNodes(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
            @Override
            public void onResult(final NodeApi.GetConnectedNodesResult pGetConnectedNodesResult) {
                mNodes = pGetConnectedNodesResult.getNodes();
                for (Node node : mNodes) {
                    SubMenu menu = mNavigationViewMenu.addSubMenu(node.getDisplayName());

                    MenuItem item = menu.add("15 sensors");
                    if (node.getDisplayName().startsWith("G")) {
                        item.setChecked(true);
                        item.setCheckable(true);
                    } else {
                        item.setChecked(false);
                        item.setCheckable(false);
                    }
                }
            }
        });*/
    }

    @Override
    protected void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
        mRemoteSensorManager.stopMeasurement();
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        private List<SensorWithMappedIndex> sensors;

        public ScreenSlidePagerAdapter(FragmentManager fm, List<SensorWithMappedIndex> symbols) {
            super(fm);
            this.sensors = symbols;
        }


        public void addNewSensor(SensorWithMappedIndex sensorWithMappedIndex) {
            this.sensors.add(sensorWithMappedIndex);
        }


        private SensorWithMappedIndex getItemObject(int position) {
            return sensors.get(position);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            return SensorFragment.newInstance(sensors.get(position).getSensorIdex());
            //return SensorFragment.newInstance(sensors.get(position).getCompositeID());
        }

        @Override
        public int getCount() {
            return sensors.size();
        }

    }



    private void notifyUSerForNewSensor(SensorWithMappedIndex sensorWithMappedIndex) {
        Toast.makeText(this, "New Sensor!\n" + sensorWithMappedIndex.getSensor().getSensorName(), Toast.LENGTH_SHORT).show();
    }
    @Subscribe
    public void onNewSensorEvent(final NewSensorEvent event) {
        ((ScreenSlidePagerAdapter) viewPager.getAdapter()).addNewSensor(event.getSensorWithMappedIndex());
        viewPager.getAdapter().notifyDataSetChanged();
        initialState.setVisibility(View.GONE);
        notifyUSerForNewSensor(event.getSensorWithMappedIndex());
    }
@Subscribe
    public void onDataBatchChangedEvent(final DataBatchChangedEvent event){
        Log.d(TAG, "databach changes");
    }
}
