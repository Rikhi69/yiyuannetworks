package com.lxl.shop.viewmodel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yanglei on 2018/11/24.
 */

public class CustomerShoppingHistory {
    public String customerName;
    public String customerId;
    public List<CustomerShoppingRecord> salesRecordVOS = new ArrayList<>();

    public int getSumPirce() {
        if (salesRecordVOS.size() == 0) {
            return 0;
        }
        int sum = 0;
        for (CustomerShoppingRecord record : salesRecordVOS) {
            sum += record.getSumPrice();
        }
        return sum;
    }
}
