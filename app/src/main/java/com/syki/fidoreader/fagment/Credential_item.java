package com.syki.fidoreader.fagment;

public class Credential_item {
    String credential_id;
    String rpid;
    int resid;

    public Credential_item(String credential_id, String rpid, int resid) {
        this.credential_id = credential_id;
        this.rpid = rpid;
        this.resid = resid;
    }

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

    @Override
    public String toString() {
        return "Credential_item{" +
                "credential_id='" + credential_id + '\'' +
                ", rpid='" + rpid + '\'' +
                ", resid=" + resid +
                '}';
    }
}
