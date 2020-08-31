package com.challenge.fidoreader

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.challenge.fidoreader.fagment.CredDeleteBottomSheetDialog
import com.challenge.fidoreader.fagment.CredItemFragment
import com.challenge.fidoreader.fagment.data.CredentialItem

class CredListActivity : AppCompatActivity(), CredDeleteBottomSheetDialog.BottomSheetListener{
    val TAG = "CredListActivity"

    lateinit var bottomSheetDialog: CredDeleteBottomSheetDialog
    lateinit var sa: CredentialListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cred_list)

        var list = intent.getParcelableArrayListExtra<CredentialItem>("Credentiallist")
        sa = CredentialListAdapter(list)

        findViewById<ListView>(R.id.listview).adapter = sa
    }

    override fun onDeleteButtonClicked(cii: CredentialItem?) {
        if(cii == null){
            return
        }
        Log.v(TAG, "onDeleteButtonClicked - CredentialItem : ${cii.credential_id}")

        try{
            //MainActivity.authenticator.myTag = CardReader.myTag
            MainActivity.authenticator.deleteCredential(cii.credential_id)

            bottomSheetDialog.dismiss()
            sa.deleteItem(cii)
            sa.notifyDataSetChanged()

            Toast.makeText(this.applicationContext, "Deletion is success", Toast.LENGTH_SHORT).show()
        }catch(e: Exception){
            Toast.makeText(this.applicationContext, e.message, Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    inner class CredentialListAdapter(var items : ArrayList<CredentialItem>) : BaseAdapter(){
        val TAG = "CredentialListAdapter"
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            var frag: CredItemFragment = if(convertView == null){
                CredItemFragment(applicationContext)
            }else{
                convertView as CredItemFragment
            }

            var cli = items[position]
            frag.setRP(cli.rpid)
            frag.setName(cli.name)
            frag.setKeyvalue(cli.credential_id)
            frag.setImage(cli.resid)

            frag.setOnClickListener {
                bottomSheetDialog = CredDeleteBottomSheetDialog(cli)
                bottomSheetDialog.show(supportFragmentManager, "BottomSheet")
            }

            return frag
        }

        fun addItem(item: CredentialItem){
            items.add(item)
        }

        fun deleteItem(item: CredentialItem){
            items.remove(item)
        }

        override fun getItem(position: Int): Any {
            return items[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return items.size
        }

    }
}