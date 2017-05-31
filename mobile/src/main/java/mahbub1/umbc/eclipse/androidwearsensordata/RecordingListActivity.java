package mahbub1.umbc.eclipse.androidwearsensordata;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import io.realm.Realm;
import io.realm.RealmResults;
import mahbub1.umbc.eclipse.androidwearsensordata.ui.RecordingListViewAdapter;
import mahbub1.umbc.eclipse.sensordatashared.database.SensorRecordingList;


/**
 * Created by mahbub on 5/13/17.
 */

public class RecordingListActivity extends FragmentActivity {
    public static final String TAG = RecordingListActivity.class.getSimpleName();
    RecordingListViewAdapter mRecordingListViewAdapter;
    Realm realm;
    RealmResults<SensorRecordingList> recordingLists;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Context mContext;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.recording_listview);

        realm = Realm.getDefaultInstance();
        mContext = this;
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        //recordingLists = new RealmResults<SensorRecordingList>().e;
        loadData();
        mRecordingListViewAdapter = new RecordingListViewAdapter(this, mLayoutManager, recordingLists);
        //mRecordingListViewAdapter.setData(recordingLists);
        // specify an adapter (see also next example)
        //mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        //newest to oldest order (database stores from oldest to newest)
        //mLayoutManager.setReverseLayout(true);
        //llm.setStackFromEnd(true);

        //mRecyclerView.setLayoutManager(mLayoutManager);
        //mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mRecordingListViewAdapter);

        //mRecyclerView.setAdapter(mRecordingListViewAdapter);


        //mRecordingListViewAdapter = new RecordingListViewAdapter(this, llm);

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    public void loadData() {
        //Realm realm = Realm.getDefaultInstance();

        recordingLists = realm.where(SensorRecordingList.class).findAll();
    }


}
