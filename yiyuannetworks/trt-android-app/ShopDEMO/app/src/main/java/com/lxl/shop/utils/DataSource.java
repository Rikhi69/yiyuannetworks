package com.lxl.shop.utils;

import com.alibaba.fastjson.JSON;
import com.lxl.shop.AttApp;
import com.lxl.shop.viewmodel.CustomerModel;
import com.lxl.shop.viewmodel.CustomerRecentModel;
import com.lxl.shop.viewmodel.CustomerShoppingHistory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yanglei on 2018/11/24.
 */

public class DataSource {


    public static CustomerModel getCustomerModel() {
        CustomerModel customerModel = new CustomerModel();
        customerModel.customerId = "SZ10003";
        customerModel.address = "黄浦区XXX路";
        customerModel.birthday = "1991-05-11";
        customerModel.email = "exmmm@qq.com";
        customerModel.name = "King";
//        customerModel.faceId = "123456";
        customerModel.mobile = "134xxxx1234";
        return customerModel;
    }

    public static CustomerShoppingHistory getCustomerShoppingHistory() {
        try {
            InputStream goodsRecord = AttApp.getContext().getAssets().open("goods_record");
            String s = IOHelper.fromIputStreamToString(goodsRecord, "utf-8");
            CustomerShoppingHistory customerShoppingHistory = JSON.parseObject(s, CustomerShoppingHistory.class);
            return customerShoppingHistory;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new CustomerShoppingHistory();
    }

    static int count = 0;

    public static List<CustomerRecentModel> getRecentCusomterList() {
        List<CustomerRecentModel> customerModels = new ArrayList<>();
        if ((count++ % 1000) == 500) {
            CustomerRecentModel recentModel = new CustomerRecentModel();
            CustomerModel model = new CustomerModel();
            model.customerId = "SZ100004";
            model.mobile = "156****1111";
            model.birthday = "1989-11-11";
//            model.gender = true;
            recentModel.customerVO = model;
            customerModels.add(recentModel);
        }
        return customerModels;
    }
}
