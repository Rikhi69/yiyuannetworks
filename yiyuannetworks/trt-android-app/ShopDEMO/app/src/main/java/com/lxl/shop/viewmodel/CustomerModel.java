package com.lxl.shop.viewmodel;

import java.io.Serializable;

/**
 * Created by yanglei on 2018/11/23.
 */

public class CustomerModel implements Serializable {

    public String customerId;
    public String name;
    public String mobile;
    public String email;
    public String birthday;
    public int age;
    public String address;
    public String gender;//true为男

    public String faceId;
    public String imgUrl;
    public UserModel userModel = new UserModel();
}
