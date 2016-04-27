package com.example.huangxingli.aboutcamera;

import android.content.Context;
import android.hardware.Camera;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

import javax.security.auth.callback.Callback;

/**
 * Created by huangxingli on 2015/5/12.
 */
public class CameraView extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private OnSurfaceInfo mOnSurfaceInfo;

    public static interface OnSurfaceInfo {
        void onSurfaceCreated();

    }


    public CameraView(Context context, Camera camera) {
        super(context);
        mCamera = camera;
        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void setOnSurfaceInfo(OnSurfaceInfo onSurfaceInfo) {
        this.mOnSurfaceInfo = onSurfaceInfo;
    }


    public void surfaceCreated(SurfaceHolder holder) {
        Log.v("TAG", "=----SURFACE CREATED----");
        // The Surface has been created, now tell the camera where to draw the preview.
        try {
            Camera.Parameters parameters = mCamera.getParameters();
            if (mOnSurfaceInfo != null) {
                mOnSurfaceInfo.onSurfaceCreated();
            }
            mCamera.setPreviewDisplay(holder);
            mCamera.setDisplayOrientation(90);

            mCamera.startPreview();
        } catch (IOException e) {
            Log.d("TAG", "Error setting camera preview: " + e.getMessage());
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.v("TAG", "---surfaceDestroyed----");
        this.getHolder().removeCallback(this);
        mCamera.stopPreview();
        Log.v("TAG", "----surfaceDestroyed---mCamera is----" + mCamera);
        mCamera.setPreviewCallback(null);
        mCamera.release();
        mCamera = null;
        Log.v("TAG", "-----====NULL mCamera is----" + mCamera);


        // empty. Take care of releasing the Camera preview in your activity.
    }

    public void setPreviewCallback(Camera.PreviewCallback previewCallback) {

        mCamera.setPreviewCallback(previewCallback);
    }


    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.
        Log.v("TAG", "----surfaceChanged-----");

        if (mHolder.getSurface() == null) {
            // preview surface does not exist
            return;
        }

    }

    public void startPreview() {
        if (mCamera == null) {
            Log.v("TAG", "---CAMERA==NULL RETURN-");
            return;
        } else {
            mCamera.startPreview();
        }

    }

    public void stopPreview() {
        if (mCamera == null) {
            return;
        } else {
            mCamera.stopPreview();
        }
    }

}