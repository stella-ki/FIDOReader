package com.challenge.fidoreader.fagment;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.challenge.fidoreader.R;

public class FingerItem implements Parcelable {
    String templateID;
    String fingerName;
    int resid;

    public FingerItem(String templateID, String fingerName, int resid) {
        Log.v("FingerItem",fingerName);
        this.templateID = templateID;
        this.fingerName = fingerName;
        this.resid = R.drawable.fingerprint;
    }

    public FingerItem(String fingerName) {
        Log.v("FingerItem",fingerName);
        this.fingerName = fingerName;
    }


    protected FingerItem(Parcel in) {
        templateID = in.readString();
        fingerName = in.readString();
        resid = in.readInt();
    }

    public static final Creator<FingerItem> CREATOR = new Creator<FingerItem>() {
        @Override
        public FingerItem createFromParcel(Parcel in) {
            return new FingerItem(in);
        }

        @Override
        public FingerItem[] newArray(int size) {
            return new FingerItem[size];
        }
    };

    public String getFingerName() {
        return fingerName;
    }

    public void setFingerName(String fingerName) {
        this.fingerName = fingerName;
    }

    public int getResid() {
        return resid;
    }

    public void setResid(int resid) {
        this.resid = resid;
    }

    public String getTemplateID() {
        return templateID;
    }

    public void setTemplateID(String templateID) {
        this.templateID = templateID;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(templateID);
        dest.writeString(fingerName);
        dest.writeInt(resid);
    }
}
