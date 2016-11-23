package com.dazone.crewchat.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.ViewGroup;
import com.dazone.crewchat.dto.ChattingDto;
import com.dazone.crewchat.dto.TreeUserDTOTemp;
import com.dazone.crewchat.utils.Utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class PullUpLoadMoreRCVAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    protected final int VIEW_ITEM = 1;
    protected final int VIEW_PROG = 0;
    protected List<T> mDataset;
    protected final List<T> itemsCopy;
    protected static String root_link;

    // The minimum amount of items to have below your current scroll position before loading more.
    protected int visibleThreshold = 2;
    protected int lastVisibleItem, totalItemCount;
    protected boolean loading = true;
    protected OnLoadMoreListener onLoadMoreListener;
    protected Context mContext;
    protected boolean isFiltering = false;

    protected final Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if(msg.what == 1) {
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Utils.printLogs("On notifyDataSetChanged ####");
                        isFiltering = true;
                        notifyDataSetChanged();
                    }
                });
            }
        }
    };


    public PullUpLoadMoreRCVAdapter(Context context, List<T> myDataSet, RecyclerView recyclerView) {

        itemsCopy = new ArrayList<>();
        mDataset = myDataSet;
        itemsCopy.addAll(mDataset);
        Utils.printLogs("Item copy size on init ="+itemsCopy.size()+" main size ="+mDataset.size());
        this.mContext = context;

        if(recyclerView.getLayoutManager()instanceof LinearLayoutManager) {

            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager)recyclerView.getLayoutManager();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    totalItemCount = linearLayoutManager.getItemCount();
                    lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                    if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                        // End has been reached
                        // Do something
                        if(onLoadMoreListener!=null) {
                            onLoadMoreListener.onLoadMore();
                        }
                        loading = true;
                    }
                }
            });
        }
    }

    public void updateData(List<T> mDataset)
    {
        this.mDataset = mDataset;
        Utils.printLogs("Item copy size on update all ="+itemsCopy.size()+" main size ="+mDataset.size());
        notifyDataSetChanged();
    }

    public void updateData(List<T> mDataset, int position)
    {
        this.mDataset = mDataset;
        Utils.printLogs("Item copy size on update position ="+itemsCopy.size()+" main size ="+mDataset.size());
        notifyItemChanged(position);
    }

    public void updateData2(List<T> mDataset)
    {
        this.mDataset = mDataset;
    }

    @Override
    public int getItemViewType(int position) {
        return mDataset.get(position)!=null? VIEW_ITEM: VIEW_PROG;
    }

    @Override
    public abstract RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType);
    @Override
    public abstract void onBindViewHolder(RecyclerView.ViewHolder holder, int position);

    public void setLoaded(){
        loading = false;
    }
    public void setLoading(){
        loading = true;
    }
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public interface OnLoadMoreListener{
        void onLoadMore();
    }

    public void updateRecyclerView(List<T> list){
        mDataset = list;
        notifyDataSetChanged();
    }

    public void filter(final String text) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                for (T c : mDataset){
                    if (!itemsCopy.contains(c)){
                        itemsCopy.add(c);
                    }
                }

                mDataset.clear();

                if(text.isEmpty()){
                    mDataset.addAll(itemsCopy);
                } else{

                    synchronized(itemsCopy) {
                        Iterator i = itemsCopy.iterator();
                        while (i.hasNext()){
                            Object item = i.next();
                            if (item instanceof ChattingDto){
                                ChattingDto dto = (ChattingDto) item;
                                Utils.printLogs("DTO="+dto.toString());
                                String message = dto.getMessage();
                                if (message != null && message.trim().length() > 0){
                                    if(message.toLowerCase().contains(text.toLowerCase()) || message.toLowerCase().contains(text.toLowerCase())){
                                        Utils.printLogs("Match text ######## "+text);
                                        mDataset.add((T) dto);
                                    }
                                }

                            }
                        }
                    }
                }

                // Send handler to update UI
                mHandler.obtainMessage(1).sendToTarget();
            }
        }).start();
    }


    public void filterRecentFavorite(final String text) {
        new Thread(new Runnable() {
            @Override
            public void run() {


                for (T c : mDataset){
                    if (!itemsCopy.contains(c)){
                        itemsCopy.add(c);
                    }
                }

                mDataset.clear();

                Utils.printLogs("Item copy size on filter = "+itemsCopy.size()+" main size ="+mDataset.size());

                if(text.isEmpty()){
                    mDataset.addAll(itemsCopy);
                } else{

                    mDataset.clear();

                        for (Object item : itemsCopy) {
                            if (item instanceof ChattingDto) {
                                ChattingDto dto = (ChattingDto) item;
                                Utils.printLogs("DTO=" + dto.toString());
                                String name = "";

                                /** SET TITLE FOR ROOM */
                                if (TextUtils.isEmpty(dto.getRoomTitle())) {
                                    if (dto.getListTreeUser() != null && dto.getListTreeUser().size() > 0) {
                                        for (TreeUserDTOTemp treeUserDTOTemp : dto.getListTreeUser()) {
                                            name += treeUserDTOTemp.getName() + ",";
                                        }
                                        if (name.length() != 0) {
                                            name = name.substring(0, name.length() - 1);
                                        }
                                    }
                                } else {
                                    name = dto.getRoomTitle();
                                }

                                if (name != null && name.trim().length() > 0) {
                                    if (name.toLowerCase().contains(text.toLowerCase()) || name.toLowerCase().contains(text.toLowerCase())) {
                                        Utils.printLogs("Match text ######## " + text);
                                        mDataset.add((T) dto);
                                    }
                                }

                            }
                        }

                }

                // Send handler to update UI
                mHandler.obtainMessage(1).sendToTarget();
            }
        }).start();
    }

    public List<T> getData(){
        return mDataset;
    }
}
