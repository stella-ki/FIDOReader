package com.challenge.fidoreader.fido;

public abstract class FIDO2_API {

    public abstract String commands(String... params);
    public abstract String responses(String... params);

}
