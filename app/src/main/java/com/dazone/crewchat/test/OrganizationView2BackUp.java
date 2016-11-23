package com.dazone.crewchat.test;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dazone.crewchat.HTTPs.HttpRequest;
import com.dazone.crewchat.R;
import com.dazone.crewchat.Tree.Dtos.TreeUserDTO;
import com.dazone.crewchat.Tree.Org_tree;
import com.dazone.crewchat.activity.ChattingActivity;
import com.dazone.crewchat.activity.ProfileUserActivity;
import com.dazone.crewchat.activity.base.BaseActivity;
import com.dazone.crewchat.constant.Statics;
import com.dazone.crewchat.database.AllUserDBHelper;
import com.dazone.crewchat.database.UserDBHelper;
import com.dazone.crewchat.dto.ChattingDto;
import com.dazone.crewchat.dto.ErrorDto;
import com.dazone.crewchat.dto.TreeUserDTOTemp;
import com.dazone.crewchat.interfaces.ICreateOneUserChatRom;
import com.dazone.crewchat.interfaces.IGetListDepart;
import com.dazone.crewchat.utils.Constant;
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
public class OrganizationView2BackUp implements View.OnClickListener {

    private ArrayList<TreeUserDTO> mPersonList = new ArrayList<>();
    private ArrayList<TreeUserDTO> temp = new ArrayList<>();
    private Context mContext;
    private int displayType = 0; // 0 folder structure , 1
    private ArrayList<TreeUserDTOTemp> listTemp = AllUserDBHelper.getUser();
    private ArrayList<TreeUserDTO> selectedPersonList;

    public OrganizationView2BackUp(Context context, ArrayList<TreeUserDTO> selectedPersonList, boolean isDisplaySelectedOnly, ViewGroup viewGroup) {
        this.mContext = context;
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
        HttpRequest.getInstance().GetListDepart(new IGetListDepart() {
            @Override
            public void onGetListDepartSuccess(ArrayList<TreeUserDTO> treeUserDTOs) {

                if (treeUserDTOs != null) {
                    convertData(treeUserDTOs);
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
                        TreeUserDTO treeUserDTO = new TreeUserDTO(treeUserDTOTemp.getName(), treeUserDTOTemp.getNameEN(), treeUserDTOTemp.getCellPhone(), treeUserDTOTemp.getAvatarUrl(), treeUserDTOTemp.getPosition(),
                                treeUserDTOTemp.getType(), treeUserDTOTemp.getStatus(), treeUserDTOTemp.getUserNo(), treeUserDTOTemp.getDepartNo());
                        temp.add(treeUserDTO);
                    }

                    mPersonList = new ArrayList<>(temp);

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

            @Override
            public void onGetListDepartFail(ErrorDto dto) {

            }
        });
    }

    private void draw(final TreeUserDTO treeUserDTO, final ViewGroup layout, final boolean checked, final int iconMargin) {
        //System.out.println("aaaaaaaaaaaaaa ----------------------------------");
        //System.out.println("aaaaaaaaaaaaaa " + treeUserDTO.toString());
        final LinearLayout layoutMain;
        final LinearLayout child_list;
        final LinearLayout iconWrapper;
        final ImageView avatar;
        final ImageView folderIcon;
        final TextView name, position;
        final RelativeLayout relAvatar;
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.row_organization2, null);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        layout.addView(view);

        layoutMain = (LinearLayout) view.findViewById(R.id.item_org_main_wrapper);
        child_list = (LinearLayout) view.findViewById(R.id.child_list);
        avatar = (ImageView) view.findViewById(R.id.avatar);
        folderIcon = (ImageView) view.findViewById(R.id.ic_folder);
        relAvatar = (RelativeLayout) view.findViewById(R.id.relAvatar);
        iconWrapper = (LinearLayout) view.findViewById(R.id.icon_wrapper);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) iconWrapper.getLayoutParams();
        if (displayType == 0) // set margin for icon if it's company type
        {
            params.leftMargin = iconMargin;
        }
        iconWrapper.setLayoutParams(params);
        name = (TextView) view.findViewById(R.id.name);
        position = (TextView) view.findViewById(R.id.position);
        String nameString = treeUserDTO.getName();
        String namePosition = treeUserDTO.getPosition();
        if (treeUserDTO.getType() == 2) {
            String url = new Prefs().getServerSite() + treeUserDTO.getAvatarUrl();
            ImageLoader.getInstance().displayImage(url, avatar, Statics.options2);
            position.setVisibility(View.VISIBLE);
            position.setText(namePosition);
            folderIcon.setVisibility(View.GONE);
            relAvatar.setVisibility(View.VISIBLE);
            layoutMain.setTag(treeUserDTO);
            layoutMain.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    TreeUserDTO userInfo = (TreeUserDTO) v.getTag();
                    String strName = userInfo.getName();
                    String strPhoneNumber = userInfo.getPhoneNumber();
                    //String strCompanyNumber = u
                    //DialogUtils.showDialogUser();
                    return false;
                }
            });

        } else {
            position.setVisibility(View.GONE);
            relAvatar.setVisibility(View.GONE);
            folderIcon.setVisibility(View.VISIBLE);
        }
        name.setText(nameString);

        final int tempMargin = iconMargin + Utils.getDimenInPx(R.dimen.dimen_20_40);

        String temp = treeUserDTO.getId() + treeUserDTO.getName() + UserDBHelper.getUser().Id;
        if (!TextUtils.isEmpty(temp)) {
            if (new Prefs().getBooleanValue(temp, true)) {
                folderIcon.setImageResource(R.drawable.home_folder_open_ic);
                child_list.setVisibility(View.VISIBLE);
            } else {
                folderIcon.setImageResource(R.drawable.home_folder_close_ic);
                child_list.setVisibility(View.GONE);
            }
        }

        layoutMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedPersonList = new ArrayList<>();
                getSelectedPersonList(treeUserDTO);
                createChatRoom(selectedPersonList);
            }
        });


        if (treeUserDTO.getSubordinates() != null && treeUserDTO.getSubordinates().size() != 0) {
            folderIcon.setOnClickListener(new View.OnClickListener() {
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
        } else {
            folderIcon.setImageResource(R.drawable.home_folder_close_ic);
        }
    }

    /**
     * Create SELECTED PERSON
     */
    private void getSelectedPersonList(TreeUserDTO treeUserDTO) {
        if (treeUserDTO.getType() == 2) {
            selectedPersonList.add(treeUserDTO);
        } else {
            ArrayList<TreeUserDTO> subordinates = treeUserDTO.getSubordinates();
            if (subordinates != null) {
                for (TreeUserDTO treeUserDTO1 : subordinates) {
                    getSelectedPersonList(treeUserDTO1);
                }
            }
        }
    }

    /**
     * SHOW DIALOG
     */
    private void showMenu(final TreeUserDTO userInfo) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(mContext);
        builderSingle.setTitle(userInfo.getName());

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                mContext,
                R.layout.row_chatting_call);

        arrayAdapter.add("Profile");

        builderSingle.setAdapter(
                arrayAdapter,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(BaseActivity.Instance, ProfileUserActivity.class);
                        intent.putExtra(Constant.KEY_INTENT_USER_NO, userInfo.getId());
                        BaseActivity.Instance.startActivity(intent);
                        BaseActivity.Instance.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }
                });
        AlertDialog dialog = builderSingle.create();
        if (arrayAdapter.getCount() > 0) {
            dialog.show();
        }


        Button b = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        if (b != null) {
            b.setTextColor(ContextCompat.getColor(mContext, R.color.light_black));
        }
    }

    /**
     * Create CHAT ROOM
     */
    private void createChatRoom(final ArrayList<TreeUserDTO> selectedPersonList) {
        if (selectedPersonList.size() == 1) {
            if (selectedPersonList.get(0).getId() == UserDBHelper.getUser().Id) {
                Utils.showMessage(Utils.getString(R.string.can_not_chat));
            } else {
                HttpRequest.getInstance().CreateOneUserChatRoom(selectedPersonList.get(0).getId(), new ICreateOneUserChatRom() {
                    @Override
                    public void onICreateOneUserChatRomSuccess(ChattingDto chattingDto) {
                        Intent intent = new Intent(BaseActivity.Instance, ChattingActivity.class);
                        intent.putExtra(Statics.TREE_USER_PC, selectedPersonList.get(0));
                        intent.putExtra(Statics.CHATTING_DTO, chattingDto);
                        BaseActivity.Instance.startActivity(intent);
                    }

                    @Override
                    public void onICreateOneUserChatRomFail(ErrorDto errorDto) {
                        Utils.showMessageShort("Fail");
                    }
                });
            }
        } else if (selectedPersonList.size() > 1) {
            HttpRequest.getInstance().CreateGroupChatRoom(selectedPersonList, new ICreateOneUserChatRom() {
                @Override
                public void onICreateOneUserChatRomSuccess(ChattingDto chattingDto) {
                    Intent intent = new Intent(BaseActivity.Instance, ChattingActivity.class);
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

    /**
     * SHOW OR HIDE FOLDER
     */
    private void showHideSubMenuView(LinearLayout child_list, ImageView icon, TreeUserDTO treeUserDTO) {
        String temp = treeUserDTO.getId() + treeUserDTO.getName() + UserDBHelper.getUser().Id;
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ic_folder:
                break;
        }
    }
}
