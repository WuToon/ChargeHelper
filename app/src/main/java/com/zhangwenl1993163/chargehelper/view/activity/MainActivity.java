package com.zhangwenl1993163.chargehelper.view.activity;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.zhangwenl1993163.chargehelper.R;
import com.zhangwenl1993163.chargehelper.view.fragment.ChargeFragment;

public class MainActivity extends AppCompatActivity {
    private Fragment charge;
    private FragmentTransaction transaction;
    private View container;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_charge:
                    transaction = getFragmentManager().beginTransaction();
                    transaction.show(charge).commit();
                    return true;
                case R.id.navigation_history:
                    transaction = getFragmentManager().beginTransaction();
                    transaction.hide(charge).commit();
                    return true;
                case R.id.navigation_setting:
                    transaction = getFragmentManager().beginTransaction();
                    transaction.hide(charge).commit();
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
        container = findViewById(R.id.panel_container);
        //获取fragment
        charge = new ChargeFragment();

        transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.panel_container,charge);
        transaction.commit();
    }
}
