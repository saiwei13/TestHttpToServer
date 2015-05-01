package com.example.chenwei.testsocket;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ApkReceiver extends BroadcastReceiver {

    private final String TAG = "chenwei.ApkReceiver";

    public ApkReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.

        Log.i(TAG,"onReceive()");

        if(intent != null){
            String action  = intent.getAction();
            String s = intent.getData().getSchemeSpecificPart();
            Log.i(TAG,"action="+action);
            Log.i(TAG,"package="+s);
//            if(context != null){
//                contex
//            }
        }

//        throw new UnsupportedOperationException("Not yet implemented");
    }
}
