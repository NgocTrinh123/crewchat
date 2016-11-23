package com.dazone.crewchat.customs;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Matrix.ScaleToFit;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Drawable;

import com.dazone.crewchat.utils.Constant;

import com.nostra13.universalimageloader.core.assist.LoadedFrom;
import com.nostra13.universalimageloader.core.display.BitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

public class RoundedOneCorner implements BitmapDisplayer {
    protected final int cornerRadius;
    protected final int margin;
    protected final int type;

    public RoundedOneCorner(int cornerRadiusPixels, int type) {
        this(cornerRadiusPixels, 0, type);
    }

    public RoundedOneCorner(int cornerRadiusPixels, int marginPixels, int type) {
        this.cornerRadius = cornerRadiusPixels;
        this.margin = marginPixels;
        this.type = type;
    }

    public void display(Bitmap bitmap, ImageAware imageAware, LoadedFrom loadedFrom) {
        if (!(imageAware instanceof ImageViewAware)) {
            throw new IllegalArgumentException("ImageAware should wrap ImageView. ImageViewAware is expected.");
        } else {
            imageAware.setImageDrawable(new RoundedOneCorner.RoundedDrawable(bitmap, this.cornerRadius, this.margin, this.type));
        }
    }

    public static class RoundedDrawable extends Drawable {
        protected final float cornerRadius;
        protected final int margin;
        protected final RectF mRect = new RectF();
        protected RectF mBitmapRect;
        protected BitmapShader bitmapShader;
        protected Paint paint;
        protected final int type;
        protected final Bitmap source;

        public RoundedDrawable(Bitmap source, int cornerRadius, int margin, int type) {
            this.source = source;
            this.type = type;
            this.cornerRadius = (float) cornerRadius;
            this.margin = margin;

        }

        protected void onBoundsChange(Rect bounds) {
            super.onBoundsChange(bounds);
            this.mRect.set((float) this.margin, (float) this.margin, (float) (bounds.width() - this.margin), (float) (bounds.height() - this.margin));

            int newHeight = 0;
            int newWidth = 0;
            int tyle = bounds.height() / bounds.width();
            if (tyle == 1) {
                if (source.getWidth() > source.getHeight()) {
                    newWidth = source.getHeight();
                    newHeight = source.getHeight();
                } else {
                    newHeight = source.getWidth();
                    newWidth = source.getWidth();
                }
            } else if (tyle == 2) {
                if (source.getWidth() > source.getHeight()) {
                    newHeight = source.getHeight();
                    newWidth = newHeight / 2;
                } else {
                    newWidth = source.getWidth();
                    newHeight = newWidth * 2;
                }
            } else if (tyle == 0) {
                if (source.getWidth() > source.getHeight()) {
                    newWidth = source.getHeight();
                    newHeight = newWidth / 2;
                } else {
                    newHeight = source.getWidth();
                    newWidth = newHeight * 2;
                }
            }

            Bitmap bitmap;
            if (newHeight == 0 || newWidth == 0) {
                bitmap = scaleCenterCrop(source, source.getHeight(), source.getWidth());
            } else {
                bitmap = scaleCenterCrop(source, newHeight, newWidth);
            }

            this.bitmapShader = new BitmapShader(bitmap, TileMode.CLAMP, TileMode.CLAMP);
            this.mBitmapRect = new RectF((float) margin, (float) margin, (float) (bitmap.getWidth() - margin), (float) (bitmap.getHeight() - margin));
            this.paint = new Paint();
            this.paint.setAntiAlias(true);
            this.paint.setShader(this.bitmapShader);
            this.paint.setFilterBitmap(true);
            this.paint.setDither(true);

            Matrix shaderMatrix = new Matrix();
            shaderMatrix.setRectToRect(this.mBitmapRect, this.mRect, ScaleToFit.FILL);
            this.bitmapShader.setLocalMatrix(shaderMatrix);
        }

        public Bitmap scaleCenterCrop(Bitmap source, int newHeight, int newWidth) {
            int sourceWidth = source.getWidth();
            int sourceHeight = source.getHeight();

            // Compute the scaling factors to fit the new height and width, respectively.
            // To cover the final image, the final scaling will be the bigger
            // of these two.
            float xScale = (float) newWidth / sourceWidth;
            float yScale = (float) newHeight / sourceHeight;
            float scale = Math.max(xScale, yScale);

            // Now get the size of the source bitmap when scaled
            float scaledWidth = scale * sourceWidth;
            float scaledHeight = scale * sourceHeight;

            // Let's find out the upper left coordinates if the scaled bitmap
            // should be centered in the new size give by the parameters
            float left = (newWidth - scaledWidth) / 2;
            float top = (newHeight - scaledHeight) / 2;

            // The target rectangle for the new, scaled version of the source bitmap will now
            // be
            RectF targetRect = new RectF(left, top, left + scaledWidth, top + scaledHeight);

            // Finally, we create a new bitmap of the specified size and draw our new,
            // scaled bitmap onto it.
            Bitmap dest = Bitmap.createBitmap(newWidth, newHeight, source.getConfig());
            Canvas canvas = new Canvas(dest);
            canvas.drawBitmap(source, null, targetRect, null);

            return dest;
        }

        public void draw(Canvas canvas) {
            float w = canvas.getWidth();
            switch (type) {
                case Constant.TYPE_ROUNDED_TOP_LEFT:
                    canvas.drawRoundRect(new RectF(0f, 0f, w * 2, w * 2), this.cornerRadius, this.cornerRadius, this.paint);
                    break;
                case Constant.TYPE_ROUNDED_TOP_RIGHT:
                    canvas.drawRoundRect(new RectF(-w, 0f, w, w * 2), this.cornerRadius, this.cornerRadius, this.paint);
                    break;
                case Constant.TYPE_ROUNDED_BOTTOM_LEFT:
                    canvas.drawRoundRect(new RectF(0f, -w, w * 2, w), this.cornerRadius, this.cornerRadius, this.paint);
                    break;
                case Constant.TYPE_ROUNDED_BOTTOM_RIGHT:
                    canvas.drawRoundRect(new RectF(-w, -w, w, w), this.cornerRadius, this.cornerRadius, this.paint);
                    break;
                case Constant.TYPE_ROUNDED_TOP:
                    canvas.drawRoundRect(new RectF(0f, 0f, w, w), this.cornerRadius, this.cornerRadius, this.paint);
                    break;
            }
        }

        public int getOpacity() {
            return -3;
        }

        public void setAlpha(int alpha) {
            this.paint.setAlpha(alpha);
        }

        public void setColorFilter(ColorFilter cf) {
            this.paint.setColorFilter(cf);
        }
    }
}
