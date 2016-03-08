package com.yourcompany.yoursetting.receiver;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.yourcompany.yoursetting.Constants;
import com.yourcompany.yoursetting.ui.InfoActivity;

import java.util.List;

import static com.yourcompany.yoursetting.receiver.MyIntentService.*;
import static com.yourcompany.yoursetting.util.isServiceWork;

public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(Constants.LOG_TAG,"收到锁屏或启动信号");
        //Intent i = new Intent(context, InfoActivity.class);
        //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //context.startActivity(i);
        if (!isServiceWork(context,"com.yourcompany.yoursetting.receiver.MyIntentService")){
            startActionFoo(context, "111111", "2222222");}
}

}
