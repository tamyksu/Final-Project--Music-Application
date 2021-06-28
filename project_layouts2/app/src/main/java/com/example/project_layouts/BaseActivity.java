package com.example.project_layouts;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {
    static Intent intent = null;
    static ClassificationService classService = null;
    static boolean mBound = false;
    static ServiceConnection connection = null;


    protected void startService(){
        if(intent != null){
            Toast.makeText(this, "cannot start! service is already running!", Toast.LENGTH_SHORT).show();
            return;
        }
        intent = new Intent(this, ClassificationService.class);

        startForegroundService(intent);
        Toast.makeText(this, "service started", Toast.LENGTH_SHORT).show();
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onResume() {
        if(! isMyServiceRunning(ClassificationService.class))
        {
            startService();
        }
        bindService();
        super.onResume();


    }

    protected void bindService(){
        if(mBound == true){
            Toast.makeText(this, "cannot bind! service is already bound!", Toast.LENGTH_SHORT).show();
            return;
        }
        if(intent == null){
            Toast.makeText(this, "cannot bind! service is not running!", Toast.LENGTH_SHORT).show();
            return;
        }

        connection = new ServiceConnection() {

            @Override
            public void onServiceConnected(ComponentName className,
                                           IBinder service) {
                // We've bound to LocalService, cast the IBinder and get LocalService instance
                ClassificationService.LocalBinder binder = (ClassificationService.LocalBinder) service;
                classService = binder.getService();
                mBound = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName arg0) {
                mBound = false;
            }
        };


        boolean temp = bindService(intent, connection, Context.BIND_AUTO_CREATE);
        temp = false;
        Toast.makeText(this, "binding succeeded", Toast.LENGTH_SHORT).show();
    }


    protected void stopService(){
        if(connection == null){
            Toast.makeText(this, "cannot stop! service is not connected!", Toast.LENGTH_SHORT).show();
            return;
        }
        unbindService(connection);

        classService = null;
        mBound = false;
        connection = null;
    }

    protected void classifySong(String filePath){
        if(classService == null){
            Toast.makeText(this, "cannot classify! service is not bound!", Toast.LENGTH_SHORT).show();
            return;
        }
        classService.sendSongToClassify(filePath);
        Toast.makeText(this, "classification succeeded", Toast.LENGTH_SHORT).show();

    }

    protected String [] getSongsURLsArray(){

        return classService.getSongsURLsArray();
    }

    protected String [] getSongsAlbumsNamesArray(){

        return classService.getSongsAlbumsNamesArray();
    }
}
