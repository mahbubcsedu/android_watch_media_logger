package mahbub1.umbc.eclipse.androidwearsensordata.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.view.Window;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.melnykov.fab.FloatingActionButton;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.realm.RealmResults;
import mahbub1.umbc.eclipse.androidwearsensordata.R;
import mahbub1.umbc.eclipse.sensordatashared.data.Data;
import mahbub1.umbc.eclipse.sensordatashared.database.WearableSensorData;
import mahbub1.umbc.eclipse.sensordatashared.database.WearableSensorDataList;
import mahbub1.umbc.eclipse.sensordatashared.serversync.LocalToServerSync;

/**
 * Created by mahbub on 5/16/17.
 */

public class RecordingExportFragment extends DialogFragment {

    public static final String TAG = RecordingExportFragment.class.getSimpleName();
    public static final String ARG_ITEM = "recordingListItem";
    RecordingListItem item;
    com.daimajia.numberprogressbar.NumberProgressBar progressBar;
    private LocalToServerSync localToServerSync;

    public RecordingExportFragment newInstance(RecordingListItem item) {
        RecordingExportFragment f = new RecordingExportFragment();
        Bundle b = new Bundle();
        b.putParcelable(ARG_ITEM, item);
        f.setArguments(b);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        item = getArguments().getParcelable(ARG_ITEM);


    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Dialog dialog = super.onCreateDialog(savedInstanceState);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_progress_lay, null);


        progressBar = (com.daimajia.numberprogressbar.NumberProgressBar) view.findViewById(R.id.number_progress_bar);
        ColorFilter filter = new LightingColorFilter
                (getResources().getColor(R.color.colorPrimary), getResources().getColor(R.color.colorPrimary));

        final FloatingActionButton floatingActionButton = (FloatingActionButton) view.findViewById(R.id.fab_play);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exportFile(v.getContext());
                floatingActionButton.setEnabled(false);
            }
        });

        builder.setView(view);

        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();

        //set transparent background
        Window window = getDialog().getWindow();
        window.setBackgroundDrawableResource(android.R.color.transparent);

        //disable buttons from dialog
        AlertDialog alertDialog = (AlertDialog) getDialog();
        alertDialog.getButton(Dialog.BUTTON_POSITIVE).setEnabled(false);
        alertDialog.getButton(Dialog.BUTTON_NEGATIVE).setEnabled(false);
        alertDialog.getButton(Dialog.BUTTON_NEUTRAL).setEnabled(false);
    }

    @Override
    public void onPause() {
        super.onPause();


    }

    @Override
    public void onDestroy() {
        super.onDestroy();


    }


    private void exportFile(Context context) {

        // mRealm = Realm.getInstance(this);
        localToServerSync = new LocalToServerSync(context);
        RealmResults<WearableSensorDataList> result = localToServerSync.retrieveDatatoTransfer(item.getPreviousId(), item.getEndId());
        final int total_row = result.size();
        final int total_col = 8;
        Log.i("Android Sensors", "total_row = " + total_row);
        final String fileprefix = "export";
        final String date = new SimpleDateFormat("yyyyMMdd-HHmmss", Locale.getDefault()).format(new Date());
        final String filename = String.format("%s_%s.csv", fileprefix, date);

        final String directory = Environment.getExternalStorageDirectory().getAbsolutePath() + "/AndroidWearSensorData";

        final File logfile = new File(directory, filename);
        final File logPath = logfile.getParentFile();

        if (!logPath.isDirectory() && !logPath.mkdirs()) {
            Log.e("SensorDashbaord", "Could not create directory for log files");
        }

        try {
            FileWriter filewriter = new FileWriter(logfile);
            BufferedWriter bw = new BufferedWriter(filewriter);

            //runOnUiThread(new Runnable() {
            //    @Override
            //    public void run() {
            progressBar.setMax(total_row);
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(0);
            //  }
            //});

            // Write the string to the file


            for (int i = 1; i < total_row; i++) {
                final int progress = i;
                //  runOnUiThread(new Runnable() {
                //     @Override
                //    public void run() {
                progressBar.setProgress(progress);
                //  }
                //});


                final Gson gson = new Gson();
                WearableSensorData wearableSensorData;
                String sourcNodeId;
                long dataId = 0;

                StringBuffer sb = new StringBuffer();
                //add heading row
                sb.append("ID");
                sb.append(" ,");

                sb.append("timestamp (ms)");
                sb.append(" ,");


                sb.append("accuracy");
                sb.append(" ,");

                sb.append("Device Id");
                sb.append(" ,");

                sb.append("sensor value 1");
                sb.append(" ,");

                sb.append("sensor value 2");
                sb.append(" ,");
                sb.append("sensor value 3");
                sb.append(" ,");
                sb.append("sensor value 4");
                sb.append(" ,");
                sb.append("sensor value 5");
                sb.append(" ,");
                sb.append("sensor value 6");
                sb.append(" ,");
                sb.append("sensor value 7");
                sb.append(" ,");
                sb.append("sensor value 8");
                sb.append(" ,");
                sb.append("sensor value 9");
                sb.append(" ,");

                sb.append("Senor Name");
                sb.append(" ,");

                sb.append("timestamp (nanosecond)");
                sb.append(" ,");

                sb.append("\n");


                for (WearableSensorDataList dataListJson : result) {

                    sourcNodeId = dataListJson.getAndroidDevice();
                    Type listOfTestObject = new TypeToken<List<Data>>() {
                    }.getType();
                    List<Data> dataList = gson.fromJson(dataListJson.getJsonAsString(), listOfTestObject);

                    for (Data dataItem : dataList) {
                        wearableSensorData = new WearableSensorData();
                        wearableSensorData.setDatasource(dataItem.getSource());
                        wearableSensorData.setAccuracy(dataItem.getAccuracy());
                        wearableSensorData.setTimestamp(dataItem.getTimestamp());
                        wearableSensorData.setAndroidDevice(sourcNodeId);

                        wearableSensorData.setTbId(dataId);
                        dataId++;

                        wearableSensorData.setSensorTimestamp(dataItem.getSensorTimeStampNonoS());


                        if (dataItem.getValues().length > 0) {
                            wearableSensorData.setVal1(dataItem.getValues()[0]);
                        } else {
                            wearableSensorData.setVal1(0.0f);

                        }

                        if (dataItem.getValues().length > 1) {

                            wearableSensorData.setVal2(dataItem.getValues()[1]);
                        } else {
                            wearableSensorData.setVal2(0.0f);

                        }

                        if (dataItem.getValues().length > 2) {

                            wearableSensorData.setVal3(dataItem.getValues()[2]);
                        } else {
                            wearableSensorData.setVal3(0.0f);

                        }

                        if (dataItem.getValues().length > 3) {

                            wearableSensorData.setVal4(dataItem.getValues()[3]);
                        } else {
                            wearableSensorData.setVal4(0.0f);

                        }

                        if (dataItem.getValues().length > 4) {

                            wearableSensorData.setVal5(dataItem.getValues()[4]);
                        } else {
                            wearableSensorData.setVal5(0.0f);

                        }
                        if (dataItem.getValues().length > 5) {

                            wearableSensorData.setVal6(dataItem.getValues()[5]);
                        } else {
                            wearableSensorData.setVal6(0.0f);

                        }
                        if (dataItem.getValues().length > 6) {

                            wearableSensorData.setVal7(dataItem.getValues()[6]);
                        } else {
                            wearableSensorData.setVal7(0.0f);

                        }
                        if (dataItem.getValues().length > 7) {

                            wearableSensorData.setVal8(dataItem.getValues()[7]);
                        } else {
                            wearableSensorData.setVal8(0.0f);

                        }
                        if (dataItem.getValues().length > 8) {

                            wearableSensorData.setVal9(dataItem.getValues()[8]);
                        } else {
                            wearableSensorData.setVal9(0.0f);

                        }

                        sb.append(String.valueOf(wearableSensorData.getTbId()));
                        sb.append(" ,");

                        sb.append(String.valueOf(wearableSensorData.getTimestamp()));
                        sb.append(" ,");


                        sb.append(String.valueOf(wearableSensorData.getAccuracy()));
                        sb.append(" ,");

                        sb.append(String.valueOf(wearableSensorData.getAndroidDevice()));
                        sb.append(" ,");

                        sb.append(String.valueOf(wearableSensorData.getVal1()));
                        sb.append(" ,");

                        sb.append(String.valueOf(wearableSensorData.getVal2()));
                        sb.append(" ,");
                        sb.append(String.valueOf(wearableSensorData.getVal3()));
                        sb.append(" ,");
                        sb.append(String.valueOf(wearableSensorData.getVal4()));
                        sb.append(" ,");
                        sb.append(String.valueOf(wearableSensorData.getVal5()));
                        sb.append(" ,");
                        sb.append(String.valueOf(wearableSensorData.getVal6()));
                        sb.append(" ,");
                        sb.append(String.valueOf(wearableSensorData.getVal7()));
                        sb.append(" ,");
                        sb.append(String.valueOf(wearableSensorData.getVal8()));
                        sb.append(" ,");
                        sb.append(String.valueOf(wearableSensorData.getVal9()));
                        sb.append(" ,");

                        sb.append(String.valueOf(wearableSensorData.getDatasource()));
                        sb.append(" ,");
                        sb.append(String.valueOf(wearableSensorData.getSensorTimestamp()));
                        sb.append(" ,");


                        sb.append("\n");
                    }
                }
                bw.write(sb.toString());
            }
            bw.flush();
            bw.close();

            //runOnUiThread(new Runnable() {
            //    @Override
            //   public void run() {

            //new Handler().postDelayed(new Runnable() {
            //     @Override
            //     public void run() {
            progressBar.setVisibility(View.GONE);
            //    }
            //  }, 1000);

            //  }
            //  });


            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setType("*/*");

            emailIntent.putExtra(Intent.EXTRA_SUBJECT,
                    filename);
            emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(logfile));
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));


            Log.i("AndroidWearSensorData", "export finished!");
        } catch (IOException ioe) {
            Log.e("AndroidWearSensorData", "IOException while writing Logfile");
        }


    }


}
