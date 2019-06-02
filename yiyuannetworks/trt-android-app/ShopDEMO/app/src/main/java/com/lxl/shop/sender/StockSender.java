package com.lxl.shop.sender;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.aiwinn.base.util.StringUtils;
import com.aiwinn.base.util.ToastUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lxl.mobile.ApiUtil;
import com.lxl.mobile.MD5Util;
import com.lxl.shop.AttApp;
import com.lxl.shop.common.ShopConstants;
import com.lxl.shop.pag.MainActivity;
import com.lxl.shop.utils.DataSource;
import com.lxl.shop.utils.IOHelper;
import com.lxl.shop.utils.LoadingDialog;
import com.lxl.shop.utils.LogUtil;
import com.lxl.shop.utils.StockUtil;
import com.lxl.shop.utils.StringUtil;
import com.lxl.shop.viewmodel.CustomerModel;
import com.lxl.shop.viewmodel.CustomerNewCustomerRecordResponse;
import com.lxl.shop.viewmodel.CustomerRecentModel;
import com.lxl.shop.viewmodel.CustomerRecentResponse;
import com.lxl.shop.viewmodel.CustomerRecongizeResponse;
import com.lxl.shop.viewmodel.CustomerShoppingHistory;
import com.lxl.shop.viewmodel.CustomerSyncResponse;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by xiangleiliu on 2017/8/6.
 */
public class StockSender {
    private static StockSender sender;
//         public static String mBaseUrl = "http://192.168.1.166:8090/";
    public static String mBaseUrl = "http://192.168.1.99:8090/";
    private boolean isTest = false;

    public static String registerAction = "/yy-face/v1/customer";//注册用户
    public static String addRecongize = "/yy-face/v1/identify-record";//传入customerId记录老客户识别记录，并获取老客户简单信息
    public static String selectCustomerAction = "/yy-face/v1/customer";//根据customerId获取用户信息
    public static String selectRecentCustomerAction = "/yy-face/v1/identify-record/latest";//查询最近访问用户信息，用户最近5秒入库记录
    public static String selectRecentCustomerRecordAction = "/yy-face/v1/identify-record/notify-list";//查询最近20条用户进店记录
    public static String getShopppingAction = "/yy-face/v1/sales-record/customerId";//获取用户购买历史
    public static String syncCustomerAction = "/yy-face/v1/customer";//查询用户列表
    public static String addNewUserRecordAction = "/yy-face/v1/visitors-record";//上传新用户的信息
    public static String uploadAction = "/yy-face/v1/customer/upload";//上传图片的接口

    public static String syncFailCustomerAction = "/yy-face/v1/exception-record";//向服务器发送消息，请求记录同步失败的用户信息

    public static String customerListByIdsAction = "/yy-face/v1/customer/ids";//向服务器发送消息，请求记录同步失败的用户信息

    public static String customerByIdAction = "/yy-face/v1/customer";//向服务器发送消息，请求记录同步失败的用户信息

    private static final String BOUNDARY = UUID.randomUUID().toString(); // 边界标识 随机生成
    private static final String PREFIX = "--";
    private static final String LINE_END = "\r\n";
    private static final String CONTENT_TYPE = "multipart/form-data"; // 内容类型

    private static final String TAG = "UploadUtil";
    private int readTimeOut = 100 * 1000; // 读取超时
    private int connectTimeout = 100 * 1000; // 超时时间
    private LogUtil log;

    /***
     * 请求使用多长时间
     */
    private static int requestTime = 0;

    private static final String CHARSET = "utf-8"; // 设置编码

    /***
     * 上传成功
     */
    public static final int UPLOAD_SUCCESS_CODE = 1;

    /**
     * 文件不存在
     */
    public static final int UPLOAD_FILE_NOT_EXISTS_CODE = 2;

    /**
     * 服务器出错
     */
    public static final int UPLOAD_SERVER_ERROR_CODE = 3;
    protected static final int WHAT_TO_UPLOAD = 1;
    protected static final int WHAT_UPLOAD_DONE = 2;

    private StockSender(Context context) {
        log = LogUtil.getInstance(context);

        File file = new File(ShopConstants.CONFIG_PATH);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        String resultValue = "";
        if (file.exists()) {
            //读取配置文件
            InputStream inputStream = IOHelper.fromFileToIputStream(file);
            List<String> strings = IOHelper.readListStrByCode(inputStream, "utf-8");
            for (String str : strings) {
                if (StringUtil.emptyOrNull(str) || !str.contains("=")) {
                    continue;
                }
                String[] split = str.split("=");
                String key = split[0];
                String value = split[1];
                if ("url".equals(key)) {
                    resultValue = value;
                } else if ("first".equals(key)) {
//                    resultValue = value;
                }
            }
            mBaseUrl = resultValue;
        } else {
            //使用配置文件
            try {
                StringBuilder builder = new StringBuilder();
                builder.append("url="+mBaseUrl+"/");
                builder.append("\n");
                builder.append("first=mobile");
                file.createNewFile();
                IOHelper.writerStrByCodeToFile(file, "utf-8", false, builder.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static synchronized StockSender getInstance() {
        if (sender == null) {
            sender = new StockSender(AttApp.getContext());
        }
        return sender;
    }


    public CustomerModel sendAddRegisterService(CustomerModel customerModel) {
        customerModel.faceId = JSON.toJSONString(customerModel.userModel.features);
        String requestJson = JSON.toJSONString(customerModel);
        if (isTest) {
            CustomerModel customerModel2 = DataSource.getCustomerModel();
            return customerModel2;
        }
        log.LogW("sendAddRegisterService,customerName:" + customerModel.name);
        String s1 = requestPostNew(registerAction, requestJson,ApiUtil.getSignMap(),null);
        if (StringUtil.emptyOrNull(s1) || "error".equals(s1)) {
            return new CustomerModel();
        }
        CustomerModel customerModel1 = JSON.parseObject(s1, CustomerModel.class);
        return customerModel1;
    }

    public CustomerModel sendSelectCustomerService(String userId) {
        return sendSelectCustomerService(userId,null);
    }

    public CustomerModel sendSelectCustomerService(String userId,Activity activity) {

        String s1 = requestGetNew(selectCustomerAction+ File.separator + userId, null, ApiUtil.getSignMap(),activity);
        if ("error".equals(s1)) {
            return new CustomerModel();
        }
        CustomerModel customerModel1 = JSON.parseObject(s1, CustomerModel.class);
        return customerModel1;
    }

    /**
     * 查询最近扫描到的客人
     *
     * @return
     */
    public List<CustomerRecentModel> sendSelectRecentCustomerService() {
        if (isTest) {
            List<CustomerRecentModel> cusomterList = DataSource.getRecentCusomterList();
            return cusomterList;
        }
        String s1 = requestGet(selectRecentCustomerAction, "");
        if (StringUtil.emptyOrNull(s1) || "error".equals(s1)) {
            return new ArrayList<>();
        }
        CustomerRecentResponse response = JSON.parseObject(s1, CustomerRecentResponse.class);
        return response.identifyRecords;
    }

    /**
     * 查询客人最近进店记录
     *
     * @return
     */
    public List<CustomerRecentModel> sendSelectRecentRecordCustomerService(Activity activity) {
        if (isTest) {
            List<CustomerRecentModel> cusomterList = DataSource.getRecentCusomterList();
            return cusomterList;
        }
        String s1 = requestPostNew(selectRecentCustomerRecordAction, "", ApiUtil.getSignMap(),activity);
        if (StringUtil.emptyOrNull(s1) || "error".equals(s1)) {
            return new ArrayList<>();
        }
        List<CustomerRecentModel> customerRecentModelList = new ArrayList<>();
        try {
            CustomerRecentResponse response = JSON.parseObject(s1, CustomerRecentResponse.class);
            customerRecentModelList = response.identifyRecords;
        }catch (Exception e){
            return customerRecentModelList;
        }
        return customerRecentModelList;
    }

    /**
     * 根据customerId获取用户购买记录
     *
     * @param customerId
     * @return
     */
    public CustomerShoppingHistory sendGetShoppingService(String customerId) {
        if (isTest) {
            CustomerShoppingHistory customerShoppingHistory = DataSource.getCustomerShoppingHistory();
            return customerShoppingHistory;
        }
        log.LogW("sendGetShoppingService,customerId:" + customerId);
        String s1 = requestPostNew(getShopppingAction + File.separator + customerId,"",  ApiUtil.getSignMap(),null);
        CustomerShoppingHistory shoppingHistory = JSON.parseObject(s1, CustomerShoppingHistory.class);
        return shoppingHistory;
    }

    /**
     * 同步用户资料
     *
     * @param customerIdList
     * @return
     */
    public List<CustomerModel> sendSyncCustomerService(List<String> customerIdList) {
        return null;//sendSyncCustomerService(customerIdList,null);
    }

    public List<CustomerModel> sendSyncCustomerService(int currentPage,int pageSize,Activity activity) {
        Map<String,String> parpMap = new HashMap<>();
        parpMap.put("currentPage",currentPage+"");
        parpMap.put("pageSize",pageSize+"");
        String s1 = requestGetNew(syncCustomerAction, parpMap, ApiUtil.getSignMap(),activity);
        List<CustomerModel> customerModelList = null;
        try {
            customerModelList = ((JSONArray) (JSONArray.parse(s1))).toJavaList(CustomerModel.class);
        }catch (Exception e){
            return null;
        }
        return customerModelList;
    }

    public List<CustomerModel> getUserListDialog(List<String> customerIdList,Activity activity){
        String requestJson = JSON.toJSONString(customerIdList);
        String s1 = requestGet(syncCustomerAction, requestJson,activity);
        CustomerSyncResponse response = JSON.parseObject(s1, CustomerSyncResponse.class);
        return response.customers;
    }

    public CustomerRecongizeResponse sendAddRecongizeCustomer(String customerId) {
        return sendAddRecongizeCustomer(customerId,null);
    }
    /**
     * 添加老客的扫描记录
     *
     * @param customerId
     * @return
     */
    public CustomerRecongizeResponse sendAddRecongizeCustomer(String customerId,String faceCoordinates) {
        if (isTest) {
            return new CustomerRecongizeResponse();
        }
        JSONObject json = new JSONObject();
        json.put("customerId", customerId);//lxltest 这个接口要改，faceId改成customerId
        if(faceCoordinates != null){
            json.put("faceCoordinates", faceCoordinates);
        }
        String s1 = requestGet(addRecongize, json.toJSONString());
        if ("error".equals(s1)) {
            CustomerRecongizeResponse response = new CustomerRecongizeResponse();
            return response;
        }
        CustomerRecongizeResponse recongizeModel = JSON.parseObject(s1, CustomerRecongizeResponse.class);
        return recongizeModel;
    }

    public CustomerNewCustomerRecordResponse sendAddNewCustomerRecord(String imgUrl, String faceId, int age, String gender) {
        return sendAddNewCustomerRecord(imgUrl,faceId,age,gender,null);
    }

    /**
     * 没有匹配到记录新客信息
     *
     * @return
     */
    public CustomerNewCustomerRecordResponse sendAddNewCustomerRecord(String imgUrl, String faceId, int age, String gender,String faceCoordinates) {
        JSONObject json = new JSONObject();
        json.put("imgUrl", imgUrl);
        json.put("faceId", faceId);
        json.put("age", age);
        json.put("gender", gender);
        if(faceCoordinates != null)
            json.put("faceCoordinates",faceCoordinates);
        String s1 = requestGet(addNewUserRecordAction, json.toJSONString());
        if ("error".equals(s1)) {
            return new CustomerNewCustomerRecordResponse();
        }
        CustomerNewCustomerRecordResponse recongizeModel = JSON.parseObject(s1, CustomerNewCustomerRecordResponse.class);
        return recongizeModel;
    }

    private String requestGet(String action, String jsonStr,Activity activity) {
        try {
            //创建连接
            Date nowDate = new Date();
            URL url = new URL(mBaseUrl + action);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);
            connection.setConnectTimeout(8000);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestProperty("Charset", "UTF-8");
            connection.setRequestProperty("Content-Type", "application/json");

            connection.connect();
            // POST请求
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            out.write(jsonStr.getBytes());
            out.flush();
            out.close();
            // 读取响应
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String lines;
            StringBuffer sb = new StringBuffer("");
            while ((lines = reader.readLine()) != null) {
                lines = URLDecoder.decode(lines, "utf-8");
                sb.append(lines);
            }
            // 断开连接
            reader.close();
            connection.disconnect();
            JSONObject jsonObject = JSON.parseObject(sb.toString());
            Integer code = jsonObject.getInteger("code");
            if (code == 200) {
                JSONObject data = jsonObject.getJSONObject("data");
                if (data == null) {
                    return "";
                }
                return data.toJSONString();
            } else {
                log.logE(sb.toString());
                if(activity != null){
                    Looper.prepare();
                    Toast.makeText(activity,jsonObject.getString("message"),Toast.LENGTH_LONG).show();
                    Looper.loop();
                }
                return "error";
            }
        } catch (Exception e) {
            log.logE("requestFail，message:" + e.getMessage());
            if(activity != null){
                Looper.prepare();
                Toast.makeText(activity,"requestFail，message:" + e.getMessage(),Toast.LENGTH_LONG).show();
                Looper.loop();
            }
            e.printStackTrace();
        }
        return "error";
    }
    private String requestGet(String action, String jsonStr) {
        return requestGet(action,jsonStr,null);
    }

    public String toUploadFile(File file, Map<String, String> param) {
        String result = null;
        requestTime = 0;

        long requestTime = System.currentTimeMillis();
        long responseTime = 0;

        try {
            URL url = new URL(mBaseUrl + uploadAction);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(readTimeOut);
            conn.setConnectTimeout(connectTimeout);
            conn.setDoInput(true); // 允许输入流
            conn.setDoOutput(true); // 允许输出流
            conn.setUseCaches(false); // 不允许使用缓存
            conn.setRequestMethod("POST"); // 请求方式
            conn.setRequestProperty("Charset", CHARSET); // 设置编码
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
            conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);
            Map<String,String> signMap = ApiUtil.getSignMap();
            conn.setRequestProperty("timestamp", signMap.get("timestamp"));
            conn.setRequestProperty("nonce", signMap.get("nonce"));
            conn.setRequestProperty("sign", MD5Util.encode(signMap.get("token")+ signMap.get("timestamp")+ signMap.get("nonce")).toUpperCase());

            /**
             * 当文件不为空，把文件包装并且上传
             */
            DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
            StringBuffer sb = null;
            String params = "";

            /***
             * 以下是用于上传参数
             */
            if (param != null && param.size() > 0) {
                Iterator<String> it = param.keySet().iterator();
                while (it.hasNext()) {
                    sb = null;
                    sb = new StringBuffer();
                    String key = it.next();
                    String value = param.get(key);
                    sb.append(PREFIX).append(BOUNDARY).append(LINE_END);
                    sb.append("Content-Disposition: form-data; name=\"").append(key).append("\"").append(LINE_END).append(LINE_END);
                    sb.append(value).append(LINE_END);
                    params = sb.toString();
                    Log.i(TAG, key + "=" + params + "##");
                    dos.write(params.getBytes());
                }
            }

            sb = null;
            params = null;
            sb = new StringBuffer();
            /**
             * 这里重点注意： name里面的值为服务器端需要key 只有这个key 才可以得到对应的文件
             * filename是文件的名字，包含后缀名的 比如:abc.png
             */
            sb.append(PREFIX).append(BOUNDARY).append(LINE_END);
            sb.append("Content-Disposition:form-data; name=file;filename=\"" + file.getName() + "\"" + LINE_END);
            sb.append("Content-Type:image/pjpeg" + LINE_END); // 这里配置的Content-type很重要的 ，用于服务器端辨别文件的类型的
            sb.append(LINE_END);
            params = sb.toString();
//            sb = null;

            Log.i(TAG, file.getName() + "=" + params + "##");
            dos.write(params.getBytes());
            /**上传文件*/
            InputStream is = new FileInputStream(file);
            Log.i("lxltest", "File:" + file.length());
//            onUploadProcessListener.initUpload((int) file.length());
            byte[] bytes = new byte[1024];
            int len = 0;
            int curLen = 0;
            while ((len = is.read(bytes)) != -1) {
                curLen += len;
                dos.write(bytes, 0, len);
//                Log.i("lxltest", "curLen:" + curLen);
//                onUploadProcessListener.onUploadProcess(curLen);
            }
            is.close();

            dos.write(LINE_END.getBytes());
            byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END).getBytes();
            dos.write(end_data);
            dos.flush();


            /**
             * 获取响应码 200=成功 当响应成功，获取响应的流
             */
            int res = conn.getResponseCode();
            responseTime = System.currentTimeMillis();
            requestTime = (int) ((responseTime - requestTime) / 1000);
            Log.e(TAG, "response code:" + res);
            if (res == 200) {
                Log.e(TAG, "request success");
                InputStream input = conn.getInputStream();
                StringBuffer sb1 = new StringBuffer();
                int ss;
                while ((ss = input.read()) != -1) {
                    sb1.append((char) ss);
                }
                result = sb1.toString();
                Log.e(TAG, "result : " + result);
                return result;
            } else {
                Log.e(TAG, "request error");
                return result;
            }
        } catch (Exception e) {
            log.logE("上传图片失败：" + e.getMessage());
            StockUtil.showToastOnMainThread(AttApp.getContext(), "上传图片失败：" + e.getMessage());
            e.printStackTrace();
            return result;
        }
    }

    //下载图片，返回本地地址
    public Bitmap uploadCustomer(String userImgUrl) {
        try {
            URL url = new URL(userImgUrl);
            //打开输入流
            InputStream inputStream = url.openStream();
            //对网上资源进行下载转换位图图片
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            return bitmap;
        } catch (Exception e) {
            log.logE("下载图片失败：URL：" + userImgUrl + "error:" + e.getMessage());
            StockUtil.showToastOnMainThread(AttApp.getContext(), "下载图片失败：URL：" + userImgUrl + "error:" + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 发送注册错误数据
     * @param customerId
     * @param msg
     * @return
     */
    public String sendFailSyncCustomerService(String customerId,String msg) {
        log.LogW("sendFailSyncCustomerService,customerId:" + customerId);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("customerId",Integer.parseInt(customerId));
        jsonObject.put("exceptionMessage",msg);
        String s1 = requestPostNew(syncFailCustomerAction, jsonObject.toJSONString(), ApiUtil.getSignMap(),null);
        return s1;
    }


    /**
     * 根据id获取数据
     * @return
     */
    public CustomerModel getCustomerById(String customerId) {
        log.LogW("getCustomerById,customerId=:" + customerId);
        String s1 = requestGetNew(customerByIdAction+File.separator+customerId, new HashMap<String, String>(), ApiUtil.getSignMap(),null);
        CustomerModel customerModel = JSONObject.parseObject(s1,CustomerModel.class);
        return customerModel;
    }

    /**
     * 根据id获取数据
     * @param  customerIdList
     * @return
     */
    public List<CustomerModel> getCustomerListByIds(List<String> customerIdList) {
        log.LogW("getCustomerListByIds,customerIdSize:" + customerIdList.size());
        StringBuffer sb = new StringBuffer();
        for (String id : customerIdList) {
            sb.append(id).append(",");
        }
        if(sb.length()>0){
            sb = new StringBuffer(sb.substring(0,sb.length()-1));
        }
        Map<String,String> parpMap = new HashMap<>();
        parpMap.put("ids",sb.toString());
        String s1 = requestGetNew(customerListByIdsAction, parpMap, ApiUtil.getSignMap(),null);
        List<CustomerModel> customerModelList = null;
        try {
            customerModelList = ((JSONArray) (JSONArray.parse(s1))).toJavaList(CustomerModel.class);
        }catch (Exception e){
            return null;
        }
        return customerModelList;
    }

    private String requestGetNew(String action, Map<String,String> paraMap,Map<String,String> signMap,Activity activity) {
        try {
            //创建连接
            Date nowDate = new Date();
            URL url = new URL(mBaseUrl + action+"?"+(StringUtils.isEmpty(ApiUtil.concatSignString(paraMap))?"":ApiUtil.concatSignString(paraMap).substring(0,ApiUtil.concatSignString(paraMap).length()-1)));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            connection.setDoOutput(true);
            connection.setDoInput(true);
//            connection.setRequestMethod("GET");
            connection.setUseCaches(false);
            connection.setConnectTimeout(8000);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestProperty("Charset", "UTF-8");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("timestamp", signMap.get("timestamp"));
            connection.setRequestProperty("nonce", signMap.get("nonce"));
            connection.setRequestProperty("token", signMap.get("token"));
            connection.setRequestProperty("sign", MD5Util.encode(ApiUtil.concatSignString(paraMap)+signMap.get("token")+ signMap.get("timestamp")+ signMap.get("nonce")).toUpperCase());
            connection.connect();

            // 读取响应
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String lines;
            StringBuffer sb = new StringBuffer("");
            while ((lines = reader.readLine()) != null) {
                lines = URLDecoder.decode(lines, "utf-8");
                sb.append(lines);
            }
            // 断开连接
            reader.close();
            connection.disconnect();
            JSONObject jsonObject = JSON.parseObject(sb.toString());
            Integer code = jsonObject.getInteger("code");
            if (code == 0) {
                try {
                    JSONObject data = jsonObject.getJSONObject("data");
                    if (data == null) {
                        return "";
                    }
                    return data.toJSONString();
                }catch (ClassCastException e){
                    JSONArray dataList = jsonObject.getJSONArray("data");
                    if (dataList == null) {
                        return "";
                    }
                    return dataList.toJSONString();
                }
            } else {
                log.logE(sb.toString());
                if(activity != null){
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(LoadingDialog.getInstance() != null) {
                                LoadingDialog.getInstance().dismiss();
                            }
                        }
                    });
                    Looper.prepare();
                    Toast.makeText(activity,jsonObject.getString("message"),Toast.LENGTH_LONG).show();
                    Looper.loop();
                }
                return "error";
            }
        } catch (Exception e) {
            log.logE("requestFail，message:" + e.getMessage());
            if(activity != null){
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(LoadingDialog.getInstance() != null) {
                            LoadingDialog.getInstance().dismiss();
                        }
                    }
                });
                Looper.prepare();
                Toast.makeText(activity,"requestFail，message:" + e.getMessage(),Toast.LENGTH_LONG).show();
                Looper.loop();
            }
            e.printStackTrace();
        }
        return "error";
    }

    private String requestPostNew(String action, String jsonStr,Map<String,String> signMap,Activity activity) {
        try {
            //创建连接
            Date nowDate = new Date();
            URL url = new URL(mBaseUrl + action);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);
            connection.setConnectTimeout(8000);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestProperty("Charset", "UTF-8");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("timestamp", signMap.get("timestamp"));
            connection.setRequestProperty("nonce", signMap.get("nonce"));
            connection.setRequestProperty("token", signMap.get("token"));
            connection.setRequestProperty("sign", MD5Util.encode(signMap.get("token")+ signMap.get("timestamp")+ signMap.get("nonce")).toUpperCase());
            connection.connect();
            // POST请求
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            out.write(jsonStr.getBytes());
            out.flush();
            out.close();
            // 读取响应
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String lines;
            StringBuffer sb = new StringBuffer("");
            while ((lines = reader.readLine()) != null) {
                lines = URLDecoder.decode(lines, "utf-8");
                sb.append(lines);
            }
            // 断开连接
            reader.close();
            connection.disconnect();
            JSONObject jsonObject = JSON.parseObject(sb.toString());
            Integer code = jsonObject.getInteger("code");
            if (code == 0) {
                JSONObject data = jsonObject.getJSONObject("data");
                if (data == null) {
                    return "";
                }
                return data.toJSONString();
            } else {
                log.logE(sb.toString());
                if(activity != null){
                    ToastUtils.showLong(jsonObject.getString("message"));
                }
                return "error";
            }
        } catch (Exception e) {
            log.logE("requestFail，message:" + e.getMessage());
            if(activity != null){
                ToastUtils.showLong("requestFail，message:" + e.getMessage());
            }
            e.printStackTrace();
        }
        return "error";
    }

}
