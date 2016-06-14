package www.beijia.com.cn.dozip;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    WebView mWebView;
    private static final String APP_CACAHE_DIRNAME = "/webcache";
    private static final String SHAREDPREFERENCES_NAME = "first_pref";

    //---第一次加载
    private boolean isFirstIn;

    private String PATH = "";
    public ZipExtractorTask.ZipOverListener mZipOverListener = new ZipExtractorTask.ZipOverListener() {
        @Override
        public void zipOver() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    loadView();
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences preferences = getSharedPreferences(
                SHAREDPREFERENCES_NAME, MODE_PRIVATE);

        // 取得相应的值，如果没有该值，说明还未写入，用true作为默认值
        isFirstIn = preferences.getBoolean("isFirstIn", true);

        PATH = Environment.getExternalStorageDirectory() + "/" + getApplication().getPackageName() + "/";
        Log.d(TAG, "Environment.getExternalStorageDirectory()=" + Environment.getExternalStorageDirectory());
        Log.d(TAG, "getCacheDir().getAbsolutePath()=" + getCacheDir().getAbsolutePath());
        findView();


        if (isFirstIn) {
            Toast.makeText(this, "拷贝资源文件，并解压到SD", Toast.LENGTH_LONG).show();
            copyBigDataToSD(PATH + "/1-16040H21141.zip");

            SharedPreferences.Editor editor = preferences.edit();
            // 存入数据
            editor.putBoolean("isFirstIn", false);
            // 提交修改
            editor.apply();
        }

        showDownLoadDialog();


        //doZipExtractorWork();
        //doDownLoadWork();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
            mWebView.goBack();// 返回前一个页面
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void loadView() {
        String url = "file:/" + PATH + "/215/index.html"; // url
        Log.i(TAG, url);
        File file = new File(PATH + "/215/index.html");
        if (!file.exists()) {
            new AlertDialog.Builder(MainActivity.this).setTitle("确认")
                    .setMessage("文件不存在访问网络？")
                    .setPositiveButton("是", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            Log.d(TAG, "onClick 1 = " + which);
                            mWebView.loadUrl("http://demo.genban.org/demo/215");
                        }
                    })
                    .setNegativeButton("否", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            Log.d(TAG, "onClick 2 = " + which);
                        }
                    })
                    .show();
        } else {
            mWebView.loadUrl("file:///" + PATH + "/215/index.html");
        }
    }

    private void findView() {

        mWebView = (WebView) findViewById(R.id.webView);

        initWebView();

        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onLoadResource(WebView view, String url) {

                Log.i(TAG, "onLoadResource url=" + url);

                super.onLoadResource(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView webview, String url) {

                Log.i(TAG, "intercept url=" + url);

                webview.loadUrl(url);

                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {

                Log.e(TAG, "onPageStarted");
            }

            @Override
            public void onPageFinished(WebView view, String url) {

                String title = view.getTitle();

                Log.e(TAG, "onPageFinished WebView title=" + title);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {

            }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public boolean onJsAlert(WebView view, String url, String message,
                                     JsResult result) {

                Log.e(TAG, "onJsAlert " + message);

                Toast.makeText(getApplicationContext(), message,
                        Toast.LENGTH_SHORT).show();

                result.confirm();

                return true;
            }

            @Override
            public boolean onJsConfirm(WebView view, String url,
                                       String message, JsResult result) {

                Log.e(TAG, "onJsConfirm " + message);

                return super.onJsConfirm(view, url, message, result);
            }

            @Override
            public boolean onJsPrompt(WebView view, String url, String message,
                                      String defaultValue, JsPromptResult result) {

                Log.e(TAG, "onJsPrompt " + url);

                return super.onJsPrompt(view, url, message, defaultValue,
                        result);
            }
        });
    }

    @SuppressWarnings({"deprecation", "deprecation"})
    private void initWebView() {

        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT); // 设置
        // 缓存模式
        // 开启 DOM storage API 功能
        mWebView.getSettings().setDomStorageEnabled(true);
        // 开启 database storage API 功能
        mWebView.getSettings().setDatabaseEnabled(true);
        String cacheDirPath = getFilesDir().getAbsolutePath()
                + APP_CACAHE_DIRNAME;
        // String cacheDirPath =
        // getCacheDir().getAbsolutePath()+Constant.APP_DB_DIRNAME;
        Log.i(TAG, "cacheDirPath=" + cacheDirPath);
        // 设置数据库缓存路径
        mWebView.getSettings().setDatabasePath(cacheDirPath);
        // 设置 Application Caches 缓存目录
        mWebView.getSettings().setAppCachePath(cacheDirPath);
        // 开启 Application Caches 功能
        mWebView.getSettings().setAppCacheEnabled(true);
    }

    /**
     * 清除WebView缓存
     */
    public void clearWebViewCache() {

        // 清理Webview缓存数据库
        try {
            deleteDatabase("webview.db");
            deleteDatabase("webviewCache.db");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // WebView 缓存文件
        File appCacheDir = new File(getFilesDir().getAbsolutePath()
                + APP_CACAHE_DIRNAME);
        Log.e(TAG, "appCacheDir path=" + appCacheDir.getAbsolutePath());

        File webviewCacheDir = new File(getCacheDir().getAbsolutePath()
                + "/webviewCache");
        Log.e(TAG, "webviewCacheDir path=" + webviewCacheDir.getAbsolutePath());

        // 删除webview 缓存目录
        if (webviewCacheDir.exists()) {
            deleteFile(webviewCacheDir);
        }
        // 删除webview 缓存 缓存目录
        if (appCacheDir.exists()) {
            deleteFile(appCacheDir);
        }
    }

    /**
     * 递归删除 文件/文件夹
     *
     * @param file
     */
    public void deleteFile(File file) {

        Log.i(TAG, "delete file path=" + file.getAbsolutePath());

        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
                File files[] = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    deleteFile(files[i]);
                }
            }
            file.delete();
        } else {
            Log.e(TAG, "delete file no exists " + file.getAbsolutePath());
        }
    }

    private void showDownLoadDialog() {
        new AlertDialog.Builder(this).setTitle("确认")
                .setMessage("资源文件存在更新，是否下载？")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        Log.d(TAG, "onClick 1 = " + which);
                        doDownLoadWork();
                    }
                })
                .setNegativeButton("否", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        Log.d(TAG, "onClick 2 = " + which);
                        loadView();
                    }
                })
                .show();
    }

    /**
     * 拷贝到SD
     * @param strOutFileName
     */
    private void copyBigDataToSD(String strOutFileName) {
        InputStream myInput = null;
        OutputStream myOutput = null;
        try {
            myOutput = new FileOutputStream(strOutFileName);
            myInput = this.getAssets().open("1-16040H21141.zip");
            byte[] buffer = new byte[1024];
            int length = myInput.read(buffer);
            while (length > 0) {
                myOutput.write(buffer, 0, length);
                length = myInput.read(buffer);
            }
            myOutput.flush();
            myInput.close();
            myOutput.close();
        } catch (IOException e) {
        } finally {
            ZipExtractorTask task = new ZipExtractorTask(PATH + "/" + "1-16040H21141.zip", PATH, MainActivity.this, true,
                    null);
            task.execute();
        }
    }

    /**
     * 下载
     */
    private void doDownLoadWork() {
        DownLoaderTask task = new DownLoaderTask("http://www.html5code.net/uploads/soft/160407/1-16040H21141.zip",
                PATH, this, mZipOverListener);
        //DownLoaderTask task = new DownLoaderTask("http://192.168.9.155/johnny/test.h264", getCacheDir().getAbsolutePath()+"/", this);
        task.execute();
    }

}
