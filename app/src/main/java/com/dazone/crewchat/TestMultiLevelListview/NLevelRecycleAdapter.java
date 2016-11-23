package com.dazone.crewchat.TestMultiLevelListview;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.*;
import android.widget.*;
import com.dazone.crewchat.HTTPs.HttpRequest;
import com.dazone.crewchat.R;
import com.dazone.crewchat.Tree.Dtos.TreeUserDTO;
import com.dazone.crewchat.activity.ChattingActivity;
import com.dazone.crewchat.activity.base.BaseActivity;
import com.dazone.crewchat.constant.Statics;
import com.dazone.crewchat.customs.AlertDialogView;
import com.dazone.crewchat.database.FavoriteGroupDBHelper;
import com.dazone.crewchat.database.FavoriteUserDBHelper;
import com.dazone.crewchat.dto.ChattingDto;
import com.dazone.crewchat.dto.ErrorDto;
import com.dazone.crewchat.interfaces.BaseHTTPCallBack;
import com.dazone.crewchat.interfaces.BaseHTTPCallbackWithJson;
import com.dazone.crewchat.interfaces.ICreateOneUserChatRom;
import com.dazone.crewchat.utils.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by DAZONE-XXX on 8/4/2016.
 */

// Custom recycle adapter to compacitice with the layout
public class NLevelRecycleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener, View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener{


    public static int FILTER_TYPE_NORMAL = 0;
    public static int FILTER_TYPE_USER_SEARCH = 1;

    private final int TYPE_FOLDER = 0;
    private final int TYPE_USER = 1;
    private final int TYPE_EMPPY = 999;

    private ArrayList<TreeUserDTO> temp = new ArrayList<>();

    List<NLevelItem> list;
    List<NLevelListItem> filtered;

    private NLevelItem mTempDto;
    private int left20dp;
    boolean isToggle = false;
    private Context mContext;
    private OnGroupShowContextMenu mCallback;

    public void setFiltered(ArrayList<NLevelListItem> filtered) {
        this.filtered = filtered;

    }

    public NLevelRecycleAdapter(Context context, List<NLevelItem> list, int left20dp, OnGroupShowContextMenu callback){
        this.list = list;
        this.filtered = filterItems();
        this.left20dp = left20dp;
        this.mContext = context;
        this.mCallback = callback;
    }

    @Override
    public void onClick(View v) {

    }

    private class UserViewHolder extends RecyclerView.ViewHolder{
        public View rootView;
        public TextView title, position, tv_work_phone, tv_personal_phone, tv_user_status;
        public ImageView avatar_imv, status_imv;
        public LinearLayout lnItemWraper;
        public RelativeLayout main;

        public UserViewHolder(View currentView){
            super(currentView);
            rootView = currentView;
            title = (TextView) currentView.findViewById(R.id.name);
			avatar_imv = (ImageView) currentView.findViewById(R.id.avatar);
            status_imv = (ImageView) currentView.findViewById(R.id.status_imv);
            position = (TextView) currentView.findViewById(R.id.position);
            lnItemWraper = (LinearLayout) currentView.findViewById(R.id.item_org_wrapper);
            //status_tv = (TextView) currentView.findViewById(R.id.status_tv);
            //checkBox = (CheckBox) currentView.findViewById(R.id.row_check);
            tv_work_phone = (TextView) currentView.findViewById(R.id.tv_work_phone);
            tv_personal_phone = (TextView) currentView.findViewById(R.id.tv_personal_phone) ;
            tv_user_status = (TextView) currentView.findViewById(R.id.tv_user_status);
            main = (RelativeLayout) currentView.findViewById(R.id.mainParent);

        }
    }

    private class FolderViewHolder extends RecyclerView.ViewHolder{
        public View rootView;
        public TextView title;
        public ImageView icon;
        public CheckBox checkBox;
        public RelativeLayout main;
        public LinearLayout mLnTittle, lnl_child;

        public FolderViewHolder(View currentView){
            super(currentView);
            rootView = currentView;
            title = (TextView) currentView.findViewById(R.id.office_title);

			title = (TextView) currentView.findViewById(R.id.office_title);
			icon = (ImageView) currentView.findViewById(R.id.ic_folder);
			main = (RelativeLayout) currentView.findViewById(R.id.mainParent);
			mLnTittle = (LinearLayout) currentView.findViewById(R.id.layout_title);
        }
    }

    private class EmptyViewHolder extends RecyclerView.ViewHolder{
        public TextView noData;
        public EmptyViewHolder(View itemView) {
            super(itemView);
            noData = (TextView) itemView.findViewById(R.id.tv_empty);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(mContext);
        switch (viewType){

            case TYPE_USER :
                View userView = inflater.inflate(R.layout.tree_user_row, null);
                return new UserViewHolder(userView);

            case TYPE_EMPPY:
                View emptyView = inflater.inflate(R.layout.row_user_empty, null);
                return new EmptyViewHolder(emptyView);

            case TYPE_FOLDER:
            default:
                View defaultView = inflater.inflate(R.layout.tree_office_row_v2, null);
                return new FolderViewHolder(defaultView);
        }
    }

    private void setupStatusImage(UserViewHolder holder, TreeUserDTO dto) {

        switch (dto.getStatus())
        {
            case Statics.USER_LOGIN:
                holder.status_imv.setImageResource(R.drawable.home_big_status_01);
                break;
            case Statics.USER_AWAY:
                holder.status_imv.setImageResource(R.drawable.home_big_status_02);
                break;
            case Statics.USER_LOGOUT:
                holder.status_imv.setImageResource(R.drawable.home_big_status_03);
                break;
            default:
                holder.status_imv.setImageResource(R.drawable.home_big_status_03);
                break;
        }
    }

    private void bindUserData(UserViewHolder holder, TreeUserDTO dto){
        holder.title.setText(dto.getName());

        String avatarUrl = new Prefs().getServerSite() + dto.getAvatarUrl();
        Utils.printLogs("Avatar url ="+avatarUrl);

        ImageUtils.showCycleImageFromLinkScale(avatarUrl, holder.avatar_imv, R.dimen.button_height);

        holder.position.setText(dto.getPosition());
        if (TextUtils.isEmpty(dto.getStatusString())){
            holder.tv_user_status.setVisibility(View.GONE);
            Utils.printLogs("Status string = Rong");
        }else{
            holder.tv_user_status.setVisibility(View.VISIBLE);
            Utils.printLogs("Status string = "+dto.getStatusString());
            holder.tv_user_status.setText(dto.getStatusString());
        }
        setupStatusImage(holder, dto);

        holder.rootView.setOnCreateContextMenuListener(this);
    }

    private void bindGroupData(FolderViewHolder holder, TreeUserDTO dto){
        holder.title.setText(dto.getName());

        if (dto.getId() != 0) {
            holder.title.setText(dto.getItemName());

            if (dto.getIsHide() == 1) {
               holder. icon.setImageResource(R.drawable.home_folder_close_ic);
                //holder.lnl_child.setVisibility(View.GONE);
            } else {
                holder.icon.setImageResource(R.drawable.home_folder_open_ic);
                //holder.lnl_child.setVisibility(View.VISIBLE);
            }

        } else {
           /* holder.title.setVisibility(View.GONE);
            holder.icon.setVisibility(View.GONE);*/
        }
        holder.mLnTittle.setOnCreateContextMenuListener(this);
    }

    private int getMarginLeft(int level){
        int marginLeft = level  * this.left20dp;
        return marginLeft;
    }

    private void createChatRoom(final TreeUserDTO dto, int type){
        if (type == TYPE_USER) {
            if (dto.getId() != Utils.getCurrentId())
                HttpRequest.getInstance().CreateOneUserChatRoom(dto.getId(), new ICreateOneUserChatRom() {
                    @Override
                    public void onICreateOneUserChatRomSuccess(ChattingDto chattingDto) {
                        Intent intent = new Intent(BaseActivity.Instance, ChattingActivity.class);
                        intent.putExtra(Constant.KEY_INTENT_ROOM_NO, chattingDto.getRoomNo());
                        intent.putExtra(Statics.TREE_USER_PC, dto);
                        intent.putExtra(Statics.CHATTING_DTO, chattingDto);
                        BaseActivity.Instance.startActivity(intent);
                    }

                    @Override
                    public void onICreateOneUserChatRomFail(ErrorDto errorDto) {
                        Utils.showMessageShort("Fail");
                    }
                });
            else
                Utils.showMessage(Utils.getString(R.string.can_not_chat));
        } else if (type == TYPE_FOLDER){

            if (dto.getSubordinates() != null && dto.getSubordinates().size() > 0) {
                getUser(dto.getSubordinates());
                if (temp != null && temp.size() > 0) {

                    HttpRequest.getInstance().CreateGroupChatRoom(temp, new ICreateOneUserChatRom() {
                        @Override
                        public void onICreateOneUserChatRomSuccess(ChattingDto chattingDto) {
                            Intent intent = new Intent(BaseActivity.Instance, ChattingActivity.class);
                            intent.putExtra(Constant.KEY_INTENT_ROOM_NO, chattingDto.getRoomNo());
                            intent.putExtra(Statics.CHATTING_DTO, chattingDto);
                            BaseActivity.Instance.startActivity(intent);
                        }

                        @Override
                        public void onICreateOneUserChatRomFail(ErrorDto errorDto) {
                            Utils.showMessageShort("Fail");
                        }
                    });
                }

            }
        }
    }

    // Get all user of this group to add to chat room
    public void getUser(List<TreeUserDTO> treeUserDTOs) {
        if (treeUserDTOs != null && treeUserDTOs.size() != 0) {
            for (TreeUserDTO dto : treeUserDTOs) {
                if (dto.getSubordinates() != null && dto.getSubordinates().size() > 0) {
                    if (dto.getType() == Statics.TYPE_USER)
                        temp.add(dto);
                    getUser(dto.getSubordinates());
                } else {
                    if (dto.getType() == Statics.TYPE_USER)
                        temp.add(dto);
                }
            }
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        NLevelListItem object = getItem(position);
        TreeUserDTO dto = null;
        if (object != null){
            dto = object.getObject();
        }

        switch (holder.getItemViewType()){
            case TYPE_USER:

                if (dto != null){
                    UserViewHolder holder2 = ((UserViewHolder) holder);
                    bindUserData(holder2, dto);

                    RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams) holder2.lnItemWraper.getLayoutParams();
                    params1.leftMargin = getMarginLeft(object.getLevel());

                    // Set event listener
                    final TreeUserDTO finalDto1 = dto;
                    holder2.rootView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Click to chat with current user
                            createChatRoom(finalDto1, TYPE_USER);
                        }
                    });

                    holder2.rootView.setTag(object);
                    holder2.rootView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            v.showContextMenu();
                            return true;
                        }
                    });
                }

                break;
            case TYPE_EMPPY:

                EmptyViewHolder holderEmppty = ((EmptyViewHolder) holder);
                LinearLayout.LayoutParams paramsEmpty = (LinearLayout.LayoutParams) holderEmppty.noData.getLayoutParams();
                paramsEmpty.weight = 1.0f;
                paramsEmpty.gravity = Gravity.CENTER_HORIZONTAL;

                // Calculate
                DisplayMetrics metrics = new DisplayMetrics();
                ((Activity)mContext).getWindowManager().getDefaultDisplay().getMetrics(metrics);
                int width = metrics.widthPixels;
                float w = holderEmppty.noData.getPaint().measureText("No data");
                paramsEmpty.leftMargin = Math.round((width - w ) / 2 - w /2);
                paramsEmpty.topMargin = 1000;

                // Nothing to do now
                break;
            case TYPE_FOLDER:
            default:
                if (dto != null){
                    FolderViewHolder holder3 = ((FolderViewHolder) holder);
                    bindGroupData(holder3, dto);

                    RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams) holder3.mLnTittle.getLayoutParams();

                    params2.leftMargin = getMarginLeft(object.getLevel());

                    // Set event listener
                    final TreeUserDTO finalDto = dto;
                    holder3.mLnTittle.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Utils.printLogs("on Create group chat");
                            createChatRoom(finalDto, TYPE_FOLDER);
                        }
                    });

                    holder3.mLnTittle.setTag(object);
                    holder3.mLnTittle.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            Utils.printLogs("on Show menu context");
                            v.showContextMenu();
                            return true;
                        }
                    });

                    holder3.icon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            isToggle = true;
                            toggle(position);
                            getFilter().filter();
                        }
                    });
                }
                break;

        }
    }

    public NLevelListItem getItem(int position) {
        if (getItemCount() == 1 && filtered.size() == 0){
            return null;
        }
        return filtered.get(position);
    }

    @Override
    public int getItemViewType(int position) {
        if (getItem(position) == null && getItemCount() == 1)
            return TYPE_EMPPY;

        if (getItem(position).getObject().getType() == Statics.TYPE_USER)
            return TYPE_USER;

        return TYPE_FOLDER;
    }

    @Override
    public int getItemCount() {
        if (filtered.size() == 0)
            return 1;
        return filtered.size();
    }

    public NLevelFilter getFilter() {
        return new NLevelFilter();
    }

    class NLevelFilter {

        public void filter() {
                new AsyncFilter().execute();
        }

        // Filter user when receive text string
        public void filterUser(String text){
            new SearchUserFilter().execute(text);
        }

        // Class filter when collapse or expand
        class AsyncFilter extends AsyncTask<Void, Void, ArrayList<NLevelListItem>> {

            @Override
            protected ArrayList<NLevelListItem> doInBackground(Void... arg0) {

                return (ArrayList<NLevelListItem>) filterItems();
            }

            @Override
            protected void onPostExecute(ArrayList<NLevelListItem> result) {
                setFiltered(result);
                NLevelRecycleAdapter.this.notifyDataSetChanged();
            }
        }

        // Class filter when search user
        class SearchUserFilter extends AsyncTask<String, Void, ArrayList<NLevelListItem>> {
            @Override
            protected ArrayList<NLevelListItem> doInBackground(String... params) {
                // Just filter user
                ArrayList<NLevelListItem> items = (ArrayList<NLevelListItem>) filterUsers(params[0]);
                Utils.printLogs("Text = "+params[0]+" Item size = "+items.size());
                return  items;
            }

            @Override
            protected void onPostExecute(ArrayList<NLevelListItem> nLevelListItems) {
                setFiltered(nLevelListItems);
                NLevelRecycleAdapter.this.notifyDataSetChanged();
            }
        }

    }

    // Filter user by UserName or User ID
    public List<NLevelListItem> filterUsers(String textString){
        List<NLevelListItem> tempfiltered = new ArrayList<NLevelListItem>();
        if (TextUtils.isEmpty(textString)){
            tempfiltered.addAll(list);
        } else {

            for (NLevelListItem item : list) {
                //add expanded items and top level items
                //if parent is null then its a top level item
                if(item.getParent() == null && item.getObject() != null && item.getObject().getName()!= null && item.getObject().getName().equals("Dazone")) {
                    tempfiltered.add(item);
                } else {
                    TreeUserDTO object = item.getObject();
                    if (object != null && object.getType() == Statics.TYPE_USER){
                        if (object.getName().contains(textString)){
                            tempfiltered.add(item);
                        }
                    }
                }
            }
        }

        return tempfiltered;
    }

    public List<NLevelListItem> filterItems() {
        List<NLevelListItem> tempfiltered = new ArrayList<NLevelListItem>();
        OUTER: for (NLevelListItem item : list) {
            //add expanded items and top level items
            //if parent is null then its a top level item
            if(item.getParent() == null) {
                tempfiltered.add(item);
            } else {
                //go through each ancestor to make sure they are all expanded
                NLevelListItem parent = item;
                while ((parent = parent.getParent())!= null) {
                    if (!parent.isExpanded()){
                        //one parent was not expanded
                        //skip the rest and continue the OUTER for loop
                        continue OUTER;
                    }
                }
                tempfiltered.add(item);
            }
        }

        return tempfiltered;
    }

    public void toggle(int arg2) {
        filtered.get(arg2).toggle();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        NLevelItem dto = null;
        try {
            dto = (NLevelItem) v.getTag();
        }catch (Exception e){
            e.printStackTrace();
        }

        if (dto != null) {
            mTempDto = dto;

            if (dto.getObject().getType() == Statics.TYPE_USER) {
                if(dto.getParent() != null) {
                    if(dto.getParent().getObject() != null) {
                        if( dto.getParent().getObject().getType() == 0 ) { return; }
                    }
                }
                if (menu.size() == 0){
                    Resources res = CrewChatApplication.getInstance().getResources();
                    MenuItem removeFavorite = menu.add(0, Statics.MENU_REMOVE_FROM_FAVORITE, 0, res.getString(R.string.remove_from_favorite));
                    MenuItem openChatRoom = menu.add(0, Statics.MENU_OPEN_CHAT_ROOM, 0, res.getString(R.string.open_chat_room));

                    removeFavorite.setOnMenuItemClickListener(this);
                    openChatRoom.setOnMenuItemClickListener(this);
                }
            } else {
                if(dto.getObject().getType() == 0) { return; }

                if (menu.size() == 0){
                    Resources res = CrewChatApplication.getInstance().getResources();
                    MenuItem registedUser = menu.add(0, Statics.MENU_REGISTERED_USERS, 0, res.getString(R.string.registered_users));
                    MenuItem modifyGroup = menu.add(0, Statics.MENU_MODIFYING_GROUP, 0, res.getString(R.string.modifying_group));
                    MenuItem deleteGroup = menu.add(0, Statics.MENU_DELETE_GROUP, 0, res.getString(R.string.delete_group));


                    registedUser.setOnMenuItemClickListener(this);
                    modifyGroup.setOnMenuItemClickListener(this);
                    deleteGroup.setOnMenuItemClickListener(this);
                }
            }
        }

    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        final TreeUserDTO dto = mTempDto.getObject();
        switch (item.getItemId()) {
            case Statics.MENU_REMOVE_FROM_FAVORITE:
                // Call API to remove an user from favorite list
                HttpRequest.getInstance().deleteFavoriteUser(dto.getParent(), dto.getId(), new BaseHTTPCallbackWithJson() {
                    @Override
                    public void onHTTPSuccess(String jsonData) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                FavoriteUserDBHelper.deleteFavoriteUser(dto.getParent(), dto.getId());
                            }
                        }).start();

                        list.remove(mTempDto);
                        reloadData();
                    }

                    @Override
                    public void onHTTPFail(ErrorDto errorDto) {
                        Toast.makeText(CrewChatApplication.getInstance(), "Has failed", Toast.LENGTH_LONG).show();
                    }
                });

                break;
            case Statics.MENU_OPEN_CHAT_ROOM:

                if (dto.getId() != Utils.getCurrentId())
                    HttpRequest.getInstance().CreateOneUserChatRoom(dto.getId(), new ICreateOneUserChatRom() {
                        @Override
                        public void onICreateOneUserChatRomSuccess(ChattingDto chattingDto) {
                            Intent intent = new Intent(BaseActivity.Instance, ChattingActivity.class);
                            intent.putExtra(Constant.KEY_INTENT_ROOM_NO, chattingDto.getRoomNo());
                            intent.putExtra(Statics.TREE_USER_PC, dto);
                            intent.putExtra(Statics.CHATTING_DTO, chattingDto);
                            BaseActivity.Instance.startActivity(intent);
                        }

                        @Override
                        public void onICreateOneUserChatRomFail(ErrorDto errorDto) {
                            Utils.showMessageShort("Fail");
                        }
                    });
                else
                    Utils.showMessage(Utils.getString(R.string.can_not_chat));

                break;

            case Statics.MENU_REGISTERED_USERS:

                ArrayList<Integer> uNos = new ArrayList<>();
                for (NLevelItem u : list){
                    if (u.getParent() != null && u.getParent().equals(mTempDto))
                    uNos.add(u.getObject().getId());
                }

                // Callback to modify user
                if (mCallback != null){
                    mCallback.onShow(mTempDto, uNos);
                }

                break;
            case Statics.MENU_MODIFYING_GROUP:

                Resources res = CrewChatApplication.getInstance().getResources();
                String groupName = res.getString(R.string.group_name);
                String confirm = res.getString(R.string.confirm);
                String cancel = res.getString(R.string.cancel);

                AlertDialogView.alertDialogComfirmWithEdittext(mContext, groupName, groupName, dto.getName() , confirm, cancel, new AlertDialogView.onAlertDialogViewClickEventData() {
                    @Override
                    public void onOkClick(String groupName) {
                        // Call API to add group
                        int sortNo = 0;
                        updateFavoriteGroup(mTempDto, groupName, sortNo);
                    }

                    @Override
                    public void onCancelClick() {
                        // Dismiss dialog
                    }
                });

                break;
            case Statics.MENU_DELETE_GROUP:
                AlertDialogView.normalAlertDialogWithCancel(mContext, Utils.getString(R.string.app_name),Utils.getString(R.string.favorite_group_delete_warning), Utils.getString(R.string.no), Utils.getString(R.string.yes) , new AlertDialogView.OnAlertDialogViewClickEvent(){
                    @Override
                    public void onOkClick(DialogInterface alertDialog) {
                        onDeleteGroup(dto.getId());
                    }

                    @Override
                    public void onCancelClick() {

                    }
                });

                break;
        }

        return false;
    }

    public void reloadData(){
        filtered = filterItems();
        notifyDataSetChanged();
    }

    /* Function call API delete favorite group */
    /*
    * Delete group and delete all user of this group
    * */
    private void onDeleteGroup(final long groupNo){
        HttpRequest.getInstance().deleteFavoriteGroup(groupNo, new BaseHTTPCallBack() {
            @Override
            public void onHTTPSuccess() {
                // Delete favorite group in new thread

                // notify local data
                for (Iterator<NLevelItem> iterator = list.iterator(); iterator.hasNext();) {
                    NLevelItem item = iterator.next();
                    if (item.getParent() != null && item.getParent().getObject().getId() == mTempDto.getObject().getId()){
                        iterator.remove();
                    }
                }
                list.remove(mTempDto);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        FavoriteGroupDBHelper.deleteFavoriteGroup(groupNo);
                    }
                }).start();

                reloadData();
            }

            @Override
            public void onHTTPFail(ErrorDto errorDto) {

            }
        });
    }

    /* Function to request to server to update favorite group */
    private void updateFavoriteGroup(final NLevelItem item, final String groupNam, int sortNo){
        final TreeUserDTO dto = item.getObject();
        HttpRequest.getInstance().updateFavoriteGroup(dto.getId(), groupNam, sortNo, new BaseHTTPCallbackWithJson() {
            @Override
            public void onHTTPSuccess(String jsonData) {
                // refresh view again, find item to update
                int indexof = list.indexOf(item);
                if (indexof != -1){

                    NLevelItem foundItem = list.get(indexof);

                    // perform update by new thread
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            FavoriteGroupDBHelper.updateGroup(dto.getId(), groupNam);
                        }
                    }).start();

                    // Set current dataset value
                    foundItem.getObject().setName(groupNam);
                    foundItem.getObject().setNameEN(groupNam);
                    // notify data set
                    reloadData();
                }

            }

            @Override
            public void onHTTPFail(ErrorDto errorDto) {
                Utils.printLogs("Update group failed ###");
            }
        });
    }
}
