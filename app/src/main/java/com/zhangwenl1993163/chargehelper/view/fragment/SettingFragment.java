package com.zhangwenl1993163.chargehelper.view.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.zhangwenl1993163.chargehelper.R;
import com.zhangwenl1993163.chargehelper.dao.ChargeDao;
import com.zhangwenl1993163.chargehelper.dao.ProductDao;
import com.zhangwenl1993163.chargehelper.model.Product;
import com.zhangwenl1993163.chargehelper.model.Record;
import com.zhangwenl1993163.chargehelper.util.CSVUtil;
import com.zhangwenl1993163.chargehelper.util.CommonUtil;
import com.zhangwenl1993163.chargehelper.util.DateUtil;
import com.zhangwenl1993163.chargehelper.view.activity.CheckActivity;

import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by zhangwenliang on 2018/2/18.
 */

public class SettingFragment extends Fragment {
    private View view,updateProduct;
    private EditText modelNameET,ModelPriceET;
    private ListView container;
    private ProductDao productDao;
    private ChargeDao chargeDao;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.setting_panel,container,false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            CommonUtil.showMsg(data.getData().getPath());
        }
    }

    private void init(){
        productDao = new ProductDao(getActivity());
        chargeDao = new ChargeDao(getActivity());
        container = getView().findViewById(R.id.setting_container);
        String[] lists = new String[]{"工价设置","导出历史数据","对账"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,lists);
        container.setAdapter(adapter);
        container.setOnItemClickListener(listener);
    }

    private AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            switch (position){
                case 0:
                    showModelPriceSettings();
                    break;
                case 1:
                    showExportCSVDialog();
                    break;
                case 2:
                    Intent intent = new Intent(getActivity().getApplicationContext(), CheckActivity.class);
                    startActivity(intent);
                    break;
                default:break;
            }
        }
    };

    private void showModelPriceSettings(){
        String[] modelPriceSettings = new String[]{"修改工价","删除工价","新增工价"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("工价设置");
        builder.setItems(modelPriceSettings, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case 0:
                        showModelPriceList(2);
                        break;
                    case 1:
                        showModelPriceList(1);
                        break;
                    case 2:
                        addProduct();
                        break;
                    default:break;
                }
            }
        });
        builder.show();
    }

    /**
     * 新增工价
     * */
    private void addProduct(){
        updateProduct = getActivity().getLayoutInflater().inflate(R.layout.update_add_model_price,null);
        modelNameET = updateProduct.findViewById(R.id.add_update_model_name);
        ModelPriceET = updateProduct.findViewById(R.id.add_update_model_price);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("新增工价");
        builder.setView(updateProduct);
        builder.setNegativeButton("取消", null);
        builder.setPositiveButton("提交",null);
        final AlertDialog productDialog = builder.show();
        productDialog.getButton(Dialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (modelNameET.getText().toString() == null || modelNameET.getText().toString().equals("")){
                    CommonUtil.showMsg("型号不可以为空");
                    return;
                }else if (ModelPriceET.getText().toString() == null || ModelPriceET.getText().toString().equals("")){
                    CommonUtil.showMsg("单价不可以为空");
                    return;
                }else if(productDao.getProductByName(modelNameET.getText().toString()).size() != 0){
                    CommonUtil.showMsg("型号 " + modelNameET.getText().toString() + " 已存在");
                    return;
                }else {
                    Product product = new Product();
                    product.setModelName(modelNameET.getText().toString());
                    product.setModelPrice(Double.parseDouble(ModelPriceET.getText().toString()));
                    product.setAddTimeStamp(System.currentTimeMillis());
                    product.setModifyTimeStamp(System.currentTimeMillis());
                    productDao.insertProduct(product);
                    CommonUtil.showMsg("添加成功");
                    productDialog.dismiss();
                }
            }
        });
    }

    /**
     *@param type 1 为删除，2 为修改
     * */
    private void showModelPriceList(int type){
        ModelPriceDialogFragment fragment = new ModelPriceDialogFragment(type);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        fragment.show(transaction,"dialog");
    }

    private void showExportCSVDialog(){
        String[] items = new String[]{"当月数据","所有数据"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("导出数据");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                exportCSV(which);
            }
        });
        builder.show();
    }

    /**
     * @param type 0为导出当月 1 导出历史所有所有
     * */
    private void exportCSV(int type){
        List<Record> records = null;
        if (type == 0){
            records = chargeDao.getRecordInRange(DateUtil.getMonthRange(System.currentTimeMillis()));
        }else {
            records = chargeDao.getAllRecord();
        }
        List<String[]> data = new ArrayList<>();
        final String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/ChargeHelper/";
        if (!new File(path).exists()){
            new File(path).mkdir();
        }
        String fileName = null;
        if (type == 0) {
            fileName = new SimpleDateFormat("yyyy年MM月").format(System.currentTimeMillis())+
                    "数据"+System.currentTimeMillis()+".csv";
        }else {
            fileName = "历史所有数据"+System.currentTimeMillis()+".csv";
        }
        final String absPath = path + fileName;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String[] titles = new String[]{"流程卡号","型号","单价","合格个数","总金额","备注","添加日期","修改日期"};
        data.add(titles);

        for (Record r : records){
            BigDecimal totalMoney = new BigDecimal(r.getQulifiedNumber()*r.getModelPrice());
            String[] strs = new String[]{r.getProcessCardNumber()+"",r.getModelName(),
                    r.getModelPrice()+"",r.getQulifiedNumber()+"",totalMoney.setScale(2,BigDecimal.ROUND_HALF_UP)+"",
                    r.getComment(),format.format(new Date(r.getAddTimeStamp())),format.format(new Date(r.getModifyTimeStamp()))};
            data.add(strs);
        }

        boolean flag = CSVUtil.exportCSV(data,absPath);
        if (flag){
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("导出成功");
            builder.setMessage((type == 0 ? new SimpleDateFormat("yyyy年MM月").
                    format(System.currentTimeMillis()) : "历史所有") + "数据已经成功导出，文件路径：" + absPath);
            //直接打开所在文件夹
//            builder.setNegativeButton("取消",null);
//            builder.setPositiveButton("打开目录", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                    Uri uri = FileProvider.getUriForFile(getActivity(),
//                            "com.zhangwenl1993163.chargehelper.fileProvider",new File(absPath));
//                    intent.setDataAndType(uri,"*/*");
//                    intent.addCategory(Intent.CATEGORY_OPENABLE);
//                    startActivity(intent);
//                }
//            });
            builder.setPositiveButton("确定",null);
            builder.show();
        }
    }
}

