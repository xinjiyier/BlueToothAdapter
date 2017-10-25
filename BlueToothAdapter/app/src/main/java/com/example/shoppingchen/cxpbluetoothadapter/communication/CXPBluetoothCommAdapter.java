package com.example.shoppingchen.cxpbluetoothadapter.communication;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.example.shoppingchen.cxpbluetoothadapter.callback.BTCommCallback;
import com.example.shoppingchen.cxpbluetoothadapter.pair.CXPBluetoothPairAdapter;

/**
 * Created by ShoppingChen on 2017/10/16.
 */

public class CXPBluetoothCommAdapter {

    private BluetoothAdapter btAdapter;
    private MyBluetoothServer btServer;
    private MyBluetoothClient btClient;
    private BluetoothDevice mDevice;
    private BTCommCallback mCallback;

    public CXPBluetoothCommAdapter(){
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        btServer = MyBluetoothServer.getInstence();
        btClient = MyBluetoothClient.getInstence();
    }

    public void startCommunicate(BluetoothDevice device, BTCommCallback callback){
        this.mCallback = callback;
        startBluetoothServerSocket();
        //connectServerSocket(device);
    }

    public void connectToServer(BluetoothDevice device, BTCommCallback callback){
        this.mCallback = callback;
        connectServerSocket(device);
    }

    private void startBluetoothServerSocket(){
        btServer.startBluetoothServerSocket(new ServerSocketCallback());
    }

    private void connectServerSocket(BluetoothDevice device){
        if(device == null){
            device = mDevice;
        }else{
            mDevice = device;
        }
        btClient.connectServerSocket(device,new ClientSocketCallback());
    }

    class ServerSocketCallback implements MyBluetoothServer.ServerCommCallback{

        @Override
        public void onConnectSucceed(BluetoothSocket socket) {
            Log.d("liuxiuting","Server 连接成功...");
            mCallback.onConnectSucceed(socket);
        }

        @Override
        public void onServerStarted() {
            mCallback.onServerStarted();
            Log.d("liuxiuting","Server 开始监听了...");
        }

        @Override
        public void onConnectFailed() {
            Log.d("liuxiuting","Server 开启监听失败，正在重新监听...");
            startBluetoothServerSocket();
        }
    }

    class ClientSocketCallback implements MyBluetoothClient.ClientCommCallback{
        @Override
        public void onConnectSucceed(BluetoothSocket socket) {
            mCallback.onConnectSucceed(socket);
            Log.d("liuxiuting","Client 连接成功...");
        }

        @Override
        public void onConnectStarted() {
            mCallback.onConnectStarted();
            Log.d("liuxiuting","Client 尝试连接中...");
        }

        @Override
        public void onConnectFailed() {
            Log.d("liuxiuting","Client 连接失败，正在重新连接...");
            connectServerSocket(null);
        }
    }

    public void stopCommunicate(){
        btServer.closeBluetoothServerSocket();
    }

}
