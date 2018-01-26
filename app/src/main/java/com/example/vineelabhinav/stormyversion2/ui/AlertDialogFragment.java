package com.example.vineelabhinav.stormyversion2.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;

import com.example.vineelabhinav.stormyversion2.R;

/**
 * Created by vineel abhinav on 1/16/2018.
 */

public class AlertDialogFragment extends DialogFragment{

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context context=getActivity();
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.error_title)).setMessage(context.getString(R.string.error_message)).setPositiveButton(context.getString(R.string.error_ok_button_text),null);
        AlertDialog dialog=builder.create();
        return dialog;
    }
}
