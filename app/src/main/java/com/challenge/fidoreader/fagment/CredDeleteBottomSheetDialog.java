package com.challenge.fidoreader.fagment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.challenge.fidoreader.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class CredDeleteBottomSheetDialog extends BottomSheetDialogFragment {
    private BottomSheetListener mListener;
    private CredentialItem cii;
    public CredDeleteBottomSheetDialog(CredentialItem cii){
        this.cii = cii;

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_bottom_sheet_layout, container, false);
        TextView textViewID =  v.findViewById(R.id.txtView_CredInfo_id);
        TextView textViewKey = v.findViewById(R.id.txtView_CredInfo_key);

        textViewID.setText(cii.getRpid());
        textViewKey.setText(cii.getCredential_id());

        Button button1 = v.findViewById(R.id.btn_cred_delete);
        Button button2 = v.findViewById(R.id.btn_cred_Cancel);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onButtonClicked(cii);
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onButtonClicked(null);
                dismiss();
            }
        });
        return v;
    }
    public interface BottomSheetListener {
        void onButtonClicked(CredentialItem cii);
       //void onButtonClicked(String str);
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (BottomSheetListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement BottomSheetListener");
        }
    }
}