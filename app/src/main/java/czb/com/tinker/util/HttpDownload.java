package czb.com.tinker.util;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Android on 2016/4/20.
 */
public class HttpDownload {
    private static final String TAG = HttpDownload.class.getSimpleName();
    private URL url = null;
    private InputStream inputStream = null;

    /**
     * 根据URL下载文件，前提是这个文件当中的内容是文本，函数的返回值就是文件当中的内容
     * 1.创建一个URL对象
     * 2.通过URL对象，创建一个HttpURLConnection对象
     * 3.得到InputStream
     * 4.从InputStream当中读取数据
     *
     * @param urlStr 要下载的文件地址
     * @return
     */
    public String download(String urlStr) {
        StringBuffer sb = new StringBuffer();
        String line = null;
        //BufferedReader有一个readLine（）方法，可以每次读取一行数据
        BufferedReader buffer = null;
        try {
            //创建一个URL对象
            url = new URL(urlStr);
            //创建一个Http连接
            HttpURLConnection urlConn = (HttpURLConnection) url
                    .openConnection();
            //使用IO流读取数据，InputStreamReader将读进来的字节流转化成字符流
            //但是字符流还不是很方便，所以再在外面套一层BufferedReader，
            //用BufferedReader的readLine（）方法，一行一行读取数据
            buffer = new BufferedReader(new InputStreamReader(urlConn
                    .getInputStream()));
            while ((line = buffer.readLine()) != null) {
                sb.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                buffer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    /**
     * 该函数返回整型     -1：代表下载文件出错     0：代表下载文件成功    1：代表文件已经存在
     *
     * @param urlStr   下载文件的网络地址
     * @param path     想要把下载过来的文件存放到哪一个SDCARD目录下
     * @param fileName 下载的文件的文件名，可以跟原来的名字不同，所以这里加一个fileName
     * @return
     */
    public int downFile(String urlStr, String path, String fileName) {

        try {
            FileUtils fileUtils = new FileUtils();
            final File f = new File(path, fileName);
            if (f.isFile() && f.exists()) {
                Log.e("tag", "文件已经存在" + f.getAbsolutePath());
                return 1;
            } else {
                inputStream = getInputStreamFromURL(urlStr);
                File resultFile = fileUtils.writeToSDFromInput(path, fileName, inputStream);
                if (resultFile == null) {
                    return -1;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        } finally {
            try {
                inputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    /**
     * 根据URL得到输入流
     *
     * @param urlStr
     * @return
     * @throws IOException
     */
    public InputStream getInputStreamFromURL(String urlStr) {
        HttpURLConnection urlConn = null;

        try {
            url = new URL(urlStr);
            urlConn = (HttpURLConnection) url.openConnection();
            inputStream = urlConn.getInputStream();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return inputStream;
    }

    private byte[] downloadGET(String urlStr) throws IOException {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(urlStr)
                .build();
        //发送请求获取响应
        Response response = okHttpClient.newCall(request).execute();
        byte[] bytes=null;
        if(response.isSuccessful()){
            bytes=response.body().bytes();
        }
        return bytes;
    }

    public int downToFile(String urlStr, String path) throws IOException {
        int flag = -1;
        File file = new File(path, getFileName(urlStr));
        if (file.exists()) {
            Log.e(TAG, "resultFile=" + file.getAbsolutePath());
            Log.e(TAG, "文件已存在");
            deleteFile(file);
            Log.e(TAG, "删除已经存在的patch");
            //重新开始下载
            return downToFile(urlStr, path);
        } else {
            final byte[] bytes = downloadGET(urlStr);
            if (bytes != null) {
                final File file1 = writeToSDFromInput(path, getFileName(urlStr), bytes);
                Log.e(TAG, "保存成功目录是：" + file1.getAbsolutePath());
                flag = 1;
            } else {
                flag = -1;
            }

        }
        return flag;
    }

    private void deleteFile(File file) {
        if (file.isFile()) {
            deleteFileSafely(file);
            return;
        }
        if (file.isDirectory()) {
            File[] childFiles = file.listFiles();
            if (childFiles == null || childFiles.length == 0) {
                deleteFileSafely(file);
                return;
            }
            for (int i = 0; i < childFiles.length; i++) {
                deleteFile(childFiles[i]);
            }
            deleteFileSafely(file);
        }
    }

    public static boolean deleteFileSafely(File file) {
        if (file != null) {
            String tmpPath = file.getParent() + File.separator + System.currentTimeMillis();
            File tmp = new File(tmpPath);
            file.renameTo(tmp);
            return tmp.delete();
        }
        return false;
    }

    public static String getFileName(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }

    public File writeToSDFromInput(String path, String fileName, byte[] data) throws IOException {
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();   // 可以创建多层目录
        }
        File file = new File(dir, fileName);
        FileOutputStream outputStream = new FileOutputStream(file);
        if (data.length > 0) {
            outputStream.write(data);
            outputStream.flush();
            outputStream.close();
        } else {
            Log.e(TAG, "数据不对");
        }
        return new File(path + File.separator + fileName);
    }
}
