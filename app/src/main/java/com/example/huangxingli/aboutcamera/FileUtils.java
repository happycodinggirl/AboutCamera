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
    public static String saveToPicture(byte[] data) {
        String path = "/sdcard/" + System.currentTimeMillis() + ".jpg";
        try {
            //�ж��Ƿ�װ��SD��
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                //�ж�SD�����Ƿ����㹻�Ŀռ�
                String storage = Environment.getExternalStorageDirectory().getAbsolutePath();
                StatFs fs = new StatFs(storage);
                //���õ�blocks������
                long availableBolocks = fs.getAvailableBlocks();
                //����block�Ĵ�С
                long blockSize = fs.getBlockSize();
                long available = availableBolocks * blockSize;
                long dataLength = data.length;
                Log.v("TAG", "----available is-ffff---" + available + "-----dataLength is---" + dataLength);
                if (available < dataLength) {
                    Log.v("TAG", "----available<data.length----");
                    //�ռ䲻��ֱ�ӷ��ؿ�
                    return null;
                } else {
                    Log.v("TAG", "---->>>>>>>>>>---right---");
                }

                File file = new File(path);
                if (!file.exists())
                    //�����ļ�
                    file.createNewFile();
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(data);
                fos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.v("TAG", "---CATCH EXCEPTION E.MESSAGE IS---" + e.getMessage());
            return null;
        }
        return path;
    }

}
