package com.dazone.crewchat.Class;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import com.dazone.crewchat.adapter.SelectionPlusAdapter;
import com.dazone.crewchat.customs.GridDecoration;
import com.dazone.crewchat.dto.SelectionPlusDto;
import com.dazone.crewchat.R;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by david on 1/5/16.
 */
public class GridSelectionChatting extends BaseViewClass {

    RecyclerView selection_rcl;
    List<SelectionPlusDto> dataSet;

    public GridSelectionChatting(Context context) {
        super(context);
        setupView();
    }


    @Override
    protected void setupView() {
        currentView = inflater.inflate(R.layout.grid_selection_chatting,null);
        selection_rcl = (RecyclerView)currentView.findViewById(R.id.selection_rcl);
        initView();
    }


    private void initView()
    {
        dataSet = new ArrayList<>();
        dataSet.add(new SelectionPlusDto(1));
        dataSet.add(new SelectionPlusDto(2));
        dataSet.add(new SelectionPlusDto(3));
        dataSet.add(new SelectionPlusDto(4));
        dataSet.add(new SelectionPlusDto(5));
        dataSet.add(new SelectionPlusDto(6));
        selection_rcl.setHasFixedSize(true);
        selection_rcl.setLayoutManager(new GridLayoutManager(context, 3));
        selection_rcl.addItemDecoration( new GridDecoration(0));
        SelectionPlusAdapter adapter = new SelectionPlusAdapter(dataSet);
        selection_rcl.setAdapter(adapter);

    }
}
