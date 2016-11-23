package com.dazone.crewchat.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.dazone.crewchat.R;
import com.dazone.crewchat.dto.ChattingDto;
import com.dazone.crewchat.interfaces.OnClickViewCallback;
import com.dazone.crewchat.utils.ImageUtils;
import com.dazone.crewchat.utils.Prefs;

import java.util.ArrayList;

public class ViewImageAdapter extends PagerAdapter {
    private ArrayList<ChattingDto> imagesURL;
    private LayoutInflater inflater;
    private Context context;
    private OnClickViewCallback mCallback;
    private boolean mShowFull = false;

    public ViewImageAdapter(Context context, ArrayList<ChattingDto> imagesURL, boolean showFull, OnClickViewCallback callback) {
        this.context = context;
        this.imagesURL = imagesURL;
        this.mCallback = callback;
        this.mShowFull = showFull;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public Object instantiateItem(ViewGroup view, int position) {
        View imageLayout = inflater.inflate(R.layout.row_view_image, view, false);

        assert imageLayout != null;
        final ImageView imageView = (ImageView) imageLayout.findViewById(R.id.imageView);

        ChattingDto dto = imagesURL.get(position);

        String url = String.format("/UI/CrewChat/MobileAttachDownload.aspx?session=%s&no=%s",
            new Prefs().getaccesstoken(), dto.getAttachNo());

        ImageUtils.showImageFull(this.context, url, imageView);

        view.addView(imageLayout, 0);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallback != null && !mShowFull){
                    mCallback.onClick();
                }
            }
        });

        return imageLayout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return imagesURL.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }
}
