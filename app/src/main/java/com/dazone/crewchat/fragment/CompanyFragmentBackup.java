package com.dazone.crewchat.fragment;

import android.os.Handler;
import android.view.View;

import com.dazone.crewchat.adapter.FavoriteListAdapter;
import com.dazone.crewchat.constant.Statics;
import com.dazone.crewchat.database.UserDBHelper;
import com.dazone.crewchat.dto.ErrorDto;
import com.dazone.crewchat.dto.TreeUserDTOTemp;
import com.dazone.crewchat.interfaces.IGetListDepart;
import com.dazone.crewchat.interfaces.IGetListOrganization;
import com.dazone.crewchat.sqlite.DAO.DepartmentDAO;
import com.dazone.crewchat.sqlite.TO.Department;
import com.dazone.crewchat.Tree.Dtos.TreeUserDTO;
import com.dazone.crewchat.Tree.Org_tree;
import com.dazone.crewchat.utils.Prefs;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by david on 12/23/15.
 */
public class CompanyFragmentBackup extends ListFragment<TreeUserDTO> implements IGetListOrganization, IGetListDepart {

    private List<TreeUserDTO> list = new ArrayList<>();
    private TreeUserDTO dto = null;

    @Override
    protected void initList() {
        mHttpRequest.GetListDepart(this);
    }

    @Override
    protected void initAdapter() {
        adapterList = new FavoriteListAdapter(mContext,dataSet, rvMainList);
        //enableLoadingMore();
    }

    @Override
    protected void reloadContentPage() {
        dataSet.add(null);
        adapterList.notifyItemInserted(dataSet.size() - 1);
//        mHttpRequest.getAllEmployeesSort(this, millis,limit,userNo,sortType);
    }

    @Override
    protected void addMoreItem() {
//        mHttpRequest.getAllEmployeesSort(this, millis,limit,userNo,sortType);
    }

//    @Override
//    public void onHTTPSuccess(List<CurrentChatDto> dtos) {
//        if(dataSet==null) {
//            return;
//        }
//        int dataSetSize= dataSet.size();
//        if(dataSetSize>0) {
//            dataSet.remove(dataSet.size() - 1);
//            adapterList.notifyItemRemoved(dataSet.size());
//        }
//        dataSet.addAll(dtos);
////        if(dataSet!=null&&dataSet.size()>0) {
////            lastID = (current_Task.get(current_Task.size() - 1)).userno;
////        }
//        adapterList.notifyItemChanged(dataSetSize, dataSet.size());
//        if(dataSetSize+limit<=dataSet.size())
//        {
//            adapterList.setLoaded();
//        }
//    }
//
//    @Override
//    public void onHTTPFail(ErrorDto errorDto) {
//
//    }


    /*//change late when have api
    @Override
    protected void initSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }*/

    @Override
    public void onGetListSuccess(ArrayList<TreeUserDTOTemp> treeUserDTOs) {
        dto = null;

        for (TreeUserDTO treeUserDTO : list) {
            //treeUserDTO.setIsHide(1);

            /** INSERT IF NOT EXITS */
            DepartmentDAO departmentDAO = new DepartmentDAO(getActivity());
            Department department = new Department();
            department.setDepartment_id(treeUserDTO.getId() + "");
            department.setDepartment_user_no(UserDBHelper.getUser().Id + "");
            department.setDepartment_name(treeUserDTO.getNameEN() + "");
            department.setDepartment_is_hide(treeUserDTO.getIsHide() + "");
            departmentDAO.insert(department);

            Department department1 = departmentDAO.getDepartmentByUserNoAndID(treeUserDTO.getId(), UserDBHelper.getUser().Id);
            treeUserDTO.setIsHide(Integer.parseInt(department1.getDepartment_is_hide()));

            if (treeUserDTO.getSubordinates() != null && treeUserDTO.getSubordinates().size() > 0) {
                treeUserDTO.setSubordinates(null);
            }
        }

        for (TreeUserDTOTemp treeUserDTOTemp : treeUserDTOs) {
            TreeUserDTO treeUserDTO = new TreeUserDTO(treeUserDTOTemp.getName(), treeUserDTOTemp.getNameEN(), treeUserDTOTemp.getCellPhone(), treeUserDTOTemp.getAvatarUrl(), treeUserDTOTemp.getPosition(),
                    treeUserDTOTemp.getType(), treeUserDTOTemp.getStatus(), treeUserDTOTemp.getUserNo(), treeUserDTOTemp.getDepartNo());
            list.add(treeUserDTO);
        }

        try {
            dto = Org_tree.buildTree(list);
            String treeUser;
            if (dto != null) {
                treeUser = new Gson().toJson(dto);
                new Prefs().putStringValue(Statics.ORANGE, treeUser);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //dataSet.add(dto);
        //adapterList.notifyDataSetChanged();
    }

    @Override
    public void onGetListFail(ErrorDto dto) {

    }

    public void convertData(List<TreeUserDTO> treeUserDTOs) {
        if (treeUserDTOs != null && treeUserDTOs.size() != 0) {
            for (TreeUserDTO dto : treeUserDTOs) {
                if (dto.getSubordinates() != null && dto.getSubordinates().size() > 0) {
                    list.add(dto);
                    convertData(dto.getSubordinates());
                } else {
                    list.add(dto);
                }
            }
        }
    }

    public void updateList() {
        if (dto != null && dataSet.size() < 1) {
            progressBar.setVisibility(View.VISIBLE);
            dataSet.add(dto);
            Handler mHandler = new Handler();
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    adapterList.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);
                }
            }, 1000);
        }
    }

    @Override
    public void onGetListDepartSuccess(ArrayList<TreeUserDTO> treeUserDTOs) {
        convertData(treeUserDTOs);
        //mHttpRequest.GetListOrganize(this);
    }

    @Override
    public void onGetListDepartFail(ErrorDto dto) {

    }
}
