package com.yiyuan.ai.common;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static android.content.Context.TELEPHONY_SERVICE;

/**
 * @Author: liaojin
 * @Description:
 * @Date: Created in 下午1:15 2018/7/4
 */
public class ApiUtil {

    public static String concatSignString(Map<String, String> map) {
        Map<String, String> paramterMap = new HashMap<>();
        paramterMap.putAll(map);
        // 按照key升续排序，然后拼接参数
        Set<String> keySet = paramterMap.keySet();
        String[] keyArray = keySet.toArray(new String[keySet.size()]);
        Arrays.sort(keyArray);
        StringBuilder sb = new StringBuilder();
        for (String k : keyArray) {
            if (paramterMap.get(k).trim().length() > 0) {
                // 参数值为空，则不参与签名
                sb.append(k).append("=").append(paramterMap.get(k).trim()).append("&");
            }
        }
        return sb.toString();
    }

    public static String getPhoneMiEI(Context context) {
        TelephonyManager TelephonyMgr = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        return TelephonyMgr.getDeviceId();
    }

    public static String getLocalMacAddressFromIp() {
        String strMacAddr = null;
        try {
            //获得IpD地址
            InetAddress ip = getLocalInetAddress();
            byte[] b = NetworkInterface.getByInetAddress(ip).getHardwareAddress();
            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < b.length; i++) {
                if (i != 0) {
                    buffer.append(':');
                }
                String str = Integer.toHexString(b[i] & 0xFF);
                buffer.append(str.length() == 1 ? 0 + str : str);
            }
            strMacAddr = buffer.toString().toUpperCase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strMacAddr;
    }


    /**
     * 获取移动设备本地IP
     *
     * @return
     */
    private static InetAddress getLocalInetAddress() {
        InetAddress ip = null;
        try {
            //列举
            Enumeration<NetworkInterface> en_netInterface = NetworkInterface.getNetworkInterfaces();
            while (en_netInterface.hasMoreElements()) {//是否还有元素
                NetworkInterface ni = (NetworkInterface) en_netInterface.nextElement();//得到下一个元素
                Enumeration<InetAddress> en_ip = ni.getInetAddresses();//得到一个ip地址的列举
                while (en_ip.hasMoreElements()) {
                    ip = en_ip.nextElement();
                    if (!ip.isLoopbackAddress() && ip.getHostAddress().indexOf(":") == -1)
                        break;
                    else
                        ip = null;
                }
                if (ip != null) {
                    break;
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return ip;
    }

    public static Map<String,String> getSignMap() {
        //校验机制
        Date nowDate = new Date();
        Map<String,String> signMap = new HashMap<>();
        signMap.put("timestamp", nowDate.getTime()+"");
        signMap.put("nonce", nowDate.getTime()+"");
        signMap.put("token", "297e325a66f62bae0166f631b0b20002");
        return signMap;
    }

    public static String getSign(Map<String,String> parpMap) {
        //校验机制
        Date nowDate = new Date();
        Map<String,String> signMap = new HashMap<>();
        signMap.put("timestamp", nowDate.getTime()+"");
        signMap.put("nonce", nowDate.getTime()+"");
        signMap.put("token", "297e325a66f62bae0166f631b0b20002");
        parpMap = parpMap == null?new HashMap<String,String>():parpMap;
        return MD5Util.encode(concatSignString(parpMap)+signMap.get("token")+ signMap.get("timestamp")+ signMap.get("nonce")).toUpperCase();
    }

    public static String getAllSign(Map<String,String> parpMap) {
        //校验机制
        Date nowDate = new Date();
        Map<String,String> signMap = new HashMap<>();
        signMap.put("timestamp", nowDate.getTime()+"");
        signMap.put("nonce", nowDate.getTime()+"");
        signMap.put("token", "297e325a66f62bae0166f631b0b20002");
        parpMap = parpMap == null?new HashMap<String,String>():parpMap;
        String surl = "timestamp="+signMap.get("timestamp")+"&nonce="+signMap.get("nonce")+"&sign="+MD5Util.encode(concatSignString(parpMap)+signMap.get("token")+ signMap.get("timestamp")+ signMap.get("nonce")).toUpperCase();
        if(parpMap.get("platform") != null){
            surl+= "&platform="+parpMap.get("platform");
        }
        return surl;
    }
}

