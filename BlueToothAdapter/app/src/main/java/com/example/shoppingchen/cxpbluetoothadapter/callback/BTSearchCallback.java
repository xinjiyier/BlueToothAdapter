package com.example.shoppingchen.cxpbluetoothadapter.callback;

import com.example.shoppingchen.cxpbluetoothadapter.utils.MyBlueToothDevice;

import java.util.List;

/**
 * Created by ShoppingChen on 2017/10/16.
 */

public interface BTSearchCallback {
    public List<MyBlueToothDevice> onActionFound(List<MyBlueToothDevice> devices);
    public void onScanFinished();
    public void onError();
}
