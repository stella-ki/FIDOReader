package com.challenge.fidoreader.fagment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.OnLifecycleEvent;

import com.challenge.fidoreader.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class FingerManageBottomSheetDialog extends BottomSheetDialogFragment {
    private BottomSheetListener mListener;
    private FingerItem cii;
    public FingerManageBottomSheetDialog(FingerItem cii){
        this.cii = cii;

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_btm_fp_sheet_layout, container, false);
        TextView textViewID =  v.findViewById(R.id.txtView_fpinfo);

        textViewID.setText(cii.fingerName);

//        Button button1 = v.findViewById(R.id.btn_fp_name);
        Button button2 = v.findViewById(R.id.btn_fp_delete);
        Button button3 = v.findViewById(R.id.btn_fp_Cancel);
        /*button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onChangeNameBtnClicked(cii);
            }
        });*/
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onDeleteBtnClicked(cii);
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


        return v;
    }

    public interface BottomSheetListener {
        void onChangeNameBtnClicked(FingerItem cii);
        void onDeleteBtnClicked(FingerItem cii);
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