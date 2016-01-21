package com.feximin.neo.circleprogress;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final CircleProgress pro0 = (CircleProgress) findViewById(R.id.pro0);
        pro0.setImageRes(R.mipmap.ic_launcher);
        final CircleProgress pro1 = (CircleProgress) findViewById(R.id.pro1);
        final CircleProgress pro2 = (CircleProgress) findViewById(R.id.pro2);

        new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i = 0; i<= 100; i++){
                    final int finalI = i;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pro0.setProgress(finalI);
                            pro1.setProgress(finalI);
                            pro2.setProgress(finalI);
                        }
                    });
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }
}
