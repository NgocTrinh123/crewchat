package com.dazone.crewchat.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.amulyakhare.textdrawable.TextDrawable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.dazone.crewchat.R;
import com.dazone.crewchat.constant.Statics;
import com.dazone.crewchat.dto.DrawImageItem;
import com.dazone.crewchat.dto.MenuDrawItem;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.File;
import java.util.ArrayList;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created by david on 12/25/15.
 */
public class ImageUtils {

    @TargetApi(Build.VERSION_CODES.M)
    public static final int getColor(Context context, int id) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= 23) {
            return context.getColor(id);
        } else {
            return context.getResources().getColor(id);
        }
    }

    public static void showRoundImage(DrawImageItem dto, ImageView view) {
        String url = "";
        if (dto == null)
            return;
        try {

  /*      if (TextUtils.isEmpty(url)) {
            ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
            // generate color based on a key (same key returns the same color), useful for list/grid views
            int iconColor = generator.getColor(ChatTextUtils.getFirstLetter(dto.getImageTitle()));
            TextDrawable drawable = TextDrawable.builder()
                    .buildRound(ChatTextUtils.getFirstLetter(dto.getImageTitle()), iconColor);
            drawable.setPadding(1, 1, 1, 1);
            view.setImageDrawable(drawable);
        } else {}*/
            ShowRoundImage(dto.getImageLink(), view);
        } catch (Exception e) {
            //e.printStackTrace();
            ImageLoader.getInstance().displayImage(Constant.UriDefaultAvatar, view, Statics.options2);
        }
    }

    public static void ShowRoundImage(String url, ImageView view) {
        String rootUrl = new Prefs().getServerSite() + url;
        ImageLoader.getInstance().displayImage(rootUrl, view, Statics.options2);
    }

    public static void showBadgeImage(int count, ImageView view) {
        TextDrawable drawable = TextDrawable.builder()
                .buildRound(String.valueOf(count), ImageUtils.getColor(view.getContext(), R.color.badge_bg_color));
        drawable.setPadding(1, 1, 1, 1);
        view.setImageDrawable(drawable);
    }

    public static void showImage(MenuDrawItem dto, ImageView view) {
        if (TextUtils.isEmpty(dto.getMenuIconUrl())) {
            view.setImageResource(dto.getIconResID());
        } else {
            showImage(dto.getMenuIconUrl(), view);
        }
    }

    public static void showRoundImage(String url, ImageView view) {
        if (url.contains("content") || url.contains("storage")) {
            File f = new File(url);
            if (f.exists()) {
                ImageLoader.getInstance().displayImage(url, view, Statics.options2);
            } else {
                ImageLoader.getInstance().displayImage(url, view, Statics.options2);
            }
        } else {
            ImageLoader.getInstance().displayImage(new Prefs().getServerSite() + url, view, Statics.options2);
        }
    }

    public static void showImageFull(final Context context, String url, final ImageView imageView) {
        //ImageLoader.getInstance().displayImage(new Prefs().getServerSite() + url, view, Statics.optionsViewAttach);
        String fullUrl = new Prefs().getServerSite() + url;
        Utils.printLogs("Full url = "+fullUrl);

        Glide.with(context)
                .load(fullUrl)
                .asBitmap()
                .placeholder(R.drawable.loading)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(R.drawable.loading)
                .fallback(R.drawable.loading)
                .into(imageView);

    }

       public static void loadImageNormal(String url, final ImageView view){
        Glide.with(CrewChatApplication.getInstance())
                .load(url)
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        view.setImageBitmap(resource);
                    }
                });
    }


    public static void loadImageNormalNoCache(String url, final ImageView view){
        Glide.with(CrewChatApplication.getInstance())
                .load(url)
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        view.setImageBitmap(resource);
                    }
                });
    }


    public static void loadImageNormal(String url, final LinearLayout lnChattingRowImage, final ImageView view){
        Glide.with(CrewChatApplication.getInstance())
                .load(url)
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        view.setImageBitmap(resource);
                        lnChattingRowImage.setVisibility(View.VISIBLE);
                    }
                });
    }

    public static void loadImageScale(String url, final ImageView view){
        Glide.with(CrewChatApplication.getInstance())
                .load(url)
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {


                        int srcWidth = resource.getWidth();
                        int srcHeight = resource.getHeight();
                        int dstWidth = (int)(srcWidth*2f);
                        int dstHeight = (int)(srcHeight*2f);

                        Bitmap putImage = Bitmap.createScaledBitmap(resource, dstWidth, dstHeight, true);

                        view.setImageBitmap(putImage);
                    }
                });
    }

    public static void showImage(final String url, final ImageView view) {
        if (TextUtils.isEmpty(url)) {
            ImageLoader.getInstance().displayImage("http://www.blogto.com/upload/2009/02/20090201-dazone.jpg", view, Statics.options);
        } else {
            if (url.contains("file")) {
                //view.setImageURI(Uri.parse(url));
                 //ImageLoader.getInstance().displayImage(url, view, Statics.options);
                //ImageLoader.getInstance().displayImage(url, view, Statics.optionsNoCache);
                // ImageUtils.loadImageNormal(url, view);
                ImageUtils.loadImageNormalNoCache(url, view);

            } else if (url.contains("content") || url.contains("storage")) {
                File f = new File(url);
                if (f.exists()) {
                    //ImageLoader.getInstance().displayImage(url, view, Statics.options2);
                    ImageUtils.loadImageNormal(url, view);
                } else {
                    //ImageLoader.getInstance().displayImage(url, view, Statics.options2);
                    ImageUtils.loadImageNormal(url, view);
                }
            } else {
                ImageLoader.getInstance().displayImage(new Prefs().getServerSite() + url, view, Statics.options, new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String s, View view) {

                    }

                    @Override
                    public void onLoadingFailed(String s, View view2, FailReason failReason) {
                        //ImageLoader.getInstance().displayImage("http://www.blogto.com/upload/2009/02/20090201-dazone.jpg", view, Statics.options);

                        ImageUtils.loadImageNormal("http://www.blogto.com/upload/2009/02/20090201-dazone.jpg", view);

                    /*String name = s.substring(s.lastIndexOf("/") + 1, s.lastIndexOf("."));
                    String type = s.substring(s.lastIndexOf("."));
                    String query = null;
                    try {
                        query = URLEncoder.encode(name, "utf-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    String urlNew = s.substring(0, s.lastIndexOf("/")+1)+query+type;
                    if(!TextUtils.isEmpty(urlNew))
                    {
                        LoadImage loadImage = new LoadImage(view);
                        loadImage.execute(new String[]{urlNew});
                    }*/
                        //ImageLoader.getInstance().displayImage(urlNew,view, Statics.options2);
                    }

                    @Override
                    public void onLoadingComplete(String s, View view2, Bitmap bitmap) {
                        view.setImageBitmap(bitmap);
                    }

                    @Override
                    public void onLoadingCancelled(String s, View view) {

                    }
                });
            }
        }
    }


    public static void showImage(final String url, LinearLayout lnChattingRowImage, final ImageView view) {
        if (TextUtils.isEmpty(url)) {
            ImageLoader.getInstance().displayImage("http://www.blogto.com/upload/2009/02/20090201-dazone.jpg", view, Statics.options);
        } else {
            if (url.contains("file")) {
                //view.setImageURI(Uri.parse(url));
                // ImageLoader.getInstance().displayImage(url, view, Statics.options);

                ImageUtils.loadImageNormal(url,lnChattingRowImage, view);



            } else if (url.contains("content") || url.contains("storage")) {
                File f = new File(url);
                if (f.exists()) {
                    //ImageLoader.getInstance().displayImage(url, view, Statics.options2);
                    ImageUtils.loadImageNormal(url,lnChattingRowImage, view);
                } else {
                    //ImageLoader.getInstance().displayImage(url, view, Statics.options2);
                    ImageUtils.loadImageNormal(url,lnChattingRowImage, view);
                }
            } else {
                ImageLoader.getInstance().displayImage(new Prefs().getServerSite() + url, view, Statics.options, new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String s, View view) {

                    }

                    @Override
                    public void onLoadingFailed(String s, View view2, FailReason failReason) {
                        //ImageLoader.getInstance().displayImage("http://www.blogto.com/upload/2009/02/20090201-dazone.jpg", view, Statics.options);

                        ImageUtils.loadImageNormal("http://www.blogto.com/upload/2009/02/20090201-dazone.jpg", view);

                    /*String name = s.substring(s.lastIndexOf("/") + 1, s.lastIndexOf("."));
                    String type = s.substring(s.lastIndexOf("."));
                    String query = null;
                    try {
                        query = URLEncoder.encode(name, "utf-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    String urlNew = s.substring(0, s.lastIndexOf("/")+1)+query+type;
                    if(!TextUtils.isEmpty(urlNew))
                    {
                        LoadImage loadImage = new LoadImage(view);
                        loadImage.execute(new String[]{urlNew});
                    }*/
                        //ImageLoader.getInstance().displayImage(urlNew,view, Statics.options2);
                    }

                    @Override
                    public void onLoadingComplete(String s, View view2, Bitmap bitmap) {
                        view.setImageBitmap(bitmap);
                    }

                    @Override
                    public void onLoadingCancelled(String s, View view) {

                    }
                });
            }
        }
    }

    public static Bitmap cycleBitmap(Bitmap source) {
        int size = Math.min(source.getWidth(), source.getHeight());

        int x = (source.getWidth() - size) / 2;
        int y = (source.getHeight() - size) / 2;

        Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
        if (squaredBitmap != source) {
            source.recycle();
        }

        Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        BitmapShader shader = new BitmapShader(squaredBitmap,
                BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
        paint.setShader(shader);
        paint.setAntiAlias(true);

        float r = size / 2f;
        canvas.drawCircle(r, r, r, paint);

        squaredBitmap.recycle();
        return bitmap;
    }

    public static void drawGroupAvatar(Context context, ImageView imv) {
        Bitmap square1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_favorite_white_24dp);
        Bitmap square2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_favorite_white_24dp);
        Bitmap square3 = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_favorite_white_24dp);
        Bitmap square4 = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_favorite_white_24dp);

        Bitmap big = Bitmap.createBitmap(square1.getWidth() * 2, square1.getHeight() * 2, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(big);
        canvas.drawBitmap(square1, 0, 0, null);
        canvas.drawBitmap(square2, square1.getWidth(), 0, null);
        canvas.drawBitmap(square3, 0, square1.getHeight(), null);
        canvas.drawBitmap(square4, square1.getWidth(), square1.getHeight(), null);

        imv.setImageBitmap(cycleBitmap(big));
    }

    public static void drawCycleImage(ImageView profilePic, int imId, int size) {
        Bitmap imageBitmap = BitmapFactory.decodeResource(CrewChatApplication.getInstance().getResources(), imId);
        imageBitmap = Bitmap.createScaledBitmap(imageBitmap, size, size, false);
        RoundedBitmapDrawable roundedBitmapDrawable =
                RoundedBitmapDrawableFactory.create(CrewChatApplication.getInstance().getResources(), imageBitmap);
        roundedBitmapDrawable.setCornerRadius(size / 2);
        roundedBitmapDrawable.setAntiAlias(true);
        profilePic.setImageDrawable(roundedBitmapDrawable);
    }

    public static void drawCycleImage(ImageView profilePic, Bitmap imageBitmap, int size) {
        if (imageBitmap != null) {
            imageBitmap = Bitmap.createScaledBitmap(imageBitmap, size, size, false);
            RoundedBitmapDrawable roundedBitmapDrawable =
                    RoundedBitmapDrawableFactory.create(CrewChatApplication.getInstance().getResources(), imageBitmap);
            roundedBitmapDrawable.setCornerRadius(size / 2);
            roundedBitmapDrawable.setAntiAlias(true);
            profilePic.setImageDrawable(roundedBitmapDrawable);
        }
    }

    /**
     * IMAGE FILE TYPE
     */
    public static void imageFileType(ImageView imageView, String fileType) {
        String uri = "";
        if (fileType.equalsIgnoreCase(".apk")) {
            uri = "drawable://" + R.drawable.android;
        } else if (fileType.equalsIgnoreCase(".doc") || fileType.equalsIgnoreCase(".docx")) {
            uri = "drawable://" + R.drawable.word;
        } else if (fileType.equalsIgnoreCase(".xls") || fileType.equalsIgnoreCase(".xlsx")) {
            uri = "drawable://" + R.drawable.excel;
        } else if (fileType.equalsIgnoreCase(".ppt") || fileType.equalsIgnoreCase(".pptx")) {
            uri = "drawable://" + R.drawable.power_point;
        } else if (fileType.equalsIgnoreCase(".hwp")) {
            uri = "drawable://" + R.drawable.hwp;
        } else if (fileType.equalsIgnoreCase(".pdf")) {
            uri = "drawable://" + R.drawable.pdf;
        } else if (fileType.equalsIgnoreCase(".exe")) {
            uri = "drawable://" + R.drawable.exe;
        } else if (fileType.equalsIgnoreCase(".txt") || fileType.equalsIgnoreCase(".log") || fileType.equalsIgnoreCase(".sql")) {
            uri = "drawable://" + R.drawable.file;
        } else if (fileType.equalsIgnoreCase(".zip") || fileType.equalsIgnoreCase(".zap") || fileType.equalsIgnoreCase(".alz")) {
            uri = "drawable://" + R.drawable.compressed;
        } else if (fileType.equalsIgnoreCase(".html") || fileType.equalsIgnoreCase(".htm")) {
            uri = "drawable://" + R.drawable.html;
        } else if (fileType.equalsIgnoreCase(".avi") || fileType.equalsIgnoreCase(".mp3") || fileType.equalsIgnoreCase(".mov") || fileType.equalsIgnoreCase(".wav") || fileType.equalsIgnoreCase(".mp4")) {
            uri = "drawable://" + R.drawable.movie;
        }
        if (TextUtils.isEmpty(uri)) {
            uri = "drawable://" + R.drawable.file;
        }
        ImageLoader.getInstance().displayImage(uri, imageView);

    }

    public static void showCycleImageSquareFromLink(String link, final ImageView imageview, int dimen_id) {
        final int size = (Utils.getDimenInPx(dimen_id));
        Glide.with(CrewChatApplication.getInstance())
                .load(link)
                .override(size, size)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .bitmapTransform(new RoundedCornersTransformation(CrewChatApplication.getInstance(), 12, 0))
                .into(imageview);
    }

    public static void showCycleImageFromLink(String link, final ImageView imageview, int dimen_id) {
        /*DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnFail(R.drawable.avatar_l).cacheInMemory(true)
                .cacheOnDisc(true).bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new RoundedBitmapDisplayer(CrewChatApplication.getInstance().getResources().getDimensionPixelSize(dimen_id))).cacheOnDisc(true)
                .build();*/
        final int size = (Utils.getDimenInPx(dimen_id));
        //ImageLoader.getInstance().displayImage(link, imageview, options);

        Glide.with(CrewChatApplication.getInstance())
                .load(link)
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transform(new CropCircleTransformation(CrewChatApplication.getInstance()))
                .into(new SimpleTarget<Bitmap>(size, size) {
                    @Override
                    public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
                        // Do something with bitmap here.
                        imageview.setImageBitmap(bitmap);
                    }
                });
    }

    public static void showCycleImageFromLinkScale(String link, final ImageView imageview, int dimen_id) {
        final int size = (Utils.getDimenInPx(dimen_id));
        Glide.with(CrewChatApplication.getInstance())
                .load(link)
                .asBitmap()
                .override(size, size)
                .placeholder(R.drawable.avatar_l)
                .fallback(R.drawable.avatar_l)
                .error(R.drawable.avatar_l)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .transform(new CircleTransform(CrewChatApplication.getInstance()))
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        imageview.setImageBitmap(resource);
                    }
                });
    }

    public static void showCycleImageFromLinkScale(Context context, String link, final ImageView imageview, int dimen_id) {
        final int size = (Utils.getDimenInPx(dimen_id));
        Glide.with(context)
                .load(link)
                .asBitmap()
                .override(size, size)
                .placeholder(R.drawable.avatar_l)
                .fallback(R.drawable.avatar_l)
                .error(R.drawable.avatar_l)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .transform(new CircleTransform(CrewChatApplication.getInstance()))
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        imageview.setImageBitmap(resource);
                    }
                });
    }

    public static void drawGroupAvatar(ImageView imv, ArrayList<Bitmap> list) {
        Bitmap big = null;
        if (list.size() == 1) {
            big = Bitmap.createBitmap(list.get(0).getWidth(), list.get(0).getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(big);
            canvas.drawBitmap(list.get(0), 0, 0, null);
        }

        if (list.size() == 2) {
            big = Bitmap.createBitmap(list.get(0).getWidth() * 2, list.get(0).getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(big);
            canvas.drawBitmap(list.get(0), 0, 0, null);
            canvas.drawBitmap(list.get(1), list.get(0).getWidth(), 0, null);
        }

        if (list.size() == 3) {
            /*big = Bitmap.createBitmap(list.get(1).getWidth()*2, list.get(1).getHeight()*2, Bitmap.Config.ARGB_8888);
            Bitmap big2 = Bitmap.createBitmap(list.get(1).getWidth()*2, list.get(1).getHeight()*2, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(big);
            canvas.drawBitmap(list.get(0), 0, 0, null);
            canvas.drawBitmap(list.get(1), 0, list.get(1).getHeight(), null);
            canvas.drawBitmap(list.get(2), list.get(1).getWidth(), list.get(1).getHeight(), null);*/
            big = Bitmap.createBitmap(list.get(0).getWidth() * 2, list.get(0).getHeight() * 2, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(big);
            canvas.drawBitmap(list.get(0), 0, 0, null);
            canvas.drawBitmap(list.get(0), list.get(0).getWidth(), 0, null);
            canvas.drawBitmap(list.get(1), 0, list.get(0).getHeight(), null);
            canvas.drawBitmap(list.get(2), list.get(0).getWidth(), list.get(0).getHeight(), null);
        }

        if (list.size() > 3) {
            big = Bitmap.createBitmap(list.get(0).getWidth() * 2, list.get(0).getHeight() * 2, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(big);
            canvas.drawBitmap(list.get(0), 0, 0, null);
            canvas.drawBitmap(list.get(1), list.get(0).getWidth(), 0, null);
            canvas.drawBitmap(list.get(2), 0, list.get(0).getHeight(), null);
            canvas.drawBitmap(list.get(3), list.get(0).getWidth(), list.get(0).getHeight(), null);
        }

        imv.setImageBitmap(cycleBitmap(big));
    }

    public static Bitmap fastblur(Bitmap sentBitmap, int radius) {

        Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);

        if (radius < 1) {
            return (null);
        }

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int[] pix = new int[w * h];
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);

        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;

        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[] = new int[Math.max(w, h)];

        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int dv[] = new int[256 * divsum];
        for (i = 0; i < 256 * divsum; i++) {
            dv[i] = (i / divsum);
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;

            for (x = 0; x < w; x++) {

                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];

                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;

                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = ( 0xff000000 & pix[yi] ) | ( dv[rsum] << 16 ) | ( dv[gsum] << 8 ) | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }
        bitmap.setPixels(pix, 0, w, 0, 0, w, h);

        return (bitmap);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and
            // keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }
}
