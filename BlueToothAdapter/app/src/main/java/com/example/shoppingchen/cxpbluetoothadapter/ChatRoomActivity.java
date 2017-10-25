package com.example.shoppingchen.cxpbluetoothadapter;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.shoppingchen.cxpbluetoothadapter.communication.MyBluetoothChat;

public class ChatRoomActivity extends Activity implements MyBluetoothChat.ChatRoomCallback{

    public Handler handler =  new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    private MyBluetoothChat chatRoom;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        initIntent();
        initViews();
        initData();
    }

    private void initIntent() {
        getIntent().getExtras();
    }

    private void initData() {
        //chatRoom = new MyBluetoothChat();
    }

    private void initViews() {

    }

    @Override
    public void onReceivedMessage(String msg) {

    }
}
