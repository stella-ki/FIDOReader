package com.challenge.fidoreader.fido;

public class Credential {

    String user;
    String credentialID;
    String publicKey;
    String credProtect;
    
    public Credential(String user, String credentialID, String publicKey){
        this.user = user;
        this.credentialID = credentialID;
        this.publicKey = publicKey;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getCredentialID() {
        return credentialID;
    }

    public void setCredentialID(String credentialID) {
        this.credentialID = credentialID;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getCredProtect() {
        return credProtect;
    }

    public void setCredProtect(String credProtect) {
        this.credProtect = credProtect;
    }

    @Override
    public String toString() {
        return "Credential{" +
                "user='" + user + '\'' +
                ", credentialID='" + credentialID + '\'' +
                ", publicKey='" + publicKey + '\'' +
                ", credProtect='" + credProtect + '\'' +
                '}';
    }
}
