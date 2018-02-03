package com.zhangwenl1993163.chargehelper.view.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.zhangwenl1993163.chargehelper.R;

public class MainActivity extends AppCompatActivity {
    private ViewGroup panelContainer;
    private View chargePanel;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_charge:
//                    panelContainer.addView(chargePanel);
                    return true;
                case R.id.navigation_history:
                    panelContainer.removeAllViews();
                    return true;
                case R.id.navigation_setting:
                    panelContainer.removeAllViews();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init(){
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        panelContainer = findViewById(R.id.panel_container);
//        chargePanel = LayoutInflater.from(this).inflate(R.layout.charge_panel,panelContainer,false);
    }
}
