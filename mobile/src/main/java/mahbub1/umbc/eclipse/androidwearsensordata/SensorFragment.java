package mahbub1.umbc.eclipse.androidwearsensordata;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.LinkedList;

import mahbub1.umbc.eclipse.androidwearsensordata.data.Sensor;
import mahbub1.umbc.eclipse.androidwearsensordata.data.SensorDataPoint;
import mahbub1.umbc.eclipse.androidwearsensordata.events.BusProvider;
import mahbub1.umbc.eclipse.androidwearsensordata.events.SensorRangeEvent;
import mahbub1.umbc.eclipse.androidwearsensordata.events.SensorUpdateEvent;
import mahbub1.umbc.eclipse.androidwearsensordata.events.TagAddEvent;
import mahbub1.umbc.eclipse.androidwearsensordata.ui.SensorGraphView;

//import io.realm.Realm;

/**
 * Created by mahbub on 1/25/17.
 */

public class SensorFragment extends Fragment{

    private static final int SENSOR_TOOGLES = 6;
    private static final String TAG=SensorFragment.class.getSimpleName();

    private static final String ARG_PARAM1 = "param1";
    private long sensorId;
    private Sensor sensor;
    private SensorGraphView sensorview;
    private float spread;
    private Context mContext;

   // private Uri mBaseUri, mSensorDataUri;

    private String mAndroidId;


    private boolean[] drawSensors = new boolean[SENSOR_TOOGLES];


    /**
     * Use this factory method to create CalendarUtils new instance of
     * this fragment using the provided parameters.
     *
     * @param sensorId Parameter 1.
     * @return A new instance of fragment SymbolFragment.
     */

    public static SensorFragment newInstance(long sensorId) {
        SensorFragment fragment = new SensorFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_PARAM1, sensorId);
        fragment.setArguments(args);
        return fragment;
    }
    /*public static SensorFragment newInstance(String sensorId) {
        SensorFragment fragment = new SensorFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, sensorId);
        fragment.setArguments(args);
        return fragment;
    }*/
    public SensorFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            sensorId = getArguments().getLong(ARG_PARAM1);
        }
        //mBaseUri = WearableSensorDataTable.SensorDataPoints.CONTENT_URI;
        //mSensorDataUri = WearableSensorDataTable.SensorDataPoints.CONTENT_URI;
        mContext = this.getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        sensor = RemoteWearSensorManager.getInstance(getActivity()).getSensor(sensorId);


        final View view = inflater.inflate(R.layout.fragment_sensor, container, false);


        ((TextView) view.findViewById(R.id.title)).setText(sensor.getSensorName()+"-"+sensor.getSourceNodeId());
        //((TextView) view.findViewById(R.id.title)).setText(R.string.title);

        sensorview = (SensorGraphView) view.findViewById(R.id.graph_view);

        Resources res = getResources();

        view.findViewById(R.id.legend1).setBackgroundColor(res.getColor(R.color.graph_color_1));
        view.findViewById(R.id.legend2).setBackgroundColor(res.getColor(R.color.graph_color_2));
        view.findViewById(R.id.legend3).setBackgroundColor(res.getColor(R.color.graph_color_3));
        view.findViewById(R.id.legend4).setBackgroundColor(res.getColor(R.color.graph_color_4));
        view.findViewById(R.id.legend5).setBackgroundColor(res.getColor(R.color.graph_color_5));
        view.findViewById(R.id.legend6).setBackgroundColor(res.getColor(R.color.graph_color_6));


        String packageName = getActivity().getPackageName();
        for (int i = 0; i < SENSOR_TOOGLES; i++) {

            // Setting click listener for toggles
            int resourceId = res.getIdentifier("legend" + (i + 1) + "_container", "id", packageName);
            final int finalI = i;
            view.findViewById(resourceId).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawSensors[finalI] = !drawSensors[finalI];
                    sensorview.setDrawSensors(drawSensors);
                    v.setSelected(drawSensors[finalI]);
                }
            });

            // Setting toggles as selected
            view.findViewById(resourceId).setSelected(true);
            drawSensors[i] = true;
        }

        sensorview.setDrawSensors(drawSensors);

        return view;
    }


    private void initialiseSensorData() {
        spread = sensor.getMaxValue() - sensor.getMinValue();
        LinkedList<SensorDataPoint> dataPoints = sensor.getDataPoints();

        if (dataPoints == null || dataPoints.isEmpty()) {
            Log.w("sensor data", "no data found for sensor " + sensor.getId() + " " + sensor.getSensorName());
            return;
        }


        ArrayList<Float>[] normalisedValues = new ArrayList[dataPoints.getFirst().getValues().length];
        ArrayList<Integer>[] accuracyValues = new ArrayList[dataPoints.getFirst().getValues().length];
        ArrayList<Long>[] timestampValues = new ArrayList[dataPoints.getFirst().getValues().length];


        for (int i = 0; i < normalisedValues.length; ++i) {
            normalisedValues[i] = new ArrayList<>();
            accuracyValues[i] = new ArrayList<>();
            timestampValues[i] = new ArrayList<>();
        }


        for (SensorDataPoint dataPoint : dataPoints) {

            for (int i = 0; i < dataPoint.getValues().length; ++i) {
                float normalised = (dataPoint.getValues()[i] - sensor.getMinValue()) / spread;
                normalisedValues[i].add(normalised);
                accuracyValues[i].add(dataPoint.getAccuracy());
                timestampValues[i].add(dataPoint.getTimeStamp());
            }
        }


        this.sensorview.setNormalisedDataPoints(normalisedValues, accuracyValues, timestampValues, RemoteWearSensorManager.getInstance(getActivity()).getTags());
        this.sensorview.setZeroLine((0 - sensor.getMinValue()) / spread);

        this.sensorview.setMaxValueLabel(MessageFormat.format("{0,number,#}", sensor.getMaxValue()));
        this.sensorview.setMinValueLabel(MessageFormat.format("{0,number,#}", sensor.getMinValue()));

    }

    @Override
    public void onResume() {
        super.onResume();
        initialiseSensorData();

        //mRealm = Realm.getInstance(getActivity());
        mAndroidId = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        BusProvider.getInstance().unregister(this);
    }


    @Subscribe
    public void onTagAddedEvent(TagAddEvent event) {
        this.sensorview.addNewTag(event.getTag());
    }

    @Subscribe
    public void onSensorUpdatedEvent(SensorUpdateEvent event) {
        Log.d(TAG,"event for sensor"+event.getSensor().getId());
        Log.d(TAG,"current sensor"+this.sensor.getId());


        //if (event.getSensor().getId() == this.sensor.getId()) {
        if (event.getSensor().getCompositeID().equals(this.sensor.getCompositeID())) {


            /*ContentValues values = new ContentValues();
            // Long now = Long.valueOf(System.currentTimeMillis());
            values.put(WearableSensorDataTable.SensorDataPoints.COLUMN_NAME_ENTRYTIME, event.getSensorDataPoint().getTimeStamp());


//            mRealm.beginTransaction();
           // DataEntry entry = mRealm.createObject(DataEntry.class);
            //entry.setAndroidDevice(mAndroidId);
            //entry.setTimestamp(event.getSensorDataPoint().getTimeStamp());
            values.put(WearableSensorDataTable.SensorDataPoints.COLUMN_NAME_ANDROID_DEVICE, mAndroidId);

            if (event.getSensorDataPoint().getValues().length > 0) {
                values.put(WearableSensorDataTable.SensorDataPoints.COLUMN_NAME_X, event.getSensorDataPoint().getValues()[0]);
                //entry.setX(event.getSensorDataPoint().getValues()[0]);
            } else {
                //entry.setX(0.0f);
                values.put(WearableSensorDataTable.SensorDataPoints.COLUMN_NAME_X, 0.0f);
            }

            if (event.getSensorDataPoint().getValues().length > 1) {
                values.put(WearableSensorDataTable.SensorDataPoints.COLUMN_NAME_Y, event.getSensorDataPoint().getValues()[1]);
                //entry.setY(event.getSensorDataPoint().getValues()[1]);
            } else {
                //entry.setY(0.0f);
                values.put(WearableSensorDataTable.SensorDataPoints.COLUMN_NAME_Y, 0.0f);
            }

            if (event.getSensorDataPoint().getValues().length > 2) {
                values.put(WearableSensorDataTable.SensorDataPoints.COLUMN_NAME_Z, event.getSensorDataPoint().getValues()[2]);
                //entry.setZ(event.getSensorDataPoint().getValues()[2]);
            } else {
                //entry.setZ(0.0f);
                values.put(WearableSensorDataTable.SensorDataPoints.COLUMN_NAME_Z, 0.0f);
            }

            //entry.setAccuracy(event.getSensorDataPoint().getAccuracy());
            values.put(WearableSensorDataTable.SensorDataPoints.COLUMN_NAME_ACCURACY, event.getSensorDataPoint().getAccuracy());

            //entry.setDatasource("Acc");
            //entry.setDatatype(event.getSensor().getId());

            values.put(WearableSensorDataTable.SensorDataPoints.COLUMN_NAME_DATA_TYPE, event.getSensor().getId());
            Log.d(TAG,"sensor type:"+event.getSensor().getId());
            //mRealm.commitTransaction();


            // values.put(Diary.DiaryItem.COLUMN_NAME_, value);
            Uri newItemUri = mContext.getContentResolver().insert(mSensorDataUri, values);
            ContentUris.parseId(newItemUri);
*/

            for (int i = 0; i < event.getSensorDataPoint().getValues().length; ++i) {
                float normalised = (event.getSensorDataPoint().getValues()[i] - sensor.getMinValue()) / spread;
                this.sensorview.addNewDataPoint(normalised, event.getSensorDataPoint().getAccuracy(), i, event.getSensorDataPoint().getTimeStamp());
            }
        }
    }


    @Subscribe
    public void onSensorRangeEvent(SensorRangeEvent event) {
        //if (event.getSensor().getId() == this.sensor.getId()) {
        if (event.getSensor().getCompositeID().equals(this.sensor.getId())) {
            initialiseSensorData();
        }
    }
}
