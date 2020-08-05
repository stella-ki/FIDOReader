package com.challenge.fidoreader.fidoReader

import android.os.AsyncTask
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import com.challenge.fidoreader.fagment.CredentialItem
import java.util.*


class GoogleTranslate(var mProgressBar: ProgressBar) : AsyncTask<Object, Object, ArrayList<CredentialItem>>() {


    override fun onPreExecute() {
        super.onPreExecute()
        Log.v("GoogleTranslate", "onPreExecute")
        mProgressBar.visibility = View.VISIBLE
    }

    override fun onPostExecute(result: ArrayList<CredentialItem>) {
        super.onPostExecute(result)
        Log.v("GoogleTranslate", "onPostExecute")
        mProgressBar.visibility = View.GONE
    }


    override fun doInBackground(vararg params: Object?): ArrayList<CredentialItem> {
        Log.v("GoogleTranslate", "doInBackground")
        val authenticator = params[0] as Authenticator
        /*String vocab = params[0];
        String source = params[1];
        String target = params[2];
        }*/
        var list: ArrayList<CredentialItem> = ArrayList() //null;

        try {
            list = authenticator.getCredentialList() as ArrayList<CredentialItem>
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

}