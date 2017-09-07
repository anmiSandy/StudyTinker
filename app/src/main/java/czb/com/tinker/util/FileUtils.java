package czb.com.tinker.util;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Android on 2016/4/20.
 */
public class FileUtils {
    private String SDPATH;

    public String getSDPATH() {
        return SDPATH;
    }

    //构造方法
    public FileUtils() {
        //得到当前外部存储设备的目录      /SDCARD/...
        SDPATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/";

    }

    /**
     * 在SD卡上创建文件
     *
     * @param fileName
     * @return
     * @throws IOException
     */
    public File createSDFile(String fileName) throws IOException {
        File file = new File(SDPATH + fileName);
        file.createNewFile();
        return file;
    }

    /**
     * 在SD卡上创建目录
     *
     * @param dirName
     * @return
     */
    public File createSDDir(String dirName) {
        File dir = new File(SDPATH + dirName);
        dir.mkdirs();
        return dir;
    }

    /**
     * 判断SD卡上的文件夹是否存在
     *
     * @param fileName
     * @return
     */
    public boolean isFileExist(String fileName) {
        File file = new File(SDPATH + fileName);
        return file.exists();
    }

    /**
     * 将一个InputStream里面的数据写入到SD卡中
     *
     * @param path
     * @param fileName
     * @param input
     * @return
     */
    public File writeToSDFromInput(String path, String fileName, InputStream input) {
        File file = null;
        FileOutputStream output = null;
        try {
            final File fPath = new File(path);
            if (!fPath.exists()) {
                fPath.mkdirs();
                Log.e("tag", "路径不存在，进行新建 一个:" + fPath.getAbsolutePath());
            }
            //createSDDir(path);
            file = new File(fPath, fileName);
            file.createNewFile();
            Log.e("tag", "文件名：" + file.getAbsolutePath());
            output = new FileOutputStream(file);
            byte buffer[] = new byte[1024];
            int length;
            while ((length = (input.read(buffer))) > 0) {
                output.write(buffer, 0, length);
            }
            //清缓存，将流中的数据保存到文件中
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (input != null)
                    input.close();
                if (output != null)
                    output.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return file;
    }
}
