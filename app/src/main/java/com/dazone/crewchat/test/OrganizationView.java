package com.dazone.crewchat.test;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.dazone.crewchat.HTTPs.HttpRequest;
import com.dazone.crewchat.R;
import com.dazone.crewchat.Tree.Dtos.TreeUserDTO;
import com.dazone.crewchat.Tree.Org_tree;
import com.dazone.crewchat.constant.Statics;
import com.dazone.crewchat.database.DepartmentDBHelper;
import com.dazone.crewchat.dto.BelongDepartmentDTO;
import com.dazone.crewchat.dto.ErrorDto;
import com.dazone.crewchat.dto.TreeUserDTOTemp;
import com.dazone.crewchat.interfaces.IGetListDepart;
import com.dazone.crewchat.utils.Prefs;
import com.dazone.crewchat.utils.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Sherry on 12/31/15.
 */
public class OrganizationView {

    private ArrayList<TreeUserDTO> mPersonList = new ArrayList<>();
    private ArrayList<TreeUserDTO> mSelectedPersonList;
    private ArrayList<TreeUserDTO> temp = new ArrayList<>();
    private Context mContext;
    private int displayType = 0; // 0 folder structure , 1
    private OnOrganizationSelectedEvent mSelectedEvent;
    private ArrayList<TreeUserDTOTemp> listTemp;
    private int myId = Utils.getCurrentId();
    private boolean mIsDisableSelected = false;
    private ArrayList<TreeUserDTO> mDepartmentList;

    public OrganizationView(Context context,ArrayList<TreeUserDTO> selectedPersonList, boolean isDisableSelected, ViewGroup viewGroup) {
        this.mContext = context;
        this.mIsDisableSelected = isDisableSelected;
        if (selectedPersonList != null)
            this.mSelectedPersonList = selectedPersonList;
        else
            this.mSelectedPersonList = new ArrayList<>();

        if (selectedPersonList != null) {
            Utils.printLogs("Selected personal size = "+selectedPersonList.size());
        }

        initWholeOrganization(viewGroup);
    }


    public void convertData(List<TreeUserDTO> treeUserDTOs) {
        if (treeUserDTOs != null && treeUserDTOs.size() != 0) {
            for (TreeUserDTO dto : treeUserDTOs) {
                if (dto.getSubordinates() != null && dto.getSubordinates().size() > 0) {
                    temp.add(dto);
                    convertData(dto.getSubordinates());
                } else {
                    temp.add(dto);
                }
            }
        }
    }

    private void initWholeOrganization(final ViewGroup viewGroup) {
        // build offline version
        // Get offline data
        mDepartmentList = DepartmentDBHelper.getDepartments();
        listTemp = Utils.getUsers();

        if (mDepartmentList != null && mDepartmentList.size() > 0){
            buildTree(mDepartmentList, viewGroup, false);
        } else { // Get department from server

            HttpRequest.getInstance().GetListDepart(new IGetListDepart() {
                @Override
                public void onGetListDepartSuccess(ArrayList<TreeUserDTO> treeUserDTOs) {
                    buildTree(treeUserDTOs, viewGroup, true);
                }
                @Override
                public void onGetListDepartFail(ErrorDto dto) {

                }
            });
        }
    }

    private void buildTree(final ArrayList<TreeUserDTO> treeUserDTOs,ViewGroup viewGroup, boolean isFromServer){
        if (treeUserDTOs != null) {
            if (isFromServer) {
                convertData(treeUserDTOs);
            }else{
                // add data offline to temp
                temp.clear();
                temp.addAll(treeUserDTOs);
            }

            for (TreeUserDTO treeUserDTO : temp) {
                if (treeUserDTO.getSubordinates() != null && treeUserDTO.getSubordinates().size() > 0) {
                    treeUserDTO.setSubordinates(null);
                }
            }

            // sort data by order
            Collections.sort(temp, new Comparator<TreeUserDTO>() {
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

            for (TreeUserDTOTemp treeUserDTOTemp : listTemp) {

                for (BelongDepartmentDTO belong : treeUserDTOTemp.getBelongs()){
                    TreeUserDTO treeUserDTO = new TreeUserDTO(
                            treeUserDTOTemp.getName(),
                            treeUserDTOTemp.getNameEN(),
                            treeUserDTOTemp.getCellPhone(),
                            treeUserDTOTemp.getAvatarUrl(),
                            belong.getPositionName(),
                            treeUserDTOTemp.getType(),
                            treeUserDTOTemp.getStatus(),
                            treeUserDTOTemp.getUserNo(),
                            belong.getDepartNo(),
                            treeUserDTOTemp.getUserStatusString(),
                            belong.getPositionSortNo()
                    );

                    for (TreeUserDTO u : mSelectedPersonList){
                        if (treeUserDTOTemp.getUserNo() == u.getId()){
                            treeUserDTO.setIsCheck(true);
                            break;
                        }
                    }
                    temp.add(treeUserDTO);
                }

            }

            mPersonList = new ArrayList<>();
            mPersonList.addAll(temp);
            TreeUserDTO dto = null;
            try {
                dto = Org_tree.buildTree(mPersonList);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (dto != null) {
                for (TreeUserDTO treeUserDTO : dto.getSubordinates()) {
                    draw(treeUserDTO, viewGroup, false, 0);
                }
            }

        }
    }

    public void setOnSelectedEvent(OnOrganizationSelectedEvent selectedEvent) {
        this.mSelectedEvent = selectedEvent;
    }


    private void draw(final TreeUserDTO treeUserDTO, final ViewGroup layout, final boolean checked, final int iconMargin) {
        final LinearLayout child_list;
        final LinearLayout iconWrapper;
        final ImageView avatar;
        final ImageView folderIcon;
        final ImageView ivStatus;
        final TextView name, position;
        final CheckBox row_check;
        final RelativeLayout relAvatar;
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.row_organization, null);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        layout.addView(view);
        child_list = (LinearLayout) view.findViewById(R.id.child_list);
        avatar = (ImageView) view.findViewById(R.id.avatar);
        folderIcon = (ImageView) view.findViewById(R.id.ic_folder);
        relAvatar = (RelativeLayout) view.findViewById(R.id.relAvatar);
        iconWrapper = (LinearLayout) view.findViewById(R.id.icon_wrapper);
        ivStatus = (ImageView) view.findViewById(R.id.status_imv);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) iconWrapper.getLayoutParams();
        if (displayType == 0) // set margin for icon if it's company type
        {
            params.leftMargin = iconMargin;
        }
        iconWrapper.setLayoutParams(params);
        name = (TextView) view.findViewById(R.id.name);
        position = (TextView) view.findViewById(R.id.position);
        row_check = (CheckBox) view.findViewById(R.id.row_check);

        row_check.setChecked(treeUserDTO.isCheck());

        if(treeUserDTO.isCheck()){
            if (mIsDisableSelected){
                row_check.setEnabled(false);
            } else {
                row_check.setEnabled(true);
            }
        }



        String nameString = treeUserDTO.getName();
        String namePosition = treeUserDTO.getPosition();
        if (treeUserDTO.getType() == 2) {
            String url = new Prefs().getServerSite() + treeUserDTO.getAvatarUrl();

            //ImageUtils.showCycleImageFromLink(url, avatar, R.dimen.button_height);
            ImageLoader.getInstance().displayImage(url, avatar, Statics.options2);

            position.setVisibility(View.VISIBLE);
            position.setText(namePosition);
            folderIcon.setVisibility(View.GONE);
            relAvatar.setVisibility(View.VISIBLE);

            int status = treeUserDTO.getStatus();
            if (status == Statics.USER_LOGIN){
                ivStatus.setImageResource(R.drawable.home_big_status_01);
            }else if(status == Statics.USER_AWAY){
                ivStatus.setImageResource(R.drawable.home_big_status_02);
            }else{ // Logout state
                ivStatus.setImageResource(R.drawable.home_big_status_03);
            }

        } else {
            position.setVisibility(View.GONE);
            relAvatar.setVisibility(View.GONE);
            folderIcon.setVisibility(View.VISIBLE);
        }
        name.setText(nameString);

        if (treeUserDTO.getId() == myId) {
            row_check.setEnabled(false);
        } /*else {
            row_check.setEnabled(true);
        }*/

        final int tempMargin = iconMargin + Utils.getDimenInPx(R.dimen.dimen_20_40);

        row_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked && treeUserDTO.getType() == 2) {
//                    unCheckFather(dto);
                    ViewGroup parent = ((ViewGroup) layout.getParent());
                    unCheckBoxParent(parent);
                } else {
                    if (buttonView.getTag() != null && !(Boolean) buttonView.getTag()) {
                        buttonView.setTag(true);
                    } else {
                        treeUserDTO.setIsCheck(isChecked);
                        if (treeUserDTO.getSubordinates() != null && treeUserDTO.getSubordinates().size() != 0) {
                            int index = 0;
                            for (TreeUserDTO dto1 : treeUserDTO.getSubordinates()) {

                                dto1.setIsCheck(isChecked);
                                View childView = child_list.getChildAt(index);
                                CheckBox childCheckBox = (CheckBox) childView.findViewById(R.id.row_check);
                                if (childCheckBox != null) {
                                    if (childCheckBox.isEnabled()) {
                                        childCheckBox.setChecked(dto1.isCheck());
                                    }

                                } else {
                                    break;
                                }
                                index++;
                            }
                        }
                    }
                }
                if (mSelectedEvent != null) {
                    mSelectedEvent.onOrganizationCheck(isChecked, treeUserDTO);
                }
            }
        });

        String temp = treeUserDTO.getId() + treeUserDTO.getName();
        if (!TextUtils.isEmpty(temp)) {
            if (new Prefs().getBooleanValue(temp, true)) {
                child_list.setVisibility(View.VISIBLE);
            } else {
                child_list.setVisibility(View.GONE);
            }
        }

        if (treeUserDTO.getSubordinates() != null && treeUserDTO.getSubordinates().size() != 0) {
            folderIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showHideSubMenuView(child_list, folderIcon, treeUserDTO);
                }
            });
            name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showHideSubMenuView(child_list, folderIcon, treeUserDTO);
                }
            });

            if (treeUserDTO.getItemName().equalsIgnoreCase("Customer Business Div.")) {
                // sort data by order
                Collections.sort(treeUserDTO.getSubordinates(), new Comparator<TreeUserDTO>() {
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

            for (TreeUserDTO dto1 : treeUserDTO.getSubordinates()) {
                draw(dto1, child_list, false, tempMargin);
            }
        }
    }

    private void unCheckBoxParent(ViewGroup view) {
        if (view.getId() == R.id.item_org_main_wrapper || view.getId() == R.id.item_org_wrapper) {
            CheckBox parentCheckBox = (CheckBox) view.findViewById(R.id.row_check);
            if (parentCheckBox.isChecked()) {
                parentCheckBox.setTag(false);
                parentCheckBox.setChecked(false);
            }
            if ((view.getParent()).getParent() instanceof ViewGroup) {
                try {
                    ViewGroup parent = (ViewGroup) (view.getParent()).getParent();
                    unCheckBoxParent(parent);
                } catch (Exception e) {
                }
            }
        }
    }

    private void showHideSubMenuView(LinearLayout child_list, ImageView icon, TreeUserDTO treeUserDTO) {
        String temp = treeUserDTO.getId() + treeUserDTO.getName();
        if (child_list.getVisibility() == View.VISIBLE) {
            child_list.setVisibility(View.GONE);
            icon.setImageResource(R.drawable.home_folder_close_ic);
            new Prefs().putBooleanValue(temp, false);

        } else {
            child_list.setVisibility(View.VISIBLE);
            icon.setImageResource(R.drawable.home_folder_open_ic);
            new Prefs().putBooleanValue(temp, true);

        }
    }
}
