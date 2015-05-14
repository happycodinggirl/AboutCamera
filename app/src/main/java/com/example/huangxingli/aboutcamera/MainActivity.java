package com.example.huangxingli.aboutcamera;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ToggleButton;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by huangxingli on 2015/5/12.
 */
public class MainActivity extends BaseActivity implements View.OnClickListener , CameraView.OnSurfaceInfo{

    private CameraView mCameraView;
    private Camera mCamera;
    private Button startButton;
    private Button stopButton;
    private Button takePicButton;
    private ToggleButton switchButton;
    private ImageView showImageView;

    private static int FONT_CAMERA=0;
    private static int BACK_CAMERA=1;
    private  FrameLayout preview;
    private int currrentCamera=0;

    private boolean willExit=false;
    private boolean firstEntered =false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getCameraInfo();
        mCamera = getCameraInstance(BACK_CAMERA);
        initCamera(mCamera);
        firstEntered =true;
    }

    @Override
    public int getContentRes() {
        return R.layout.main;
    }

    @Override
    public void initView() {
        preview = (FrameLayout) findViewById(R.id.camera_preview);
        startButton= (Button) findViewById(R.id.start);
        stopButton= (Button) findViewById(R.id.stop);
        takePicButton= (Button) findViewById(R.id.takePic);
        switchButton= (ToggleButton) findViewById(R.id.switchbutton);
        showImageView= (ImageView) findViewById(R.id.imageview);
        startButton.setOnClickListener(this);
        stopButton.setOnClickListener(this);
        switchButton.setOnClickListener(this);
        takePicButton.setOnClickListener(this);
        disableBtn(startButton);
        enableBtn(stopButton);
    }


    public void initCamera(Camera camera){
        int childCount1=preview.getChildCount();
        Log.v("TAG", "---init Camera ---childCount is---" + childCount1);
        mCameraView = new CameraView(this, camera);
        mCameraView.setOnSurfaceInfo(this);
        mCameraView.setPreviewCallback(new MyPreviewCallback());
        preview.addView(mCameraView);
        int childCount=preview.getChildCount();
        Log.v("TAG", "---init Camera ---childCount is---" + childCount);
    }


    public static Camera getCameraInstance(int cameraType){
        Camera c = null;
        try {
            c = Camera.open(cameraType); // attempt to get a Camera instance
        }
        catch (Exception e){
            Log.v("TAG","----openCamera exception --"+e.getMessage());
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }


    @Override
    public void onClick(View v) {
        int id=v.getId();
        switch (id){
            case R.id.start:
                disableBtn(startButton);
                enableBtn(stopButton);
                mCameraView.startPreview();
            break;
            case R.id.stop:
                disableBtn(stopButton);
                enableBtn(startButton);
                mCameraView.stopPreview();
            break;
            case R.id.switchbutton:
                switchCamera();

                int childCount=preview.getChildCount();
                Log.v("TAG","----CHILDCOUNT IS----"+childCount);
                if (childCount>0){
                    preview.removeView(mCameraView);
                }
                reopenCamera();
                int childCount1=preview.getChildCount();
                Log.v("TAG","---child count is---"+childCount1);
                Log.v("TAG", "----SWITCHBUTTON IS CLICKED---");
                break;
            case R.id.takePic:
                mCamera.takePicture(null,null,jpegCallback);
                break;
        }
    }
    private Camera.PictureCallback jpegCallback = new Camera.PictureCallback(){

        public void onPictureTaken(byte[] data, Camera camera) {
            Camera.Parameters ps = camera.getParameters();
            if(ps.getPictureFormat() == PixelFormat.JPEG){
                //存储拍照获得的图片
                String path = FileUtils.saveToPicture(data);
                mCamera.startPreview();
                Log.v("TAG","----path is---"+path);

            }
        }
    };


    public void reopenCamera(){
        if (willExit){
            Log.v("TAG","----WILLEXIT DESTROY SURFACE---");
        }else {
            Log.v("TAG","-----WILLEXIT IS FALSE0-----REOPEN====");
            mCamera= getCameraInstance(currrentCamera);
            initCamera(mCamera);
        }
    }

    private void switchCamera() {
        if (currrentCamera==FONT_CAMERA){
            currrentCamera=BACK_CAMERA;
        }else {
            currrentCamera=FONT_CAMERA;
        }
    }

    public void getCameraInfo(){
        int cameraCount=Camera.getNumberOfCameras();
        Camera.CameraInfo cameraInfo=new Camera.CameraInfo();
        for (int i=0;i<cameraCount;i++){
            Camera.getCameraInfo(i,cameraInfo);
            if (cameraInfo.facing==Camera.CameraInfo.CAMERA_FACING_FRONT){
                FONT_CAMERA=i;
            }else if (cameraInfo.facing==Camera.CameraInfo.CAMERA_FACING_BACK){
                BACK_CAMERA=i;
            }
        }
    }

    public static void setCameraDisplayOrientation(Activity activity,
                                                   int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

    public void disableBtn(Button button){
        if (button.isEnabled()){
            button.setEnabled(false);
        }
    }

    public void enableBtn(Button button){
        if (!button.isEnabled()){
            button.setEnabled(true);
        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v("TAG","=----onDestroy----");
    }

    @Override
    protected void onPause() {
        super.onPause();
        int childCount=preview.getChildCount();
        //removeView会将surfaceView销毁，会去调用onSurfaceDestroyed方法。该方法释放了camera，所以在onResume时重新打开
        //相机。
        if (childCount>0){
            Log.v("TAG","---onPause==childCount >0");
            preview.removeView(mCameraView);
        }
        firstEntered =false;
        Log.v("TAG", "-----ONPAUSE ---currentCamera is---" + currrentCamera);
        Log.v("TAG","---onPause----");


    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.v("TAG", "---onResume =-currentCamera is---" + currrentCamera);
        if (firstEntered){

        }else{
            Log.v("TAG","---not -----firstEntered not background or interrupted---reopenCamera----");
            reopenCamera();
        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        willExit=true;
        Log.v("TAG","----onBackPressed====");
    }

    @Override
    public void onSurfaceCreated() {
        Log.v("TAG", "----onSurfaceCreated----");
        setCameraDisplayOrientation(this, currrentCamera, mCamera);
        //设置使拍照成像显示正常
        Camera.Parameters parameters=mCamera.getParameters();
        if (currrentCamera==FONT_CAMERA) {
            parameters.setRotation(270);
        }else if (currrentCamera==BACK_CAMERA){
            parameters.setRotation(90);
        }
        mCamera.setParameters(parameters);
    }

    public   class MyPreviewCallback implements Camera.PreviewCallback{

        @Override
        public void onPreviewFrame(byte[] data1, Camera camera) {
            Camera.Parameters parameters1 = camera.getParameters();
            int width1 = parameters1.getPreviewSize().width;
            int height1 = parameters1.getPreviewSize().height;
            byte[] data=rotateYUV420Degree90(data1,width1,height1);

            Camera.Parameters parameters = camera.getParameters();
            int width = parameters.getPreviewSize().width;
            int height = parameters.getPreviewSize().height;

            YuvImage yuv1 = new YuvImage(data, parameters.getPreviewFormat(), height, width, null);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            yuv1.compressToJpeg(new Rect(0, 0, height, width), 50, out);

            byte[] bytes = out.toByteArray();
            final Bitmap bitmap1 = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

            MainActivity.this.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    showImageView.setImageBitmap(bitmap1);
                }
            });
        }
    }

    private byte[] rotateYUV420Degree90(byte[] data, int imageWidth, int imageHeight)
    {
        byte [] yuv = new byte[imageWidth*imageHeight*3/2];
        //y占1，u，v各占1/4;
        // Rotate the Y luma
        int i = 0;
        for(int x = 0;x < imageWidth;x++)
        {
            for(int y = imageHeight-1;y >= 0;y--)
            {
                yuv[i] = data[y*imageWidth+x];
                i++;
            }
        }
        // Rotate the U and V color components
        i = imageWidth*imageHeight*3/2-1;
        for(int x = imageWidth-1;x > 0;x=x-2)
        {
            for(int y = 0;y < imageHeight/2;y++)
            {
                yuv[i] = data[(imageWidth*imageHeight)+(y*imageWidth)+x];
                i--;
                yuv[i] = data[(imageWidth*imageHeight)+(y*imageWidth)+(x-1)];
                i--;
            }
        }
        return yuv;
    }
}