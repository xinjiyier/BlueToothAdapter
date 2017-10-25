package com.example.shoppingchen.cxpbluetoothadapter.pair;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

/**
 * Created by ShoppingChen on 2017/10/16.
 */

public class CXPBluetoothPairAdapter {

    private BluetoothAdapter btAdapter;
    public CXPBluetoothPairAdapter(){
        btAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public boolean createBond(BluetoothDevice device){
        if(device.getBondState()==BluetoothDevice.BOND_NONE){
            return device.createBond();
        }
        return true;
    }
}
