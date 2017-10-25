# BlueToothAdapter
### 一个简单的蓝牙框架
#### 功能：
1. 打开，关闭蓝牙设备，开启可见性
2. 搜索周围蓝牙设备，并进行配对
3. 通过蓝牙进行聊天
一、打开，关闭蓝牙设备，开启可见性
使用之前需要动态申请权限：

在 onCreate();
在每次打开蓝牙开关的时候进行验证。
代码如下;
```
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
    // Android M Permission check
    if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
    }
}
```
#### 初始化需要的类
```
private void initData() {
//搜索蓝牙所用到的类
    btSearchAdapter = new CXPBluetoothSearchAdapter(MainActivity.this);
//蓝牙配对所用到的类
    btPairAdapter = new CXPBluetoothPairAdapter();
//蓝牙通信所用到的类
    btCommAdapter = new CXPBluetoothCommAdapter();
}
```
#### 搜索周围的蓝牙设备：
```
btSearchAdapter.startDiscovery(new BTSearchCallback() {
    @Override
    public List<MyBlueToothDevice> onActionFound(List<MyBlueToothDevice> devices) {
   //在这儿直接返回当前扫描已经发现的所有设备
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
```
#### 与周围蓝牙进行配对：
```
btPairAdapter.createBond(mDevices.get(i).getBtDevice());
```
一般情况下这样做：
```
 if(mDevices.get(i).getBtDevice().getBondState()== BluetoothDevice.BOND_BONDED){
//如果已经配对，则在这儿做一些处理
 }else{
     //如果没有配对，则进行配对
     btPairAdapter.createBond(mDevices.get(i).getBtDevice());
 }
```
//与蓝牙进行连接，为通信做准备
这一过程分为 开启监听和进行连接两个过程。
```
startSocketServer(i);
startConnToServer(i);
```
```
private void startSocketServer(int i) {
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
```
上边代码中的callback 是个回调,如果连接成功就会有回调信息。
```
class MyBTCommCallback implements BTCommCallback{
    @Override
    public void onConnectSucceed(BluetoothSocket socket) {
        //Toast.makeText(MainActivity.this,"连接成功...",Toast.LENGTH_SHORT).show();
   //如果连接成功就会回调这个方法，获取到我们需要的 socket
        startListening(socket);//开启监听连接蓝牙设备给我们发的消息
        startWriteTest();//这是为了测试写数据而写，不用在意
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
```
下边就是startListening() 方法。
```
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
```
暂时还没有考虑一些细节问题，后续继续优化==。==