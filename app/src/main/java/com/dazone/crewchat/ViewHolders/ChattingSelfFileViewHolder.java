package com.dazone.crewchat.ViewHolders;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dazone.crewchat.activity.base.BaseActivity;
import com.dazone.crewchat.constant.Statics;
import com.dazone.crewchat.dto.AttachDTO;
import com.dazone.crewchat.dto.ChattingDto;
import com.dazone.crewchat.fragment.ChattingFragment;
import com.dazone.crewchat.interfaces.Urls;
import com.dazone.crewchat.R;
import com.dazone.crewchat.utils.CrewChatApplication;
import com.dazone.crewchat.utils.ImageUtils;
import com.dazone.crewchat.utils.Prefs;
import com.dazone.crewchat.utils.TimeUtils;
import com.dazone.crewchat.utils.Utils;

/**
 * Created by david on 12/25/15.
 */
public class ChattingSelfFileViewHolder extends BaseChattingHolder {

    TextView date_tv, file_name_tv, file_size_tv, file_receive_tv;
    TextView tvUnread;
    ImageView file_thumb;
    LinearLayout linearLayout;
    ProgressBar progressBar;

    public ChattingSelfFileViewHolder(View v) {
        super(v);
    }

    @Override
    protected void setup(View v) {
        date_tv = (TextView) v.findViewById(R.id.date_tv);
        file_name_tv = (TextView) v.findViewById(R.id.file_name_tv);
        file_size_tv = (TextView) v.findViewById(R.id.file_size_tv);
        file_receive_tv = (TextView) v.findViewById(R.id.file_receive_tv);
        file_thumb = (ImageView) v.findViewById(R.id.file_thumb);
        linearLayout = (LinearLayout) v.findViewById(R.id.main_attach);
        progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
        tvUnread = (TextView) v.findViewById(R.id.text_unread);

    }

    @Override
    public void bindData(final ChattingDto dto) {
        if (dto.getmType() == Statics.CHATTING_VIEW_TYPE_SELECT_FILE) {
            file_name_tv.setText(dto.getAttachFileName());
            file_size_tv.setText(Utils.readableFileSize(new Long(dto.getAttachFileSize())));
            file_receive_tv.setVisibility(View.GONE);
            /** Set IMAGE FILE TYPE */
            String fileType = Utils.getFileType(dto.getAttachFileName());
            ImageUtils.imageFileType(file_thumb, fileType);

            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(0);
            ChattingFragment.instance.SendTo(dto.getAttachFilePath(), progressBar, getAdapterPosition());

        } else {
            if (TextUtils.isEmpty(dto.getRegDate())) {
                date_tv.setText(TimeUtils.showTimeWithoutTimeZone(dto.getTime(), Statics.DATE_FORMAT_YY_MM_DD_DD_H_M));
            } else
                date_tv.setText(TimeUtils.displayTimeWithoutOffset(CrewChatApplication.getInstance().getApplicationContext(), dto.getRegDate(), 0, TimeUtils.KEY_FROM_SERVER));

            /** Set IMAGE FILE TYPE */
            String fileType = Utils.getFileType(dto.getAttachInfo().getFileName());
            ImageUtils.imageFileType(file_thumb, fileType);

            file_name_tv.setText(dto.getAttachInfo().getFileName());
            file_size_tv.setText(Utils.readableFileSize(new Long(dto.getAttachInfo().getSize())));
            file_receive_tv.setVisibility(View.GONE);
            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dto != null) {
                        AttachDTO attachDTO = dto.getAttachInfo();
                        if (attachDTO != null) {
                            String url = new Prefs().getServerSite() + Urls.URL_DOWNLOAD + "session=" + CrewChatApplication.getInstance().getmPrefs().getaccesstoken() + "&no=" + attachDTO.getAttachNo();
                            Utils.displayoDownloadFileDialog(BaseActivity.Instance, url, attachDTO.getFileName());
                        }
                    }
                }
            });
//        ImageUtils.showRoundImage(dto, avatar_imv);

        }
        String strUnReadCount = dto.getUnReadCount() + "";
        tvUnread.setText(strUnReadCount);
        tvUnread.setVisibility(dto.getUnReadCount() == 0 ? View.GONE : View.VISIBLE);

    }
}
