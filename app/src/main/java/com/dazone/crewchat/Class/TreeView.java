package com.dazone.crewchat.Class;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.dazone.crewchat.HTTPs.HttpRequest;
import com.dazone.crewchat.R;
import com.dazone.crewchat.Tree.Dtos.TreeUserDTO;
import com.dazone.crewchat.activity.ChattingActivity;
import com.dazone.crewchat.activity.base.BaseActivity;
import com.dazone.crewchat.constant.Statics;
import com.dazone.crewchat.dto.ChattingDto;
import com.dazone.crewchat.dto.ErrorDto;
import com.dazone.crewchat.interfaces.ICreateOneUserChatRom;
import com.dazone.crewchat.interfaces.OnOrganizationSelectedEvent;
import com.dazone.crewchat.utils.Constant;
import com.dazone.crewchat.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by david on 1/4/16.
 */
public abstract class TreeView extends BaseViewClass {
    protected TreeUserDTO dto;
    protected TextView title;
    protected CheckBox checkBox;
    protected RelativeLayout main;
    protected LinearLayout lnl_child;
    private OnOrganizationSelectedEvent mSelectedEvent;
    private ArrayList<TreeUserDTO> list = new ArrayList<>();

    public TreeView(Context context, TreeUserDTO dto) {
        super(context);
        this.dto = dto;
    }

    public void setOnSelectedEvent(OnOrganizationSelectedEvent selectedEvent) {
        this.mSelectedEvent = selectedEvent;
    }

    protected void handleItemClick(boolean task) {
        if (dto.getType() == 2) {
            if (task)
                main.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

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
                    }
                });

        } else {
            if (task)
                main.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (dto.getSubordinates() != null && dto.getSubordinates().size() > 0) {
                            convertData(dto.getSubordinates());
                            if (list != null && list.size() > 0)
                                HttpRequest.getInstance().CreateGroupChatRoom(list, new ICreateOneUserChatRom() {
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
                });
        }

        /*main.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final List<String> itemList = new ArrayList<>();
                itemList.add(Utils.getString(R.string.add_fav));
                Utils.displaySingleChoiceList(main.getContext(), itemList, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Utils.showMessage(Utils.getString(R.string.developing));
                    }
                }, Utils.getString(R.string.app_name));
                return true;
            }
        });*/

        if (checkBox != null)
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    if (dto.getType() != 2) {
                        if (buttonView.getTag() != null && !(Boolean) buttonView.getTag()) {
                            buttonView.setTag(true);
                        } else {
                            dto.setIsCheck(isChecked);
                            if (dto.getSubordinates() != null && dto.getSubordinates().size() != 0) {
                                int index = 0;
                                for (TreeUserDTO dto1 : dto.getSubordinates()) {

                                    dto1.setIsCheck(isChecked);
                                    View childView = lnl_child.getChildAt(index);
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
                    } else {
                        if (lnl_child != null) {
                            if (!isChecked) {
                                ViewGroup parent = ((ViewGroup) lnl_child.getParent());
                                unCheckBoxParent(parent);
                            }
                        }
                    }

                    if (mSelectedEvent != null)
                        mSelectedEvent.onOrganizationCheck(isChecked, dto);
                }
            });
    }

    private void unCheckBoxParent(ViewGroup view) {
        if (view.getId() == R.id.mainParent) {
            CheckBox parentCheckBox = (CheckBox) view.findViewById(R.id.row_check);
            if (parentCheckBox.isChecked()) {
                parentCheckBox.setTag(false);
                parentCheckBox.setChecked(false);
            }

            try {
                ViewGroup parent = (ViewGroup) (view.getParent()).getParent().getParent();
                unCheckBoxParent(parent);
            } catch (Exception e) {
            }
            /*if ((view.getParent()).getParent() instanceof ViewGroup) {
                try {
                    ViewGroup parent = (ViewGroup) (view.getParent()).getParent();
                    unCheckBoxParent(parent);
                } catch (Exception e) {
                }
            }*/
        }
    }

    public void convertData(List<TreeUserDTO> treeUserDTOs) {
        if (treeUserDTOs != null && treeUserDTOs.size() != 0) {
            for (TreeUserDTO dto : treeUserDTOs) {
                if (dto.getSubordinates() != null && dto.getSubordinates().size() > 0) {
                    if (dto.getType() == 2)
                        list.add(dto);
                    convertData(dto.getSubordinates());
                } else {
                    if (dto.getType() == 2)
                        list.add(dto);
                }
            }
        }
    }
}
