package com.challenge.fidoreader.fagment;

import android.os.Parcel;
import android.os.Parcelable;

import com.challenge.fidoreader.R;

public class CredentialItem implements Parcelable {
    String credential_id;
    String rpid;
    String name;
    int resid;

    public CredentialItem(String credential_id, String rpid, String name, int resid) {
        this.credential_id = credential_id;
        this.rpid = rpid;
        this.resid = resid;
        this.name = name;
    }
    
    
    public CredentialItem(String credential_id, String rpid, String name) {
        this.credential_id = credential_id;
        this.rpid = rpid;
        this.name = name;
        this.resid = R.drawable.authenticator_key;
    }

    protected CredentialItem(Parcel in) {
        credential_id = in.readString();
        rpid = in.readString();
        name = in.readString();
        resid = in.readInt();
    }

    public static final Creator<CredentialItem> CREATOR = new Creator<CredentialItem>() {
        @Override
        public CredentialItem createFromParcel(Parcel in) {
            return new CredentialItem(in);
        }

        @Override
        public CredentialItem[] newArray(int size) {
            return new CredentialItem[size];
        }
    };

    public String getCredential_id() {
        return credential_id;
    }

    public void setCredential_id(String credential_id) {
        this.credential_id = credential_id;
    }

    public String getRpid() {
        return rpid;
    }

    public void setRpid(String rpid) {
        this.rpid = rpid;
    }

    public int getResid() {
        return resid;
    }

    public void setResid(int resid) {
        this.resid = resid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "CredentialItem{" +
                "credential_id='" + credential_id + '\'' +
                ", rpid='" + rpid + '\'' +
                ", name='" + name + '\'' +
                ", resid=" + resid +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(credential_id);
        dest.writeString(rpid);
        dest.writeString(name);
        dest.writeInt(resid);
    }
}
