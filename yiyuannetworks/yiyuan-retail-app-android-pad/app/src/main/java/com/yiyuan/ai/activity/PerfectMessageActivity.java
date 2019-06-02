package com.yiyuan.ai.activity;

import android.os.Handler;
import android.os.Message;
import android.support.design.widget.MultiOperationInputLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aiwinn.base.activity.BaseActivity;
import com.aiwinn.base.util.StringUtils;
import com.aiwinn.base.util.ToastUtils;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseItemDraggableAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yiyuan.ai.R;
import com.yiyuan.ai.common.DeviceUtil;
import com.yiyuan.ai.common.DialogUtil;
import com.yiyuan.ai.common.HttpUtil;
import com.yiyuan.ai.common.ValidateUtil;
import com.yiyuan.ai.model.CustomerModel;
import com.yiyuan.ai.model.Tag;
import com.yiyuan.ai.widget.StockTextView;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangyu on 2019/4/12.
 */

public class PerfectMessageActivity extends BaseActivity{

    private RecyclerView recyclerView;
    private ArrayList<String> mStringArrayList = new ArrayList<>();
    private TagsAdapter tagsAdapter;
    private CustomerModel customerModel;
    private Handler timeHandler = new Handler();
    private int smsCodeTime = 0;

    private ImageView tvSHowYe;
    private ImageView tvBack;
    private TextView btnNext;
    private LinearLayout ll2;
    private LinearLayout ll3;

    private int page = 1;


    private ImageView user_icon_view_img;//头像
    private StockTextView gendarMan;//性别男
    private StockTextView genderWoman;//性别女
    private int pixelFromDip = 0;
    private EditText name;//姓名
    private EditText age;//年龄
    private MultiOperationInputLayout phone_moil;
    private EditText phone;//电话号码
    private EditText code;//手机验证码
    private EditText email;//邮箱

    private List<String> checkedTagList = new ArrayList<>();

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 0){
                DialogUtil.dissmisDialog();
                if(msg.obj != null){
                    List<Tag> tagList = (List<Tag>) msg.obj;
                    for(Tag tag:tagList){
                        mStringArrayList.add(tag.getId()+";"+tag.getName()+";"+"0");
                    }
                    initTags();
                    tagsAdapter.replaceData(mStringArrayList);
                }
            }else if(msg.what == 2){
                DialogUtil.dissmisDialog();
                ToastUtils.showLong(getString(R.string.perfect_save_success));
                finish();
            }else if(msg.what == -1){
                DialogUtil.dissmisDialog();
                ToastUtils.showLong(getString(R.string.perfect_save_fail));
            }else if(msg.what == 3){//验证码错误
                DialogUtil.dissmisDialog();
                ToastUtils.showLong(msg.obj.toString());
            }
        }
    };
    @Override
    public int getLayoutId() {
        return R.layout.ai_activity_perfect_message;
    }
    @Override
    public void initViews() {
        customerModel = (CustomerModel) getIntent().getSerializableExtra("customerModel");
        recyclerView = findViewById(R.id.rlv_tags);
        tvSHowYe = findViewById(R.id.shouye);
        tvBack = findViewById(R.id.back);
        btnNext = findViewById(R.id.btn_next);
        ll2 = findViewById(R.id.ll2);
        ll3 = findViewById(R.id.ll3);

        user_icon_view_img = findViewById(R.id.user_icon_view_img);
        gendarMan = findViewById(R.id.gender_man);
        genderWoman = findViewById(R.id.gender_woman);
        name = findViewById(R.id.name);
        age = findViewById(R.id.age);
        phone_moil = findViewById(R.id.phone_moil);
        phone = findViewById(R.id.phone);
        code = findViewById(R.id.code);
        email = findViewById(R.id.email);

        GridLayoutManager lm = new GridLayoutManager(PerfectMessageActivity.this, 4);
        recyclerView.setLayoutManager(lm);
        mStringArrayList = new ArrayList<>();
        mStringArrayList.clear();
        tagsAdapter = new TagsAdapter(mStringArrayList);
        recyclerView.setAdapter(tagsAdapter);
        prepareActivityString();
    }

    private void prepareActivityString() {
        DialogUtil.showDialog(mContext,getString(R.string.perfect_tags_loading));
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = Message.obtain();
                message.what = 0;
                try {
                    List<Tag> tagList = HttpUtil.getInstance().getTags();
                    message.obj = tagList;
                }catch (Exception e){
                    e.printStackTrace();
                    ToastUtils.showLong(getString(R.string.perfect_tags_error));
                }
                mHandler.sendMessage(message);
            }
        }).start();
    }

    public void initTags(){
        List<Tag> tagList = customerModel.tags;
        for (int i=0;i<mStringArrayList.size();i++){
            String str = mStringArrayList.get(i);
            String id = str.split(";")[0];
            String name = str.split(";")[1];
            for(Tag tag : tagList){
                if(id.equals(String.valueOf(tag.getId())) && name.equals(tag.getName())){
                    mStringArrayList.set(i,str.substring(0,str.length()-1)+"1");
                    break;
                }
            }
        }
    }

    @Override
    public void initData() {
        pixelFromDip =  DeviceUtil.getPixelFromDip(this, 30);
        if(customerModel == null){
            ToastUtils.showLong(getString(R.string.perfect_customer_info_error));
            finish();
            return;
        }
        Glide.with(getApplicationContext()).load(customerModel.imgUrl).into(user_icon_view_img);
        if("男".equals(customerModel.gender)){
            selectMan();
        }else{
            selectWoMan();
        }
        name.setText(customerModel.name);
        age.setText(customerModel.age+"");
        phone.setText(customerModel.mobile);
        email.setText(customerModel.email);

    }



    private void selectMan() {
        gendarMan.setSelected(true);
        genderWoman.setSelected(false);
        gendarMan.setCompoundDrawable(getResources().getDrawable(R.drawable.shop_register_man_select), 0, pixelFromDip, pixelFromDip);
        genderWoman.setCompoundDrawable(getResources().getDrawable(R.drawable.shop_register_woman), 0, pixelFromDip, pixelFromDip);
    }

    private void selectWoMan() {
        genderWoman.setSelected(true);
        gendarMan.setSelected(false);
        gendarMan.setCompoundDrawable(getResources().getDrawable(R.drawable.shop_register_man), 0, pixelFromDip, pixelFromDip);
        genderWoman.setCompoundDrawable(getResources().getDrawable(R.drawable.shop_register_woman_select), 0, pixelFromDip, pixelFromDip);
    }

    @Override
    public void initListeners() {
        phone_moil.setOperationTextViewOnclickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!ValidateUtil.valiedateInput(phone,phone.getText().toString(),2,true)){
                    return;
                }
                if(smsCodeTime == 0 ) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            HttpUtil.getInstance().getSmsCode(customerModel.customerId,phone.getText().toString());
                        }
                    }).start();
                    Runnable r = new Runnable() {
                        @Override
                        public void run() {
                            if(smsCodeTime>0) {
                                phone_moil.setOperationTextString(getString(R.string.perfect_code_again)+"(" + smsCodeTime-- + ")");
                                timeHandler.postDelayed(this, 1000);
                            }else{
                                phone_moil.setOperationTextString(getString(R.string.perfect_code));
                            }
                        }
                    };
                    smsCodeTime = 59;
                    timeHandler.postDelayed(r, 1000);//延时100毫秒
                }
            }
        });

        gendarMan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectMan();
            }
        });
        genderWoman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectWoMan();
            }
        });
        tagsAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                LinearLayout ll = view.findViewById(R.id.tag_ll);
                if("0".equals(ll.getTag().toString())){
                    ll.setBackgroundResource(R.drawable.circle_blue);
                    ll.setTag("1");
                    mStringArrayList.set(position,mStringArrayList.get(position).substring(0,mStringArrayList.get(position).length()-1)+"1");
                    checkedTagList.add(mStringArrayList.get(position));
                }else{
                    ll.setBackgroundResource(R.drawable.circle);
                    ll.setTag("0");
                    mStringArrayList.set(position,mStringArrayList.get(position).substring(0,mStringArrayList.get(position).length()-1)+"0");
                    checkedTagList.remove(mStringArrayList.get(position).substring(0,mStringArrayList.get(position).length()-1)+"1");
                }
            }
        });

        //点击返回按钮
        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                page = 1;
                tvBack.setVisibility(View.GONE);
                btnNext.setText(getString(R.string.perfect_next));
                ll3.setVisibility(View.GONE);
                ll2.setVisibility(View.VISIBLE);
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(page == 1) {
                    page = 2;
                    btnNext.setText(getString(R.string.perfect_save));
                    tvBack.setVisibility(View.VISIBLE);
                    ll2.setVisibility(View.GONE);
                    ll3.setVisibility(View.VISIBLE);
                }else if(page == 2){

                    if(!ValidateUtil.valiedateInput(name,name.getText().toString(),9,false)){
                        return;
                    }
                    final String nameHttp = name.getText().toString();
                    if(!ValidateUtil.valiedateInput(age,age.getText().toString(),1,false)){
                        return;
                    }
                    final String ageHttp = age.getText().toString();
                    if(!StringUtils.isEmpty(code.getText().toString())){
                        if(!ValidateUtil.valiedateInput(phone,phone.getText().toString(),2,false)){
                            return;
                        }
                        if(!ValidateUtil.valiedateInput(code,code.getText().toString(),3,false)){
                            return;
                        }
                    }
                    final String phoneHttp = phone.getText().toString();
                    if(!ValidateUtil.valiedateInput(email,email.getText().toString(),5,true)){
                        return;
                    }
                    final String emailHttp = email.getText().toString();
                    final String codeHttp = code.getText().toString();
                    final String genderHttp = gendarMan.isSelected()?"男":"女";
                    //获取tags
                    final com.alibaba.fastjson.JSONArray jsonArray = new com.alibaba.fastjson.JSONArray();
                    for(String str : checkedTagList){
                        jsonArray.add(str.split(";")[0]);
                    }
                    DialogUtil.showDialog(mContext,getString(R.string.perfect_saveing));
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String data = "error";
                            try {
                                data = HttpUtil.getInstance().updateCustomer(customerModel.imgUrl, customerModel.customerId,emailHttp, jsonArray, genderHttp, nameHttp, ageHttp, phoneHttp, codeHttp);
                            }catch (Exception e){
                                e.printStackTrace();
                            }finally {
                                if(!"error".equals(data)){
                                    if(data instanceof String && !data.startsWith("{")){
                                        Message msg = mHandler.obtainMessage();
                                        msg.obj = data;
                                        msg.what = 3;
                                        mHandler.sendMessage(msg);
                                    }else{
                                        mHandler.sendEmptyMessage(2);
                                    }
                                }else{
                                    mHandler.sendEmptyMessage(-1);
                                }
                            }
                        }
                    }).start();
                }
            }
        });

        tvSHowYe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogUtil.finishActivity(mContext);
            }
        });
    }

    class TagsAdapter extends BaseItemDraggableAdapter<String, BaseViewHolder> {

        public TagsAdapter(ArrayList<String> strings) {
            super(R.layout.tag_item_activity, strings);
        }
        @Override
        protected void convert(BaseViewHolder helper, String item) {
            if (item != null) {
                helper.setText(R.id.activity,item.split(";")[1]);
                LinearLayout ll = helper.getView(R.id.tag_ll);
                if(item.split(";")[2].equals("1")){
                    ll.setBackgroundResource(R.drawable.circle_blue);
                    ll.setTag("1");
                    checkedTagList.add(item);
                }
            }
        }
    }
}
