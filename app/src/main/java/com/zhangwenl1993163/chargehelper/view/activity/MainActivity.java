package com.zhangwenl1993163.chargehelper.view.activity;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.zhangwenl1993163.chargehelper.R;
import com.zhangwenl1993163.chargehelper.view.fragment.ChargeFragment;
import com.zhangwenl1993163.chargehelper.view.fragment.HistoryFragment;
import com.zhangwenl1993163.chargehelper.view.fragment.SettingFragment;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private Fragment charge,history,setting;
    private FragmentTransaction transaction;
    private View under;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            InputMethodManager manager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(under.getWindowToken(),0);
            switch (item.getItemId()) {
                case R.id.navigation_charge:
                    transaction = getFragmentManager().beginTransaction();
                    transaction.show(charge).hide(history).hide(setting).commit();
                    return true;
                case R.id.navigation_history:
                    transaction = getFragmentManager().beginTransaction();
                    transaction.show(history).hide(charge).hide(setting).commit();
                    return true;
                case R.id.navigation_setting:
                    transaction = getFragmentManager().beginTransaction();
                    transaction.show(setting).hide(charge).hide(history).commit();
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.container:
                InputMethodManager manager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                manager.hideSoftInputFromWindow(v.getWindowToken(),0);
                break;
            default:
                break;
        }
    }

    private void init(){
        findViewById(R.id.container).setOnClickListener(this);
        under = findViewById(R.id.under_navigation);
        final BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        //动态设置空白view的高度和底部导航栏一样，避免内容被覆盖
        navigation.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                navigation.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                ViewGroup.LayoutParams params = under.getLayoutParams();
                params.height = navigation.getHeight();
                under.setLayoutParams(params);
            }
        });

        //获取fragment
        charge = new ChargeFragment();
        history = new HistoryFragment();
        setting = new SettingFragment();

        transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.panel_container,charge);
        transaction.add(R.id.panel_container,history);
        transaction.add(R.id.panel_container,setting);
        transaction.hide(history);
        transaction.hide(setting);
        transaction.commit();
    }
}
