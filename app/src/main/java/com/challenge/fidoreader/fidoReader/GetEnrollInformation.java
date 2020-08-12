package com.challenge.fidoreader.fidoReader;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.challenge.fidoreader.fagment.CredentialItem;
import com.challenge.fidoreader.fagment.FingerItem;

import java.util.ArrayList;

public class GetEnrollInformation extends AsyncTask<Object, Object, Object> {

    private ProgressBar mProgressBar;

    public GetEnrollInformation(ProgressBar progressBar) {
        super();
        mProgressBar = progressBar;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.v("translate", "onPreExecute");
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPostExecute(Object s) {
        Log.v("translate", "onPostExecute");
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    protected Object doInBackground(Object... params) {
        Log.v("translate", "doInBackground");
        Authenticator authenticator = (Authenticator)params[0];
        /*String vocab = params[0];
        String source = params[1];
        String target = params[2];
        }*/
        ArrayList<FingerItem> list = new ArrayList<>();//null;
        try {
            list = authenticator.readEnrollInformation();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}