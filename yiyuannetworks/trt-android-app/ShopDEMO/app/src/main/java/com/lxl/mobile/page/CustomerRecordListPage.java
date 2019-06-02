package com.lxl.mobile.page;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
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
import com.bumptech.glide.Glide;
import com.lxl.shop.AttApp;
import com.lxl.shop.R;
import com.lxl.shop.common.ShopConstants;
import com.lxl.shop.sender.StockSender;
import com.lxl.shop.utils.LoadingDialog;
import com.lxl.shop.utils.StringUtil;
import com.lxl.shop.viewmodel.CustomerModel;
import com.lxl.shop.viewmodel.CustomerRecentModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yanglei on 2018/12/1.
 */

public class CustomerRecordListPage extends PageInitActivity implements AdapterView.OnItemClickListener {

    ListView registerListview;
    List<CustomerRecentModel> customerModelList = new ArrayList<>();
    private LayoutInflater mInflater;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInflater = (LayoutInflater)
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        setContentView(R.layout.shop_page_record_list);
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
        final MyAdapter adapter = new MyAdapter();
        registerListview.setAdapter(adapter);
        registerListview.setDivider(getResources().getDrawable(R.drawable.shop_gray_divider));
        adapter.notifyDataSetChanged();
        final SharedPreferences idSp = getSharedPreferences(ShopConstants.SP_Register_CustomerId, 0);
        final Activity activity = this;
        LoadingDialog.getInstance(activity).show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    List<CustomerRecentModel> customerRecentModels = StockSender.getInstance().sendSelectRecentRecordCustomerService(activity);

                    for (CustomerRecentModel customerRecentModel : customerRecentModels) {
                        CustomerModel customerVO = customerRecentModel.customerVO;
                        if (StringUtil.emptyOrNull(customerVO.customerId)) {
                            continue;
                        }
                        customerModelList.add(customerRecentModel);
                    }
                    if (customerModelList.size() == 0) {
                        ToastUtils.showLong("暂无会员进店记录");
                    }
                    mHander.post(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });
                }catch (Exception e){
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
        }).start();

    }

    @Override
    protected void initListeners() {
        registerListview.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        CustomerRecentModel customerRecentModel = customerModelList.get(i);
        String customerId = customerRecentModel.customerVO.customerId;
        Intent intent = new Intent();
        intent.setClass(this, CustomerMainPage.class);
        intent.putExtra(ShopConstants.CUSTOMERID, customerId);
        startActivity(intent);
    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return customerModelList.size();
        }

        @Override
        public CustomerRecentModel getItem(int i) {
            CustomerRecentModel customerModel = customerModelList.get(i);
            return customerModel;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.shop_page_record_item, parent, false);
            }
            CustomerRecentModel item = getItem(i);
            bindData(convertView, item);
            return convertView;
        }

        private void bindData(View convertView, CustomerRecentModel model) {
            ImageView imageView = convertView.findViewById(R.id.customer_icon);
            TextView recordText = convertView.findViewById(R.id.record_text);
            TextView customerName = convertView.findViewById(R.id.customer_name);
            CustomerModel item = model.customerVO;
            Glide.with(getBaseContext()).load(item.imgUrl).into(imageView);
            customerName.setText(item.name);
            recordText.setText(model.createTime);
        }
    }

}
