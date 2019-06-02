package com.yiyuan.ai.websocket;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;

import com.aiwinn.base.module.utils.StringUtils;
import com.aiwinn.facedetectsdk.FaceDetectManager;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;
import com.yiyuan.ai.AIConstants;
import com.yiyuan.ai.SocketUserUtil;
import com.yiyuan.ai.common.ApiUtil;
import com.yiyuan.ai.common.CallbackDataWrapper;
import com.yiyuan.ai.common.CallbackWrapper;
import com.yiyuan.ai.common.ICallback;
import com.yiyuan.ai.common.IWsCallback;
import com.yiyuan.ai.common.LogSocketUtil;
import com.yiyuan.ai.request.Action;
import com.yiyuan.ai.request.Request;
import com.yiyuan.ai.response.ResAction;
import com.yiyuan.aiwinn.faceattendance.AttApp;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by zly on 2017/6/8.
 */

public class WsManager {
    /**
     * WebSocket config
     */

    private int reconnectCount = 0;//重连次数
    private long minInterval = 3000;//重连最小时间间隔
    private long maxInterval = 60000;//重连最大时间间隔
    private static final long HEARTBEAT_INTERVAL = 180000;//心跳间隔
    private static final int FRAME_QUEUE_SIZE = 5;
    private static final int CONNECT_TIMEOUT = 5000;
    private static final int REQUEST_TIMEOUT = 10000;//请求超时时间
    private WsStatus mStatus;
    private WebSocket ws;
    private WsListener mListener;
    private AtomicLong seqId = new AtomicLong(SystemClock.uptimeMillis());//每个请求的唯一标识
    private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private Map<Long, CallbackWrapper> callbacks = new HashMap<>();

    private final int SUCCESS_HANDLE = 0x01;
    private final int ERROR_HANDLE = 0x02;

    private LogSocketUtil log = LogSocketUtil.getInstance();
    private Context mContext;

    private String url ;
    private static WsManager mInstance;

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SUCCESS_HANDLE:
                    CallbackDataWrapper successWrapper = (CallbackDataWrapper) msg.obj;
                    successWrapper.getCallback().onSuccess(successWrapper.getData());
                    break;
                case ERROR_HANDLE:
                    CallbackDataWrapper errorWrapper = (CallbackDataWrapper) msg.obj;
                    errorWrapper.getCallback().onFail((String) errorWrapper.getData());
                    break;
            }
        }
    };


    private WsManager(Context mContext) {
        this.mContext = mContext;
    }


    public static synchronized WsManager getInstance(Context context) {
        if(mInstance == null){

            mInstance = new WsManager(context);
        }
        return mInstance;
    }

    public void init() {
        try {
            /**
             * configUrl其实是缓存在本地的连接地址
             * 这个缓存本地连接地址是app启动的时候通过http请求去服务端获取的,
             * 每次app启动的时候会拿当前时间与缓存时间比较,超过6小时就再次去服务端获取新的连接地址更新本地缓存
             */
            url = initUrl();
            ws = new WebSocketFactory().createSocket(url, CONNECT_TIMEOUT)
                    .setFrameQueueSize(FRAME_QUEUE_SIZE)//设置帧队列最大值为5
                    .setMissingCloseFrameAllowed(false)//设置不允许服务端关闭连接却未发送关闭帧
                    .addListener(mListener = new WsListener())//添加回调监听
                    .connectAsynchronously();//异步连接
            setStatus(WsStatus.CONNECTING);
            log.LogW("第一次连接");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void doAuth() {
        sendReq(Action.LOGIN, null, new ICallback() {
            @Override
            public void onSuccess(Object o) {
                log.LogW("授权成功");
                setStatus(WsStatus.AUTH_SUCCESS);
                delaySyncData();
            }


            @Override
            public void onFail(String msg) {

            }
        });
    }

    //同步数据
    private void delaySyncData() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                sendReq(Action.SYNC, null, new ICallback() {
                    @Override
                    public void onSuccess(Object o) {

                    }

                    @Override
                    public void onFail(String msg) {

                    }
                });
            }
        }, 300);
    }


    private void startHeartbeat() {
        mHandler.postDelayed(heartbeatTask, HEARTBEAT_INTERVAL);
    }


    private void cancelHeartbeat() {
        heartbeatFailCount = 0;
        mHandler.removeCallbacks(heartbeatTask);
    }


    private int heartbeatFailCount = 0;
    private Runnable heartbeatTask = new Runnable() {
        @Override
        public void run() {
            sendReq(Action.HEARTBEAT, null, new ICallback() {
                @Override
                public void onSuccess(Object o) {
                    heartbeatFailCount = 0;
                }


                @Override
                public void onFail(String msg) {
                    heartbeatFailCount++;
                    if (heartbeatFailCount >= 3) {
                        reconnect();
                    }
                }
            });

            mHandler.postDelayed(this, HEARTBEAT_INTERVAL);
        }
    };


    public void sendReq(Action action, Object req, ICallback callback) {
        sendReq(action, req, callback, REQUEST_TIMEOUT);
    }


    public void sendReq(Action action, Object req, ICallback callback, long timeout) {
        sendReq(action, req, callback, timeout, 1);
    }


    /**
     * @param action   Action
     * @param req      请求参数
     * @param callback 回调
     * @param timeout  超时时间
     * @param reqCount 请求次数
     */
    @SuppressWarnings("unchecked")
    private <T> void sendReq(Action action, T req, final ICallback callback, final long timeout, int reqCount) {
        if (!isNetConnect()) {
            callback.onFail("网络不可用");
            return;
        }

        if (WsStatus.AUTH_SUCCESS.equals(getStatus()) || Action.LOGIN.equals(action)) {
            Request request = new Request.Builder<T>()
                    .action(action.getAction())
                    .reqEvent(action.getReqEvent())
                    .seqId(seqId.getAndIncrement())
                    .reqCount(reqCount)
                    .req(req)
                    .build();

            ScheduledFuture timeoutTask = enqueueTimeout(request.getSeqId(), timeout);//添加超时任务

            IWsCallback tempCallback = new IWsCallback() {

                @Override
                public void onSuccess(Object o) {
                    mHandler.obtainMessage(SUCCESS_HANDLE, new CallbackDataWrapper(callback, o))
                            .sendToTarget();
                }


                @Override
                public void onError(String msg, Request request, Action action) {
                    mHandler.obtainMessage(ERROR_HANDLE, new CallbackDataWrapper(callback, msg))
                            .sendToTarget();
                }


                @Override
                public void onTimeout(Request request, Action action) {
                    timeoutHandle(request, action, callback, timeout);
                }
            };

            callbacks.put(request.getSeqId(),
                    new CallbackWrapper(tempCallback, timeoutTask, action, request));

            log.LogW("send text : "+request.getAction() );
            ws.sendText(request.getAction());
        } else {
            callback.onFail("用户授权失败");
        }
    }


    /**
     * 添加超时任务
     */
    private ScheduledFuture enqueueTimeout(final long seqId, long timeout) {
        return executor.schedule(new Runnable() {
            @Override
            public void run() {
                CallbackWrapper wrapper = callbacks.remove(seqId);
                if (wrapper != null) {
                    log.LogW("(action:"+wrapper.getAction().getAction()+")第"+wrapper.getRequest().getReqCount()+"次请求超时");
                    wrapper.getTempCallback().onTimeout(wrapper.getRequest(), wrapper.getAction());
                }
            }
        }, timeout, TimeUnit.MILLISECONDS);
    }


    /**
     * 超时处理
     */
    private void timeoutHandle(Request request, Action action, ICallback callback, long timeout) {
        if (request.getReqCount() > 3) {
            log.LogW("(action:"+action.getAction()+")连续"+request.getReqCount()+"次请求超时,请求重连");
            reconnect();
        } else {
            sendReq(action, request.getReq(), callback, timeout, request.getReqCount() + 1);
            log.LogW("(action:"+action.getAction()+")发起第"+request.getReqCount()+"次请求");
        }
    }


    /**
     * 继承默认的监听空实现WebSocketAdapter,重写我们需要的方法
     * onTextMessage 收到文字信息
     * onConnected 连接成功
     * onConnectError 连接失败
     * onDisconnected 连接关闭
     */
    class WsListener extends WebSocketAdapter {
        @Override
        public void onTextMessage(WebSocket websocket, String text) throws Exception {
            super.onTextMessage(websocket, text);
            log.LogW("receiverMsg:"+text);
            JSONObject jsonObject = JSONObject.parseObject(text);
            if(jsonObject != null){
                String action = jsonObject.get("action").toString();
                if(ResAction.INIT_DATA.getAction().equals(action)){
                    log.LogW("服务器推送指令:对人脸数据库进行重置修复");
                    SocketUserUtil.initDataByServer(mContext);
                }else if(ResAction.PULL_DATA.getAction().equals(action)){
                    log.LogW("服务器推送指令:对人脸数据库进行批量更新");
                    List<String> customIdList = ((JSONArray)(jsonObject.get("array"))).toJavaList(String.class);
                    SocketUserUtil.pullDataByServer(mContext,customIdList);
                }else if(ResAction.SAVE_DATA.getAction().equals(action)){
                    log.LogW("服务器推送指令:对人脸数据库进行新增");
                    String object = jsonObject.get("object").toString();
                    SocketUserUtil.saveDataByServer(mContext,object);
                }else if(ResAction.UPDATE_DATA.getAction().equals(action)){
                    log.LogW("服务器推送指令:对人脸数据库进行修改");
                    String object = jsonObject.get("object").toString();
                    SocketUserUtil.updateDataByServer(mContext,object);
                }else if(ResAction.DELETE_DATA.getAction().equals(action)){
                    log.LogW("服务器推送指令:对人脸数据库进行删除");
                    List<String> customIdList = ((JSONArray)(jsonObject.get("array"))).toJavaList(String.class);
                    SocketUserUtil.DeleteByServer(mContext,customIdList);
                }
            }
        }


        @Override
        public void onConnected(WebSocket websocket, Map<String, List<String>> headers)
                throws Exception {
            super.onConnected(websocket, headers);
            log.LogW("连接成功");
            if(FaceDetectManager.queryCount() == 0){
                SocketUserUtil.initDataByServer(mContext);
            }
            setStatus(WsStatus.CONNECT_SUCCESS);
            cancelReconnect();//连接成功的时候取消重连,初始化连接次数
            setStatus(WsStatus.AUTH_SUCCESS);
            startHeartbeat();
            //doAuth();
        }


        @Override
        public void onConnectError(WebSocket websocket, WebSocketException exception)
                throws Exception {
            super.onConnectError(websocket, exception);
            log.LogW("连接错误");
            setStatus(WsStatus.CONNECT_FAIL);
            reconnect();//连接错误的时候调用重连方法
        }

        @Override
        public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer)
                throws Exception {
            super.onDisconnected(websocket, serverCloseFrame, clientCloseFrame, closedByServer);
            log.LogW("断开连接");
            setStatus(WsStatus.CONNECT_FAIL);
            reconnect();//连接断开的时候调用重连方法
        }
    }


    private void setStatus(WsStatus status) {
        this.mStatus = status;
    }


    private WsStatus getStatus() {
        return mStatus;
    }


    public void disconnect() {
        if (ws != null) {
            ws.disconnect();
        }
    }

    public void reconnect() {
        //一旦开启重连，取消心跳
        cancelHeartbeat();
        if (!isNetConnect()) {
            reconnectCount = 0;
            log.logE("重连失败网络不可用");
            try {
                Thread.sleep(maxInterval);
                reconnect();
            } catch (InterruptedException e) {
                e.printStackTrace();
                log.logE("重连线程睡眠失败"+e.getMessage());
            }
            return;
        }

        //这里其实应该还有个用户是否登录了的判断 因为当连接成功后我们需要发送用户信息到服务端进行校验
        if (ws != null &&
                !ws.isOpen() &&//当前连接断开了
                getStatus() != WsStatus.CONNECTING) {//不是正在重连状态

            reconnectCount++;
            setStatus(WsStatus.CONNECTING);
           // cancelHeartbeat();

            long reconnectTime = minInterval;
            if (reconnectCount > 3) {
                if(!StringUtils.isEmpty(url)){
                    url = initUrl();
                }
                long temp = minInterval * (reconnectCount - 2);
                reconnectTime = temp > maxInterval ? maxInterval : temp;
            }

            log.LogW("准备开始第"+reconnectCount+"次重连,重连间隔"+reconnectTime+" -- url:"+url);
            mHandler.postDelayed(mReconnectTask, reconnectTime);
        }
    }

    private String initUrl() {
        try {
            String mime = ApiUtil.getLocalMacAddressFromIp();///ApiUtil.getPhoneMiEI(mContext);
            if(mime == null){
//                CustomerRecordUtil.showToast(mContext,"请授权获取手机序列号");
//                ToastUtils.showLong("请授权获取手机序列号");
               // System.exit(0);
            }
            Map<String,String> parpMap = new HashMap<>();
            parpMap.put("platform","ai-client");
            url = "ws://"+new URL(AIConstants.mBaseUrl).getHost()+":9800"+ File.separator+mime+"?"+ApiUtil.getAllSign(parpMap);
        } catch (MalformedURLException e) {
            log.logE(e.getMessage());
            e.printStackTrace();
        }
        return url;
    }


    private Runnable mReconnectTask = new Runnable() {

        @Override
        public void run() {
            try {
                ws = new WebSocketFactory().createSocket(url, CONNECT_TIMEOUT)
                        .setFrameQueueSize(FRAME_QUEUE_SIZE)//设置帧队列最大值为5
                        .setMissingCloseFrameAllowed(false)//设置不允许服务端关闭连接却未发送关闭帧
                        .addListener(mListener = new WsListener())//添加回调监听
                        .connectAsynchronously();//异步连接
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };


    private void cancelReconnect() {
        reconnectCount = 0;
        mHandler.removeCallbacks(mReconnectTask);
    }


    private boolean isNetConnect() {
        ConnectivityManager connectivity = (ConnectivityManager) AttApp.getContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                // 当前网络是连接的
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    // 当前所连接的网络可用
                    return true;
                }
            }
        }
        return false;
    }
    public enum WsStatus {
        AUTH_SUCCESS,//授权成功
        CONNECT_SUCCESS,//连接成功
        CONNECT_FAIL,//连接失败
        CONNECTING;//正在连接
    }

}
