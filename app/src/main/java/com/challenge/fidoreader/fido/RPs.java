package com.challenge.fidoreader.fido;

import java.util.ArrayList;

public class RPs {
    String rp;
    String rpIDHash;
    ArrayList<Credential> credentials;

    private RPs() {

    }

    public RPs(String rp, String rpIDHash) {
        this.credentials = new ArrayList<Credential>();
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
        return credentials.get(num);
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
