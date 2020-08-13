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

import com.challenge.fidoreader.fagment.CredDeleteBottomSheetDialog;
import com.challenge.fidoreader.fagment.CredItemFragment;
import com.challenge.fidoreader.fagment.CredentialItem;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.ArrayList;

public class CredListActivity extends AppCompatActivity implements CredDeleteBottomSheetDialog.BottomSheetListener{
    public final static String TAG = "CredList";

    ListView listView;
    singerAdapter2 sa;

    CredDeleteBottomSheetDialog bottomSheet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cred_list);

        Intent data = getIntent();
        ArrayList<CredentialItem> list = data.getParcelableArrayListExtra("Credentiallist");

        sa = new singerAdapter2(list);
        listView = findViewById(R.id.listview);
        listView.setAdapter(sa);


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //mainActivity.onChangeFragmentToMain();
                sa.reset();
            }
        });

    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Log.v(TAG, "onBackPressed");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.v(TAG, "onStart");
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.v(TAG, "onResume");
    }

    @Override
    public void onPause(){
        super.onPause();
        Log.v(TAG, "onPause");

    }

    @Override
    public void onStop(){
        super.onStop();
        Log.v(TAG, "onStop");
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.v(TAG, "onDestroy");
    }


    @Override
    public void onDeleteButtonClicked(CredentialItem cii) {
        Log.v(TAG, "onDeleteButtonClicked");
        if(cii == null){
            bottomSheet.dismiss();
            return;
        }
        Log.v(TAG, "onDeleteButtonClicked : " + cii.getCredential_id());

        try{
            MainActivity.authenticator.setTag(MainActivity.cardReader.myTag);
            MainActivity.authenticator.deleteCredential(cii.getCredential_id());
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

    class singerAdapter2 extends BaseAdapter {
        ArrayList<CredentialItem> items = new ArrayList<>();

        singerAdapter2(ArrayList<CredentialItem> cii){
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

        public void addItem(CredentialItem credential_item){
            items.add(credential_item);
        }

        public void deleteItem(CredentialItem credential_item){
            items.remove(credential_item);

        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            CredItemFragment iff = null;
            if(convertView == null){
                iff = new CredItemFragment(getApplicationContext());
            }else{
                iff = (CredItemFragment)convertView;
            }
            final CredentialItem cii = items.get(position);
            iff.setRp(cii.getRpid());
            iff.setName(cii.getName());
            iff.setkeyalue(cii.getCredential_id());
            iff.setImage(cii.getResid());

            iff.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   //rmfja mainActivity.onChangeFragmentToDelete(cii);
                    bottomSheet = new CredDeleteBottomSheetDialog(cii);
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