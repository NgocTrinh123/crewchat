package com.dazone.crewchat.Class;

/**
 * Created by THANHTUNG on 23/02/2016.
 */

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dazone.crewchat.interfaces.OnOrganizationSelectedEvent;
import com.dazone.crewchat.R;
import com.dazone.crewchat.Tree.Dtos.TreeUserDTO;

import java.util.Collections;
import java.util.Comparator;

/**
 * Created by david on 1/4/16.
 */
public class TreeParent extends TreeView {

    ImageView icon;
    OnOrganizationSelectedEvent onOrganizationSelectedEvent;

    public TreeParent(Context context, TreeUserDTO dto, OnOrganizationSelectedEvent onOrganizationSelectedEvent) {
        super(context, dto);
        this.onOrganizationSelectedEvent = onOrganizationSelectedEvent;
        setupView();

    }

    @Override
    protected void setupView() {
        currentView = inflater.inflate(R.layout.tree_office_row, null);
        title = (TextView) currentView.findViewById(R.id.office_title);
        icon = (ImageView) currentView.findViewById(R.id.ic_folder);
        checkBox = (CheckBox) currentView.findViewById(R.id.row_check);
        main = (RelativeLayout) currentView.findViewById(R.id.mainParent);
        bindData();
    }

    private void bindData() {
        if (dto == null)
            return;
        checkBox.setVisibility(View.VISIBLE);
        title.setText(dto.getItemName());
        lnl_child = (LinearLayout) currentView.findViewById(R.id.lnl_child);
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
        handleItemClick(false);
    }

    private void setIconClick() {
        if (icon == null)
            return;
        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dto.setIsHide(dto.getIsHide() == 0 ? 1 : 0);
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
        if (dto.getType() != 2) {
            treeView = new TreeParent(context, dto, onOrganizationSelectedEvent);
        } else {
            treeView = new TreeChild(context, dto, lnl_child);
        }
        treeView.addToView(lnl_child);
        treeView.setOnSelectedEvent(onOrganizationSelectedEvent);
    }

}
