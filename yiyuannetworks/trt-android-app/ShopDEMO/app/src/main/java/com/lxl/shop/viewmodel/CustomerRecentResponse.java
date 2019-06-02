package com.lxl.shop.viewmodel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yanglei on 2018/11/23.
 */

public class CustomerRecentResponse implements Serializable {
    public List<CustomerRecentModel> identifyRecords = new ArrayList<>();
}
