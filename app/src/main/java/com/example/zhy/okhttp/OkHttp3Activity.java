package com.example.zhy.okhttp;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.zhy.guidepage.MainActivity;
import com.example.zhy.guidepage.R;
import com.example.zhy.util.LogUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkHttp3Activity extends AppCompatActivity {

    private static final String GET_URL = "http://www.baidu.com";
    private static final String DOWNLOAD_WEIXIN_URL = "http://dldir1.qq.com/weixin/android/weixin6514android1120.apk";
    private static final String UPLOAD_INITRC_URL = "\"https://api.github.com/markdown/raw";
    private static final MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("text/x-markdown; charset=utf-8");
    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
    private OkHttpClient mOkHttpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_okhttp3);
        mOkHttpClient = new OkHttpClient();
    }


    /**
     * OkHttp设置
     * 和OkHttp2.x有区别的是不能通过OkHttpClient直接设置超时时间和缓存了，
     * 而是通过OkHttpClient.Builder来设置，通过builder配置好OkHttpClient
     * 后用builder.build()来返回OkHttpClient，所以我们通常不会调用
     * new OkHttpClient()来得到OkHttpClient，而是通过builder.build()
     * @param view
     */
    public void okSettings(View view){
        File sdCache = getExternalCacheDir();
        int cacheSize = 10 * 1024 * 1024;
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .cache(new Cache(sdCache.getAbsoluteFile(), cacheSize));
        mOkHttpClient = builder.build();
    }

    /**
     * OkHttp get同步请求
     * @param view
     */
    public void okGetDirect(View view){
        Request request = new Request.Builder()
                .url(GET_URL).build();
        try {
            Response response = mOkHttpClient.newCall(request).execute();
            if(null != response && response.isSuccessful()){
                LogUtils.log(OkHttp3Activity.class, "同步请求成功");
            }else {
                LogUtils.log(OkHttp3Activity.class, "同步请求失败");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * OkHttp 异步Get请求
     * @param view
     */
    public void okGet(View view){
        //get请求
        Request.Builder builder = new Request.Builder()
                .url(GET_URL);
        builder.method("GET",null); //可以不设置，默认get
        Call call = mOkHttpClient.newCall(builder.build());
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtils.log(OkHttp3Activity.class, "请求失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(null != response.cacheResponse()){
                    LogUtils.log(OkHttp3Activity.class, "cache: "+response.cacheResponse().body().string());
                }else {
                    LogUtils.log(OkHttp3Activity.class, "请求成功："+response.code()+
                            "\r\nbody: "+response.body().string());
                }
            }
        });
    }

    /**
     * 异步大文件下载
     * @param view
     */
    public void okDownload(View view){
        Request.Builder  builder = new Request.Builder().url(DOWNLOAD_WEIXIN_URL);
        Call call = mOkHttpClient.newCall(builder.build());
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream inputStream = response.body().byteStream();
                try {
                    FileOutputStream outputStream = OkHttp3Activity.this.openFileOutput("weixin.apk", Context.MODE_PRIVATE);
                    byte[] buffer = new byte[1024];
                    int length = 0;
                    while ((length = inputStream.read(buffer)) > 0){
                        outputStream.write(buffer, 0, length);
                    }
                    outputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                LogUtils.log(OkHttp3Activity.class, "下载apk成功");

            }
        });

    }

    /**
     * OKHttp Post请求
     * @param view
     */
    public void okPost(View view){
        FormBody body = new FormBody.Builder()
                .add("name", "ssd")
                .build();
        Request.Builder builder = new Request.Builder();
        builder.url(GET_URL).post(body);
        Call call = mOkHttpClient.newCall(builder.build());
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                LogUtils.log(OkHttp3Activity.class, "body: "+response.body().string());
            }
        });
    }

    /**
     * 异步文件上传
     * 上传文件本身就是一个Post请求
     * @param view
     */
    public void okUpload(View view){
        File file = new File("/sdcard/abc.txt");
        RequestBody body = RequestBody.create(MEDIA_TYPE_MARKDOWN, file);
        Request request = new Request.Builder()
                .url(UPLOAD_INITRC_URL)
                .post(body)
                .build();
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtils.log(MainActivity.class, "上传失败");

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                LogUtils.log(MainActivity.class, "body: "+response.body().string());
            }
        });
    }

    /**
     * 异步上传multipart文件
     * 这种场景很常用，我们有时会上传文件同时还需要传其他类型的字段,利用OkHttp3实现起来很简单
     * @param view
     */
    public void okUploadMutipart(View view){
        File file = new File("/sdcard/xxxx.png");
        RequestBody fileBody = RequestBody.create(MEDIA_TYPE_MARKDOWN, file);
        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("title", "xxxx")
                .addFormDataPart("image", "xxxx.png", fileBody)
                .build();
        Request request = new Request.Builder()
                .header("Authorization", "Client-Id"+"...")
                .url(GET_URL)
                .post(body)
                .build();
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtils.log(OkHttp3Activity.class, "Multipart文件上传错误");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                LogUtils.log(OkHttp3Activity.class, "body: "+response.body().string());
            }
        });
    }

}
