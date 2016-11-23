package com.dazone.crewchat.ViewHolders;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.dazone.crewchat.R;
import com.dazone.crewchat.activity.ChattingActivity;
import com.dazone.crewchat.constant.Statics;
import com.dazone.crewchat.dto.SelectionPlusDto;
import com.dazone.crewchat.libGallery.MediaChooser;
import com.dazone.crewchat.libGallery.activity.BucketHomeFragmentActivity;
import com.dazone.crewchat.utils.CrewChatApplication;
import com.dazone.crewchat.utils.Utils;
import com.nononsenseapps.filepicker.FilePickerActivity;
import com.onegravity.contactpicker.contact.ContactDescription;
import com.onegravity.contactpicker.contact.ContactSortOrder;
import com.onegravity.contactpicker.core.ContactPickerActivity;
import com.onegravity.contactpicker.picture.ContactPictureType;

/**
 * Created by david on 12/25/15.
 */
public class SelectionChattingViewHolder extends ItemViewHolder<SelectionPlusDto> {
    public TextView title;
    public ImageView icon;
    public LinearLayout layout;
    private Uri uri;

    public SelectionChattingViewHolder(View v) {
        super(v);
    }

    @Override
    protected void setup(View v) {
        title = (TextView) v.findViewById(R.id.title);
        icon = (ImageView) v.findViewById(R.id.ic_folder);
        layout = (LinearLayout) v.findViewById(R.id.layout);
    }

    @Override
    public void bindData(SelectionPlusDto dto) {
        switch (dto.getType()) {
            case 1:
                icon.setImageResource(R.drawable.attach_ic_camera);
                title.setText(Utils.getString(R.string.camera));
                layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            captureImage(Statics.MEDIA_TYPE_IMAGE);
                        } catch (Exception e) {

                        }
                    }
                });
                break;
            case 2:
                icon.setImageResource(R.drawable.attach_ic_video_record);
                title.setText(Utils.getString(R.string.video_record));
                layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            recordVideo();
                        } catch (Exception e) {

                        }
                    }
                });
                break;
            case 3:
                icon.setImageResource(R.drawable.attach_ic_images);
                title.setText(Utils.getString(R.string.image));
                layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            selectImage();
                        } catch (Exception e) {
                        }
                    }
                });
                break;
            case 4:
                icon.setImageResource(R.drawable.attach_ic_video);
                title.setText(Utils.getString(R.string.video));
                layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            Intent intentVideo = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                            intentVideo.setType(Statics.MIME_TYPE_VIDEO);
                            ChattingActivity.Instance.startActivityForResult(intentVideo, Statics.VIDEO_PICKER_SELECT);
                            ChattingActivity.Instance.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        } catch (Exception e) {
                        }
                    }
                });
                break;
            case 5:
                icon.setImageResource(R.drawable.attach_ic_file);
                title.setText(Utils.getString(R.string.file));
                layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(ChattingActivity.Instance, FilePickerActivity.class);
                        i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
                        i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, false);
                        i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_FILE);
                        i.putExtra(FilePickerActivity.EXTRA_START_PATH, Environment.getExternalStorageDirectory().getPath());
                        ChattingActivity.Instance.startActivityForResult(i, Statics.FILE_PICKER_SELECT);
                    }
                });
                break;

            case 6:
                icon.setImageResource(R.drawable.attach_ic_contact);
                title.setText(Utils.getString(R.string.contact));
                layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        contactPicker();
                    }
                });
                break;
        }
//        title.setText(TimeUtils.showTime(dto.getTime(), Statics.DATE_FORMAT_YY_MM_DD));
    }

    private void contactPicker(){


        //Intent intent = new Intent(ChattingActivity.Instance, ContactPickerMultiActivity.class);


        Intent intent = new Intent(ChattingActivity.Instance, ContactPickerActivity.class)
                //.putExtra(ContactPickerActivity.EXTRA_THEME, mDarkTheme ? R.style.Theme_Dark : R.style.Theme_Light)
                .putExtra(ContactPickerActivity.EXTRA_CONTACT_BADGE_TYPE, ContactPictureType.ROUND.name())
                .putExtra(ContactPickerActivity.EXTRA_CONTACT_DESCRIPTION, ContactDescription.ADDRESS.name())
                .putExtra(ContactPickerActivity.EXTRA_CONTACT_DESCRIPTION_TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                .putExtra(ContactPickerActivity.EXTRA_CONTACT_SORT_ORDER, ContactSortOrder.AUTOMATIC.name());

        ChattingActivity.Instance.startActivityForResult(intent, Statics.CONTACT_PICKER_SELECT);
    }

    private void recordVideo() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        Uri videoPath = ChattingActivity.getOutputMediaFileUri(Statics.MEDIA_TYPE_VIDEO);
        takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, videoPath);
        ChattingActivity.videoPath = videoPath;
        if (takeVideoIntent.resolveActivity(CrewChatApplication.getInstance().getPackageManager()) != null) {
            ChattingActivity.Instance.startActivityForResult(takeVideoIntent, Statics.CAMERA_VIDEO_REQUEST_CODE);
        }
    }

    //Capture camera
    private void captureImage(int task) {
        if (task == Statics.MEDIA_TYPE_IMAGE) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            uri = ChattingActivity.getOutputMediaFileUri(Statics.MEDIA_TYPE_IMAGE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            ChattingActivity.Instance.startActivityForResult(intent, Statics.CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
        }
        /*if (task == Statics.MEDIA_TYPE_VIDEO) {
            Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            uri = getOutputMediaFileUri(Statics.MEDIA_TYPE_VIDEO);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            ChattingActivity.Instance.startActivityForResult(intent, Statics.CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
        }*/
    }

    private void selectImage() {
        MediaChooser.showOnlyImageTab();
        //MediaChooser.showCameraVideoView(false);
        Intent intent = new Intent(ChattingActivity.Instance, BucketHomeFragmentActivity.class);
        ChattingActivity.Instance.startActivity(intent);

    }

}
