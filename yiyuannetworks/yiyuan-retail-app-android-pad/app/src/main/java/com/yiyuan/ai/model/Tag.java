package com.yiyuan.ai.model;

import java.io.Serializable;

/**
 * Created by wangyu on 2019/4/12.
 */

public class Tag implements Serializable{

    private Integer id;

    private String name;

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
