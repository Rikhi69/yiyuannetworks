package com.lxl.shop.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lxl.shop.R;
import com.lxl.shop.viewmodel.CustomerShoppingItemModel;
import com.lxl.shop.viewmodel.CustomerShoppingRecord;

import java.util.List;

/**
 * Created by yanglei on 2018/11/24.
 */

public class CustomerShoppingAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<CustomerShoppingRecord> shoppingHistory;

    public CustomerShoppingAdapter(Context context) {
        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return shoppingHistory.size();
    }

    @Override
    public CustomerShoppingRecord getItem(int i) {
        return shoppingHistory.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.shop_view_record_item, parent, false);
        }
        CustomerShoppingRecord item = getItem(position);
        bindData(convertView, item);
        return convertView;
    }

    private void bindData(View convertView, CustomerShoppingRecord item) {
        TextView shoppingDate = convertView.findViewById(R.id.shopping_date);
        TextView shoppingDesc = convertView.findViewById(R.id.shopping_desc);
        LinearLayout listView = convertView.findViewById(R.id.shopping_list_view);

        shoppingDate.setText(item.createTime);
        shoppingDesc.setText("共" + item.salesDetailVOS.size() + "件商品，合计 ¥" + item.getSumPrice());
        listView.removeAllViews();
        List<CustomerShoppingItemModel> shoppingList = item.salesDetailVOS;
        for (int i = 0; i < shoppingList.size(); i++) {
            CustomerShoppingItemModel itemModel = shoppingList.get(i);
            View inflate = mInflater.inflate(R.layout.shop_view_shopping_list_item, listView, false);
            ImageView itemIcon = inflate.findViewById(R.id.item_icon);
            TextView itemName = inflate.findViewById(R.id.item_name);
            TextView itemCount = inflate.findViewById(R.id.item_count);
            TextView itemPrice = inflate.findViewById(R.id.item_price);
            itemName.setText(itemModel.product.name);
            itemCount.setText("*" + itemModel.quantity);
            itemPrice.setText("¥" + itemModel.product.price);
            listView.addView(inflate);
        }
    }

    public void setShoppingHistory(List<CustomerShoppingRecord> shoppingHistory) {
        this.shoppingHistory = shoppingHistory;
    }
}
