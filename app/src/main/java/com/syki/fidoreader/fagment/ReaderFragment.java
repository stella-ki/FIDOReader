package com.syki.fidoreader.fagment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.syki.fidoreader.R;

import java.util.ArrayList;


public class ReaderFragment extends Fragment {
    ListView listView;
    singerAdapter sa = new singerAdapter();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reader, container, false);
        listView = view.findViewById(R.id.listview);
        listView.setAdapter(sa);
        return view;
    }

    public void addCredentialItem(Credential_item ci){
        sa.addItem(new Credential_item("test1", "testtest1", R.drawable.ic_icc_off));

    }

    class singerAdapter extends BaseAdapter{
        ArrayList<Credential_item> items = new ArrayList<>();


        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        public void addItem(Credential_item credential_item){
            items.add(credential_item);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ItemFragment iff = null;
            if(convertView == null){
                iff = new ItemFragment(getActivity().getApplicationContext());
            }else{
                iff = (ItemFragment)convertView;
            }
            Credential_item cii = items.get(position);
            iff.setName(cii.getCredential_id());
            iff.setMobile(cii.getRpid());
            iff.setImage(cii.getResid());

            return iff;
        }
    }
}
