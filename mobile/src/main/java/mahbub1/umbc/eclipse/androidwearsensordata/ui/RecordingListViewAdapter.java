package mahbub1.umbc.eclipse.androidwearsensordata.ui;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import io.realm.RealmResults;
import mahbub1.umbc.eclipse.androidwearsensordata.R;
import mahbub1.umbc.eclipse.sensordatashared.database.SensorRecordingList;

/**
 * Created by mahbub on 5/13/17.
 */

public class RecordingListViewAdapter extends RecyclerView.Adapter<RecordingListViewAdapter.RecordingsViewHolder> {
    public static final String TAG = RecordingListViewAdapter.class.getSimpleName();
    Context mContext;
    RecyclerView.LayoutManager llm;
    //Realm mRealm;
    SensorRecordingList item;
    List<SensorRecordingList> itemList;

    public RecordingListViewAdapter(Context context, RecyclerView.LayoutManager linearLayoutManager, RealmResults<SensorRecordingList> recordingLists) {
        super();

        mContext = context;
        llm = linearLayoutManager;
        itemList = recordingLists;
    }


    @Override
    public RecordingListViewAdapter.RecordingsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.recordingview, parent, false);

        //mContext = parent.getContext();

        Log.d(TAG, "view created");
        return new RecordingsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecordingListViewAdapter.RecordingsViewHolder holder, final int position) {
        item = getItem(position);

        holder.vName.setText(item.getRecording());
        holder.vDateAdded.setText(item.getStartTime());
        Log.d(TAG, "binding view");


        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "cardview clicked", Toast.LENGTH_LONG).show();
                SensorRecordingList recordingList = getItem(position);
                RecordingListItem percebleItem = new RecordingListItem();
                percebleItem.setId(recordingList.getId());
                percebleItem.setName(recordingList.getRecording());
                percebleItem.setTime(recordingList.getStartTime());
                percebleItem.setPreviousId(recordingList.getPreviousRecordingLastId());
                percebleItem.setPreferences(recordingList.getPreferences());


                //is last item
                //TODO: query from this position entry until last
                if (itemList.size() - 1 == position) {
                    percebleItem.setStartId(recordingList.getId());
                    percebleItem.setEndId(-1);
                    try {
                        RecordingExportFragment recordingExportFragment =
                                new RecordingExportFragment().newInstance(percebleItem);

                        android.support.v4.app.FragmentTransaction transaction = ((FragmentActivity) mContext)
                                .getSupportFragmentManager()
                                .beginTransaction();

                        recordingExportFragment.show(transaction, "export");//.show(transaction, "dialog_playback");

                    } catch (Exception e) {
                        Log.e(TAG, "exception", e);
                    }
                } else {
                    //TODO: query greater then current entry to the next entry
                    long nextEntry = getItem(position + 1).getPreviousRecordingLastId();

                    percebleItem.setStartId(recordingList.getId());
                    percebleItem.setEndId(nextEntry);

                    try {
                        RecordingExportFragment recordingExportFragment =
                                new RecordingExportFragment().newInstance(percebleItem);

                        android.support.v4.app.FragmentTransaction transaction = ((FragmentActivity) mContext)
                                .getSupportFragmentManager()
                                .beginTransaction();

                        recordingExportFragment.show(transaction, "export");//.show(transaction, "dialog_playback");

                    } catch (Exception e) {
                        Log.e(TAG, "exception", e);
                    }
                }

            }
        });

    }

    @Override
    public long getItemId(int position) {
        Log.d(TAG, " item id:" + position);
        return position;

    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "item count=" + itemList.size());
        return itemList.size();
    }

    public SensorRecordingList getItem(int position) {
        return itemList.get(position);
    }

    // generate each items view
    public static class RecordingsViewHolder extends RecyclerView.ViewHolder {
        protected TextView vName;
        //protected TextView vLength;
        protected TextView vDateAdded;
        protected View cardView;
        protected CheckBox checkBoxExport;

        public RecordingsViewHolder(View v) {
            super(v);
            vName = (TextView) v.findViewById(R.id.file_name_text);
            //vLength = (TextView) v.findViewById(R.id.file_length_text);
            vDateAdded = (TextView) v.findViewById(R.id.file_date_added_text);
            checkBoxExport = (CheckBox) v.findViewById(R.id.chb_export);
            cardView = v.findViewById(R.id.card_view);
        }


    }
}
