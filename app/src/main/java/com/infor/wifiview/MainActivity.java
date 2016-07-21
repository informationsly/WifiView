package com.infor.wifiview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final WifiView wifiView = (WifiView) findViewById(R.id.wifi_view);
        wifiView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(wifiView.animIsStart()){
                    wifiView.stopAnimation();
                }else{
                    wifiView.startAnimation();
                }
            }
        });
    }
}
