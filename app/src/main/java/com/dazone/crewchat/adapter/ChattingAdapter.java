package com.dazone.crewchat.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dazone.crewchat.R;
import com.dazone.crewchat.ViewHolders.BaseChattingHolder;
import com.dazone.crewchat.ViewHolders.ChattingContactViewHolder;
import com.dazone.crewchat.ViewHolders.ChattingDateViewHolder;
import com.dazone.crewchat.ViewHolders.ChattingGroupViewHolder;
import com.dazone.crewchat.ViewHolders.ChattingGroupViewHolderNew;
import com.dazone.crewchat.ViewHolders.ChattingPersonFileViewHolder;
import com.dazone.crewchat.ViewHolders.ChattingPersonImageViewHolder;
import com.dazone.crewchat.ViewHolders.ChattingPersonVideoNotShowViewHolder;
import com.dazone.crewchat.ViewHolders.ChattingPersonViewHolder;
import com.dazone.crewchat.ViewHolders.ChattingSelfFileViewHolder;
import com.dazone.crewchat.ViewHolders.ChattingSelfImageViewHolder;
import com.dazone.crewchat.ViewHolders.ChattingSelfVideoViewHolder;
import com.dazone.crewchat.ViewHolders.ChattingSelfViewHolder;
import com.dazone.crewchat.constant.Statics;
import com.dazone.crewchat.dto.ChattingDto;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by david on 12/24/15.
 */
public class ChattingAdapter extends PullUpLoadMoreRCVAdapter<ChattingDto> {
    private Activity mActivity;

    public ChattingAdapter(Context context, Activity activity, List<ChattingDto> mDataSet, RecyclerView view) {
        super(context,mDataSet, view);
        Collections.sort(mDataSet, new Comparator<ChattingDto>() {
            @Override
            public int compare(ChattingDto chattingDto, ChattingDto t1) {
                return 1;
            }
        });
        mActivity = activity;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v;
        switch (viewType) {
            case Statics.CHATTING_VIEW_TYPE_DATE:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_chatting_date, parent, false);
                vh = new ChattingDateViewHolder(v);
                break;
            case Statics.CHATTING_VIEW_TYPE_SELF:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_chatting_self, parent, false);
                ChattingSelfViewHolder tempVh = new ChattingSelfViewHolder(v);
                tempVh.setAdapter(this);
                vh = tempVh;
                break;
            case Statics.CHATTING_VIEW_TYPE_SELF_NOT_SHOW:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_chatting_self_not_show, parent, false);
                ChattingSelfViewHolder tempVh2 = new ChattingSelfViewHolder(v);
                tempVh2.setAdapter(this);
                vh = tempVh2;
                break;
            case Statics.CHATTING_VIEW_TYPE_PERSON:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_chatting_person, parent, false);
                vh = new ChattingPersonViewHolder(v);
                break;

            case Statics.CHATTING_VIEW_TYPE_PERSON_NOT_SHOW:
                v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_chatting_person_not_show, parent, false);
                vh = new ChattingSelfViewHolder(v);
                break;

            case Statics.CHATTING_VIEW_TYPE_GROUP:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_chatting_group, parent, false);
                vh = new ChattingGroupViewHolder(v);
                break;

            case Statics.CHATTING_VIEW_TYPE_GROUP_NEW:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_chatting_group, parent, false);
                vh = new ChattingGroupViewHolderNew(v);
                break;
            case Statics.CHATTING_VIEW_TYPE_SELF_IMAGE:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_chatting_self_image, parent, false);
                vh = new ChattingSelfImageViewHolder(mActivity,v);
                break;
            case Statics.CHATTING_VIEW_TYPE_SELF_FILE:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_chatting_self_file, parent, false);
                vh = new ChattingSelfFileViewHolder(v);
                break;
            case Statics.CHATTING_VIEW_TYPE_PERSON_IMAGE:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_chatting_person_image, parent, false);
                vh = new ChattingPersonImageViewHolder(mActivity,v);
                break;
            case Statics.CHATTING_VIEW_TYPE_PERSON_IMAGE_NOT_SHOW:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_chatting_person_image_not_show, parent, false);
                vh = new ChattingSelfImageViewHolder(mActivity,v);
                break;
            case Statics.CHATTING_VIEW_TYPE_PERSON_FILE:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_chatting_person_file, parent, false);
                vh = new ChattingPersonFileViewHolder(v);
                break;
            case Statics.CHATTING_VIEW_TYPE_PERSON_FILE_NOT_SHOW:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_chatting_person_file_not_show, parent, false);
                vh = new ChattingSelfFileViewHolder(v);
                break;

            case Statics.CHATTING_VIEW_TYPE_SELECT_IMAGE:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_chatting_self_image, parent, false);
                vh = new ChattingSelfImageViewHolder(mActivity,v);
                break;
            case Statics.CHATTING_VIEW_TYPE_SELECT_FILE:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_chatting_self_file, parent, false);
                vh = new ChattingSelfFileViewHolder(v);
                break;

            case Statics.CHATTING_VIEW_TYPE_CONTACT:

                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_chatting_contact, parent, false);
                vh = new ChattingContactViewHolder(v);

                break;

            case Statics.CHATTING_VIEW_TYPE_EMPTY:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_chatting_empty, parent, false);
                vh = new ChattingDateViewHolder(v);
                break;

            case Statics.CHATTING_VIEW_TYPE_SELF_VIDEO:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_chatting_self_video, parent, false);
                vh = new ChattingSelfVideoViewHolder(mActivity,v);
                break;

            case Statics.CHATTING_VIEW_TYPE_SELECT_VIDEO:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_chatting_self_video, parent, false);
                vh = new ChattingSelfVideoViewHolder(mActivity,v);
                break;

            case Statics.CHATTING_VIEW_TYPE_PERSON_VIDEO_NOT_SHOW:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_chatting_person_video_not_show, parent, false);
                vh = new ChattingPersonVideoNotShowViewHolder(mActivity,v);
                break;

            default:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_chatting_date, parent, false);
                vh = new ChattingDateViewHolder(v);
                break;

        }
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) != Statics.CHATTING_VIEW_TYPE_EMPTY){
            final ChattingDto item = mDataset.get(position);
            BaseChattingHolder viewHolder = (BaseChattingHolder) holder;


            viewHolder.bindData(item);
        }
    }

    @Override
    public int getItemCount() {
        if (mDataset.size() == 0 && isFiltering){
            return 1;
        }
        return mDataset.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (getItemCount() == 1 && mDataset.size() == 0){
            return Statics.CHATTING_VIEW_TYPE_EMPTY;
        }
        return mDataset.get(position).getmType();
    }

}