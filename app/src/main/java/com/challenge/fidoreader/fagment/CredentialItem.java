package com.challenge.fidoreader.fagment;

public class CredentialItem {
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
}
