package com.igm.product.app;

import com.igm.product.entity.Cart;

import java.util.ArrayList;

/**
 * User: Amir Nikjoo,  02/01/2016,  11:57 AM
 */
public class DataHolder  {
    private ArrayList<Cart> cartArrayList;

    public ArrayList<Cart> getCartArrayList() {
        return cartArrayList;
    }

    public void setCartArrayList(ArrayList<Cart> cartArrayList) {
        this.cartArrayList = cartArrayList;
    }

    private static final DataHolder holder = new DataHolder();
    public static DataHolder getInstance (){
        return holder;
    }
}
