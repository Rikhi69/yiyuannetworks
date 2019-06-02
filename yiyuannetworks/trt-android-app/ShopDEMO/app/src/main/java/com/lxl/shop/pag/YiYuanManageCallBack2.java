package com.lxl.shop.pag;

/**
 * @Author wangyu
 * @Description: Copyright yiYuan Networks 上海义援网络科技有限公司. All rights reserved.
 * @Date 2019/1/4
 */
public interface YiYuanManageCallBack2 {
    /*
     * 响应回调函数
     */
    public void getJpgData(int lJpgHandle, int nErrorType, int nErrorCode, byte[] pJpgBuffer, int lJpgBufSize);
}