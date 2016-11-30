package com.dazone.crewchat.ViewHolders;

import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dazone.crewchat.HTTPs.HttpRequest;
import com.dazone.crewchat.R;
import com.dazone.crewchat.adapter.ChattingAdapter;
import com.dazone.crewchat.database.ChatMessageDBHelper;
import com.dazone.crewchat.dto.ChattingDto;
import com.dazone.crewchat.dto.ErrorDto;
import com.dazone.crewchat.interfaces.SendChatMessage;
import com.dazone.crewchat.utils.CrewChatApplication;
import com.dazone.crewchat.utils.DialogUtils;
import com.dazone.crewchat.utils.TimeUtils;
import com.dazone.crewchat.utils.Utils;

/**
 * Created by david on 12/25/15.
 */
public class ChattingSelfViewHolder extends BaseChattingHolder {
    private TextView date_tv, content_tv;
    private TextView tvUnread;
    private RelativeLayout layoutMain;
    private ProgressBar progressBarSending;
    private LinearLayout lnSendFailed;
    private ImageView btnResend, btnDelete;
    private ChattingAdapter mAdapter;

    public void setAdapter(ChattingAdapter adapter) {
        this.mAdapter = adapter;
    }

    public ChattingSelfViewHolder(View v) {
        super(v);
    }

    @Override
    protected void setup(View v) {
        layoutMain = (RelativeLayout) v.findViewById(R.id.layout_main);
        date_tv = (TextView) v.findViewById(R.id.date_tv);
        content_tv = (TextView) v.findViewById(R.id.content_tv);
        tvUnread = (TextView) v.findViewById(R.id.text_unread);

        progressBarSending = (ProgressBar) v.findViewById(R.id.progressbar_sending);
        lnSendFailed = (LinearLayout) v.findViewById(R.id.ln_send_failed);

        btnResend = (ImageView) v.findViewById(R.id.btn_resend);
        btnDelete = (ImageView) v.findViewById(R.id.btn_delete);
    }

    @Override
    public void bindData(final ChattingDto dto) {
        if (!TextUtils.isEmpty(dto.getLastedMsgDate())) {
            date_tv.setText(TimeUtils.displayTimeWithoutOffset(CrewChatApplication.getInstance().getApplicationContext(), dto.getLastedMsgDate(), 0, TimeUtils.KEY_FROM_SERVER));
        } else {
            date_tv.setText(TimeUtils.displayTimeWithoutOffset(CrewChatApplication.getInstance().getApplicationContext(), dto.getRegDate(), 0, TimeUtils.KEY_FROM_SERVER));
        }
        if (dto.getMessage() != null) {
            content_tv.setText(Html.fromHtml(dto.getMessage()));
        }
        String strUnReadCount = dto.getUnReadCount() + "";
        tvUnread.setText(strUnReadCount);
        tvUnread.setVisibility(dto.getUnReadCount() == 0 ? View.GONE : View.VISIBLE);

        if (dto.isHasSent()) {
            if (progressBarSending != null) progressBarSending.setVisibility(View.GONE);
            if (lnSendFailed != null) lnSendFailed.setVisibility(View.GONE);
        } else {
            if (lnSendFailed != null) lnSendFailed.setVisibility(View.VISIBLE);
        }

        /** SHOW DIALOG */
        layoutMain.setTag(content_tv.getText().toString());
        layoutMain.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                String content = (String) v.getTag();
                DialogUtils.showDialogChat(content);
                return false;
            }
        });


        // Set event listener for failed message
        if (btnResend != null) {
            btnResend.setTag(dto.getId());
            btnResend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final Integer localId = (Integer) v.getTag();
                    HttpRequest.getInstance().SendChatMsg(dto.getRoomNo(), dto.getMessage(), new SendChatMessage() {
                        @Override
                        public void onSenChatMessageSuccess(final ChattingDto chattingDto) {
                            // update old chat message model --> messageNo from server
                            // perform update when send message success
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    dto.setRegDate(TimeUtils.convertTimeDeviceToTimeServer(chattingDto.getRegDate()));
                                    ChatMessageDBHelper.updateMessage(dto, localId);
                                }
                            }).start();
                            // Notify current adapter
                            if (mAdapter != null) {

                                if (mAdapter.getData() != null) {
                                    dto.setHasSent(true);
                                    dto.setUnReadCount(chattingDto.getUnReadCount());
                                    String time = TimeUtils.convertTimeDeviceToTimeServer(chattingDto.getRegDate());
                                    dto.setRegDate(time);
                                    dto.setMessageNo(chattingDto.getMessageNo());
                                    // Can update more information
                                }
                                mAdapter.notifyDataSetChanged();
                            }

                        }

                        @Override
                        public void onSenChatMessageFail(ErrorDto errorDto, String url) {
                            Utils.printLogs("Send message failed !");
                        }
                    });

                }
            });

        }

        if (btnDelete != null) {
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // delete or call callback
                    if (ChatMessageDBHelper.deleteMessage(dto.getMessageNo())) {
                        if (mAdapter != null && mAdapter.getData() != null) {
                            mAdapter.getData().remove(dto);
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                }
            });
        }

    }
}
