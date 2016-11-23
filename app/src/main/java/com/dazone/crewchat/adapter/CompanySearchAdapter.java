package com.dazone.crewchat.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dazone.crewchat.HTTPs.HttpRequest;
import com.dazone.crewchat.R;
import com.dazone.crewchat.activity.ChattingActivity;
import com.dazone.crewchat.activity.base.BaseActivity;
import com.dazone.crewchat.constant.Statics;
import com.dazone.crewchat.dto.ChattingDto;
import com.dazone.crewchat.dto.ErrorDto;
import com.dazone.crewchat.dto.TreeUserDTOTemp;
import com.dazone.crewchat.interfaces.ICreateOneUserChatRom;
import com.dazone.crewchat.utils.Constant;
import com.dazone.crewchat.utils.ImageUtils;
import com.dazone.crewchat.utils.Prefs;
import com.dazone.crewchat.utils.Utils;

import java.util.ArrayList;

/**
 * Created by Dat on 4/20/2016.
 */
public class CompanySearchAdapter extends RecyclerView.Adapter<CompanySearchAdapter.CompanySearchViewHolder> implements View.OnClickListener {

    private ArrayList<TreeUserDTOTemp> listData;
    private Context context;
    private int myId;
    private Prefs prefs;

    public CompanySearchAdapter(Context context, ArrayList<TreeUserDTOTemp> listData) {
        this.context = context;
        this.listData = listData;

        prefs = new Prefs();
        myId = Utils.getCurrentId();
    }

    public void updateListData(ArrayList<TreeUserDTOTemp> listData) {
        this.listData = listData;
        notifyDataSetChanged();
    }

    @Override
    public CompanySearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_company_search, parent, false);

        return new CompanySearchViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CompanySearchViewHolder holder, int position) {
        /** GET ITEM */
        TreeUserDTOTemp item = listData.get(position);

        /** GET URL */

        String url = prefs.getServerSite() + item.getAvatarUrl();

        //ImageLoader.getInstance().displayImage(url, holder.ivAvatar, Statics.options2);
        ImageUtils.showCycleImageFromLinkScale(url, holder.ivAvatar, R.dimen.button_height);

        holder.tvName.setText(item.getName());
        holder.tvPosition.setText(item.getPosition());

        holder.layout.setTag(item);
        holder.layout.setOnClickListener(this);

        Utils.printLogs("Current user name = "+item.getName());
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_main:
                TreeUserDTOTemp treeUserDTOTemp = (TreeUserDTOTemp) v.getTag();
                if (myId != treeUserDTOTemp.getUserNo()) {
                   /* final TreeUserDTO treeUserDTO = new TreeUserDTO(treeUserDTOTemp.getName(),
                            treeUserDTOTemp.getNameEN(),
                            treeUserDTOTemp.getCellPhone(),
                            treeUserDTOTemp.getAvatarUrl(),
                            treeUserDTOTemp.getPosition(),
                            treeUserDTOTemp.getType(),
                            treeUserDTOTemp.getStatus(),
                            treeUserDTOTemp.getUserNo(),
                            treeUserDTOTemp.getDepartNo());*/
                    HttpRequest.getInstance().CreateOneUserChatRoom(treeUserDTOTemp.getUserNo(), new ICreateOneUserChatRom() {
                        @Override
                        public void onICreateOneUserChatRomSuccess(ChattingDto chattingDto) {
                            Intent intent = new Intent(BaseActivity.Instance, ChattingActivity.class);
                            //intent.putExtra(Statics.TREE_USER_PC, treeUserDTO);
                            intent.putExtra(Statics.CHATTING_DTO, chattingDto);
                            intent.putExtra(Constant.KEY_INTENT_ROOM_NO, chattingDto.getRoomNo());
                            BaseActivity.Instance.startActivity(intent);
                        }

                        @Override
                        public void onICreateOneUserChatRomFail(ErrorDto errorDto) {
                            Utils.showMessageShort("Fail");
                        }
                    });

                } else {
                    Toast.makeText(context, context.getResources().getString(R.string.can_not_chat), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public class CompanySearchViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout layout;
        public ImageView ivAvatar;
        public ImageView ivStatus;
        public TextView tvName;
        public TextView tvPosition;

        public CompanySearchViewHolder(View view) {
            super(view);
            layout = (RelativeLayout) view.findViewById(R.id.layout_main);
            ivAvatar = (ImageView) view.findViewById(R.id.iv_avatar);
            ivStatus = (ImageView) view.findViewById(R.id.iv_status);
            tvName = (TextView) view.findViewById(R.id.tv_username);
            tvPosition = (TextView) view.findViewById(R.id.tv_position);
        }
    }
}
