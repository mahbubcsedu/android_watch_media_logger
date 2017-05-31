package mahbub1.umbc.eclipse.androidwearsensordata;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.CircledImageView;
import android.support.wearable.view.WatchViewStub;
import android.support.wearable.view.WearableListView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by mahbub on 4/12/17.
 */

public class ListViewActivity extends WearableActivity implements WearableListView.ClickListener {

    private WearableListView mListView;
    private MyListAdapter mAdapter;

    private float mDefaultCircleRadius;
    private float mSelectedCircleRadius;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);

        mDefaultCircleRadius = getResources().getDimension(R.dimen.default_settings_circle_radius);
        mSelectedCircleRadius = getResources().getDimension(R.dimen.selected_settings_circle_radius);
        mAdapter = new MyListAdapter();

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mListView = (WearableListView) stub.findViewById(R.id.listView1);
                mListView.setAdapter(mAdapter);
                mListView.setClickListener(ListViewActivity.this);
            }
        });

        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
        }
    }

    private static ArrayList<Integer> listItems;

    static {
        listItems = new ArrayList<Integer>();
        listItems.add(R.drawable.ic_start);
        listItems.add(R.drawable.ic_stop);
        listItems.add(R.drawable.ic_server_upload);


    }

    @Override
    public void onClick(WearableListView.ViewHolder viewHolder) {
        if (viewHolder.getPosition() == 0) {

            Intent i = new Intent(this, StorageOptionActivity.class);
            this.startActivity(i);
            /*

            final Intent i=new Intent(getApplicationContext(), SensorServices.class);
            Runnable r = new Runnable() {
                @Override
                public void run() {

                    startService(i);


                }
            };

            Thread t = new Thread(r);
            t.start();*/
            //Toast.makeText(this, String.format("Sensor data recording started"), Toast.LENGTH_LONG).show();
        } else if (viewHolder.getPosition() == 1) {

            final Intent i = new Intent(getApplicationContext(), SensorServices.class);

            Runnable r = new Runnable() {
                @Override
                public void run() {

                    stopService(i);


                }
            };

            Thread t = new Thread(r);
            t.start();


            //stopService(new Intent(this, SensorServices.class));
            Toast.makeText(this, String.format("Sensor data recording  stopped"), Toast.LENGTH_LONG).show();
        } else if (viewHolder.getPosition() == 2) {

            final Intent i = new Intent();

            i.setClassName("mahbub1.umbc.eclipse.androidwearsensordata", "mahbub1.umbc.eclipse.androidwearsensordata.ExportActivity");
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Runnable r = new Runnable() {
                @Override
                public void run() {

                    startActivity(i);


                }
            };

            Thread t = new Thread(r);
            t.start();

        }
        //Toast.makeText(this, String.format("Sensor data recording  %s", viewHolder.getPosition()==0?"started":"stopped"), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onTopEmptyRegionClick() {
        Toast.makeText(this, "You tapped Top empty area", Toast.LENGTH_SHORT).show();
    }

    public class MyListAdapter extends WearableListView.Adapter {

        @Override
        public WearableListView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new WearableListView.ViewHolder(new ListItemView(ListViewActivity.this));
        }

        @Override
        public void onBindViewHolder(WearableListView.ViewHolder viewHolder, int i) {
            ListItemView itemView = (ListItemView) viewHolder.itemView;

            TextView txtView = (TextView) itemView.findViewById(R.id.text);

            if (i == 0)
                txtView.setText(String.format("Start "));
            else if (i == 1)
                txtView.setText(String.format("Stop "));
            else if (i == 2)
                txtView.setText((String.format("Upload ")));

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
