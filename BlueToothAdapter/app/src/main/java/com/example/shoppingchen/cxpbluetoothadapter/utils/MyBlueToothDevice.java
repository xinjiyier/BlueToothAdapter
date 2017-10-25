package com.example.shoppingchen.cxpbluetoothadapter.utils;

import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;

/**
 * Created by ShoppingChen on 2017/9/29.
 */

public class MyBlueToothDevice {
    private short btRssi;
    private BluetoothClass btClass;
    private String btName;
    private BluetoothDevice btDevice;

    public MyBlueToothDevice(short btRssi, BluetoothClass btClass, String btName, BluetoothDevice btDevice) {
        this.btRssi = btRssi;
        this.btClass = btClass;
        this.btName = btName;
        this.btDevice = btDevice;
    }

    public short getBtRssi() {
        return btRssi;
    }

    public void setBtRssi(short btRssi) {
        this.btRssi = btRssi;
    }

    public BluetoothClass getBtClass() {
        return btClass;
    }

    public void setBtClass(BluetoothClass btClass) {
        this.btClass = btClass;
    }

    public String getBtName() {
        return btName;
    }

    public void setBtName(String btName) {
        this.btName = btName;
    }

    public BluetoothDevice getBtDevice() {
        return btDevice;
    }

    public void setBtDevice(BluetoothDevice btDevice) {
        this.btDevice = btDevice;
    }

    @Override
    public String toString() {
        return "BlueToothDevice{" +
                "btRssi=" + btRssi +
                ", btClass=" + btClass +
                ", btName='" + btName + '\'' +
                ", btDevice=" + btDevice +
                '}';
    }
}
