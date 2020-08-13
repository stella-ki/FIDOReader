package com.challenge.fidoreader;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.challenge.fidoreader.fagment.FingerEnrollBottomSheetDialog;
import com.challenge.fidoreader.fagment.FingerItem;
import com.challenge.fidoreader.fagment.FingerItemFragment;
import com.challenge.fidoreader.fagment.FingerManageBottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class EnrollManageActivty extends AppCompatActivity implements FingerManageBottomSheetDialog.BottomSheetListener, FingerEnrollBottomSheetDialog.BottomSheetListener{
    public final static String TAG = "CredList";

    ListView listView;
    singerAdapter sa;
    FingerManageBottomSheetDialog bottomSheet;
    FingerEnrollBottomSheetDialog bottomSheet2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enroll_manage_activty);

        Intent data = getIntent();
        ArrayList<FingerItem> list = data.getParcelableArrayListExtra("fingerItem");

        sa = new singerAdapter(list);
        listView = findViewById(R.id.listfinger);
        listView.setAdapter(sa);


        FloatingActionButton fab = findViewById(R.id.fab_enroll);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheet2 = new FingerEnrollBottomSheetDialog();
                bottomSheet2.show(getSupportFragmentManager(), "exampleBottomSheet");
            }
        });

    }

    @Override
    public void onChangeNameBtnClicked(FingerItem cii) {
        if(cii == null){
            return;
        }
        Log.v(TAG, "onChangeNameBtnClicked : " + cii.getFingerName());

        try{
            MainActivity.authenticator.setTag(MainActivity.cardReader.myTag);
            MainActivity.authenticator.changeEnroll(cii.getTemplateID(), "");
            bottomSheet.dismiss();
            sa.deleteItem(cii);
            sa.notifyDataSetChanged();

            Toast.makeText(this.getApplicationContext(),"Deletion is success", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Toast.makeText(this.getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }finally {
        }
    }

    @Override
    public void onDeleteBtnClicked(FingerItem cii) {
        if(cii == null){
            return;
        }
        Log.v(TAG, "onDeleteBtnClicked : " + cii.getFingerName());

        try{
            MainActivity.authenticator.setTag(MainActivity.cardReader.myTag);
            MainActivity.authenticator.deleteEnroll(cii.getTemplateID());
            bottomSheet.dismiss();
            sa.deleteItem(cii);
            sa.notifyDataSetChanged();

            Toast.makeText(this.getApplicationContext(),"Deletion is success", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Toast.makeText(this.getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }finally {
        }
    }

    @Override
    public void onButtonClicked(FingerItem list) {
        Log.v(TAG, "onButtonClicked : ");
        if(list != null){
            sa.addItem(list);
            sa.notifyDataSetChanged();
        }
        bottomSheet2.dismiss();
    }


    class singerAdapter extends BaseAdapter {
        ArrayList<FingerItem> items = new ArrayList<>();

        singerAdapter(ArrayList<FingerItem> cii){
            this.items = cii;
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        public void addItem(FingerItem credential_item){
            items.add(credential_item);
        }

        public void deleteItem(FingerItem credential_item){
            items.remove(credential_item);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            FingerItemFragment iff = null;
            if(convertView == null){
                iff = new FingerItemFragment(getApplicationContext());
            }else{
                iff = (FingerItemFragment)convertView;
            }
            final FingerItem cii = items.get(position);
            iff.setName(cii.getFingerName());
            iff.setImage(cii.getResid());

            iff.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //rmfja mainActivity.onChangeFragmentToDelete(cii);
                    bottomSheet = new FingerManageBottomSheetDialog(cii);
                    bottomSheet.show(getSupportFragmentManager(), "exampleBottomSheet");
                }
            });

            return iff;
        }

        public void reset(){
            items.clear();
        }
    }
}