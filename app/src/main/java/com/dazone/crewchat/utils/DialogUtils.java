package com.dazone.crewchat.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

import android.widget.TextView;
import com.dazone.crewchat.activity.ProfileUserActivity;
import com.dazone.crewchat.activity.base.BaseActivity;
import com.dazone.crewchat.adapter.ListMenuAdapter;
import com.dazone.crewchat.adapter.SelectListAdapter;
import com.dazone.crewchat.constant.Statics;
import com.dazone.crewchat.dto.MenuDrawItem;
import com.dazone.crewchat.dto.MenuDto;
import com.dazone.crewchat.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by david on 12/25/15.
 */
public class DialogUtils {
    public static void normalAlertDialog(final Context context, String title, String message, final OnAlertDialogViewClickEvent clickEvent) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setPositiveButton(R.string.string_ok, new DialogInterface.OnClickListener() {
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

    public static void normalAlertDialogWithCancel(final Context context, String title, String message, final OnAlertDialogViewClickEvent clickEvent) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setPositiveButton(R.string.string_cancel, new DialogInterface.OnClickListener() {
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

    public interface OnAlertDialogViewClickEvent {
        void onOkClick(DialogInterface alertDialog);

        void onCancelClick();
    }


    public static void singleSelectDialogCustom(final Context context, List<MenuDrawItem> itemList, DialogInterface.OnClickListener listener, String title) {
        final AlertDialog.Builder adb = new AlertDialog.Builder(context).setTitle(title);
        SelectListAdapter adapter = new SelectListAdapter(context, itemList);
        adb.setAdapter(adapter, listener);
        adb.show();
    }

    public static void showGetImageDialog(final Activity activity) {
        if (null == activity)
            return;
        ArrayList<MenuDrawItem> list = new ArrayList<>();
        list.add(new MenuDto(R.string.string_take_photo, R.drawable.ic_photo_camera_black_24dp));
        list.add(new MenuDto(R.string.string_get_from_gallery, R.drawable.ic_photo_black_24dp));
        DialogInterface.OnClickListener onclick = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        activity.startActivityForResult(takePicture, Statics.CAMERA_CAPTURE_IMAGE_REQUEST_CODE);//zero can be replaced with any action code
                        break;
                    case 1:
                        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        activity.startActivityForResult(pickPhoto, Statics.IMAGE_PICKER_SELECT);
                        break;
                }
            }
        };
        singleSelectDialogCustom(activity, list, onclick, activity.getString(R.string.app_name));
    }

    public static void showGetImageDialogFragment(final Fragment activity, Context context) {
        if (null == activity)
            return;
        ArrayList<MenuDrawItem> list = new ArrayList<>();
        list.add(new MenuDto(R.string.string_take_photo, R.drawable.ic_photo_camera_black_24dp));
        list.add(new MenuDto(R.string.string_get_from_gallery, R.drawable.ic_photo_black_24dp));
        DialogInterface.OnClickListener onclick = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        activity.startActivityForResult(takePicture, Statics.CAMERA_CAPTURE_IMAGE_REQUEST_CODE);//zero can be replaced with any action code
                        break;
                    case 1:
                        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        activity.startActivityForResult(pickPhoto, Statics.IMAGE_PICKER_SELECT);
                        break;
                }
            }
        };
        singleSelectDialogCustom(context, list, onclick, activity.getString(R.string.app_name));
    }

    /**
     * SHOW DIALOG LONG CHAT CONTENT
     */
    public static void showDialogChat(final String content) {
        android.support.v7.app.AlertDialog.Builder builderSingle = new android.support.v7.app.AlertDialog.Builder(BaseActivity.Instance);
        builderSingle.setTitle(Utils.getString(R.string.app_name));

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                CrewChatApplication.getInstance(),
                R.layout.row_chatting_call);
        arrayAdapter.add("Copy");

        builderSingle.setAdapter(
                arrayAdapter,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                int sdk = android.os.Build.VERSION.SDK_INT;
                                if(sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
                                    android.text.ClipboardManager clipboard = (android.text.ClipboardManager) CrewChatApplication.getInstance().getSystemService(Context.CLIPBOARD_SERVICE);
                                    clipboard.setText(content);
                                } else {
                                    android.content.ClipboardManager clipboard = (android.content.ClipboardManager) CrewChatApplication.getInstance().getSystemService(Context.CLIPBOARD_SERVICE);
                                    android.content.ClipData clip = android.content.ClipData.newPlainText("Copy",content);
                                    clipboard.setPrimaryClip(clip);
                                }
                                break;
                        }
                    }
                });
        AlertDialog dialog = builderSingle.create();
        if (arrayAdapter.getCount() > 0) {
            dialog.show();
        }


        Button b = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        if (b != null) {
            b.setTextColor(ContextCompat.getColor(CrewChatApplication.getInstance(), R.color.light_black));
        }
    }

    /**
     * SHOW DIALOG LONG CLICK USER
     */
    public static void showDialogUser(String name, String phoneNumber, String companyNumber, final int userNo) {
        android.support.v7.app.AlertDialog.Builder builderSingle = new android.support.v7.app.AlertDialog.Builder(BaseActivity.Instance);
        builderSingle.setTitle(name);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                CrewChatApplication.getInstance(),
                R.layout.row_chatting_call);

        arrayAdapter.add("Profile");

        final String phone = !TextUtils.isEmpty(phoneNumber.trim()) ?
                phoneNumber :
                !TextUtils.isEmpty(companyNumber.trim()) ?
                        companyNumber :
                        "";

        if (!TextUtils.isEmpty(phone.trim())) {
            arrayAdapter.add("Call (" + phone + ")");
        }

        builderSingle.setAdapter(
                arrayAdapter,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                Intent intent = new Intent(BaseActivity.Instance, ProfileUserActivity.class);
                                intent.putExtra(Constant.KEY_INTENT_USER_NO, userNo);
                                BaseActivity.Instance.startActivity(intent);
                                BaseActivity.Instance.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                break;
                            case 1:
                                Utils.CallPhone(BaseActivity.Instance, phone);
                                break;
                        }
                    }
                });
        AlertDialog dialog = builderSingle.create();
        if (arrayAdapter.getCount() > 0) {
            dialog.show();
        }


        Button b = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        if (b != null) {
            b.setTextColor(ContextCompat.getColor(CrewChatApplication.getInstance(), R.color.light_black));
        }
    }




    /*
    * Show action menu for user
    * */

    public static void showDialogActionUser(Context context,String name, String phoneNumber, String companyNumber, final int userNo) {
        android.support.v7.app.AlertDialog.Builder builderSingle = new android.support.v7.app.AlertDialog.Builder(BaseActivity.Instance);
        builderSingle.setTitle(name);

        ArrayList<String> action = new ArrayList<>();
        action.add("Remove from favorites");
        action.add("Change name");

        ListMenuAdapter adapter = new ListMenuAdapter(context, action);

        builderSingle.setAdapter(
                adapter,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                /*Intent intent = new Intent(BaseActivity.Instance, ProfileUserActivity.class);
                                intent.putExtra(Constant.KEY_INTENT_USER_NO, userNo);
                                BaseActivity.Instance.startActivity(intent);
                                BaseActivity.Instance.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);*/
                                break;
                            case 1:
                                //Utils.CallPhone(BaseActivity.Instance, phone);
                                break;
                        }
                    }
                });

        AlertDialog dialog = builderSingle.create();
        int divierId = dialog.getContext().getResources().getIdentifier("android:id/titleDivider", null, null);
        View divider = dialog.findViewById(divierId);
        if(divider!=null){
            divider.setBackgroundColor(CrewChatApplication.getInstance().getResources().getColor(R.color.black));}

        View line = dialog.findViewById(R.id.view_line_top);
        if (line != null) {
            line.setVisibility(View.VISIBLE);
        }
        if (adapter.getCount() > 0) {
            dialog.show();
        }


        Button b = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        if (b != null) {
            b.setTextColor(ContextCompat.getColor(CrewChatApplication.getInstance(), R.color.light_black));
        }
    }
}
