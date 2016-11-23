package com.dazone.crewchat.test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.dazone.crewchat.HTTPs.HttpRequest;
import com.dazone.crewchat.R;
import com.dazone.crewchat.Tree.Dtos.TreeUserDTO;
import com.dazone.crewchat.activity.ChattingActivity;
import com.dazone.crewchat.activity.base.BaseActivity;
import com.dazone.crewchat.constant.Statics;
import com.dazone.crewchat.database.AllUserDBHelper;
import com.dazone.crewchat.dto.ChattingDto;
import com.dazone.crewchat.dto.ErrorDto;
import com.dazone.crewchat.dto.TreeUserDTOTemp;
import com.dazone.crewchat.fragment.BaseFragment;
import com.dazone.crewchat.interfaces.ICreateOneUserChatRom;
import com.dazone.crewchat.utils.Constant;
import com.dazone.crewchat.utils.Utils;

import java.util.ArrayList;

/**
 * Created by Dinh Huynh on 12/30/15.
 */
public class OrganizationFragment extends BaseFragment implements OnOrganizationSelectedEvent {

    private View mView;
    private ArrayList<TreeUserDTO> selectedPersonList;
    private LinearLayout mSharePersonContent;
    private OrganizationView orgView;
    private boolean mIsDisplaySelectedOnly = false;
    private static String SELECTED_PERSON_LIST = "selected_person_list";
    private static String IS_DISABLE_SELETED = "is_disable_selected_person_list";
    private ArrayList<Integer> selectedUserNos;
    private ArrayList<TreeUserDTOTemp> listTemp = AllUserDBHelper.getUser();
    private boolean isEnableSelected = false;

    public static OrganizationFragment newInstance(ArrayList<Integer> selectedPerson, boolean isEnableSelected) {
        Bundle args = new Bundle();
        args.putIntegerArrayList(SELECTED_PERSON_LIST,selectedPerson);
        args.putBoolean(IS_DISABLE_SELETED,isEnableSelected);
        OrganizationFragment fragment = new OrganizationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null){
            selectedUserNos = bundle.getIntegerArrayList(SELECTED_PERSON_LIST);
            isEnableSelected = bundle.getBoolean(IS_DISABLE_SELETED);
        }
        if (selectedPersonList == null) {
            selectedPersonList = new ArrayList<>();
        }

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_organization, container, false);
        mSharePersonContent = (LinearLayout) mView.findViewById(R.id.container);
        setSelectedPersonName();
        initOrganizationTree();
        return mView;
    }

    private void initOrganizationTree() {
        orgView = new OrganizationView(getActivity(),selectedPersonList, isEnableSelected, mSharePersonContent);
        orgView.setOnSelectedEvent(this);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case Statics.ORGANIZATION_DISPLAY_SELECTED_ACTIVITY:
                    /*ArrayList<TreeUserDTO> resultList = data.getExtras().getParcelableArrayList(StaticsBundle.BUNDLE_LIST_PERSON);
                    selectedPersonList.clear();
                    if(resultList != null)
                        selectedPersonList.addAll(resultList);
                    mSharePersonContent.removeAllViews();
                    orgView = new OrganizationView(getActivity(), selectedPersonList,mIsDisplaySelectedOnly,mSharePersonContent);
                    orgView.setOnSelectedEvent(this);*/
                    break;
            }
        }
    }


    private void unCheckParentData(TreeUserDTO personData) {
        if (mIsDisplaySelectedOnly) {

            TreeUserDTO needRemovePerson = null;
            for (TreeUserDTO selectedPerson : selectedPersonList) {
                if (personData.getType() == 2 && selectedPerson.getType() == 1 && selectedPerson.getId() == personData.getId()) {
                    needRemovePerson = selectedPerson;
                    break;
                } else if (personData.getType() == 1 && selectedPerson.getType() == 1 && selectedPerson.getId() == personData.getParent()) {
                    needRemovePerson = selectedPerson;
                    break;
                }
            }
            if (needRemovePerson != null) {
                selectedPersonList.remove(needRemovePerson);
                if (needRemovePerson.getParent() > 0) {
                    unCheckParentData(needRemovePerson);
                }
            }

        }
    }

    @Override
    public void onOrganizationCheck(boolean isCheck, TreeUserDTO personData) {
        int indexOf = selectedPersonList.indexOf(personData);

        if (indexOf != -1) {
            if (!isCheck) {
                selectedPersonList.remove(indexOf);
                unCheckParentData(personData);
            } else {
                selectedPersonList.get(indexOf).setIsCheck(true);
            }
        } else {
            if (isCheck) {
                selectedPersonList.add(personData);
            }
        }
        if (!mIsDisplaySelectedOnly) {
            setSelectedPersonName();
        }
    }

    private void setSelectedPersonName() {

        // First time init selected person list
        if (selectedUserNos != null && selectedUserNos.size() > 0){
            for (TreeUserDTOTemp treeUserDTOTemp : listTemp) {
                for (Integer uNo : selectedUserNos){

                    if (uNo == treeUserDTOTemp.getUserNo()){
                        TreeUserDTO treeUserDTO = new TreeUserDTO(treeUserDTOTemp.getName(), treeUserDTOTemp.getNameEN(), treeUserDTOTemp.getCellPhone(), treeUserDTOTemp.getAvatarUrl(), treeUserDTOTemp.getPosition(),
                                treeUserDTOTemp.getType(), treeUserDTOTemp.getStatus(), treeUserDTOTemp.getUserNo(), treeUserDTOTemp.getDepartNo());
                        treeUserDTO.setIsCheck(true);
                        selectedPersonList.add(treeUserDTO);
                    }
                }
            }
        }


        /*String shareString = "";
        for (TreeUserDTO selectedPerson : selectedPersonList) {
            if (!TextUtils.isEmpty(shareString)) {
                shareString += "; ";
            }
            shareString += selectedPerson.getName();
        }
        Utils.printLogs("Set selected person name = "+shareString);*/
    }

    public ArrayList<TreeUserDTO> getListUser() {
        return selectedPersonList;
    }

    public void callChat() {
        if (selectedPersonList != null)
            if (selectedPersonList.size() == 0) {
            } else if (selectedPersonList.size() == 1) {
                HttpRequest.getInstance().CreateOneUserChatRoom(selectedPersonList.get(0).getId(), new ICreateOneUserChatRom() {
                    @Override
                    public void onICreateOneUserChatRomSuccess(ChattingDto chattingDto) {
                        Intent intent = new Intent(BaseActivity.Instance, ChattingActivity.class);
                        intent.putExtra(Statics.TREE_USER_PC, selectedPersonList.get(0));
                        intent.putExtra(Statics.CHATTING_DTO, chattingDto);
                        intent.putExtra(Constant.KEY_INTENT_ROOM_NO, chattingDto.getRoomNo());
                        BaseActivity.Instance.startActivity(intent);
                    }

                    @Override
                    public void onICreateOneUserChatRomFail(ErrorDto errorDto) {
                        Utils.showMessageShort("Fail");
                    }
                });
            } else if (selectedPersonList.size() > 1) {
                HttpRequest.getInstance().CreateGroupChatRoom(selectedPersonList, new ICreateOneUserChatRom() {
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
