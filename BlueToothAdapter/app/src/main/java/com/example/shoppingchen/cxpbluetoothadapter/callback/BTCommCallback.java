package com.example.shoppingchen.cxpbluetoothadapter.callback;

import android.bluetooth.BluetoothSocket;

/**
 * Created by ShoppingChen on 2017/10/16.
 */

public interface BTCommCallback {
    void onConnectSucceed(BluetoothSocket socket);
    void onServerStarted();
    void onConnectStarted();
    void onConnectFailed();
}
