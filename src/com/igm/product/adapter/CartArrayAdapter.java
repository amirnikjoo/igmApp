package com.igm.product.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import com.igm.product.R;
import com.igm.product.entity.Cart;

import java.util.ArrayList;

/**
 * User: Amir Nikjoo,  01/23/2016,  04:03 PM
 */
public class CartArrayAdapter extends BaseAdapter implements ListAdapter {
    private final Context context;
    private ArrayList<Cart> cartList;

    public CartArrayAdapter(Context context, ArrayList<Cart> values) {
        this.context = context;
        this.cartList = values;
    }

    @Override
    public int getCount() {
        return cartList.size();
    }

    @Override
    public Object getItem(int i) {
        return cartList.get(i);
    }

    @Override
    public long getItemId(int i) {
        Cart c = cartList.get(i);
        return c.getPart().getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.cart_list_item, null);
        Cart c = cartList.get(position);

        TextView txtDesc = (TextView) rowView.findViewById(R.id.txtCartDescription);
        txtDesc.setText(c.getPart().getDescription());

        TextView txtQty = (TextView) rowView.findViewById(R.id.txtCartQty);
        txtQty.setText(String.valueOf(c.getQty()));

        TextView txtCarType = (TextView) rowView.findViewById(R.id.txtCartCarType);
        int i = c.getPart().getCarType();
        String carName = "";
        if (i == 1) {
            carName = "دوو";
        } else if (i == 2) {
            carName = "پژو";
        } else if (i == 3) {
            carName = "پراید";
        } else {
            carName = "عجیبه!";
        }

        txtCarType.setText(carName);

        ImageView btnImage = (ImageView) rowView.findViewById(R.id.imageButton1);
        boolean x = c.getIsDelButtonVisible();
        if (x)
            btnImage.setVisibility(View.VISIBLE);
        else
            btnImage.setVisibility(View.INVISIBLE);

        return rowView;
    }
}
