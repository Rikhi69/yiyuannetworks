package com.lxl.shop.viewmodel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yanglei on 2018/11/24.
 */

public class CustomerShoppingRecord {
    public String createTime;
    public String remark;
    public List<CustomerShoppingItemModel> salesDetailVOS = new ArrayList<>();

    public int getSumPrice() {
        if (salesDetailVOS.size() == 0) {
            return 0;
        }
        int sum = 0;
        for (CustomerShoppingItemModel itemModel : salesDetailVOS) {
            sum += (itemModel.quantity * itemModel.product.price);
        }
        return sum;
    }
}
