package com.example.shoppingchen.cxpbluetoothadapter.communication;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.example.shoppingchen.cxpbluetoothadapter.utils.BluetoothUtils;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by ShoppingChen on 2017/10/12.
 */

public class MyBluetoothClient {
    private static MyBluetoothClient mBTClient;
    private ClientCommCallback mCallback;
    private int clientState = BluetoothUtils.CLIENT_FREE;
    private MyBluetoothClient(){

    }

    public static MyBluetoothClient getInstence(){
        if(mBTClient ==null){
            mBTClient = new MyBluetoothClient();
        }
        return mBTClient;
    }

    public void connectServerSocket(BluetoothDevice device,ClientCommCallback callback){
        mCallback = callback;
        if(clientState == BluetoothUtils.CLIENT_FREE){
            new ConnectThread(device).start();
            Log.d("liuxiuting","");
            //ShowToast.showShortToast("开始连接");
        }else if(clientState == BluetoothUtils.CLIENT_CONNECTING){
            //正在连接中.. 请稍候
            Log.d("liuxiuting","");
            //ShowToast.showShortToast("正在连接中.. 请稍候");
        }else{
            //已连接
            Log.d("liuxiuting","");
            //ShowToast.showShortToast("已连接，请不要重复操作");
        }
    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket,
            // because mmSocket is final
            BluetoothSocket tmp = null;
            mmDevice = device;

            // Get a BluetoothSocket to connect with the given BluetoothDevice
/*            try {
                // MY_UUID is the app's UUID string, also used by the server code
                tmp = device.createRfcommSocketToServiceRecord( UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66"));
            } catch (IOException e) {
                mCallback.onConnectFailed();
            }*/
            try {
                if (false) {
                    tmp = device.createRfcommSocketToServiceRecord(UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66"));
                } else {
                    tmp = device.createInsecureRfcommSocketToServiceRecord(UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66"));
                }
            } catch (IOException e) {
                //BleLog.e("Socket Type: " + mSocketType + "create() failed", e);
                mCallback.onConnectFailed();
            }

            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it will slow down the connection
            //mBluetoothAdapter.cancelDiscovery();
            Log.d("liuxiuting","started to connect to serversocket");
            try {
                // Connect the device through the socket. This will block
                // until it succeeds or throws an exception
                clientState = BluetoothUtils.CLIENT_CONNECTING;
                //ShowToast.showShortToast("开始连接");
                mCallback.onConnectStarted();
                mmSocket.connect();
                clientState = BluetoothUtils.CLIENT_CONNECTED;
                clientStarted(mmSocket);
                Log.d("liuxiuting","succeed to connect to serversocket");
                //ShowToast.showShortToast("succeed to connect to serversocket");
            } catch (IOException connectException) {
                // Unable to connect; close the socket and get out
                clientState = BluetoothUtils.CLIENT_FREE;
                mCallback.onConnectFailed();
                Log.d("liuxiuting","fail to connect to serversocket");
                //ShowToast.showShortToast("fail to connect to serversocket");
                try {
                    mmSocket.close();
                } catch (IOException closeException) { }
                return;
            }

            // Do work to manage the connection (in a separate thread)
            //manageConnectedSocket(mmSocket);
        }

        private void clientStarted(BluetoothSocket mmSocket) {
            mCallback.onConnectSucceed(mmSocket);
        }

        /** Will cancel an in-progress connection, and close the socket */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }

    public static interface ClientCommCallback{
        void onConnectSucceed(BluetoothSocket socket);
        void onConnectStarted();
        void onConnectFailed();
    }
}
