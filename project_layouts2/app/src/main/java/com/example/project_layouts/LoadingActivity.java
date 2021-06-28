package com.example.project_layouts;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

public class LoadingActivity extends  BaseActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.progressbar_layout);
        LoadAPI loadAPI = new LoadAPI();
        new Thread(loadAPI).start();
    }


    private class LoadAPI implements Runnable {


        @Override
        public void run() {
            try {
                Thread.sleep(2000);
            }
            catch (Exception e)
            {

            }

            Intent intent = new Intent(LoadingActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }


}


