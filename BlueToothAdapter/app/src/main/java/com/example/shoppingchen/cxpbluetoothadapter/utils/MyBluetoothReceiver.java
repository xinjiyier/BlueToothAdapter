package com.example.shoppingchen.cxpbluetoothadapter.utils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.Toast;

import com.example.shoppingchen.cxpbluetoothadapter.callback.BTSearchCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ShoppingChen on 2017/9/29.
 */

public class MyBluetoothReceiver {
    private Context mContext;
    private BTSearchCallback callback;
    private boolean isNewScan = true;
    private BluetoothReceiver br;
    //保存搜索到的设备信息
    public List<MyBlueToothDevice> devices = new ArrayList<>();

    public MyBluetoothReceiver(Context context, BTSearchCallback btsc){
        this.mContext = context;
        this.callback = btsc;
        //registerMyReceiver();
    }

    public void registerMyReceiver() {
        br = new BluetoothReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        mContext.registerReceiver(br,intentFilter);
    }

    class BluetoothReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()){
                case BluetoothDevice.ACTION_FOUND:
                    if(isNewScan){
                        isNewScan = false;
                        devices.clear();
                    }
                    short rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);
                    BluetoothClass btClass = intent.getParcelableExtra(BluetoothDevice.EXTRA_CLASS);
                    String name = intent.getStringExtra(BluetoothDevice.EXTRA_NAME);
                    BluetoothDevice device = intent
                            .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    MyBlueToothDevice mbd = new MyBlueToothDevice(rssi,btClass,name,device);
                    Toast.makeText(mContext,mbd.getBtRssi()+"  "+mbd.getBtClass()+"  "+mbd.getBtName()+"  "+mbd.getBtDevice().getAddress(),Toast.LENGTH_SHORT).show();
                    devices.add(mbd);
                    callback.onActionFound(devices);
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    Toast.makeText(mContext,"ACTION_DISCOVERY_FINISHED",Toast.LENGTH_SHORT).show();
                    isNewScan = true;
                    callback.onScanFinished();
                    unRegisterReceiver();
                    break;
            }
        }
    }

    public List<MyBlueToothDevice> getDevicesData(){
        return devices;
    }

    public void unRegisterReceiver(){
        mContext.unregisterReceiver(br);
    }
}
