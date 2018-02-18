package com.zhangwenl1993163.chargehelper.view.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.zhangwenl1993163.chargehelper.R;
import com.zhangwenl1993163.chargehelper.dao.ProductDao;
import com.zhangwenl1993163.chargehelper.model.Product;

/**
 * Created by zhangwenliang on 2018/2/18.
 */

public class SettingFragment extends Fragment {
    private View view,updateProduct;
    private EditText modelNameET,ModelPriceET;
    private ListView container;
    private ProductDao productDao;

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

    private void init(){
        productDao = new ProductDao(getActivity());
        container = getView().findViewById(R.id.setting_container);
        String[] lists = new String[]{"工价设置","导出CVS"};
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
                    showMsg("导出CVS");
                    break;
                default:break;
            }
        }
    };

    private void showModelPriceSettings(){
//        String[] modelPriceSettings = new String[]{"修改工价","删除工价","新增工价"};
        String[] modelPriceSettings = new String[]{"修改工价","新增工价"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("工价设置");
        builder.setItems(modelPriceSettings, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case 0:
                        showModelPriceList(2);
                        break;
//                    case 1:
//                        showModelPriceList(1);
//                        break;
                    case 1:
                        addProduct();
                        break;
                    default:break;
                }
            }
        });
        builder.show();
    }

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
                    showMsg("型号不可以为空");
                    return;
                }else if (ModelPriceET.getText().toString() == null || ModelPriceET.getText().toString().equals("")){
                    showMsg("单价不可以为空");
                    return;
                }else {
                    Product product = new Product();
                    product.setModelName(modelNameET.getText().toString());
                    product.setModelPrice(Double.parseDouble(ModelPriceET.getText().toString()));
                    product.setAddTimeStamp(System.currentTimeMillis());
                    product.setModifyTimeStamp(System.currentTimeMillis());
                    productDao.insertProduct(product);
                    showMsg("添加成功");
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

    private void showMsg(String msg){
        Toast.makeText(getActivity(),msg,Toast.LENGTH_SHORT).show();
    }
}

