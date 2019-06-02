package com.lxl.mobile.page;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.aiwinn.base.util.ToastUtils;
import com.aiwinn.facedetectsdk.FaceDetectManager;
import com.aiwinn.facedetectsdk.bean.UserBean;
import com.bumptech.glide.Glide;
import com.lxl.shop.R;
import com.lxl.shop.common.ShopConstants;
import com.lxl.shop.sender.StockSender;
import com.lxl.shop.utils.LoadingDialog;
import com.lxl.shop.utils.StockUtil;
import com.lxl.shop.utils.StringUtil;
import com.lxl.shop.viewmodel.CustomerModel;
import com.lxl.shop.widget.StockTitleView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by yanglei on 2018/12/1.
 */

public class CustomerRegisterListPage extends PageInitActivity implements AdapterView.OnItemClickListener {

    ListView registerListview;
    List<CustomerModel> customerModelList = new ArrayList<>();
    private LayoutInflater mInflater;
    private StockTitleView page_title;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            List<CustomerModel> customerModels = (List<CustomerModel>) data.getSerializable("data");
            //进行检查，如果customerId重复，则删除
            final SharedPreferences idSp = getSharedPreferences(ShopConstants.SP_Register_CustomerId, 0);
            final SharedPreferences nameSp = getSharedPreferences(ShopConstants.SP_Register_CustomerName, 0);
            final SharedPreferences genderSp = getSharedPreferences(ShopConstants.SP_Register_CustomerGender, 0);
            Iterator<CustomerModel> iterator = customerModels.iterator();
            Map<String, CustomerModel> map = new HashMap<>();
            while (iterator.hasNext()) {
                CustomerModel next = iterator.next();
                CustomerModel customerModel = map.get(next.customerId);
                if (customerModel == null) {
                    map.put(next.customerId, next);
                    customerModelList.add(next);
                } else {
                    idSp.edit().remove(next.userModel.userId).apply();
                    nameSp.edit().remove(next.userModel.userId).apply();
//                FaceDetectManager.deleteByUserInfo(StockUtil.userModel2userBean(next.userModel));
                    iterator.remove();
                }
            }
            MyAdapter adapter = new MyAdapter();
            registerListview.setAdapter(adapter);
            registerListview.setDivider(getResources().getDrawable(R.drawable.shop_gray_divider));
            adapter.notifyDataSetChanged();
            page_title.setTitle("会员列表（"+customerModels.size()+"）");
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInflater = (LayoutInflater)
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        setContentView(R.layout.shop_page_register_list);
        page_title = findViewById(R.id.page_title);
        initView();
        initAction();
        initData();
        initListeners();
    }

    @Override
    protected void initView() {
        registerListview = findViewById(R.id.register_listview);
    }

    @Override
    protected void initAction() {

    }

    @Override
    protected void initData() {
//        List<UserBean> userBeanList = FaceDetectManager.queryAll();
//        if (userBeanList == null) {
//            return;
//        }
//        List<UserBean> userBeans = new ArrayList<>();
//        userBeans.addAll(userBeanList);
//        final SharedPreferences idSp = getSharedPreferences(ShopConstants.SP_Register_CustomerId, 0);
//        final SharedPreferences nameSp = getSharedPreferences(ShopConstants.SP_Register_CustomerName, 0);
//        final SharedPreferences genderSp = getSharedPreferences(ShopConstants.SP_Register_CustomerGender, 0);
//        List<CustomerModel> customerModels = new ArrayList<>();
//        for (UserBean userBean : userBeans) {
//            CustomerModel customerModel = new CustomerModel();
//            String userId = userBean.userId;
//            String customerId = idSp.getString(userId, "");
//            String customerName = nameSp.getString(userId, "");
//            String customerGender = genderSp.getString(userId, "");
//            if (StringUtil.emptyOrNull(customerId)) {
//                //删除该用户
//                idSp.edit().remove(customerId).apply();
//                nameSp.edit().remove(customerId).apply();
//                genderSp.edit().remove(customerId).apply();
//                FaceDetectManager.deleteByUserInfo(userBean);
//                continue;
//            }
//            customerModel.customerId = customerId;
//            customerModel.name = customerName;
//            customerModel.gender = customerGender;
//            customerModel.userModel = StockUtil.userBean2UserModel(userBean);
//            customerModels.add(customerModel);
//        }
        //使用数据库的同步数据
        final Activity activity = this;
        LoadingDialog.getInstance(activity).show();
        new Thread(){
            @Override
            public void run()
            {
                List<CustomerModel> customerModels = new ArrayList<>();
                try {
                    customerModels = StockSender.getInstance().sendSyncCustomerService(1,1000,activity);
                    if(customerModels == null || customerModels.size() == 0){
                        ToastUtils.showLong("暂无会员数据");
                    }else{
                        Message msg = new Message();
                        Bundle data = new Bundle();
                        data.putSerializable("data", (Serializable) customerModels);
                        msg.setData(data);
                        handler.sendMessage(msg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                        LoadingDialog.getInstance().dismiss();
                        }
                    });
                }
            }
        }.start();

    }

    @Override
    protected void initListeners() {
        registerListview.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        CustomerModel customerModel = customerModelList.get(i);
        Intent intent = new Intent();
        intent.setClass(this, CustomerMainPage.class);
        intent.putExtra(ShopConstants.CUSTOMERID, customerModel.customerId);
        startActivity(intent);

    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return customerModelList.size();
        }

        @Override
        public CustomerModel getItem(int i) {
            CustomerModel customerModel = customerModelList.get(i);
            return customerModel;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.shop_page_register_item, parent, false);
            }
            CustomerModel item = getItem(i);
            bindData(convertView, item,i);
            return convertView;
        }

        private void bindData(View convertView, CustomerModel item,int i) {
            ImageView imageView = convertView.findViewById(R.id.customer_icon);
            TextView customerId = convertView.findViewById(R.id.customer_id);
//            TextView faceId = convertView.findViewById(R.id.face_id);
            TextView customerName = convertView.findViewById(R.id.customer_name);
            TextView numberTextView = convertView.findViewById(R.id.number);
            Glide.with(getBaseContext()).load(item.imgUrl).into(imageView);
            customerId.setText("会员编号："+item.customerId);
            numberTextView.setText((i+1)+"");
//            faceId.setText("faceId:" + item.userModel.userId);
            customerName.setText(item.name);

        }
    }

}
