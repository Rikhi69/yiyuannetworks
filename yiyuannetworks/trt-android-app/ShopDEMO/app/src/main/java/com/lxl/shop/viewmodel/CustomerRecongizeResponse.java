package com.lxl.shop.viewmodel;

import java.io.Serializable;

/**
 * Created by yanglei on 2018/11/23.
 */

public class CustomerRecongizeResponse implements Serializable {

    public int result = 0;
    public CustomerModel customer = new CustomerModel();
    public String createTime = "";
}
