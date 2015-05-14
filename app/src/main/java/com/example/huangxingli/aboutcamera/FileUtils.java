package com.example.huangxingli.aboutcamera;

import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by huangxingli on 2015/5/14.
 */
public class FileUtils {
    public static String  saveToPicture(byte[] data){
        String path = "/sdcard/"+System.currentTimeMillis()+".jpg";
        try {
            //判断是否装有SD卡
            if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                //判断SD卡上是否有足够的空间
                String storage = Environment.getExternalStorageDirectory().getAbsolutePath();
                StatFs fs = new StatFs(storage);
                //可用的blocks的数量
                long availableBolocks=fs.getAvailableBlocks();
                //单个block的大小
                long blockSize=fs.getBlockSize();
                long available=availableBolocks*blockSize;
                long dataLength=data.length;
                Log.v("TAG", "----available is----" + available + "-----dataLength is---" + dataLength);
                if(available<dataLength){
                    Log.v("TAG","----available<data.length----");
                    //空间不足直接返回空
                    return null;
                }else{
                    Log.v("TAG","---->>>>>>>>>>---right---");
                }

                File file = new File(path);
                if(!file.exists())
                    //创建文件
                    file.createNewFile();
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(data);
                fos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.v("TAG","---CATCH EXCEPTION E.MESSAGE IS---"+e.getMessage());
            return null;
        }
        return path;
    }

}
