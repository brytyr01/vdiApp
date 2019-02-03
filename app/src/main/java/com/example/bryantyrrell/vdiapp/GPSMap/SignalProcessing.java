package com.example.bryantyrrell.vdiapp.GPSMap;

import android.hardware.SensorEvent;
import android.os.AsyncTask;

import java.util.ArrayList;

public class SignalProcessing extends AsyncTask<SensorEvent, Void, Void> {

    private AccelerometerActivity activity;
    private static double[] output=new double[] { 0, 0, 0 };
    private long startTime;
    private static int count = 0;
    private double timeConstant;
    ArrayList<AccelData> meanFilterList;

    public SignalProcessing(AccelerometerActivity accelerometerActivity, double timeConstant, long startTime, ArrayList<AccelData> meanFilterList) {
        activity = accelerometerActivity;
        this.startTime = startTime;
        this.timeConstant=timeConstant;
        this.meanFilterList=meanFilterList;
    }

    public void resetStaticValues(){
         output=new double[] { 0, 0, 0 };
         count = 0;
    }

//    public AccelData dataprocessing(SensorEvent event) {
//        // alpha is calculated as t / (t + dT)
//        // with t, the low-pass filter's time-constant
//        // and dT, the event delivery rate
//
//        final float alpha = 0.8f;
//
//        gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
//        gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
//        gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];
//
//        linear_acceleration[0] = event.values[0] - gravity[0];
//        linear_acceleration[1] = event.values[1] - gravity[1];
//        linear_acceleration[2] = event.values[2] - gravity[2];
//
//        long timestamp = System.currentTimeMillis();
//        AccelData data = new AccelData(timestamp, linear_acceleration[0], linear_acceleration[1], linear_acceleration[2]);
//
//        return data;
//    }

    @Override
    protected Void doInBackground(SensorEvent... events) {

        long timestamp = System.currentTimeMillis();
        MeanProcessing processing = new MeanProcessing(meanFilterList);
        AccelData data = processing.getDataPoint();

        for(SensorEvent event : events) {


           // AccelData data = new AccelData(timestamp, event.values[0], event.values[1], event.values[2]);
            processAccelData(data);
            onPostExecute(data);
        }
        return null;
    }

    private void processAccelData(AccelData data) {

        // Find the sample period (between updates) and convert from
        // nanoseconds to seconds. Note that the sensor delivery rates can
        // individually vary by a relatively large time frame, so we use an
        // averaging technique with the number of sensor updates to
        // determine the delivery rate.
        float dt = 1 / (count++ / ((data.getTimestamp() - startTime) / 1000000000.0f));

        double alpha = timeConstant / (timeConstant + dt);

        output[0] = alpha * output[0] + (1 - alpha) * data.getX();
        output[1] = alpha * output[1] + (1 - alpha) * data.getY();
        output[2] = alpha * output[2] + (1 - alpha) * data.getZ();

        System.out.println("output value Before: "+data.getX());
        System.out.println("output value Before: "+data.getY());
        System.out.println("output value Before: "+data.getZ());

        data.setX(data.getX() - output[0]);
        data.setY(data.getY() - output[1]);
        data.setZ(data.getZ() - output[2]);
    }

    protected void onPostExecute(AccelData data) {
        System.out.println("Count value: "+count);
        System.out.println("output value After : "+output[0]);
        System.out.println("output value After: "+output[1]);
        System.out.println("output value After: "+output[2]);
        System.out.println("output value: "+data.getX());
        System.out.println("output value: "+data.getY());
        System.out.println("output value: "+data.getZ());
        activity.addDataPoint(data);

    }
}

