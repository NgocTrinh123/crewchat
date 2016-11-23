package com.dazone.crewchat.customs;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import com.dazone.crewchat.R;
import com.dazone.crewchat.utils.Utils;


public class AlertDialogView {

    public static void normalAlertDialog(final Context context,String title,String message,String okButton, final OnAlertDialogViewClickEvent clickEvent){
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setPositiveButton(okButton, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (clickEvent != null) {
                    clickEvent.onOkClick(dialog);
                } else {
                    dialog.dismiss();
                }
            }
        });
        alertDialog.show();
    }

    public static void normalAlertDialogNotBack(final Context context,String title,String message,String okButton,String noButton, final OnAlertDialogViewClickEvent clickEvent){
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(title);
        alertDialog.setCancelable(false);
        alertDialog.setMessage(message);
        alertDialog.setPositiveButton(okButton, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (clickEvent != null) {
                    clickEvent.onOkClick(dialog);
                } else {
                    dialog.dismiss();
                }
            }
        });

        alertDialog.setNegativeButton(noButton, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (clickEvent != null) {
                    clickEvent.onCancelClick();
                } else {
                    dialog.dismiss();
                }
            }
        });
        alertDialog.show();
    }

    public static void alertDialogComfirmWithEdittext(final Context context, String title, String hint, String defaultValue, String okButton, String noButton, final onAlertDialogViewClickEventData clickEvent){
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);

        final EditText one = new EditText(context);
        if (TextUtils.isEmpty(defaultValue)){
            one.setHint(hint+"...");//optional
        }else{
            one.setText(defaultValue);
            one.setSelection(one.getText().length());
        }

        LinearLayout lay = new LinearLayout(context);
        lay.setOrientation(LinearLayout.VERTICAL);
        int pixel = Utils.convertDipToPixels(context, 10);
        lay.setPadding(pixel, pixel, pixel, pixel);
        lay.addView(one);
        builder.setView(lay);

        builder.setTitle(title);

        builder.setPositiveButton(okButton, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                if (TextUtils.isEmpty(one.getText().toString())){
                    one.setError("");
                    one.requestFocus();
                    return;
                }

                if (clickEvent != null) {
                    clickEvent.onOkClick(one.getText().toString());
                } else {
                    dialog.dismiss();
                }
            }
        });
        builder.setNegativeButton(noButton, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                if (clickEvent != null) {
                    clickEvent.onCancelClick();
                }
                dialog.cancel();



            }
        });


        AlertDialog alert = builder.create();
        alert.show();

        // You Can Customise your Message here
        /*TextView messageView = (TextView) alert
                .findViewById(android.R.id.message);
        messageView.setGravity(Gravity.CENTER);*/

        Button btnYes = alert.getButton(DialogInterface.BUTTON_POSITIVE);
        Button btnNo = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
        int color =  context.getResources().getColor(R.color.black);
        btnYes.setTextColor(color);
        btnNo.setTextColor(color);
    }

    public static void normalAlertDialogWithCancel(final Context context,String title,String message,String okButton,String noButton, final OnAlertDialogViewClickEvent clickEvent){
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(noButton, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                if (clickEvent != null) {
                    clickEvent.onOkClick(dialog);
                } else {
                    dialog.dismiss();
                }
            }
        });
        builder.setNegativeButton(okButton, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                if (clickEvent != null) {
                    clickEvent.onCancelClick();
                }
                dialog.cancel();



            }
        });


        AlertDialog alert = builder.create();
        alert.show();

        // You Can Customise your Message here
        /*TextView messageView = (TextView) alert
                .findViewById(android.R.id.message);
        messageView.setGravity(Gravity.CENTER);*/

        Button btnYes = alert.getButton(DialogInterface.BUTTON_POSITIVE);
        Button btnNo = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
        int color =  context.getResources().getColor(R.color.black);
        btnYes.setTextColor(color);
        btnNo.setTextColor(color);

    }

    public static void normalAlertDialogWithCancelWhite(final Context context,String title,String message,String okButton,String noButton, final OnAlertDialogViewClickEvent clickEvent){
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton(noButton, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                if (clickEvent != null) {
                    clickEvent.onOkClick(dialog);
                } else {
                    dialog.dismiss();
                }
            }
        });
        builder.setNegativeButton(okButton, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                if (clickEvent != null) {
                    clickEvent.onCancelClick();
                }
                dialog.cancel();



            }
        });


        AlertDialog alert = builder.create();
        alert.show();

        // You Can Customise your Message here
        /*TextView messageView = (TextView) alert
                .findViewById(android.R.id.message);
        messageView.setGravity(Gravity.CENTER);*/

        Button btnYes = alert.getButton(DialogInterface.BUTTON_POSITIVE);
        Button btnNo = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
        int color =  context.getResources().getColor(R.color.white);
        btnYes.setTextColor(color);
        btnNo.setTextColor(color);

    }

    /*public static void alertDialogShow(final Context context,
                                       final String resultMobile) {

        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.prompt,
                null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);
        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.editTextDialogUserInput);
        userInput.setText(resultMobile);
        userInput.setEnabled(false);

        Button btnCancel = (Button) promptsView.findViewById(R.id.aaa);
        btnCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

            }
        });
    }*/

    public static ProgressDialog displayProgressDialog(Context context){
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setCancelable(false);
        dialog.setMessage(context.getString(R.string.please_wait));
        dialog.show();
        return dialog;
    }

    public interface onAlertDialogViewClickEventData{
        void onOkClick(String groupName);
        void onCancelClick();
    }

    public interface OnAlertDialogViewClickEvent{
        void onOkClick(DialogInterface alertDialog);
        void onCancelClick();
    }
}
