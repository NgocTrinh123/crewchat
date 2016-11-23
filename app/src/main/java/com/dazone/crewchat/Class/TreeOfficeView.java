package com.dazone.crewchat.Class;

import android.content.Context;
import android.content.res.Resources;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.dazone.crewchat.R;
import com.dazone.crewchat.Tree.Dtos.TreeUserDTO;
import com.dazone.crewchat.constant.Statics;
import com.dazone.crewchat.interfaces.OnDeleteFavoriteGroup;
import com.dazone.crewchat.sqlite.DAO.DepartmentDAO;
import com.dazone.crewchat.utils.CrewChatApplication;
import com.dazone.crewchat.utils.Utils;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

/**
 * Created by david on 1/4/16.
 */
public class TreeOfficeView extends TreeView implements View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener{

    LinearLayout lnl_child, mLnTittle;
    ImageView icon;
    private Context context;
    private HashMap<Integer, ImageView> mStatusViewMap;
    private int marginLeft = 0;
    private OnDeleteFavoriteGroup mCallback;

    public TreeOfficeView(Context context, TreeUserDTO dto) {
        super(context, dto);
        this.context = context;
        setupView();
    }

    public TreeOfficeView(Context context, TreeUserDTO dto, HashMap<Integer, ImageView> statusViewMap , int marginLeft, OnDeleteFavoriteGroup deleteCallback) {
        super(context, dto);
        this.context = context;
        this.mStatusViewMap = statusViewMap;
        this.marginLeft = marginLeft;
        this.mCallback = deleteCallback;
        setupView();
    }

    @Override
    protected void setupView() {
        currentView = inflater.inflate(R.layout.tree_office_row, null);
        title = (TextView) currentView.findViewById(R.id.office_title);
        icon = (ImageView) currentView.findViewById(R.id.ic_folder);
        checkBox = (CheckBox) currentView.findViewById(R.id.row_check);
        main = (RelativeLayout) currentView.findViewById(R.id.mainParent);
        mLnTittle = (LinearLayout) currentView.findViewById(R.id.layout_title);

        mLnTittle.setOnCreateContextMenuListener(this);

        bindData();
    }

    private void bindData() {
        if (dto == null)
            return;



        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) icon.getLayoutParams();
        params.leftMargin = marginLeft;

        lnl_child = (LinearLayout) currentView.findViewById(R.id.lnl_child);

        if (dto.getId() != 0) {
            title.setText(dto.getItemName());
            if (dto.getSubordinates() != null && dto.getSubordinates().size() != 0) {
                if (dto.getItemName().equalsIgnoreCase("Customer Business Div.")) {
                    // sort data by order
                    Collections.sort(dto.getSubordinates(), new Comparator<TreeUserDTO>() {
                        @Override
                        public int compare(TreeUserDTO r1, TreeUserDTO r2) {
                            if (r1.getmSortNo() > r2.getmSortNo()) {
                                return 1;
                            } else if (r1.getmSortNo() == r2.getmSortNo()) {
                                return 0;
                            } else {
                                return -1;
                            }
                        }
                    });
                }
                for (TreeUserDTO dto1 : dto.getSubordinates()) {
                    setupChild(dto1);
                }
            }
            setupHideShow();
            setIconClick();
            handleItemClick(true);
        } else {
            title.setVisibility(View.GONE);
            icon.setVisibility(View.GONE);
            if (dto.getSubordinates() != null && dto.getSubordinates().size() != 0) {
                if (dto.getItemName().equalsIgnoreCase("Customer Business Div.")) {
                    // sort data by order
                    Collections.sort(dto.getSubordinates(), new Comparator<TreeUserDTO>() {
                        @Override
                        public int compare(TreeUserDTO r1, TreeUserDTO r2) {
                            if (r1.getmSortNo() > r2.getmSortNo()) {
                                return 1;
                            } else if (r1.getmSortNo() == r2.getmSortNo()) {
                                return 0;
                            } else {
                                return -1;
                            }
                        }
                    });
                }
                for (TreeUserDTO dto1 : dto.getSubordinates()) {
                    setupChild(dto1);
                }
            }
        }

        mLnTittle.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // Implement context menu
                v.showContextMenu();
                return true;
            }
        });

    }

    private void setIconClick() {
        if (icon == null)
            return;
        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dto.setIsHide(dto.getIsHide() == 0 ? 1 : 0);
                DepartmentDAO departmentDAO = new DepartmentDAO(context);
                departmentDAO.updateDepartmentByUserNoAndID(dto.getId(), Utils.getCurrentId(), dto.getIsHide());
                setupHideShow();

            }
        });

    }

    private void setupHideShow() {
        if (dto.getIsHide() == 1) {
            icon.setImageResource(R.drawable.home_folder_close_ic);
            lnl_child.setVisibility(View.GONE);
        } else {
            icon.setImageResource(R.drawable.home_folder_open_ic);
            lnl_child.setVisibility(View.VISIBLE);
        }
    }

    private void setupChild(TreeUserDTO dto) {
        if (dto == null)
            return;
        final TreeView treeView;
        final int tempMargin;
        if (marginLeft == -1){
            tempMargin = 0;
        }else{
            tempMargin = marginLeft + Utils.getDimenInPx(R.dimen.dimen_20_40);
        }

        if (dto.getType() != 2) {
            treeView = new TreeOfficeView(context, dto, mStatusViewMap, tempMargin, mCallback);

        } else {
            treeView = new TreeUserView(context, dto, mStatusViewMap, tempMargin);
        }
        treeView.addToView(lnl_child);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

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

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        long groupNo = dto.getId();
        switch (item.getItemId()) {
            case Statics.MENU_REGISTERED_USERS:
                if (mCallback != null){
                    mCallback.onAdd(groupNo, dto.getSubordinates());
                }
                break;
            case Statics.MENU_MODIFYING_GROUP:
                if (mCallback != null){
                    mCallback.onEdit(groupNo, dto.getName());
                }
                break;
            case Statics.MENU_DELETE_GROUP:
                if (mCallback != null){
                    mCallback.onDelete(groupNo);
                }

                break;
        }
        return false;
    }
}
