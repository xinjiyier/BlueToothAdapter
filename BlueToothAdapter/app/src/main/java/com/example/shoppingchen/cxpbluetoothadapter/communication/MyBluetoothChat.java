package com.example.shoppingchen.cxpbluetoothadapter.communication;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by ShoppingChen on 2017/10/13.
 */

public class MyBluetoothChat {

    private OutputStream op;
    private InputStream ip;
    private ReadThread rt;
    private boolean flag = true;
    private ChatRoomCallback mCallback;

    public MyBluetoothChat(BluetoothSocket socket, ChatRoomCallback callback){
        this.mCallback = callback;
        try {
            ip = socket.getInputStream();
            op = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void startListening(){
        flag = true;
        rt = new ReadThread();
        rt.start();
    }

    public void stopListening(){
        flag = false;
        rt = null;
    }

    public void write(final byte[] buffer){
        new Thread(){
            @Override
            public void run() {
                super.run();
                if(op == null){
                    return;
                }
                try {
                    op.write(buffer);
                    String bu = new String(buffer);
                    Log.d("liuxiuting","write "+bu+"  succeed");
                } catch (IOException e) {
                    Log.d("liuxiuting","failed to write");
                    e.printStackTrace();
                }
            }
        }.start();
    }

    class ReadThread extends Thread{
        @Override
        public void run() {
            super.run();
            byte[] buffer = new byte[1024];
            int bytes;
            while(flag){
                if(ip == null){
                    return;
                }
                try {
                    bytes = ip.read(buffer);
                    mCallback.onReceivedMessage(buffer.toString());
                    Log.d("liuxiuting","get from socket :="+buffer.toString());
                } catch (IOException e) {
                    Log.d("liuxiuting","failed to read");
                    e.printStackTrace();
                }
            }
        }
    }

    public static interface ChatRoomCallback{
        void onReceivedMessage(String msg);
    }
}
