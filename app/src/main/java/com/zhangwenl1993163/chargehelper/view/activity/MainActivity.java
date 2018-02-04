package com.zhangwenl1993163.chargehelper.view.activity;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.ViewGroup;

import com.zhangwenl1993163.chargehelper.R;
import com.zhangwenl1993163.chargehelper.view.fragment.ChargeFragment;

public class MainActivity extends AppCompatActivity {
    private ViewGroup panelContainer;
    private Fragment charge;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_charge:
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.panel_container,charge);
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    transaction.commit();
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
        charge = new ChargeFragment();
    }
}
