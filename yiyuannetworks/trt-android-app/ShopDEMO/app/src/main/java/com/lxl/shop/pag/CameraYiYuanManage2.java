package com.lxl.shop.pag;

import com.example.administrator.myapplication.Dllipcsdk;

import java.io.File;
import java.util.UUID;

/**
 * @Author wangyu
 * @Description: Copyright yiYuan Networks 上海义援网络科技有限公司. All rights reserved.
 * @Date 2019/1/4
 */
public class CameraYiYuanManage2 implements Dllipcsdk.CBJpgData{

    private String filePath;
    private YiYuanManageCallBack2 yiYuanManageCallBack;
    private static CameraYiYuanManage2 cameraYiYuanManage;
    private long lJpgData = -1;

    public static synchronized CameraYiYuanManage2 getInstance(){
        if (cameraYiYuanManage == null) {
            cameraYiYuanManage = new CameraYiYuanManage2();
        }
        return cameraYiYuanManage;
    }


    public  void initCameraData(String ip,Integer port,String userName,String password,String filePath,YiYuanManageCallBack2 yiYuanManageCallBack){
        lJpgData = Dllipcsdk.IPCNET_StartJpgData(ip,port,userName, password, this);
        this.filePath = filePath;
        this.yiYuanManageCallBack = yiYuanManageCallBack;
    }

    public void initCameraData(String ip,Integer port,String userName,String password,String filePath){
        initCameraData(ip,port,userName,password,filePath,null);
    }

    public void initCameraData(String ip,Integer port,String userName,String password,YiYuanManageCallBack2 yiYuanManageCallBack){
        initCameraData(ip,port,userName,password,null,yiYuanManageCallBack);
    }


    @Override
    public void JpgData(int lJpgHandle, int nErrorType, int nErrorCode, byte[] pJpgBuffer, int lJpgBufSize) {

        if(filePath != null && !"".equals(filePath.trim())) {
            //生成缓存图片，放在filepath文件夹下面
            String s = YiYuanUtil.getCurrentTime() + UUID.randomUUID().toString();
            filePath = filePath + File.separator + s + ".jpg";
            IOHelper.byte2image(pJpgBuffer, filePath);
        }
        yiYuanManageCallBack.getJpgData(lJpgHandle,nErrorType,nErrorCode,pJpgBuffer,lJpgBufSize);
    }

    public void destoryCameraData() {
        if (lJpgData != -1) {
            Dllipcsdk.IPCNET_StopJpgData(lJpgData);
            lJpgData = -1;
        }
    }

}
