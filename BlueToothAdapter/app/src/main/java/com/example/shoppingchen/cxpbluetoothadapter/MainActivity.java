package com.example.shoppingchen.cxpbluetoothadapter;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shoppingchen.cxpbluetoothadapter.callback.BTCommCallback;
import com.example.shoppingchen.cxpbluetoothadapter.callback.BTSearchCallback;
import com.example.shoppingchen.cxpbluetoothadapter.communication.CXPBluetoothCommAdapter;
import com.example.shoppingchen.cxpbluetoothadapter.communication.MyBluetoothChat;
import com.example.shoppingchen.cxpbluetoothadapter.pair.CXPBluetoothPairAdapter;
import com.example.shoppingchen.cxpbluetoothadapter.search.CXPBluetoothSearchAdapter;
import com.example.shoppingchen.cxpbluetoothadapter.utils.BluetoothUtils;
import com.example.shoppingchen.cxpbluetoothadapter.utils.MyBlueToothDevice;

import java.util.List;

public class MainActivity extends Activity {

    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1111;
    private ListView lvShowBtDevices;
    private ListAdapter mAdapter;
    private CXPBluetoothSearchAdapter btSearchAdapter;
    private CXPBluetoothPairAdapter btPairAdapter;
    private CXPBluetoothCommAdapter btCommAdapter;
    private List<MyBlueToothDevice> mDevices;
    private MyBluetoothChat myChat;
    private MyBTCommCallback callback;

    private Handler handler =  new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case BluetoothUtils.CONNECTED_SUCCEED:
                    Toast.makeText(MainActivity.this,"连接成功...",Toast.LENGTH_SHORT).show();
                    break;
                case BluetoothUtils.SERVER_START_SUCCEED:
                    Toast.makeText(MainActivity.this,"Server启动成功...",Toast.LENGTH_SHORT).show();
                    break;
                case BluetoothUtils.CLIENT_START_SUCCEED:
                    Toast.makeText(MainActivity.this,"Client启动成功...",Toast.LENGTH_SHORT).show();
                    break;
                case BluetoothUtils.SHOW_RECEIVED_MESSAGE:
                    Toast.makeText(MainActivity.this,"获得数据 ： "+msg.getData().getString("msg"),Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    private void startListening(BluetoothSocket socket) {
        myChat = new MyBluetoothChat(socket,new MyBluetoothChat.ChatRoomCallback(){

            @Override
            public void onReceivedMessage(String msg) {
                Log.d("liuxiuting","获得数据 ： "+msg);
                Message m = new Message();
                Bundle data = new Bundle();
                data.putString("msg",msg);
                m.setData(data);
                m.what = BluetoothUtils.SHOW_RECEIVED_MESSAGE;
                handler.sendMessage(m);
            }
        });
        myChat.startListening();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android M Permission check
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
            }
        }
        initViews();
        initData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option_items, menu);
        //menu.findItem(R.id.menu_connect).setVisible(true).setTitle(getResources().getString(R.string.))
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_refresh:
                startDiscovery();
                return true;
            case R.id.menu_visiable:
                setBluetoothVisiable();
                return true;
            case R.id.menu_close:
                closeBluetooth();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void closeBluetooth() {
        btSearchAdapter.closeBluetooth();
    }

    private void setBluetoothVisiable() {
        btSearchAdapter.openBluetoothAndAvailable();
    }

    private void startDiscovery() {
        btSearchAdapter.startDiscovery(new BTSearchCallback() {
            @Override
            public List<MyBlueToothDevice> onActionFound(List<MyBlueToothDevice> devices) {
                mDevices = devices;
                mAdapter.notifyDataSetChanged();
                return null;
            }

            @Override
            public void onScanFinished() {

            }

            @Override
            public void onError() {

            }
        });
    }

    private void initData() {
        btSearchAdapter = new CXPBluetoothSearchAdapter(MainActivity.this);
        btPairAdapter = new CXPBluetoothPairAdapter();
        btCommAdapter = new CXPBluetoothCommAdapter();
        mAdapter = new ListAdapter();
        lvShowBtDevices.setAdapter(mAdapter);
    }

    private void initViews() {
        lvShowBtDevices = findViewById(R.id.lv_show_btdevices);
        lvShowBtDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(MainActivity.this,"该条目被点击了",Toast.LENGTH_SHORT).show();
                if(mDevices.get(i).getBtDevice().getBondState()== BluetoothDevice.BOND_BONDED){
                    //跳转到一个新的 界面中
                    if(callback == null){
                        callback = new MyBTCommCallback();
                    }

                    startListenToSocket(i);
                    startConnToServer(i);
/*                   Intent intent = new Intent();
                    intent.setClass(MainActivity.this,ChatRoomActivity.class);
                    startActivity(intent);*/
                }else{
                    btPairAdapter.createBond(mDevices.get(i).getBtDevice());
                }
            }
        });
    }

    private void startListenToSocket(int i) {
        btCommAdapter.startCommunicate(mDevices.get(i).getBtDevice(),callback);
    }

    private void startConnToServer(final int i) {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                btCommAdapter.connectToServer(mDevices.get(i).getBtDevice(),callback);
            }
        }, 3000);
    }

    class MyBTCommCallback implements BTCommCallback{

        @Override
        public void onConnectSucceed(BluetoothSocket socket) {
            //Toast.makeText(MainActivity.this,"连接成功...",Toast.LENGTH_SHORT).show();
            startListening(socket);
            startWriteTest();
            handler.sendEmptyMessage(BluetoothUtils.CONNECTED_SUCCEED);
        }

        @Override
        public void onServerStarted() {
            //Toast.makeText(MainActivity.this,"Server启动成功...",Toast.LENGTH_SHORT).show();
            handler.sendEmptyMessage(BluetoothUtils.SERVER_START_SUCCEED);
        }

        @Override
        public void onConnectStarted() {
            //Toast.makeText(MainActivity.this,"Client启动成功...",Toast.LENGTH_SHORT).show();
            handler.sendEmptyMessage(BluetoothUtils.CLIENT_CONNECTED);
        }

        @Override
        public void onConnectFailed() {

        }
    }

    private void startWriteTest() {
        new Thread(){
            @Override
            public void run() {
                super.run();
                for(int i = 0;i< 10;i++){
                    Log.d("liuxiuting","开始写入第"+(i+1)+"条数据");
                    String str = "aa"+i;
                    myChat.write(str.getBytes());
                    try {
                        sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    class ListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mDevices!=null?mDevices.size():0;
        }

        @Override
        public Object getItem(int i) {
            return mDevices!=null?mDevices.get(i):null;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHodler viewHodler;
            if(view == null){
                view = View.inflate(MainActivity.this,R.layout.bluetooth_item,null);
                viewHodler = new ViewHodler();
                viewHodler.tvBtName = view.findViewById(R.id.tv_bluetooth_name);
                viewHodler.tvBtAddress = view.findViewById(R.id.tv_bluetooth_address);
                //viewHodler.tvPairState = view.findViewById(R.id.tv_paired_state);
                view.setTag(viewHodler);
            }else{
                viewHodler = (ViewHodler) view.getTag();
            }
            viewHodler.tvBtName.setText(mDevices.get(i).getBtName());
            viewHodler.tvBtAddress.setText(mDevices.get(i).getBtDevice().getAddress());
            //viewHodler.tvPairState.setText(mDevices.get(i).getBtDevice().getBondState());
            return view;
        }
    }

    class ViewHodler{
        TextView tvBtName;
        TextView tvBtAddress;
        //TextView tvPairState;
    }
}
