package com.challenge.fidoreader;

import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import com.challenge.fidoreader.fagment.CredentialItem;
import com.challenge.fidoreader.fido.Authenticator;

import java.util.ArrayList;

public class GoogleTranslate extends AsyncTask<Object, Object, Object> {

    private ProgressBar mProgressBar;

    public GoogleTranslate(ProgressBar progressBar) {
        super();
        mProgressBar = progressBar;
    }

    @Override
    protected void onPreExecute() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPostExecute(Object s) {
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    protected Object doInBackground(Object... params) {
        Authenticator authenticator = (Authenticator)params[0];
        /*String vocab = params[0];
        String source = params[1];
        String target = params[2];
        }*/
        ArrayList<CredentialItem> list = null;
        try {
            list = authenticator.getCredentialList();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}