package com.dazone.crewchat.activity.base;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import com.dazone.crewchat.R;
import com.dazone.crewchat.Tree.Dtos.TreeUserDTO;
import com.dazone.crewchat.test.OrganizationFragment;
import com.dazone.crewchat.utils.Constant;
import com.dazone.crewchat.utils.Utils;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by THANHTUNG on 22/02/2016.
 */
public class OrganizationFavoriteActivity extends BaseSingleActivity {
    OrganizationFragment fragment;
    private long groupNo = -1;
    private int countMember = 0;
    private ArrayList<Integer> userNos;

    static {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    @Override
    protected void addFragment(Bundle bundle) {
        Intent intent = getIntent();
        if (intent != null) {
            try {
                groupNo = intent.getLongExtra(Constant.KEY_INTENT_GROUP_NO, 0);
                userNos = intent.getIntegerArrayListExtra(Constant.KEY_INTENT_COUNT_MEMBER);
            } catch (Exception e) {
                groupNo = -1;
                countMember = 0;
                e.printStackTrace();
            }
        }
        fragment = OrganizationFragment.newInstance(userNos, true);
        if (intent != null) {
            Utils.addFragmentToActivity(getSupportFragmentManager(), fragment, R.id.content_base_single_activity, false);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showSave();
        HiddenTitle();
        ivMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fragment != null) {
                    if (groupNo == -1) {
                        finish();
                    } else {
                        ArrayList<TreeUserDTO> list = fragment.getListUser();
                        if (list != null && list.size() > 0) {


                            Iterator<TreeUserDTO> iter = list.iterator();
                            while(iter.hasNext()) {
                                TreeUserDTO tree = iter.next();
                                for (Integer userId : userNos) {
                                    if (tree.getId() == userId){
                                        iter.remove();
                                    }
                                }
                            }

                            Intent intent = new Intent();

                            Bundle args = new Bundle();
                            args.putLong(Constant.KEY_INTENT_GROUP_NO, groupNo);
                            args.putSerializable(Constant.KEY_INTENT_SELECT_USER_RESULT, list);
                            intent.putExtras(args);

                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    }
                }

            }
        });
    }
}
