package com.challenge.fidoreader.fagment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.challenge.fidoreader.MainActivity;
import com.challenge.fidoreader.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;


public class ReaderListFragment extends Fragment {
    public final static String TAG = "ReaderListFragment";
    MainActivity mainActivity;

    ListView listView;
    singerAdapter sa = new singerAdapter();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.v(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_readerlist, container, false);
        listView = view.findViewById(R.id.listview);
        listView.setAdapter(sa);

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              mainActivity.onChangeFragmentToMain();
              sa.reset();
            }
        });

        return view;
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
    public void onDestroyView(){
        super.onDestroyView();
        Log.v(TAG, "onDestroyView");
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.v(TAG, "onDestroy");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity)getActivity();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mainActivity = null;
        Log.v(TAG, "onDetach");
    }

    public void addCredentialItem(CredentialItem ci){
        sa.addItem(new CredentialItem(ci.credential_id, ci.rpid, ci.getName(), R.drawable.authenticator_key));

    }

    public void deleteCredentialItem(CredentialItem ci){
        sa.deleteItem(ci);
        sa.notifyDataSetChanged();
    }

    class singerAdapter extends BaseAdapter{
        ArrayList<CredentialItem> items = new ArrayList<>();


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
                iff = new CredItemFragment(getActivity().getApplicationContext());
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
                    mainActivity.onChangeFragmentToDelete(cii);

                }
            });

            return iff;
        }

        public void reset(){
            items.clear();
        }
    }
}
