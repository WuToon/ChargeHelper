package com.zhangwenl1993163.chargehelper.view.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.zhangwenl1993163.chargehelper.R;
import com.zhangwenl1993163.chargehelper.dao.ProductDao;
import com.zhangwenl1993163.chargehelper.model.Product;
import com.zhangwenl1993163.chargehelper.util.CommonUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangwenliang on 2018/2/18.
 */

public class ModelPriceDialogFragment extends DialogFragment {
    private View view,updateProduct;
    private EditText modelNameET,ModelPriceET;
    private Dialog dialog;
    // 1 为删除，2 为修改
    private int operateType;
    private ListView container;
    private ProductDao dao;
    private List<Product> products;
    private List<Map<String,Object>> maps;
    private SimpleAdapter adapter;

    @SuppressLint({"NewApi","ValidFragment"})
    public ModelPriceDialogFragment(int operateType){
        this.operateType = operateType;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if (view == null)
            view = inflater.inflate(R.layout.model_price_dialog,container,false);
        return view;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (view == null)
            view = getActivity().getLayoutInflater().inflate(R.layout.model_price_dialog,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        if (operateType == 1){
            builder.setTitle("选中删除");
        }else{
            builder.setTitle("选中修改");
        }
        builder.setNegativeButton("取消",null);
        dialog = builder.create();
        return dialog;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        dao = new ProductDao(getActivity());
        container = view.findViewById(R.id.model_price_container);
        products = dao.getAllProduct();
        maps = new ArrayList<>();
        //保存成map形式
        for (Product product : products){
            Map<String,Object> map = new HashMap<>();
            map.put("id",product.getId());
            map.put("name",product.getModelName());
            map.put("price",product.getModelPrice() + " 元");
            map.put("addTime",product.getAddTimeStamp());
            map.put("modifyTime",product.getModifyTimeStamp());
            maps.add(map);
        }
        adapter = new SimpleAdapter(getActivity(),maps,
                R.layout.product_list_item,new String[]{"name","price"},
                new int[]{R.id.product_list_model_name,R.id.product_list_model_price});
        container.setAdapter(adapter);
        container.setOnItemClickListener(listener);
    }

    private AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (operateType == 1){
                deleteProduct(position);
            }else {
                updateProduct(position);
            }
        }
    };

    private void deleteProduct(final int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("确认删除");
        builder.setMessage("是否删除 " + products.get(position).getModelName() + " ?");
        builder.setNegativeButton("取消",null);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dao.deleteProductById(products.get(position).getId());
                products.remove(position);
                maps.remove(position);
                adapter.notifyDataSetChanged();
            }
        });
        builder.show();
    }

    private void updateProduct(final int position){
        final Product product = products.get(position);
        updateProduct = getActivity().getLayoutInflater().inflate(R.layout.update_add_model_price,null);
        modelNameET = updateProduct.findViewById(R.id.add_update_model_name);
        modelNameET.setText(product.getModelName());
        modelNameET.setEnabled(false);
        ModelPriceET = updateProduct.findViewById(R.id.add_update_model_price);
        ModelPriceET.setText(String.valueOf(product.getModelPrice()));

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("修改型号单价");
        builder.setView(updateProduct);
        builder.setNegativeButton("取消", null);
        builder.setPositiveButton("提交",null);
        final AlertDialog productDialog = builder.show();
        productDialog.getButton(Dialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ModelPriceET.getText().toString() == null || ModelPriceET.getText().toString().equals("")){
                    CommonUtil.showMsgLong("价格不可以为空");
                    return;
                }else {
                    product.setModelPrice(Double.parseDouble(ModelPriceET.getText().toString()));
                    product.setModifyTimeStamp(System.currentTimeMillis());
                    dao.updateProduct(product);
                    maps.get(position).put("price",product.getModelPrice() + " 元");
                    products.set(position,product);
                    adapter.notifyDataSetChanged();
                    CommonUtil.showMsgShort("修改成功");
                    productDialog.dismiss();
                }
            }
        });
    }
}
