package com.igm.product.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * User: Amir Nikjoo,  01/30/2016,  10:50 AM
 */
public class Cart implements Serializable, Parcelable{
    Part part;
    int qty;
    boolean isDelButtonVisible = false;

    public Cart(Part part, int qty) {
        this.part = part;
        this.qty = qty;
    }

    public Cart(Part part, int qty, boolean isDelButtonVisible) {
        this.part = part;
        this.qty = qty;
        this.isDelButtonVisible = isDelButtonVisible;
    }

    public Part getPart() {
        return part;
    }

    public void setPart(Part part) {
        this.part = part;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public boolean getIsDelButtonVisible() {
        return isDelButtonVisible;
    }

    public void setIsDelButtonVisible(boolean isDelButtonVisible) {
        this.isDelButtonVisible = isDelButtonVisible;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(this.part);
        dest.writeInt(this.qty);
//        dest.writeInt(this.isDelButtonVisible);
    }

    private Cart(Parcel in) {
        this.part = (Part) in.readSerializable();
        this.qty = in.readInt();
//        this.isDelButtonVisible = in.readInt();
    }

    public static final Creator<Cart> CREATOR = new Creator<Cart>() {
        public Cart createFromParcel(Parcel source) {
            return new Cart(source);
        }

        public Cart[] newArray(int size) {
            return new Cart[size];
        }
    };
}
