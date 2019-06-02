package com.lxl.shop.viewmodel;

import android.graphics.Bitmap;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by yanglei on 2018/11/24.
 */

public class UserModel implements Serializable {
    public long id;
    public String name;
    public String userId;
    public String localImagePath;
    public String urlImagePath;
    public float compareScore;
    public ArrayList<Float> features;
    @JSONField(serialize = false)
    public Bitmap headImage;
    public boolean serverSync;
}
