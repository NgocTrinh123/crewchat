package com.dazone.crewchat.Views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;

import com.dazone.crewchat.R;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.Landmark;

/**
 * Created by Paul on 11/4/15.
 */
public class FaceOverlayView extends View {

    public Bitmap getmBitmap() {
        return mBitmap;
    }

    public void setmBitmap(Bitmap mBitmap) {
        this.mBitmap = mBitmap;
    }

    public SparseArray<Face> getmFaces() {
        return mFaces;
    }

    public boolean isFaceDetected() {

        return isFaceDetected;
    }

    private boolean isFaceDetected = false;
    private Bitmap mBitmap;
    private SparseArray<Face> mFaces;

    public FaceOverlayView(Context context) {
        this(context, null);
    }

    public FaceOverlayView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FaceOverlayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setBitmap(Bitmap bitmap) {
        if (bitmap != null) {
            mBitmap = bitmap;
            FaceDetector detector = new FaceDetector.Builder(getContext())
                    .setTrackingEnabled(true)
                    .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                    .setMode(FaceDetector.ACCURATE_MODE)
                    .build();

            if (!detector.isOperational()) {
                //Handle contingency
            } else {
                Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                mFaces = detector.detect(frame);
                detector.release();
            }
//        logFaceData();
            invalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if ((mBitmap != null) && (mFaces != null)) {
            double scale = drawBitmap(canvas);
//            drawFaceLandmarks(canvas, scale);
//            drawFaceBox(canvas, scale);
        }
    }

    private double drawBitmap(Canvas canvas) {

        double viewWidth = canvas.getWidth();
        double viewHeight = canvas.getHeight();
        double imageWidth = mBitmap.getWidth();
        double imageHeight = mBitmap.getHeight();
        int size = (int) getResources().getDimension(R.dimen.common_avatar);
        double scale = Math.min(viewWidth / imageWidth, viewHeight / imageHeight);
//        Rect destBounds = new Rect(0, 0, (int) (imageWidth * scale), (int) (imageHeight * scale));
//        canvas.drawBitmap(mBitmap, null, destBounds, null);

        float left = 0;
        float top = 0;
        float right = 0;
        float bottom = 0;
        Path path = new Path();
        path.addCircle(((float) size - 1) / 2, ((float) size - 1) / 2, (Math.min(((float) size + 2), ((float) size + 2)) / 2), Path.Direction.CCW);
        canvas.clipPath(path);
        if (mFaces.size() > 0) {
            isFaceDetected = true;
            Face face = mFaces.valueAt(0);
            left = (float) (face.getPosition().x);
            top = (float) (face.getPosition().y);
            right = (float) (face.getPosition().x + face.getWidth());
            bottom = (float) (face.getPosition().y + face.getHeight());

            Rect src = new Rect((int) left, (int) top, (int) (right), (int) (bottom));
            Rect dst = new Rect(0, 0, size, size);
            canvas.drawBitmap(mBitmap, src, dst, null);
        } else {
            isFaceDetected = false;
            mBitmap = Bitmap.createScaledBitmap(mBitmap, size, size, true);
            canvas.drawBitmap(mBitmap, 0, 0, null);
        }

        return scale;
    }

    private void drawFaceBox(Canvas canvas, double scale) {
        //This should be defined as a member variable rather than
        //being created on each onDraw request, but left here for
        //emphasis.
        Paint paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(1);
        paint.setAntiAlias(true);
        int size = (int) getResources().getDimension(R.dimen.size);

        float left = 0;
        float top = 0;
        float right = 0;
        float bottom = 0;
        Matrix matrix = new Matrix();
        matrix.postScale(1f, 1f);
        for (int i = 0; i < mFaces.size(); i++) {
            Face face = mFaces.valueAt(i);
            left = (float) (face.getPosition().x);
            top = (float) (face.getPosition().y);
            right = (float) (face.getPosition().x + face.getWidth());
            bottom = (float) (face.getPosition().y + face.getHeight());
            Rect r = new Rect((int) left, (int) top, (int) right, (int) bottom);
            canvas.drawRect(left, top, right, bottom, paint);

        }
        Log.e("face", "left:" + left + " top:" + top + " right:" + right + " bottom:" + bottom);
//        Rect src = new Rect(0, 0, mBitmap.getWidth(), mBitmap.getHeight() );
//        Rect dst = new Rect(0, 0, mBitmap.getWidth() , mBitmap.getHeight());
//        canvas.drawBitmap(mBitmap, src, dst, null);

    }

    private void drawFaceLandmarks(Canvas canvas, double scale) {
        Paint paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        int cx = 0;
        int cy = 0;
        for (int i = 0; i < mFaces.size(); i++) {
            Face face = mFaces.valueAt(i);
            for (Landmark landmark : face.getLandmarks()) {
                cx = (int) (landmark.getPosition().x * scale);
                cy = (int) (landmark.getPosition().y * scale);
                canvas.drawCircle(cx, cy, 10, paint);
            }
        }
    }

    private void logFaceData() {
        float smilingProbability;
        float leftEyeOpenProbability;
        float rightEyeOpenProbability;
        float eulerY;
        float eulerZ;
        for (int i = 0; i < mFaces.size(); i++) {
            Face face = mFaces.valueAt(i);

            smilingProbability = face.getIsSmilingProbability();
            leftEyeOpenProbability = face.getIsLeftEyeOpenProbability();
            rightEyeOpenProbability = face.getIsRightEyeOpenProbability();
            eulerY = face.getEulerY();
            eulerZ = face.getEulerZ();

            Log.e("Tuts+ Face Detection", "Smiling: " + smilingProbability);
            Log.e("Tuts+ Face Detection", "Left eye open: " + leftEyeOpenProbability);
            Log.e("Tuts+ Face Detection", "Right eye open: " + rightEyeOpenProbability);
            Log.e("Tuts+ Face Detection", "Euler Y: " + eulerY);
            Log.e("Tuts+ Face Detection", "Euler Z: " + eulerZ);
        }
    }
}
