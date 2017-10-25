package com.example.shoppingchen.cxpbluetoothadapter.communication;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by ShoppingChen on 2017/10/11.
 */

public class MyBluetoothServer {
    private static MyBluetoothServer chatRoom;
    private BluetoothServerSocket mBsServerSocket;
    private ServerCommCallback mCallback;
    private boolean isRunning = false;

    private MyBluetoothServer(){

    }

    public static MyBluetoothServer getInstence(){
        if(chatRoom ==null){
            chatRoom = new MyBluetoothServer();
        }
        return chatRoom;
    }

    public void startBluetoothServerSocket(ServerCommCallback callback){
        Log.d("liuxiuting","startBluetoothServerSocket");
        mCallback = callback;
        if(!isRunning){
            Log.d("liuxiuting","startBluetoothServerSocket  enter............");
            new BluetoothServerThread().start();
        }
    }

    class BluetoothServerThread extends Thread{
        BluetoothServerThread(){
            if(mBsServerSocket ==null){
                try {
                    /*mBsServerSocket = BluetoothAdapter.getDefaultAdapter().listenUsingRfcommWithServiceRecord(
                            BluetoothAdapter.getDefaultAdapter().getName(), UUID.fromString("00001105-0000-1000-8000-00805f9b34fb"));
                   //mBsServerSocket = BluetoothAdapter.getDefaultAdapter().listenUsingRfcommWithServiceRecord();*/
                    if (false) {
                        mBsServerSocket = BluetoothAdapter.getDefaultAdapter().listenUsingRfcommWithServiceRecord(BluetoothAdapter.getDefaultAdapter().getName(),UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66"));
                    } else {
                        mBsServerSocket = BluetoothAdapter.getDefaultAdapter().listenUsingInsecureRfcommWithServiceRecord(BluetoothAdapter.getDefaultAdapter().getName(),UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66"));
                    }
                    mCallback.onServerStarted();
                } catch (IOException e) {
                    //ShowToast.showShortToast("fail to get serversocket of bluetooth");
                    Log.d("liuxiuting","fail to get serversocket of bluetooth"+e.toString());
                    mCallback.onConnectFailed();
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void run() {
            super.run();
            BluetoothSocket btSocket;
            if(mBsServerSocket!=null){
                isRunning = true;
                Log.d("liuxiuting","mBsServerSocket!=null");
                    try {
                        btSocket = mBsServerSocket.accept();
                        Log.d("liuxiuting","得到。。。。。。。。。。。。。");
                        // If a connection was accepted
                        if (btSocket != null) {
                            // Do work to manage the connection (in a separate thread)
                            serverStrted(btSocket);
                            mBsServerSocket.close();
                        }
                    } catch (IOException e) {
                    }
            }
        }

        private void serverStrted(BluetoothSocket btSocket) {
            //获取到了客户端的 btSocket
            //ShowToast.showShortToast("获取到了客户端的socket");
            mCallback.onConnectSucceed(btSocket);
            Log.d("liuxiuting","获取到了客户端的socket");
        }
    }

    public void closeBluetoothServerSocket(){
        try {
            mBsServerSocket.close();
        } catch (IOException e) {
            Log.d("liuxiuting","failed to close serversocket");
            e.printStackTrace();
        }
    }

    public static interface ServerCommCallback{
        void onConnectSucceed(BluetoothSocket socket);
        void onServerStarted();
        void onConnectFailed();
    }
}
