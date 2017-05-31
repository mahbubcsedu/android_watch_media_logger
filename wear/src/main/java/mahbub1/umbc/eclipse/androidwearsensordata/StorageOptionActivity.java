package mahbub1.umbc.eclipse.androidwearsensordata;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.BoxInsetLayout;
import android.support.wearable.view.CircledImageView;
import android.support.wearable.view.WatchViewStub;
import android.support.wearable.view.WearableListView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import mahbub1.umbc.eclipse.sensordatashared.status.StorageStatus;

/**
 * Created by mahbub on 4/14/17.
 */
/*
TODO: have to add this list of select local or remote data storage
 */
public class StorageOptionActivity extends WearableActivity implements WearableListView.ClickListener {
    public static final String TAG = StorageOptionActivity.class.getSimpleName();
    private BoxInsetLayout mContainerView;
    private TextView textView;
    //private LocalToServerSync localToServerSync;
    private Context mContext;
    private StorageStatus storageStatus;
    // ProgressBar mProgress;
    CircledImageView localWatch, remote_smartphone;
    private WearableListView mListView;
    private MyListAdapter mAdapter;

    private float mDefaultCircleRadius;
    private float mSelectedCircleRadius;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupUi();

        mDefaultCircleRadius = getResources().getDimension(R.dimen.default_settings_circle_radius);
        mSelectedCircleRadius = getResources().getDimension(R.dimen.selected_settings_circle_radius);
        mAdapter = new MyListAdapter();

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mListView = (WearableListView) stub.findViewById(R.id.listView1);
                mListView.setAdapter(mAdapter);
                mListView.setClickListener(StorageOptionActivity.this);
            }
        });
        mContext = this;
        /*Resources res = getResources();
        Drawable drawable = res.getDrawable(R.drawable.circular);
        localWatch = (CircledImageView) findViewById(R.id.local_watch);
        remote_smartphone = (CircledImageView) findViewById(R.id.remote_phone);
        textView = (TextView) findViewById(R.id.description);*/
        //Context context = getApplicationContext();
        storageStatus = new StorageStatus();
    }


    @Override
    public void onClick(WearableListView.ViewHolder viewHolder) {
        if (viewHolder.getPosition() == 0) {

            storageStatus.setStorageOnLocalWatch(true);
            startSensorService();
            startActivity(new Intent(this, MainWearActivity.class));


        } else if (viewHolder.getPosition() == 1) {


            storageStatus.setStorageOnLocalWatch(false);
            startSensorService();
            startActivity(new Intent(this, MainWearActivity.class));
        }


    }

    private void startSensorService() {
        final Intent i = new Intent(getApplicationContext(), SensorServices.class);
        i.putExtra("isStorageLocal", storageStatus.isStorageOnLocalWatch());

        Runnable r = new Runnable() {
            @Override
            public void run() {
                startService(i);
            }
        };

        Thread t = new Thread(r);
        t.start();
    }

    private void setupUi() {
        setContentView(R.layout.activity_option);
        //setAmbientEnabled();

        mContainerView = (BoxInsetLayout) findViewById(R.id.container);

    }

    @Override
    public void onTopEmptyRegionClick() {
        Toast.makeText(this, "You tapped Top empty area", Toast.LENGTH_SHORT).show();
    }


    private static ArrayList<Integer> listItems;

    static {
        listItems = new ArrayList<Integer>();
        listItems.add(R.drawable.ic_watch);
        listItems.add(R.drawable.ic_smartphone);


    }

    public class MyListAdapter extends WearableListView.Adapter {

        @Override
        public WearableListView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new WearableListView.ViewHolder(new ListItemView(StorageOptionActivity.this));
        }

        @Override
        public void onBindViewHolder(WearableListView.ViewHolder viewHolder, int i) {
            ListItemView itemView = (ListItemView) viewHolder.itemView;

            TextView txtView = (TextView) itemView.findViewById(R.id.text);

            if (i == 0)
                txtView.setText(String.format("Watch "));
            else if (i == 1)
                txtView.setText(String.format("Phone "));


            Integer resourceId = listItems.get(i);
            CircledImageView imgView = (CircledImageView) itemView.findViewById(R.id.image);
            imgView.setImageResource(resourceId);
        }

        @Override
        public int getItemCount() {
            return listItems.size();
        }
    }

    private final class ListItemView extends FrameLayout implements WearableListView.OnCenterProximityListener {

        final CircledImageView imgView;
        final TextView txtView;
        private float mScale;
        private final int mFadedCircleColor;
        private final int mChosenCircleColor;

        public ListItemView(Context context) {
            super(context);
            View.inflate(context, R.layout.row_advanced_item_layout, this);
            imgView = (CircledImageView) findViewById(R.id.image);
            txtView = (TextView) findViewById(R.id.text);
            mFadedCircleColor = getResources().getColor(android.R.color.darker_gray);
            mChosenCircleColor = getResources().getColor(android.R.color.holo_blue_dark);
        }

        @Override
        public void onCenterPosition(boolean b) {
            //Animation example to be ran when the view becomes the centered one
            imgView.animate().scaleX(1f).scaleY(1f).alpha(1);
            txtView.animate().scaleX(1f).scaleY(1f).alpha(1);
        }

        @Override
        public void onNonCenterPosition(boolean b) {
            //Animation example to be ran when the view is not the centered one anymore
            imgView.animate().scaleX(0.8f).scaleY(0.8f).alpha(0.6f);
            txtView.animate().scaleX(0.8f).scaleY(0.8f).alpha(0.6f);
        }
    }
}
