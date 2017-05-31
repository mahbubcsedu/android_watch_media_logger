package mahbub1.umbc.eclipse.androidwearsensordata.ui;

/**
 * Created by mahbub on 2/15/17.
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmResults;
import mahbub1.umbc.eclipse.androidwearsensordata.R;
import mahbub1.umbc.eclipse.sensordatashared.data.Data;
import mahbub1.umbc.eclipse.sensordatashared.database.WearableSensorDataList;
import mahbub1.umbc.eclipse.sensordatashared.database.WearableSensorData;
import mahbub1.umbc.eclipse.sensordatashared.serversync.LocalToServerSync;
import mahbub1.umbc.eclipse.sensordatashared.utils.DataTransferUtils;


/**
 * Created by mahbub on 2/6/17.
 */

public class ExportActivity extends AppCompatActivity {
    private ProgressBar dataProgressbar;
    private NumberProgressBar numberProgressBar;
    public static final String TAG = ExportActivity.class.getSimpleName();
    private Context mContext;
    private final int NETWORK_TRANSFER_TIMEOUT_MS = 60 * 1000;
    private ProgressDialog progressDialog;
    private LocalToServerSync localToServerSync;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_export);
        mContext = this;
        localToServerSync = new LocalToServerSync(this);

        dataProgressbar = (ProgressBar) findViewById(R.id.export_progress);
        numberProgressBar = (NumberProgressBar) findViewById(R.id.number_progress_bar);
        numberProgressBar.setVisibility(View.INVISIBLE);
        // tagProgressbar = (ProgressBar) findViewById(R.id.export_progress_tag);

        //   setSupportActionBar((Toolbar) findViewById(R.id.my_awesome_toolbar));
        // getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("Export Data");
        Button exportButton = (Button) findViewById(R.id.exportButton);
        Button syncButton = (Button) findViewById(R.id.synceButton);
        Button btnMarkAsSync = (Button) findViewById(R.id.btnMarkSync);
        btnMarkAsSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Realm realm = Realm.getDefaultInstance();
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        WearableSensorDataList wearableSensorDataList = new WearableSensorDataList();
                        wearableSensorDataList.setStatus(DataTransferUtils.STATUS_DATA_TRANSFER_COMPLETE);
                        realm.copyToRealmOrUpdate(wearableSensorDataList);
                    }
                });
            }
        });

        syncButton.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(final View v) {

                        Runnable r = new Runnable() {
                            @Override
                            public void run() {
                                //syncDataInChunk();
                                exportFromRealm();
                            }
                        };
                        Thread t = new Thread(r);
                        t.start();
                    }
                });


        exportButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {


                        Runnable r = new Runnable() {
                            @Override
                            public void run() {
                                exportFile();

                            }
                        };

                        Thread t = new Thread(r);
                        t.start();
                    }
                }

        );


        Button btnMarkAsUnsync = (Button) findViewById(R.id.btnUnsynced);
        btnMarkAsUnsync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Realm realm = Realm.getDefaultInstance();
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        WearableSensorDataList wearableSensorDataList = new WearableSensorDataList();
                        wearableSensorDataList.setStatus(DataTransferUtils.STATUS_DATA_TRANSFER_INCOMPLETE);
                        realm.copyToRealmOrUpdate(wearableSensorDataList);
                    }
                });

                //Log.d(TAG, updatedrow + " sensor row fetched");
                //Toast.makeText(mContext, updatedrow+ " rows marked as unsynced", Toast.LENGTH_SHORT).show();
            }
        });

        Button btnDelete = (Button) findViewById(R.id.deleteButton);

        Button btnDatabaseAsIs = (Button) findViewById(R.id.btnDatabaseWatch);

        btnDatabaseAsIs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exportAsIsInDatabase();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Realm realm = Realm.getDefaultInstance();
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        //WearableSensorDataList wearableSensorDataList = new WearableSensorDataList();
                        //wearableSensorDataList.setStatus(DataTransferUtils.STATUS_DATA_TRANSFER_INCOMPLETE);
                        realm.deleteAll();//.copyToRealmOrUpdate(wearableSensorDataList);
                    }
                });
            }
        });
        //Button btnMarkSynce = (Button) findViewById()

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }



   /* private void syncUpdateRealm(String jsonResponse){
        //long table_row_id=-1;
        try {
            JSONObject jsonObj = new JSONObject(jsonResponse);

            try {
                if (jsonObj.getString("error").equals("")) {
                    final long table_row_id = Long.parseLong(jsonObj.getString("rows"));

                    Realm realm = Realm.getDefaultInstance();

                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            WearableSensorDataList wearableSensorDataList = new WearableSensorDataList();
                            wearableSensorDataList.setId(table_row_id);
                            wearableSensorDataList.setStatus(DataTransferUtils.STATUS_DATA_TRANSFER_COMPLETE);

                            realm.copyToRealmOrUpdate(wearableSensorDataList);


                        }
                    });

                } else {
                    String error_message = jsonObj.getString("error");
                    Log.d(TAG, "error occured:"+error_message);
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


        } catch (JSONException e) {
            //System.out.println("its not a json object and can't parse" + e.getMessage());
            //e.printStackTrace();
        }



    }

*/

    /*private void syncDataToServerAsJson(List<Data> dataList, String androidDevice,long id)
    {
        final String URL = mContext.getResources().getString(R.string.server_url);
        List<WearableSensorData> listData=new ArrayList<>();

        for(Data dataItem: dataList){
            WearableSensorData wearableSensorData = new WearableSensorData();
            wearableSensorData.setDatasource(dataItem.getSource());
            wearableSensorData.setAccuracy(dataItem.getAccuracy());
            wearableSensorData.setTimestamp(dataItem.getTimestamp());
            wearableSensorData.setAndroidDevice(androidDevice);
            wearableSensorData.setTbId(id);


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

            listData.add(wearableSensorData);
        }

        //ArrayList<WearableSensorData> result = retrieveDatatoTransfer();
        Gson gson = new Gson();
        JsonElement element = gson.toJsonTree(listData, new TypeToken<List<WearableSensorData>>() {
        }.getType());

        if (!element.isJsonArray()) {

        }

        JsonArray jsonArray = element.getAsJsonArray();
        final String jsonData = jsonArray.toString();



        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                syncUpdateRealm(response);
                Log.i("VOLLEY", response);


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("VOLLEY", error.toString());
                //progressDialog.dismiss();
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return jsonData == null ? null : jsonData.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", jsonData, "utf-8");
                    return null;
                }
            }

            *//* @Override
             protected Response<String> parseNetworkResponse(NetworkResponse response) {
                 String responseString = "";
                 if (response != null) {
                     responseString = String.valueOf(response.statusCode);

                     // can get more details such as response.headers
                 }
                 return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
             }*//*
            @Override
            public Map<String,String> getHeaders() throws AuthFailureError {
                Map<String,String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };


        stringRequest.setRetryPolicy(new DefaultRetryPolicy(NETWORK_TRANSFER_TIMEOUT_MS, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        // add the request object to the queue to be executed
        TransferSingleTon.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);

    }

*/


    public String getSQL(JSONArray jsonArray) {

        return jsonArray.toString().replace("[", "(").replace("]", ")");
        /*
        String whereUpdate = "";
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                whereUpdate += WearableSensorDataTable.SensorDataPoints.COLUMN_NAME_ID + "=" + jsonArray.getString(i) + " OR ";
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        whereUpdate = whereUpdate.substring(0, whereUpdate.length() - 4);
        //System.out.println(whereUpdate);
        return whereUpdate;*/
    }

    private void exportFromRealm() {


        final RealmResults<WearableSensorDataList> result = localToServerSync.retrieveDatatoTransfer();
        if (result.size() == 0) return;
        final int dataSize = result.size();

        final Gson gson = new Gson();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                numberProgressBar.setMax(dataSize);
                numberProgressBar.setVisibility(View.VISIBLE);
                numberProgressBar.setProgress(0);
            }
        });


        int i = 0;
        for (WearableSensorDataList dataListJson : result) {
            Log.d(TAG, "retrieved data:" + dataListJson.getJsonAsString().toString());

            //int range = Math.min(i, unsyncData.size());
            final int progress = i;

            Type listOfTestObject = new TypeToken<List<Data>>() {
            }.getType();
            List<Data> listOfData = gson.fromJson(dataListJson.getJsonAsString(), listOfTestObject);
            String sourcNodeId = dataListJson.getAndroidDevice();
            long id = dataListJson.getId();

            Log.d(TAG, "data as list: " + listOfData.size() + "data" + listOfData.toArray().toString());

            localToServerSync.syncDataToServerAsJson(listOfData, sourcNodeId, id);


            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //dataProgressbar.setMax(total_row);
                    //dataProgressbar.setVisibility(View.VISIBLE);
                    numberProgressBar.setProgress(progress);
                }
            });
            i = i + 1;


        }


        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        numberProgressBar.setVisibility(View.GONE);
                    }
                }, 1000);

            }
        });
    }


    private void exportAsIsInDatabase() {
        RealmResults<WearableSensorDataList> result = localToServerSync.retrieveDatatoTransfer();
        final int total_row = result.size();
        final int total_col = 8;
        Log.i("Android Sensors", "total_row = " + total_row);
        final String fileprefix = "export";
        final String date = new SimpleDateFormat("yyyyMMdd-HHmmss", Locale.getDefault()).format(new Date());
        final String filename = String.format("%s_%s.txt", fileprefix, date);

        final String directory = Environment.getExternalStorageDirectory().getAbsolutePath() + "/AndroidWearSensorData";

        final File logfile = new File(directory, filename);
        final File logPath = logfile.getParentFile();

        if (!logPath.isDirectory() && !logPath.mkdirs()) {
            Log.e("SensorDashbaord", "Could not create directory for log files");
        }

        try {
            FileWriter filewriter = new FileWriter(logfile);
            BufferedWriter bw = new BufferedWriter(filewriter);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dataProgressbar.setMax(total_row);
                    dataProgressbar.setVisibility(View.VISIBLE);
                    dataProgressbar.setProgress(0);
                }
            });

            // Write the string to the file

            for (int i = 1; i < total_row; i++) {
                final int progress = i;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dataProgressbar.setProgress(progress);
                    }
                });

                StringBuffer sb = new StringBuffer();
                final Gson gson = new Gson();
                //WearableSensorData wearableSensorData;
                //String sourcNodeId;
                for (WearableSensorDataList dataListJson : result) {
                    //sourcNodeId = dataListJson.getAndroidDevice();
                    Type listOfTestObject = new TypeToken<List<Data>>() {
                    }.getType();
                    List<Data> dataList = gson.fromJson(dataListJson.getJsonAsString(), listOfTestObject);
                    sb.append(dataList);
                    sb.append("\n");
                }
                bw.write(sb.toString());
            }
            bw.flush();
            bw.close();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dataProgressbar.setVisibility(View.GONE);
                        }
                    }, 1000);

                }
            });


            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setType("*/*");

            emailIntent.putExtra(Intent.EXTRA_SUBJECT,
                    "AndroidWearSensorData data export");
            emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(logfile));
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));


            Log.i("AndroidWearSensorData", "export finished!");
        } catch (IOException ioe) {
            Log.e("AndroidWearSensorData", "IOException while writing Logfile");
        }
    }

    private void exportFile() {

        // mRealm = Realm.getInstance(this);

        RealmResults<WearableSensorDataList> result = localToServerSync.retrieveDatatoTransfer();
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

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dataProgressbar.setMax(total_row);
                    dataProgressbar.setVisibility(View.VISIBLE);
                    dataProgressbar.setProgress(0);
                }
            });

            // Write the string to the file


            //for (int i = 1; i < total_row; i++) {


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


            int i = 0;

            for (WearableSensorDataList dataListJson : result) {


                final int progress = i;
                i++;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dataProgressbar.setProgress(progress);
                    }
                });


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
            // }
            bw.flush();
            bw.close();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dataProgressbar.setVisibility(View.GONE);
                        }
                    }, 1000);

                }
            });


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

    /*private RealmResults<WearableSensorDataList> retrieveDatatoTransfer() {


        Realm realm = Realm.getDefaultInstance();

        final RealmResults<WearableSensorDataList> unsyncData = realm.where(WearableSensorDataList.class)
                .equalTo("status", DataTransferUtils.STATUS_DATA_TRANSFER_INCOMPLETE).findAll();
       *//*realm.executeTransaction(new Realm.Transaction() {
           @Override
           public void execute(Realm realm) {

                unsyncData = realm.where(WearableSensorDataList.class)
                       .equalTo("status", DataTransferUtils.STATUS_DATA_TRANSFER_INCOMPLETE).findAll();
           }});*//*

        return unsyncData;


    }
*/


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Export Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }


}

