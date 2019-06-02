package com.lxl.mobile.page;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ListView;
import android.widget.TextView;

import com.lxl.shop.R;
import com.lxl.shop.adapter.CustomerShoppingAdapter;
import com.lxl.shop.common.ShopConstants;
import com.lxl.shop.sender.StockSender;
import com.lxl.shop.viewmodel.CustomerShoppingHistory;

/**
 * Created by yanglei on 2018/11/24.
 */

public class CustomerShoppingHistoryPage extends PageInitActivity {

    TextView mPriceText;
    ListView mShoppingHistoryList;
    CustomerShoppingAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shop_page_customer_shopping_history);
        initView();
        initAction();
        initData();
    }

    @Override
    protected void initView() {
        mPriceText = findViewById(R.id.sum_price_text);
        mShoppingHistoryList = findViewById(R.id.shopping_history_list);
    }

    @Override
    protected void initAction() {

    }

    @Override
    protected void initData() {
        final String customerId = getIntent().getExtras().getString(ShopConstants.CUSTOMERID);
        mAdapter = new CustomerShoppingAdapter(this);

        new Thread(new Runnable() {
            @Override
            public void run() {
                final CustomerShoppingHistory shoppingHistory = StockSender.getInstance().sendGetShoppingService(customerId);
                mHander.post(new Runnable() {
                    @Override
                    public void run() {
                        refreshView(shoppingHistory);
                    }
                });

            }
        }).start();

    }

    @Override
    protected void initListeners() {

    }

    private void refreshView(CustomerShoppingHistory shoppingHistory) {
        mAdapter.setShoppingHistory(shoppingHistory.salesRecordVOS);
        mShoppingHistoryList.setAdapter(mAdapter);
        mShoppingHistoryList.setDivider(getResources().getDrawable(R.drawable.stock_big_divider));
        mAdapter.notifyDataSetChanged();
        mPriceText.setText("¥" + shoppingHistory.getSumPirce());
    }


}
