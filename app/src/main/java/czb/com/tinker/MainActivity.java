package czb.com.tinker;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.widget.Toast;

import com.tencent.tinker.lib.tinker.TinkerInstaller;

import java.io.File;
import java.io.IOException;

import czb.com.tinker.util.HttpDownload;
import czb.com.tinker.util.LoadBugClass;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private String fileState = null;

    private String str="你觉得你最棒？";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.v(TAG, "onCreate");

        Log.e(TAG, "i am on patch log");

        Toast.makeText(MainActivity.this, str+"-"+new LoadBugClass().getStr(), Toast.LENGTH_SHORT).show();
        findViewById(R.id.btn_load_patch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //准备补丁,从assert里拷贝到dex里
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String url = "http://otherinfo.cn-gd.ufileos.com/patch_signed_7zip.apk";
                        final HttpDownload httpDownload = new HttpDownload();
                        try {
                            String path = Environment.getExternalStorageDirectory() + File.separator + "TinkerHotFix";
                            int flag = httpDownload.downToFile(url, path);
                            if (flag == 1) {
                                fileState = "下载完成";
                                final String pathUrl = path + "/" + HttpDownload.getFileName(url);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        try{
                                            TinkerInstaller.onReceiveUpgradePatch(getApplicationContext(), pathUrl);

                                        }catch (Exception e){
                                            Log.e(TAG,e.getMessage());
                                        }
                                        Log.e(TAG, "安装完成");
                                        Toast.makeText(getApplicationContext(), "安装完成", Toast.LENGTH_LONG).show();

                                    }
                                });
                            } else if (flag == -1) {
                                fileState = "下载错误";
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this, fileState, Toast.LENGTH_SHORT).show();

                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                    }
                }).start();

            }
        });

    }

    public void onRestart(View view) {
        //重启应用
        Intent i = getPackageManager()
                .getLaunchIntentForPackage(getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
