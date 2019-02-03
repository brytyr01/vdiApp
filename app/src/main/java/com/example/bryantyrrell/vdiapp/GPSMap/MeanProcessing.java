package com.example.bryantyrrell.vdiapp.GPSMap;

import android.hardware.SensorEvent;

import java.util.ArrayList;

class MeanProcessing  {
    private ArrayList<AccelData> meanFilterList;
    private AccelData meanValue;
    private int count = 0;
    double[] Summedvalues;
    private long Summedtimestamp;

    public MeanProcessing(ArrayList<AccelData> meanFilterList) {
        this.meanFilterList=meanFilterList;
        Summedvalues=new double[]{0,0,0};
        CalculateMean();
    }

    private void CalculateMean() {
        int size = meanFilterList.size();
        System.out.println("The size of the arraylist is: "+size);
        for(int i=1;i<4;i++){
            Summedtimestamp=Summedtimestamp+meanFilterList.get(size-i).getTimestamp();
            Summedvalues[0]=Summedvalues[0]+meanFilterList.get(size-i).getX();
            Summedvalues[1]=Summedvalues[1]+meanFilterList.get(size-i).getY();
            Summedvalues[2]=Summedvalues[2]+meanFilterList.get(size-i).getZ();
            count++;
        }
        System.out.println("Count value is: "+count);
        meanValue=new AccelData((Summedtimestamp/count),(Summedvalues[0]/count),(Summedvalues[1]/count),(Summedvalues[2]/count));
    }

    public AccelData getDataPoint() {
        return meanValue;
    }
}
