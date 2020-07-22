package com.challenge.fidoreader.fido;

import com.challenge.fidoreader.Util.ArrayCustomList;

import java.util.ArrayList;

public class RPs {
    String rp;
    String rpIDHash;
    ArrayCustomList<Credential> credentials;

    private RPs() {

    }

    public RPs(String rp, String rpIDHash) {
        this.credentials = new ArrayCustomList<Credential>();
        this.rp = rp;
        this.rpIDHash = rpIDHash;
    }

    public String getRp() {
        return rp;
    }

    public void setRp(String rp) {
        this.rp = rp;
    }

    public String getRpIDHash() {
        return rpIDHash;
    }

    public void setRpIDHash(String rpIDHash) {
        this.rpIDHash = rpIDHash;
    }

    public ArrayList<Credential> getCredentials() {
        return credentials;
    }

    public void addCredential(Credential cred){
        credentials.add(cred);
    }

    public Credential getCredential(int num){
        return (Credential)credentials.get(num);
    }

    public void setCredentialExpectedCnt(int num){
        credentials.setExpectedCount(num);
    }

    public int getCredentialExpectedCnt(){
        return credentials.getExpectedCount();
    }


    @Override
    public String toString() {
        return "RPs{" +
                "rp='" + rp + '\'' +
                ", rpIDHash='" + rpIDHash + '\'' +
                ", credentials=" + credentials +
                '}';
    }
}
