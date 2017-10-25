package com.example.shoppingchen.cxpbluetoothadapter.search;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;

import com.example.shoppingchen.cxpbluetoothadapter.callback.BTSearchCallback;
import com.example.shoppingchen.cxpbluetoothadapter.utils.MyBlueToothDevice;
import com.example.shoppingchen.cxpbluetoothadapter.utils.MyBluetoothReceiver;

import java.util.Set;

/**
 * Created by ShoppingChen on 2017/10/16.
 */

public class CXPBluetoothSearchAdapter {

    private BluetoothAdapter btAdapter;
    private Context mContext;
    private MyBluetoothReceiver receiver;

    public CXPBluetoothSearchAdapter(Context context){
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        mContext = context;
    }

    /*
       获取已经绑定的蓝牙设备信息
     */
    public Set<BluetoothDevice> getBondedDevices() {
        return btAdapter.getBondedDevices();
    }

    /*
       获取BluetoothAdapter
     */
    public BluetoothAdapter getBtAdapter(){
        return btAdapter;
    }

    /**
     * 打开蓝牙部分
     * @param isVisiable 本机蓝牙是否可见
     */
    public void openBluetooth(boolean isVisiable){
        if(isVisiable){
            openBluetoothAndAvailable();
        }else{
            openBluetooth();
        }
    }

    /**
     * 打开蓝牙并使蓝牙可见
     */
    public void openBluetoothAndAvailable() {
        if(btAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE){
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,300);
            mContext.startActivity(intent);
        }
    }

    /**
     * 打开蓝牙
     */
    private void openBluetooth(){
        if(!btAdapter.isEnabled()){
            btAdapter.enable();
        }
    }

    /**
     * 关闭蓝牙设备
     */
    public void closeBluetooth(){
        if(btAdapter.isEnabled()){
            btAdapter.disable();
        }
    }

    /**
     * 搜索周围蓝牙设备
     * @return
     */
    public boolean startDiscovery(BTSearchCallback callback) {
        if(!btAdapter.isEnabled()){
            //蓝牙未打开，或不可用
            // 此处因为打开蓝牙是一个耗时操作，后边确定是不是阻塞的，如果不是，则需要做一些额外的操作
            btAdapter.enable();
        }
        if(btAdapter.isDiscovering()){
            return true;
        }
        if(receiver==null){
            receiver = new MyBluetoothReceiver(mContext,callback);
        }
        receiver.registerMyReceiver();
        return btAdapter.startDiscovery();
    }

    /**
     * 停止周围蓝牙设备的搜索
     * @return
     */
    public boolean cancelDiscovery(){
        if(!btAdapter.isDiscovering()){
            return true;
        }
        if(receiver!=null){
            receiver.unRegisterReceiver();
        }
        return btAdapter.cancelDiscovery();
    }
}
