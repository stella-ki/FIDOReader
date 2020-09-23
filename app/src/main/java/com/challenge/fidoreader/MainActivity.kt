package com.challenge.fidoreader

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.challenge.fidoreader.Util.Code
import com.challenge.fidoreader.Util.ParcelableActivityData
import com.challenge.fidoreader.Util.cardReader
import com.challenge.fidoreader.frag.reader.AuthenticatorFragment
import com.challenge.fidoreader.frag.reader.ReaderButtonFragment
import com.challenge.fidoreader.fidoReader.Authenticator
import com.challenge.fidoreader.frag.auth.AuthPreferenceFragement
import com.challenge.fidoreader.reader.CardReader
import com.challenge.fidoreader.reader.NFCSender
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity(){
    val TAG = "MainActivity"

    lateinit var readerButtonFragment : ReaderButtonFragment
    //lateinit var authenticatorFragment: AuthenticatorFragment
    lateinit var authPreferenceFragement: AuthPreferenceFragement

    companion object{
        var authenticator: Authenticator = Authenticator(NFCSender())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        var toolbar : Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        readerButtonFragment = ReaderButtonFragment()
        //authenticatorFragment = AuthenticatorFragment()
        authPreferenceFragement = AuthPreferenceFragement()

        var fraglist = mutableListOf<Fragment>(readerButtonFragment, authPreferenceFragement)
        var titles = mutableListOf<String>("FIDO2 Reader", "Authenticators")

        var tabs : TabLayout = findViewById(R.id.tabLayout)

        var viewPager2 = findViewById<ViewPager2>(R.id.view_pager)

        viewPager2.adapter = ViewPagerAdapter(this, fraglist);
        TabLayoutMediator(tabs,  viewPager2){
            tab, position ->
            tab.text = titles.get(position)
        }.attach()

        cardReader = CardReader(this, intent)

    }

    override fun onStart() {
        super.onStart()
        Log.v(TAG, "onStart")

        if(readerButtonFragment.isReady){
            if(readerButtonFragment.isResumed){
                readerButtonFragment.setResult(cardReader, cardReader.result)
            }
        }
    }


    override fun onResume() {
        super.onResume()
        Log.v(TAG, "onResume")
        cardReader.onResume(this)
        if(readerButtonFragment.isReady){
            if(readerButtonFragment.isResumed){
                readerButtonFragment.setResult(cardReader, cardReader.result)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        Log.v(TAG, "onPause")
        cardReader.onPause(this)
        if(readerButtonFragment.isReady){
            if(readerButtonFragment.isResumed){
                readerButtonFragment.setResult(cardReader, cardReader.result)
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        cardReader.resolveIntent(intent!!)
        if(readerButtonFragment.isReady){
            if(readerButtonFragment.isResumed){
                readerButtonFragment.setResult(cardReader, cardReader.result)
            }
        }
    }



    fun showActivityList(padata : ParcelableActivityData, list : ArrayList<Parcelable>?){
        Log.v(TAG, "onchangeFragment")
        if(list == null){
            Toast.makeText(this.applicationContext,"Error 발생", Toast.LENGTH_SHORT).show();
            return;
        }else if(list.size == 0){
            Toast.makeText(this.applicationContext, padata.errormsg, Toast.LENGTH_SHORT).show();
            if(padata.isEnd){
                return;
            }
        }

        try{
            val intent = Intent(applicationContext, padata.cls)
            intent.putParcelableArrayListExtra(padata.listkeyword, list)
            startActivityForResult(intent, Code.requestCode);

        }catch (e:Exception){
            Toast.makeText(this.applicationContext, "Error 발생", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }

    }

    /*fun showCredentialList(list : ArrayList<CredentialItem> ){
        Log.v(TAG, "onchangeFragment")
        if(list == null){
            Toast.makeText(this.applicationContext,"Error 발생", Toast.LENGTH_SHORT).show();
            return;
        }else if(list.size == 0){
            Toast.makeText(this.applicationContext,"Credential is not exist", Toast.LENGTH_SHORT).show();
            return;
        }

        try{
            val intent = Intent(applicationContext, CredListActivity::class.java)
            intent.putParcelableArrayListExtra("Credentiallist", list)
            startActivityForResult(intent, Code.requestCode);

        }catch (e:Exception){
            Toast.makeText(this.applicationContext, "Error 발생", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }

    }*/


/*

    fun showEnrollmentList(list : ArrayList<FingerItem> ){
        Log.v(TAG, "onchangeFragment")
        if(list == null){
            Toast.makeText(this.applicationContext,"Error 발생", Toast.LENGTH_SHORT).show();
            return;
        }else if(list.size == 0){
            Toast.makeText(this.applicationContext,"Enrollment is not exist", Toast.LENGTH_SHORT).show();
        }
        try {
            val intent = Intent(applicationContext, EnrollManageActivty::class.java)
            intent.putParcelableArrayListExtra("fingerItem", list)
            startActivityForResult(intent, Code.requestCode)
        } catch (e: java.lang.Exception) {
            Toast.makeText(this.applicationContext, "Error 발생", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }
*/

}
