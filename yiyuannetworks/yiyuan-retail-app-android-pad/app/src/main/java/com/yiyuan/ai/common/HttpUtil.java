package com.yiyuan.ai.common;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.aiwinn.base.util.StringUtils;
import com.aiwinn.facedetectsdk.bean.UserBean;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yiyuan.ai.model.CustomerModel;
import com.yiyuan.ai.model.Tag;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
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

import static com.yiyuan.ai.AIConstants.mBaseUrl;

/**
 * Created by wangyu on 2019/4/11.
 */

public class HttpUtil {
    public static String syncFailCustomerAction = "/yy-face/v1/exception-record";//向服务器发送消息，请求记录同步失败的用户信息

    public static String customerByIdAction = "/yy-face/v1/customer/";//向服务器发送消息，根据id获取数据

    public static String customerListByIdsAction = "/yy-face/v1/customer/ids";//向服务器发送消息，根据ids获取数据

    public static String welcomeAd = "/yy-face/v1/advertising-images/device/";//获取广告图片

    public static String syncCustomerAction = "/yy-face/v1/customer";//根据已注册的faceIdList获取未注册的用户列表

    public static String uploadAction = "/yy-face/v1/customer/upload";//上传图片的接口

    public static String registerAction = "/yy-face/v1/customer";//注册用户

    public static String tagsUrl = "/yy-face/v1/tags";//向服务器发送消息，获取标签列表

    public static String smsCode = "/yy-face/v1/sms/sender/code";//向服务器发送消息，获取短信验证码

    public static String updateCustomer = "/yy-face/v1/customer/ai-supper/";//向服务器发送消息，获取标签列表

    public static String sendEmailAction = "/yy-face/v1/customer/sender-photo";//向服务器发送消息，获取标签列表

    LogUtil log = LogUtil.getInstance();


    private static class SingletonInstance{
        private static final HttpUtil INSTANCE = new HttpUtil();
    }
    public static HttpUtil getInstance(){
        return SingletonInstance.INSTANCE;
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

    public String sendEmail(String imageUrl, String email) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("url",imageUrl);
        jsonObject.put("email",email);
        String s1 = requestPostNew(sendEmailAction, jsonObject.toJSONString(), ApiUtil.getSignMap(),null);
        return s1;
    }


    /**
     * 获取手机验证码
     * @param customerId
     * @param mobile
     */
    public String getSmsCode(String customerId, String mobile) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("mobile",mobile);
        String s1 = requestPostNew(smsCode, jsonObject.toJSONString(), ApiUtil.getSignMap(),null);
        return s1;
    }

    public String updateCustomer(String imgUrl,String customerId,String emailHttp, JSONArray jsonArray,String genderHttp, String nameHttp, String ageHttp, String phoneHttp, String codeHttp) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("imgUrl",imgUrl);
        jsonObject.put("tagIds",jsonArray);
        jsonObject.put("gender",genderHttp);
        jsonObject.put("name",nameHttp);
        jsonObject.put("email",emailHttp);
        jsonObject.put("age",ageHttp);
        jsonObject.put("mobile",phoneHttp);
        jsonObject.put("smsCode",codeHttp);
        String s1 = requestPostNew(updateCustomer+customerId, jsonObject.toJSONString(), ApiUtil.getSignMap(),null,"PUT");
        return s1;
    }

    public List<Tag> getTags() {
        String s1 = requestGetNew(tagsUrl, new HashMap<String, String>(), ApiUtil.getSignMap(),null);
        if("error".equals(s1)){
            return null;
        }
        return  ((JSONArray) (JSONArray.parse(s1))).toJavaList(Tag.class);
    }
    public List<CustomerModel> sendSyncCustomerService(int currentPage,int pageSize,Activity activity) {
        Map<String,String> parpMap = new HashMap<>();
        parpMap.put("currentPage",currentPage+"");
        parpMap.put("pageSize",pageSize+"");
        String s1 = requestGetNew(syncCustomerAction, parpMap,ApiUtil.getSignMap(),activity);
        List<CustomerModel> customerModelList = null;
        try {
            customerModelList = ((JSONArray) (JSONArray.parse(s1))).toJavaList(CustomerModel.class);
        }catch (Exception e){
            return null;
        }
        return customerModelList;
    }


    /**
     * 根据id获取数据
     * @return
     */
    public CustomerModel getCustomerById(String customerId) {
        log.LogW("getCustomerById,customerId=:" + customerId);
        String s1 = requestGetNew(customerByIdAction+ customerId, new HashMap<String, String>(), ApiUtil.getSignMap(),null);
        if("error".equals(s1)){
            return new CustomerModel();
        }
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

    private List<String> requestGetNew2(String action, Map<String,String> paraMap,Map<String,String> signMap,Activity activity) {
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
            JSONObject jsonObject = JSONObject.parseObject(sb.toString());
            Integer code = jsonObject.getInteger("code");
            if (code == 0) {
                try {
                    JSONObject data = jsonObject.getJSONObject("data");
                    if (data == null) {
                        return null;
                    }
                    return null;
                }catch (ClassCastException e){
                    JSONArray dataList = jsonObject.getJSONArray("data");
                    if (dataList == null) {
                        return null;
                    }
                    return dataList.toJavaList(String.class);
                }
            } else {
                log.logE(sb.toString());
                if(activity != null){
                    Looper.prepare();
                    Toast.makeText(activity,jsonObject.getString("message"),Toast.LENGTH_LONG).show();
                    Looper.loop();
                }
                return null;
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
        return null;
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
    private String requestPostNew(String action, String jsonStr,Map<String,String> signMap,Activity activity) {
        return requestPostNew(action,jsonStr,signMap,activity,null);
    }

    private String requestPostNew(String action, String jsonStr,Map<String,String> signMap,Activity activity,String method) {
        try {
            //创建连接
            Date nowDate = new Date();
            URL url = new URL(mBaseUrl + action);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            if(StringUtils.isEmpty(method)) {
                connection.setRequestMethod("POST");
            }else{
                connection.setRequestMethod(method);
            }
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
                if(jsonObject.get("data") instanceof String || jsonObject.get("data") instanceof Integer){
                    return "";
                }
                JSONObject data = jsonObject.getJSONObject("data");
                if (data == null) {
                    return "";
                }
                return data.toJSONString();
            }else if(code == 1005 || code == 1004 || code == 1006){
                return jsonObject.get("msg").toString();
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

    public List<String> findWelcomeList(String macStr,Activity activity) {
        Map<String,String> parpMap = new HashMap<>();
        List<String> s1 = requestGetNew2(welcomeAd+macStr, parpMap,ApiUtil.getSignMap(),activity);
        if(s1 == null){
            return new ArrayList<String>();
        }
        return s1;
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
            e.printStackTrace();
        }
        return null;
    }

    public CustomerModel sendAddRegisterService(CustomerModel customerModel) {
        customerModel.faceId = JSON.toJSONString(customerModel.userModel.features);
        String requestJson = JSON.toJSONString(customerModel);
        log.LogW("sendAddRegisterService,customerName:" + customerModel.name);
        String s1 = requestPostNew(registerAction, requestJson, ApiUtil.getSignMap(),null);
        if (StringUtil.emptyOrNull(s1) || "error".equals(s1)) {
            return new CustomerModel();
        }
        CustomerModel customerModel1 = JSON.parseObject(s1, CustomerModel.class);
        return customerModel1;
    }

    /**
     * 上传图片
     * @param file
     * @param param
     * @return
     */
    public String toUploadFile(File file, Map<String, String> param) {
        String result = null;
        long requestTime = System.currentTimeMillis();
        long responseTime = 0;

        try {
            URL url = new URL(mBaseUrl + uploadAction);
            String boundary = UUID.randomUUID().toString();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10*1000);
            conn.setConnectTimeout(10*1000);
            conn.setDoInput(true); // 允许输入流
            conn.setDoOutput(true); // 允许输出流
            conn.setUseCaches(false); // 不允许使用缓存
            conn.setRequestMethod("POST"); // 请求方式
            conn.setRequestProperty("Charset", "utf-8"); // 设置编码
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
            conn.setRequestProperty("Content-Type", "multipart/form-data" + ";boundary=" + boundary);
            Map<String,String> signMap = ApiUtil.getSignMap();
            conn.setRequestProperty("timestamp", signMap.get("timestamp"));
            conn.setRequestProperty("nonce", signMap.get("nonce"));
            conn.setRequestProperty("token", signMap.get("token"));
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
                    sb.append("--").append(boundary).append("\r\n");
                    sb.append("Content-Disposition: form-data; name=\"").append(key).append("\"").append("\r\n").append("\r\n");
                    sb.append(value).append("\r\n");
                    params = sb.toString();
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
            sb.append("--").append(boundary).append("\r\n");
            sb.append("Content-Disposition:form-data; name=file;filename=\"" + file.getName() + "\"" + "\r\n");
            sb.append("Content-Type:image/pjpeg" + "\r\n"); // 这里配置的Content-type很重要的 ，用于服务器端辨别文件的类型的
            sb.append("\r\n");
            params = sb.toString();
//            sb = null;
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

            dos.write("\r\n".getBytes());
            byte[] end_data = ("--" + boundary + "--" + "\r\n").getBytes();
            dos.write(end_data);
            dos.flush();


            /**
             * 获取响应码 200=成功 当响应成功，获取响应的流
             */
            int res = conn.getResponseCode();
            responseTime = System.currentTimeMillis();
            requestTime = (int) ((responseTime - requestTime) / 1000);
            if (res == 200) {
                InputStream input = conn.getInputStream();
                StringBuffer sb1 = new StringBuffer();
                int ss;
                while ((ss = input.read()) != -1) {
                    sb1.append((char) ss);
                }
                result = sb1.toString();
                log.logE( "result : " + result);
                return result;
            } else {
                log.logE("request error");
                return result;
            }
        } catch (Exception e) {
            log.logE("上传图片失败：" + e.getMessage());
            e.printStackTrace();
            return result;
        }
    }
}
