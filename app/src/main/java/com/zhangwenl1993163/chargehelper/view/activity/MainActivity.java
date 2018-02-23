package com.zhangwenl1993163.chargehelper.view.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.zhangwenl1993163.chargehelper.R;
import com.zhangwenl1993163.chargehelper.util.CommonUtil;
import com.zhangwenl1993163.chargehelper.view.fragment.AnalyzeFragment;
import com.zhangwenl1993163.chargehelper.view.fragment.ChargeFragment;
import com.zhangwenl1993163.chargehelper.view.fragment.HistoryFragment;
import com.zhangwenl1993163.chargehelper.view.fragment.SettingFragment;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private Fragment charge,history,setting,analyze;
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
                    transaction.show(charge).hide(history).hide(setting).hide(analyze).commit();
                    return true;
                case R.id.navigation_history:
                    transaction = getFragmentManager().beginTransaction();
                    transaction.show(history).hide(charge).hide(setting).hide(analyze).commit();
                    return true;
                case R.id.navigation_analyze:
                    transaction = getFragmentManager().beginTransaction();
                    transaction.show(analyze).hide(history).hide(charge).hide(setting).commit();
                    return true;
                case R.id.navigation_setting:
                    transaction = getFragmentManager().beginTransaction();
                    transaction.show(setting).hide(charge).hide(history).hide(analyze).commit();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //检查权限
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("提示");
            builder.setMessage("导出记录需要文件读写权限，请授予权限，否则无法使用！");
            builder.setNegativeButton("授予权限", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.READ_EXTERNAL_STORAGE},1);
                }
            });
            builder.show();
        }
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
        analyze = new AnalyzeFragment();
        setting = new SettingFragment();

        transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.panel_container,charge);
        transaction.add(R.id.panel_container,history);
        transaction.add(R.id.panel_container,setting);
        transaction.add(R.id.panel_container,analyze);
        transaction.hide(history);
        transaction.hide(analyze);
        transaction.hide(setting);
        transaction.commit();
    }
}
