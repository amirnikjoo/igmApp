package com.igm.product.entity;

import android.os.Parcel;
import android.os.Parcelable;
import com.igm.product.util.Constants;

import java.io.Serializable;

/**
 * User: Amir Nikjoo,  01/23/2016,  12:40 PM
 */
public class Part implements Serializable, Parcelable{
    int id;
    String description;
    int carType;
    int status;

    public Part() {
    }

    public Part(int id, String description, int carType, int status) {
        this.id = id;
        this.description = description;
        this.carType = carType;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCarType() {
        return carType;
    }

    public void setCarType(int carType) {
        this.carType = carType;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.description);
        dest.writeInt(this.carType);
        dest.writeInt(this.status);
    }

    private Part(Parcel in) {
        this.id = in.readInt();
        this.description = in.readString();
        this.carType = in.readInt();
        this.status = in.readInt();
    }

    public static final Creator<Part> CREATOR = new Creator<Part>() {
        public Part createFromParcel(Parcel source) {
            return new Part(source);
        }

        public Part[] newArray(int size) {
            return new Part[size];
        }
    };
}
