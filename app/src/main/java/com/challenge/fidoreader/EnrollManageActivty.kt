package com.challenge.fidoreader


import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.challenge.fidoreader.frag.reader.FingerEnrollBottomSheetDialog
import com.challenge.fidoreader.frag.data.FingerItem
import com.challenge.fidoreader.frag.reader.FingerItemFragment
import com.challenge.fidoreader.frag.reader.FingerManageBottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*


class EnrollManageActivty : AppCompatActivity(), FingerManageBottomSheetDialog.BottomSheetListener, FingerEnrollBottomSheetDialog.BottomSheetListener{
    val TAG = "EnrollManageActivty"

    lateinit var ea: EnrollmentAdapter
    lateinit var fingerManageBottomSheetDialog: FingerManageBottomSheetDialog
    lateinit var fingerEnrollBottomSheetDialog: FingerEnrollBottomSheetDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enroll_manage)

        var list = intent.getParcelableArrayListExtra<FingerItem>("fingerItem")
        ea = EnrollmentAdapter(list)
        findViewById<ListView>(R.id.listfinger).adapter = ea

        findViewById<FloatingActionButton>(R.id.fab_enroll).setOnClickListener {
            fingerEnrollBottomSheetDialog = FingerEnrollBottomSheetDialog()
            fingerEnrollBottomSheetDialog.show(supportFragmentManager, "exampleBottomSheet")
        }
    }

    override fun onChangeNameBtnClicked(cii: FingerItem, newName: String) {
        if (cii == null) {
            return
        }
        Log.v(TAG, "onChangeNameBtnClicked : " + cii.fingerName)
        try {
            //MainActivity.authenticator.myTag = CardReader.myTag
            MainActivity.authenticator.changeEnrollName(cii.templateID, newName)
            fingerManageBottomSheetDialog.dismiss()
            cii.fingerName = newName;
            ea.notifyDataSetChanged()
            Toast.makeText(this.applicationContext, "Changing name is success", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this.applicationContext, e.message, Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        } finally {
        }
    }

    override fun onDeleteBtnClicked(cii: FingerItem) {
        if (cii == null) {
            return
        }
        Log.v(TAG, "onDeleteBtnClicked : " + cii.fingerName)
        try {
            //MainActivity.authenticator.myTag = CardReader.myTag
            MainActivity.authenticator.deleteEnroll(cii.templateID)
            fingerManageBottomSheetDialog.dismiss()
            ea.deleteItem(cii)
            ea.notifyDataSetChanged()
            Toast.makeText(this.applicationContext, "Deletion is success", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this.applicationContext, e.message, Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        } finally {

        }
    }

    override fun onUpdateFingerEnrollResult(list: FingerItem?) {
        Log.v(TAG, "onUpdateFingerEnrollResult : ")
        if (list != null) {
            ea.addItem(list)
            ea.notifyDataSetChanged()
        }else{
            Toast.makeText(this.applicationContext,"Enrollment is not successful", Toast.LENGTH_SHORT).show();
        }
        fingerEnrollBottomSheetDialog.dismiss()
    }

    inner class EnrollmentAdapter(cii: ArrayList<FingerItem>) : BaseAdapter() {
        val TAG = "EnrollmentAdapter"

        var items: ArrayList<FingerItem> = ArrayList<FingerItem>()
        override fun getCount(): Int {
            return items.size
        }

        override fun getItem(position: Int): Any {
            return items[position]
        }

        fun addItem(credential_item: FingerItem) {
            items.add(credential_item)
        }

        fun deleteItem(credential_item: FingerItem?) {
            items.remove(credential_item)
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var iff = if (convertView == null) {
                FingerItemFragment(applicationContext)
            } else {
                convertView as FingerItemFragment
            }

            val cii: FingerItem = items[position]
            iff.setName(cii.fingerName)
            iff.setImage(cii.resid)
            iff.setOnClickListener(View.OnClickListener {
                fingerManageBottomSheetDialog = FingerManageBottomSheetDialog(cii);
                fingerManageBottomSheetDialog.show(supportFragmentManager, "exampleBottomSheet");
            })
            return iff
        }

        fun reset() {
            items.clear()
        }

        init {
            items = cii
        }
    }
}