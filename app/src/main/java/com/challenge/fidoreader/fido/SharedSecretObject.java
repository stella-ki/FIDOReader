package com.challenge.fidoreader.fido;

import androidx.annotation.NonNull;

import com.challenge.fidoreader.Util.Util;

import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECPublicKeySpec;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Security;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;

import javax.crypto.KeyAgreement;

public class SharedSecretObject {

    private boolean isSharedSecretReady = false;
    private String sharedSecret;
    private String publickey;
    private String privatekey;

    public void init(){
        publickey = "";
        privatekey   = "";
        sharedSecret = "";
        isSharedSecretReady = false;
    }

    public String getPublickey() throws Exception{
        if(!isSharedSecretReady){
            throw new Exception("Generated key is required");
        }
        return publickey;
    }


    public String getPrivatekey() throws Exception{
        if(!isSharedSecretReady){
            throw new Exception("Generated key is required");
        }
        return privatekey;
    }

    public void generateSharedSecret(String authenticator_pubKey) throws Exception{
        //Generate platform key
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("EC");
        kpg.initialize(256);
        KeyPair kp = kpg.generateKeyPair();
        byte[] outPubK = kp.getPublic().getEncoded();
        byte[] outPriK = ((ECPrivateKey)kp.getPrivate()).getS().toByteArray();

        //platform public key and private key
        publickey = Util.getHexString(outPubK).substring(54);
        privatekey = Util.getHexString(outPriK);

        //make agreementkey
        Security.addProvider(new BouncyCastleProvider());
        KeyFactory keyFactory = KeyFactory.getInstance("ECDSA");
        ECPublicKey pubkey = (ECPublicKey)keyFactory.generatePublic(
                new ECPublicKeySpec(ECNamedCurveTable.getParameterSpec("secp256r1").getCurve().createPoint(
                        new BigInteger(authenticator_pubKey.substring(0,64),16), new BigInteger(authenticator_pubKey.substring(64), 16)),
                        ECNamedCurveTable.getParameterSpec("secp256r1")
                ));
        KeyAgreement ka = KeyAgreement.getInstance("ECDH");
        ka.init(kp.getPrivate());
        ka.doPhase(pubkey, true);

        byte[] b_sharedSecret = ka.generateSecret();

        sharedSecret = Util.getHexString(Util.sha_256(b_sharedSecret));

        isSharedSecretReady = true;
    }

    public String getSharedSecret() {
        return sharedSecret;
    }

    @NonNull
    @Override
    public String toString() {
        return "SharedSecretObject{" +
                "sharedSecret='" + sharedSecret + '\'' +
                ", publickey='" + publickey + '\'' +
                ", privatekey='" + privatekey + '\'' +
                '}';
    }
}
