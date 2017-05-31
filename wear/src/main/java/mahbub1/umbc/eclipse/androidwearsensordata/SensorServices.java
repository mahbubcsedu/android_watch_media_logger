package mahbub1.umbc.eclipse.androidwearsensordata;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;

import mahbub1.umbc.eclipse.sensordatashared.data.Data;
import mahbub1.umbc.eclipse.sensordatashared.data.DataBatch;
import mahbub1.umbc.eclipse.sensordatashared.representation.MatrixF4x4;
import mahbub1.umbc.eclipse.sensordatashared.sensors.SensorNames;
import mahbub1.umbc.eclipse.sensordatashared.status.StorageStatus;

import static android.hardware.SensorManager.SENSOR_DELAY_NORMAL;

/**
 * Created by mahbub on 1/24/17.
 */

public class SensorServices extends Service implements SensorEventListener {
    private static final String TAG = SensorServices.class.getSimpleName(); // set tag for this service


    private Map<Integer, DataBatch> sensorDataBatches;
    private SensorNames mSensorNames;

    // 2 general sensor usually embeded on all system
    private final static int SENSOR_ACCELEROMETER = Sensor.TYPE_ACCELEROMETER;
    private final static int SENSOR_MAGNETIC_FIELD = Sensor.TYPE_MAGNETIC_FIELD;
    //  3 @Deprecated Orientation sensors
    private final static int SENSOR_GYROSCOPE = Sensor.TYPE_GYROSCOPE;
    private final static int SENSOR_LIGHT = Sensor.TYPE_LIGHT;
    private final static int SENSOR_PRESSURE = Sensor.TYPE_PRESSURE;
    // 7 @Deprecated temperature sensors
    private final static int SENSOR_PROXIMITY = Sensor.TYPE_PROXIMITY;
    private final static int SENSOR_GRAVITY = Sensor.TYPE_GRAVITY;
    private final static int SENSOR_LINEAR_ACCELERATION = Sensor.TYPE_LINEAR_ACCELERATION;
    private final static int SENSOR_ROTATION_VECTOR = Sensor.TYPE_ROTATION_VECTOR;
    private final static int SENSOR_RELATIVE_HUMIDITY = Sensor.TYPE_RELATIVE_HUMIDITY;
    // TODO: these sensoror may be included in future
    private final static int SENSOR_AMBIENT_TEMPERATURE = Sensor.TYPE_AMBIENT_TEMPERATURE;
    private final static int SENSOR_MAGNETIC_FIELD_UNCALLIBRATED = Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED;
    private final static int SENSOR_GAME_ROTATION_VECTOR = Sensor.TYPE_GAME_ROTATION_VECTOR;
    private final static int SENSOR_GYROSCOPE_UNCALIBRATED = Sensor.TYPE_GYROSCOPE_UNCALIBRATED;
    private final static int SENSOR_SIGNIFICANT_MOTION = Sensor.TYPE_SIGNIFICANT_MOTION;
    private final static int SENSOR_STEP_DETECTOR = Sensor.TYPE_STEP_DETECTOR;
    private final static int SENSOR_STEP_COUNTER = Sensor.TYPE_STEP_COUNTER;
    private final static int SENSOR_GEOMAGNETIC_ROTATION_VECTOR = Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR;
    private final static int SENSOR_HEARTRATE = Sensor.TYPE_HEART_RATE;
    protected MatrixF4x4 currentOrientationRotationMatrix;
    // get instance of sensor manager to get sensor services
    SensorManager mSensorManager;

    private Sensor mHeartrateSensor;


    // Create a constant to convert nanoseconds to seconds.
    private static final float NS2S = 1.0f / 1000000000.0f;
    private final float[] deltaRotationVector = new float[4];
    private float timestamp;
    private static final double EPSILON = 0.1f;



    private AndroidWearClient mAndroidWearClient;
    private ScheduledExecutorService mScheduler;

    private NotificationManager mNM;
    private long[] numberOfData = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    // Unique Identification Number for the Notification.
    // We use it on Notification start, and to cancel it.
    private int NOTIFICATION = R.string.local_service_started;
    private StorageStatus storageStatus;
    private boolean isStoreToLocal = false;
    private int sensorFrequency;
    private static final int NOTIFICATION_ID = 1;
    private static final String GROUP_KEY_MESSAGES = "messages";


    // angular speeds from gyro
    private float[] gyro = new float[3];

    // rotation matrix from gyro data
    private float[] gyroMatrix = new float[9];

    // orientation angles from gyro matrix
    private float[] gyroOrientation = new float[3];

    // magnetic field vector
    private float[] magnet = new float[3];

    // accelerometer vector
    private float[] accel = new float[3];

    // orientation angles from accel and magnet
    private float[] accMagOrientation = new float[3];

    // final orientation angles from sensor fusion
    private float[] fusedOrientation = new float[3];

    // accelerometer and magnetometer based rotation matrix
    private float[] rotationMatrix = new float[9];

    @SuppressLint("LongLogTag")
    @Override
    public void onCreate() {
        super.onCreate();
        mAndroidWearClient = AndroidWearClient.getInstance(this);
        Log.i(TAG, "Sensor remote connected");
        /*Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle("Streaming..");
        builder.setContentText("Collecting sensor data..");
        builder.setSmallIcon(R.mipmap.ic_launcher);
*/


        PendingIntent conversationPendingIntent = getConversationPendingIntent("Sensing...", 0);

        NotificationCompat.Builder notification = new NotificationCompat.Builder(this)
                .setContentTitle("Streaming..")
                .setContentText("Swip right to change")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(conversationPendingIntent)
                .setCategory(Notification.CATEGORY_MESSAGE)
                .setPriority(Notification.PRIORITY_HIGH)
                .setDefaults(Notification.DEFAULT_ALL);
        //.build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        startForeground(NOTIFICATION_ID, notification.build());

        keepCPUAwake();

        //startSensorDataRecording();
        storageStatus = new StorageStatus();
        this.sensorFrequency = SENSOR_DELAY_NORMAL;
        currentOrientationRotationMatrix = new MatrixF4x4();

    }


    private PendingIntent getConversationPendingIntent(String chattingWith, int requestCode) {
        Intent conversationIntent = new Intent(this, ListViewActivity.class);

        if (chattingWith != null) {
            //conversationIntent.putExtra(ListViewActivity.EXTRA_CHATTING_WITH, chattingWith);
        }

        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(this);
        taskStackBuilder.addParentStack(ListViewActivity.class);
        taskStackBuilder.addNextIntent(conversationIntent);

        return taskStackBuilder.getPendingIntent(requestCode, PendingIntent.FLAG_CANCEL_CURRENT);
    }


    @SuppressLint("LongLogTag")
    private void initializeSensorDataBatches() {
        sensorDataBatches = new HashMap<>();
        //List<Sensor> availableSensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);
        //for (Sensor sensor : availableSensors) {
        sensorDataBatches.put(Sensor.TYPE_ACCELEROMETER, getDataBatch(Sensor.TYPE_ACCELEROMETER));
        sensorDataBatches.put(Sensor.TYPE_GYROSCOPE, getDataBatch(Sensor.TYPE_GYROSCOPE));
        sensorDataBatches.put(Sensor.TYPE_MAGNETIC_FIELD, getDataBatch(Sensor.TYPE_MAGNETIC_FIELD));
        sensorDataBatches.put(Sensor.TYPE_ROTATION_VECTOR, getDataBatch(Sensor.TYPE_GAME_ROTATION_VECTOR));


        Log.d(TAG, "sensor object created");
        //}
    }

    private void keepCPUAwake() {
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                TAG);
        wakeLock.acquire();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (null == intent) {
            String source = null == intent ? "intent" : "action";
            Log.e(TAG, source + " was null, flags=" + flags + " bits=" + Integer.toBinaryString(flags));
        } else {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                this.isStoreToLocal = (boolean) extras.getBoolean("isStorageLocal");
                this.storageStatus.setStorageOnLocalWatch(this.isStoreToLocal);
                this.sensorFrequency = (int) extras.getInt("frequency", SensorManager.SENSOR_DELAY_GAME);
            }
        }

        startSensorDataRecording();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Log.d(TAG, "Destroy");
        //mNM.cancel(R.string.remote_service_started);

        // Tell the user we stopped.
        //Toast.makeText(this, R.string.remote_service_stopped, Toast.LENGTH_SHORT).show();
        stopSensorDataRecording();
    }


    @SuppressLint("LongLogTag")
    protected void startSensorDataRecording() {
        Log.d(TAG, "start data recording");
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensorNames = new SensorNames();
        initializeSensorDataBatches();

        //this.storageStatus.setFinishedUnsentData(false);

        if (BuildConfig.DEBUG) {
            logAvailableSensors();
        }

        Sensor accelerometerSensor = mSensorManager.getDefaultSensor(SENSOR_ACCELEROMETER);
       /* Sensor ambientTemperatureSensor = mSensorManager.getDefaultSensor(SENSOR_AMBIENT_TEMPERATURE);
        Sensor gameRotationVectorSensor = mSensorManager.getDefaultSensor(SENSOR_GAME_ROTATION_VECTOR);
        Sensor geomagneticSensor = mSensorManager.getDefaultSensor(SENSOR_GEOMAGNETIC_ROTATION_VECTOR);
        Sensor gravitySensor = mSensorManager.getDefaultSensor(SENSOR_GRAVITY);
        */
        Sensor gyroscopeSensor = mSensorManager.getDefaultSensor(SENSOR_GYROSCOPE);
        /*Sensor gyroscopeUncalibratedSensor = mSensorManager.getDefaultSensor(SENSOR_GYROSCOPE_UNCALIBRATED);
        mHeartrateSensor = mSensorManager.getDefaultSensor(SENSOR_HEARTRATE);
        Sensor heartrateSamsungSensor = mSensorManager.getDefaultSensor(65562);
        Sensor lightSensor = mSensorManager.getDefaultSensor(SENSOR_LIGHT);
        Sensor linearAccelerationSensor = mSensorManager.getDefaultSensor(SENSOR_LINEAR_ACCELERATION);*/
        Sensor magneticFieldSensor = mSensorManager.getDefaultSensor(SENSOR_MAGNETIC_FIELD);
        /*Sensor magneticFieldUncalibratedSensor = mSensorManager.getDefaultSensor(SENSOR_MAGNETIC_FIELD_UNCALLIBRATED);
        Sensor pressureSensor = mSensorManager.getDefaultSensor(SENSOR_PRESSURE);
        Sensor proximitySensor = mSensorManager.getDefaultSensor(SENSOR_PROXIMITY);
        Sensor humiditySensor = mSensorManager.getDefaultSensor(SENSOR_RELATIVE_HUMIDITY);*/
        Sensor rotationVectorSensor = mSensorManager.getDefaultSensor(SENSOR_ROTATION_VECTOR);
       /* Sensor significantMotionSensor = mSensorManager.getDefaultSensor(SENSOR_SIGNIFICANT_MOTION);
        Sensor stepCounterSensor = mSensorManager.getDefaultSensor(SENSOR_STEP_COUNTER);
        Sensor stepDetectorSensor = mSensorManager.getDefaultSensor(SENSOR_STEP_DETECTOR);
        */

        Log.d(TAG, "sensor frequency=" + this.sensorFrequency);

        // Register the listener
        if (mSensorManager != null) {
            if (accelerometerSensor != null) {


                //mSensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_FASTEST);//.SENSOR_DELAY_NORMAL);//SensorManager.SENSOR_DELAY_GAME);//.SENSENSOR_DELAY_FASTEST);
                //mSensorManager.registerListener(this, accelerometerSensor, 16667);
                mSensorManager.registerListener(this, accelerometerSensor, this.sensorFrequency);

            } else {
                Log.w(TAG, "No Accelerometer found");
            }

           /* if (ambientTemperatureSensor != null) {
                mSensorManager.registerListener(this, ambientTemperatureSensor, SensorManager.SENSOR_DELAY_NORMAL);
            } else {
                Log.w(TAG, "Ambient Temperature Sensor not found");
            }

            if (gameRotationVectorSensor != null) {
                mSensorManager.registerListener(this, gameRotationVectorSensor, SensorManager.SENSOR_DELAY_NORMAL);
            } else {
                Log.w(TAG, "Gaming Rotation Vector Sensor not found");
            }

            if (geomagneticSensor != null) {
                mSensorManager.registerListener(this, geomagneticSensor, SensorManager.SENSOR_DELAY_NORMAL);
            } else {
                Log.w(TAG, "No Geomagnetic Sensor found");
            }

            if (gravitySensor != null) {
                mSensorManager.registerListener(this, gravitySensor, SensorManager.SENSOR_DELAY_NORMAL);
            } else {
                Log.w(TAG, "No Gravity Sensor");
            }*/

            if (gyroscopeSensor != null) {//mSensorManager.registerListener(this, gyroscopeSensor, SensorManager.SENSOR_DELAY_FASTEST);//.SENSOR_DELAY_FASTEST);
                // mSensorManager.registerListener(this,gyroscopeSensor,this.sensorFrequency);
                //mSensorManager.registerListener(this, gyroscopeSensor, 16667);
                mSensorManager.registerListener(this, gyroscopeSensor, this.sensorFrequency);
            } else {
                Log.w(TAG, "No Gyroscope Sensor found");
            }
            /*

            if (gyroscopeUncalibratedSensor != null) {
                mSensorManager.registerListener(this, gyroscopeUncalibratedSensor, SensorManager.SENSOR_DELAY_NORMAL);
            } else {
                Log.w(TAG, "No Uncalibrated Gyroscope Sensor found");
            }

            if (mHeartrateSensor != null) {
                final int measurementDuration   = 30;   // Seconds
                final int measurementBreak      = 15;    // Seconds

                mScheduler = Executors.newScheduledThreadPool(1);
                mScheduler.scheduleAtFixedRate(
                        new Runnable() {
                            @Override
                            public void run() {
                                Log.d(TAG, "register Heartrate Sensor");
                                mSensorManager.registerListener(SensorServices.this, mHeartrateSensor, SensorManager.SENSOR_DELAY_FASTEST);

                                try {
                                    Thread.sleep(measurementDuration * 1000);
                                } catch (InterruptedException e) {
                                    Log.e(TAG, "Interrupted while waitting to unregister Heartrate Sensor");
                                }

                                Log.d(TAG, "unregister Heartrate Sensor");
                                mSensorManager.unregisterListener(SensorServices.this, mHeartrateSensor);
                            }
                        }, 3, measurementDuration + measurementBreak, TimeUnit.SECONDS);

            } else {
                Log.d(TAG, "No Heartrate Sensor found");
            }

            if (heartrateSamsungSensor != null) {
                mSensorManager.registerListener(this, heartrateSamsungSensor, SensorManager.SENSOR_DELAY_FASTEST);
            } else {
                Log.d(TAG, "Samsungs Heartrate Sensor not found");
            }

            if (lightSensor != null) {
                mSensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
            } else {
                Log.d(TAG, "No Light Sensor found");
            }

            if (linearAccelerationSensor != null) {
                mSensorManager.registerListener(this, linearAccelerationSensor, SensorManager.SENSOR_DELAY_NORMAL);
            } else {
                Log.d(TAG, "No Linear Acceleration Sensor found");
            }*/

            if (magneticFieldSensor != null) {
                mSensorManager.registerListener(this, magneticFieldSensor, this.sensorFrequency);//SensorManager.SENSOR_DELAY_NORMAL);
            } else {
                Log.d(TAG, "No Magnetic Field Sensor found");
            }
/*
            if (magneticFieldUncalibratedSensor != null) {
                mSensorManager.registerListener(this, magneticFieldUncalibratedSensor, SensorManager.SENSOR_DELAY_NORMAL);
            } else {
                Log.d(TAG, "No uncalibrated Magnetic Field Sensor found");
            }

            if (pressureSensor != null) {
                mSensorManager.registerListener(this, pressureSensor, SensorManager.SENSOR_DELAY_NORMAL);
            } else {
                Log.d(TAG, "No Pressure Sensor found");
            }

            if (proximitySensor != null) {
                mSensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
            } else {
                Log.d(TAG, "No Proximity Sensor found");
            }

            if (humiditySensor != null) {
                mSensorManager.registerListener(this, humiditySensor, SensorManager.SENSOR_DELAY_NORMAL);
            } else {
                Log.d(TAG, "No Humidity Sensor found");
            }
*/
            if (rotationVectorSensor != null) {
                mSensorManager.registerListener(this, rotationVectorSensor, this.sensorFrequency);
            } else {
                Log.d(TAG, "No Rotation Vector Sensor found");
            }
/*
            if (significantMotionSensor != null) {
                mSensorManager.registerListener(this, significantMotionSensor, SensorManager.SENSOR_DELAY_NORMAL);
            } else {
                Log.d(TAG, "No Significant Motion Sensor found");
            }

            if (stepCounterSensor != null) {
                mSensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_NORMAL);
            } else {
                Log.d(TAG, "No Step Counter Sensor found");
            }

            if (stepDetectorSensor != null) {
                mSensorManager.registerListener(this, stepDetectorSensor, SensorManager.SENSOR_DELAY_NORMAL);
            } else {
                Log.d(TAG, "No Step Detector Sensor found");
            }
            */
        }
    }



       /* Sensor accelerometerSensor = mSensorManager.getDefaultSensor(SENSOR_ACCELEROMETER);
        Sensor gyroscopeSensor = mSensorManager.getDefaultSensor(SENSOR_GYROSCOPE);
        Sensor gravitySensor = mSensorManager.getDefaultSensor(SENSOR_GRAVITY);
        Sensor geometricSensor = mSensorManager.getDefaultSensor(SENSOR_GEOMAGNETIC_ROTATION_VECTOR);
        Sensor significantMotionSensor = mSensorManager.getDefaultSensor(SENSOR_SIGNIFICANT_MOTION);
        Sensor proximitySensor = mSensorManager.getDefaultSensor(SENSOR_PROXIMITY);
        Sensor rotationVectorSensor = mSensorManager.getDefaultSensor(SENSOR_ROTATION_VECTOR);
        Sensor magneticFieldSensor = mSensorManager.getDefaultSensor(SENSOR_MAGNETIC_FIELD);



        // register the sensor listener first to get any sensor data

        if(mSensorManager!=null){

            if(accelerometerSensor != null){
            mSensorManager.registerListener(this,accelerometerSensor,mSensorManager.SENSOR_DELAY_NORMAL);

            }else {
            Log.w(TAG, "No accelerometer found");
            }

            if(magneticFieldSensor != null){
                mSensorManager.registerListener(this,magneticFieldSensor,mSensorManager.SENSOR_DELAY_NORMAL);
            }else{
                Log.w(TAG, "no magnatometer found");
            }

            if(rotationVectorSensor != null){
                mSensorManager.registerListener(this,rotationVectorSensor,mSensorManager.SENSOR_DELAY_NORMAL);
            }else{
                Log.w(TAG, "no rotationVectorSensor found");
            }
        }
    }*/


    protected void stopSensorDataRecording() {
        Log.d(TAG, "stop data recording");
        if (mSensorManager != null) {
            mSensorManager.unregisterListener(this);
        }

        if (mScheduler != null && !mScheduler.isTerminated()) {
            mScheduler.shutdown();
        }
        // this.storageStatus.setFinishedUnsentData(true);
        mAndroidWearClient.sendExistingSensorDataBeforeStop(this.isStoreToLocal, this.sensorDataBatches, Sensor.TYPE_ACCELEROMETER);

    }

    @SuppressLint("LongLogTag")
    @Override

    public void onSensorChanged(SensorEvent sensorEvent) {

        //Log.d(TAG, "new sensor event occured " + sensorEvent.sensor.getType());

        switch (sensorEvent.sensor.getType()) {

            case Sensor.TYPE_ROTATION_VECTOR:
                SensorManager.getRotationMatrixFromVector(currentOrientationRotationMatrix.matrix, sensorEvent.values);
                //float[] angles = new float[3];
                //SensorManager.getOrientation(currentOrientationRotationMatrix.matrix,angles);


                float[] angles = new float[3];
                SensorManager.getOrientation(currentOrientationRotationMatrix.matrix, angles);
                //Log.d(TAG, "angles:" + Arrays.toString(angles));
                numberOfData[sensorEvent.sensor.getType()]++;


            /*float[] vals = new float[sensorEvent.values.length];
            System.arraycopy(sensorEvent.values, 0, vals, 0, sensorEvent.values.length);
            Log.d(TAG, "rotation vector value: " + Arrays.toString(vals));
*/
                //Log.d(TAG, "data event:" + Arrays.toString(values));

                float[] degree = new float[3];
                degree[0] = (float) (angles[0] * 180 / Math.PI);
                degree[1] = (float) (angles[1] * 180 / Math.PI);
                degree[2] = (float) (angles[2] * 180 / Math.PI);
                Data data = new Data(sensorEvent.sensor.getName(), degree);
                //Log.d(TAG, "timestamp(ms)=" + System.currentTimeMillis() + " sensorTimestamp(nano):" + sensorEvent.timestamp + " sensorType=" + sensorEvent.sensor.getType() + "angles:"+ Arrays.toString(degree));
                Log.d(TAG, "angles:" + Arrays.toString(degree));
                //Log.d(TAG, "pro_sto_data accelerometer= " + numberOfData[1] + " Gyroscope+" + numberOfData[4]);
                //Log.d(TAG, "data_reporting: timestamp= " + System.currentTimeMillis() + " , sensorType= "+sensorEvent.sensor.getType()+ ", values=" +Arrays.toString(values));
                //Logger.d(this,TAG,"timestamp= " + System.currentTimeMillis() + " , sensorType= "+sensorEvent.sensor.getType()+ ", values=" + Arrays.toString(values));
                data.setAccuracy(sensorEvent.accuracy);
                data.setSensorTimeStampNonoS(sensorEvent.timestamp);
                getDataBatch(sensorEvent.sensor.getType()).addData(data);

                mAndroidWearClient.sendSensorData(this.isStoreToLocal, this.sensorDataBatches, sensorEvent.sensor.getType());
                break;
            case Sensor.TYPE_ACCELEROMETER:
                // copy new accelerometer data into accel array and calculate orientation
                System.arraycopy(sensorEvent.values, 0, accel, 0, 3);
                calculateAccMagOrientation();
                break;

            case Sensor.TYPE_GYROSCOPE:
                // process gyro data
                //gyroFunction(event);
                break;

            case Sensor.TYPE_MAGNETIC_FIELD:
                // copy new magnetometer data into magnet array
                System.arraycopy(sensorEvent.values, 0, magnet, 0, 3);
                break;
        }

        /*if (sensorEvent.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            // convert the rotation-vector to a 4x4 matrix. the matrix
            // is interpreted by Open GL as the inverse of the
            // rotation-vector, which is what we want.
            SensorManager.getRotationMatrixFromVector(currentOrientationRotationMatrix.matrix, sensorEvent.values);
            //float[] angles = new float[3];
            //SensorManager.getOrientation(currentOrientationRotationMatrix.matrix,angles);


            float[] angles = new float[3];
            SensorManager.getOrientation(currentOrientationRotationMatrix.matrix, angles);
            //Log.d(TAG, "angles:" + Arrays.toString(angles));
            numberOfData[sensorEvent.sensor.getType()]++;


            *//*float[] vals = new float[sensorEvent.values.length];
            System.arraycopy(sensorEvent.values, 0, vals, 0, sensorEvent.values.length);
            Log.d(TAG, "rotation vector value: " + Arrays.toString(vals));
*//*
            //Log.d(TAG, "data event:" + Arrays.toString(values));

            float[] degree = new float[3];
            degree[0] = (float)(angles[0] * 180/Math.PI);
            degree[1] = (float)(angles[1] * 180/Math.PI);
            degree[2] = (float)(angles[2] * 180/Math.PI);
            Data data = new Data(sensorEvent.sensor.getName(), degree);
            //Log.d(TAG, "timestamp(ms)=" + System.currentTimeMillis() + " sensorTimestamp(nano):" + sensorEvent.timestamp + " sensorType=" + sensorEvent.sensor.getType() + "angles:"+ Arrays.toString(degree));
            Log.d(TAG, "angles:"+ Arrays.toString(degree));
            //Log.d(TAG, "pro_sto_data accelerometer= " + numberOfData[1] + " Gyroscope+" + numberOfData[4]);
            //Log.d(TAG, "data_reporting: timestamp= " + System.currentTimeMillis() + " , sensorType= "+sensorEvent.sensor.getType()+ ", values=" +Arrays.toString(values));
            //Logger.d(this,TAG,"timestamp= " + System.currentTimeMillis() + " , sensorType= "+sensorEvent.sensor.getType()+ ", values=" + Arrays.toString(values));
            data.setAccuracy(sensorEvent.accuracy);
            data.setSensorTimeStampNonoS(sensorEvent.timestamp);
            getDataBatch(sensorEvent.sensor.getType()).addData(data);

            mAndroidWearClient.sendSensorData(this.isStoreToLocal, this.sensorDataBatches, sensorEvent.sensor.getType());
        }*/

        /* if (sensorEvent.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            if (timestamp != 0) {
                final float dT = (sensorEvent.timestamp - timestamp) * NS2S;
                // Axis of the rotation sample, not normalized yet.
                float axisX = sensorEvent.values[0];
                float axisY = sensorEvent.values[1];
                float axisZ = sensorEvent.values[2];

                // Calculate the angular speed of the sample
                float omegaMagnitude = (float)Math.sqrt(axisX*axisX + axisY*axisY + axisZ*axisZ);

                // Normalize the rotation vector if it's big enough to get the axis
                // (that is, EPSILON should represent your maximum allowable margin of error)
                if (omegaMagnitude > EPSILON) {
                    axisX /= omegaMagnitude;
                    axisY /= omegaMagnitude;
                    axisZ /= omegaMagnitude;
                }

                // Integrate around this axis with the angular speed by the timestep
                // in order to get a delta rotation from this sample over the timestep
                // We will convert this axis-angle representation of the delta rotation
                // into a quaternion before turning it into the rotation matrix.
                double thetaOverTwo = omegaMagnitude * dT / 2.0f;
                double sinThetaOverTwo = Math.sin(thetaOverTwo);
                double cosThetaOverTwo = Math.cos(thetaOverTwo);
                deltaRotationVector[0] = (float)sinThetaOverTwo * axisX;
                deltaRotationVector[1] = (float)sinThetaOverTwo * axisY;
                deltaRotationVector[2] = (float)sinThetaOverTwo * axisZ;
                deltaRotationVector[3] = (float)cosThetaOverTwo;
            }
            timestamp = sensorEvent.timestamp;
            Log.d(TAG, "gyroscode: "+Arrays.toString(deltaRotationVector));
            float[] deltaRotationMatrix = new float[9];
            //SensorManager.getRotationMatrixFromVector(deltaRotationMatrix, deltaRotationVector);
            // User code should concatenate the delta rotation we computed with the current rotation
            // in order to get the updated rotation.
            // rotationCurrent = rotationCurrent * deltaRotationMatrix;
        }*//*
            Log.d(TAG, "angles:"+ Arrays.toString(angles));
            numberOfData[sensorEvent.sensor.getType()]++;


            //float[] vals = new float[sensorEvent.values.length];
            //System.arraycopy(sensorEvent.values, 0, vals, 0, sensorEvent.values.length);

            //Log.d(TAG, "data event:" + Arrays.toString(values));
            Data data = new Data(sensorEvent.sensor.getName(), angles);

            Log.d(TAG,"timestamp(ms)="+System.currentTimeMillis()+" sensorTimestamp(nano):"+sensorEvent.timestamp +" sensorType="+sensorEvent.sensor.getType());
            Log.d(TAG, "pro_sto_data accelerometer= " + numberOfData[1] +" Gyroscope+"+numberOfData[4]);
            //Log.d(TAG, "data_reporting: timestamp= " + System.currentTimeMillis() + " , sensorType= "+sensorEvent.sensor.getType()+ ", values=" +Arrays.toString(values));
            //Logger.d(this,TAG,"timestamp= " + System.currentTimeMillis() + " , sensorType= "+sensorEvent.sensor.getType()+ ", values=" + Arrays.toString(values));
            data.setAccuracy(sensorEvent.accuracy);
            data.setSensorTimeStampNonoS(sensorEvent.timestamp);
            getDataBatch(sensorEvent.sensor.getType()).addData(data);

            mAndroidWearClient.sendSensorData(this.isStoreToLocal, this.sensorDataBatches, sensorEvent.sensor.getType());
        }*//*
        else {

            numberOfData[sensorEvent.sensor.getType()]++;


            float[] values = new float[sensorEvent.values.length];
            System.arraycopy(sensorEvent.values, 0, values, 0, sensorEvent.values.length);

            //Log.d(TAG, "data event:" + Arrays.toString(values));
            Data data = new Data(sensorEvent.sensor.getName(), values);

            Log.d(TAG, "timestamp(ms)=" + System.currentTimeMillis() + " sensorTimestamp(nano):" + sensorEvent.timestamp + " sensorType=" + sensorEvent.sensor.getType());
            Log.d(TAG, "pro_sto_data accelerometer= " + numberOfData[1] + " Gyroscope+" + numberOfData[4]);
            //Log.d(TAG, "data_reporting: timestamp= " + System.currentTimeMillis() + " , sensorType= "+sensorEvent.sensor.getType()+ ", values=" +Arrays.toString(values));
            //Logger.d(this,TAG,"timestamp= " + System.currentTimeMillis() + " , sensorType= "+sensorEvent.sensor.getType()+ ", values=" + Arrays.toString(values));
            data.setAccuracy(sensorEvent.accuracy);
            data.setSensorTimeStampNonoS(sensorEvent.timestamp);
            getDataBatch(sensorEvent.sensor.getType()).addData(data);

            mAndroidWearClient.sendSensorData(this.isStoreToLocal, this.sensorDataBatches, sensorEvent.sensor.getType());
            //}
            //mAndroidWearClient.sendSensorData(sensorEvent.sensor.getType(),sensorEvent.accuracy,sensorEvent.timestamp,sensorEvent.values);

        }*/
    }

    public void calculateAccMagOrientation() {
        if (SensorManager.getRotationMatrix(rotationMatrix, null, accel, magnet)) {
            SensorManager.getOrientation(rotationMatrix, accMagOrientation);
        }
    }
    public DataBatch getDataBatch(int sensorType) {
        DataBatch dataBatch = sensorDataBatches.get(sensorType);
        if (dataBatch == null) {
            dataBatch = createDataBatch(sensorType);
            sensorDataBatches.put(sensorType, dataBatch);
        }
        return dataBatch;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("LongLogTag")
    private void logAvailableSensors() {
        final List<Sensor> sensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);
        Log.d(TAG, "=== LIST AVAILABLE SENSORS ===");
        Log.d(TAG, String.format(Locale.getDefault(), "|%-35s|%-38s|%-6s|", "SensorName", "StringType", "Type"));
        for (Sensor sensor : sensors) {
            Log.v(TAG, String.format(Locale.getDefault(), "|%-35s|%-38s|%-6s|", sensor.getName(), sensor.getStringType(), sensor.getType()));
        }

        Log.d(TAG, "=== LIST AVAILABLE SENSORS ===");
    }


    private DataBatch createDataBatch(int sensorType) {

        String sensorName = this.mSensorNames.getName(sensorType);
        DataBatch dataBatch = new DataBatch(sensorName);
        dataBatch.setType(sensorType);
        return dataBatch;
    }

}
