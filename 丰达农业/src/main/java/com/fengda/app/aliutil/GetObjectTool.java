package com.fengda.app.aliutil;

import android.util.Log;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.GetObjectRequest;
import com.alibaba.sdk.android.oss.model.GetObjectResult;
import com.alibaba.sdk.android.oss.model.ObjectMetadata;
import com.alibaba.sdk.android.oss.model.Range;
import com.fengda.app.utils.FileUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;


/**
 * Created by zhouzhuo on 12/3/15.
 */
public class GetObjectTool {

    private OSS oss;
    private String testBucket;
    private String testObject;

    private List<String> urls=new ArrayList<>();


    private static String TAG = "DownloadService" ;
    public static final int IO_BUFFER_SIZE = 8 * 1024;
    private static final String CACHE_FILENAME_PREFIX = "cache_";
    private static ExecutorService SINGLE_TASK_EXECUTOR = null;
    private static ExecutorService LIMITED_TASK_EXECUTOR = null;
    private static ExecutorService FULL_TASK_EXECUTOR = null;
    private static final ExecutorService DEFAULT_TASK_EXECUTOR ;
    private static Object lock = new Object();
    static {
        // SINGLE_TASK_EXECUTOR = (ExecutorService)
        // Executors.newSingleThreadExecutor();
       LIMITED_TASK_EXECUTOR = (ExecutorService) Executors. newFixedThreadPool(1);
    //    FULL_TASK_EXECUTOR = (ExecutorService)Executors.newCachedThreadPool();
        DEFAULT_TASK_EXECUTOR = LIMITED_TASK_EXECUTOR ;
    };
    // 下载状态监听，提供回调
    DownloadStateListener listener;
    // 下载目录
    private String downloadPath;

    // 下载链接集合
    private List<String> listURL;
    // 下载个数
    private int size = 0;

    private static GetObjectTool instance;
    public static GetObjectTool getInstance(OSS client, String testBucket, String downloadPath, List<String> listURL, DownloadStateListener listener) {
        if (instance == null) {
            synchronized (GetObjectTool.class) {
                instance = new GetObjectTool( client,  testBucket, downloadPath,  listURL,listener);
            }
        }
        return instance;
    }


    public GetObjectTool(OSS client, String testBucket, String downloadPath, List<String> listURL, DownloadStateListener listener) {
        size = 0;
        this.oss = client;
        this.testBucket = testBucket;
        this.downloadPath = downloadPath;
        this.listURL = listURL;
        this.listener = listener;
    }

    public GetObjectTool(OSS client, String testBucket, String testObject) {
        this.oss = client;
        this.testBucket = testBucket;
        this.testObject = testObject;
    }

    /**
     * 简单下载
     下载指定文件，下载后将获得文件的输入流，此操作要求用户对该Object有读权限。同步调用：
     */
    public void getObjectSample(String url) {

        // 构造下载文件请求
        GetObjectRequest get = new GetObjectRequest(testBucket, url);

        try {
            // 同步执行下载请求，返回结果
            GetObjectResult getResult = oss.getObject(get);

            Log.d("Content-Length", "" + getResult.getContentLength());

            // 获取文件输入流
            InputStream inputStream = getResult.getObjectContent();

            byte[] buffer = new byte[2048];
            int len;

            while ((len = inputStream.read(buffer)) != -1) {
                // 处理下载的数据，比如图片展示或者写入文件等
                Log.d("asyncGetObjectSample", "read length: " + len);
            }
            Log.d("asyncGetObjectSample", "download success.");

            // 下载后可以查看文件元信息
            ObjectMetadata metadata = getResult.getMetadata();
            Log.d("ContentType", metadata.getContentType());
        } catch (ClientException e) {
            // 本地异常如网络异常等
            e.printStackTrace();
        } catch (ServiceException e) {
            // 服务异常
            Log.e("RequestId", e.getRequestId());
            Log.e("ErrorCode", e.getErrorCode());
            Log.e("HostId", e.getHostId());
            Log.e("RawMessage", e.getRawMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 下载图片
     * @param urlString
     * @return
     */
    private File downloadBitmap(String urlString) {

        String[] temp = null;
        temp = urlString.split(",");
        urlString=temp[0];
        temp = urlString.split("/");
        String name=temp[temp.length-1];

        BufferedOutputStream out = null;
        // 构造下载文件请求
        GetObjectRequest get = new GetObjectRequest(testBucket, urlString);
        try {
            // 同步执行下载请求，返回结果
            GetObjectResult getResult = oss.getObject(get);
            // 图片命名方式
            final File cacheFile = new File(createFilePath(new File(downloadPath), name));
            Log.d("Content-Length", "" + getResult.getContentLength());
            // 获取文件输入流
            InputStream inputStream = getResult.getObjectContent();
            out = new BufferedOutputStream(new FileOutputStream(cacheFile), IO_BUFFER_SIZE);
            int b;
            while ((b = inputStream.read()) != -1) {
                out.write(b);
            }
            // 每下载成功一个，统计一下图片个数
            statDownloadNum();
            urls.add(cacheFile.getAbsolutePath());
            // 下载后可以查看文件元信息
            ObjectMetadata metadata = getResult.getMetadata();
            Log.d("ContentType", metadata.getContentType());
            return cacheFile;
        } catch (ClientException e) {
            // 本地异常如网络异常等
            urls.add(urlString);
            e.printStackTrace();
        } catch (ServiceException e) {
            // 服务异常
            Log.e("RequestId", e.getRequestId());
            Log.e("ErrorCode", e.getErrorCode());
            Log.e("HostId", e.getHostId());
            Log.e("RawMessage", e.getRawMessage());
            urls.add(urlString);
        }catch (final IOException e) {
            urls.add(urlString);
//            // 有一个下载失败，则表示批量下载没有成功
//            listener.onFailed();
        } finally {
            if (out != null ) {
                try {
                    out.close();
                } catch (final IOException e) {

                }
            }
        }
        return null ;
    }



    /**
     * 异步调用：
     */
    public File asyncGetObjectSample(final File file) {

        GetObjectRequest get = new GetObjectRequest(testBucket, testObject);

        OSSAsyncTask task = oss.asyncGetObject(get, new OSSCompletedCallback<GetObjectRequest, GetObjectResult>() {
            @Override
            public void onSuccess(GetObjectRequest request, GetObjectResult result) {
                // 请求成功
                InputStream inputStream = result.getObjectContent();
                FileUtils.writeFile(file,inputStream);
//                byte[] buffer = new byte[2048];
//                int len;
//                try {
//                    while ((len = inputStream.read(buffer)) != -1) {
//                        // 处理下载的数据
//                        Log.d("asyncGetObjectSample", "read length: " + len);
//                    }
//                    Log.d("asyncGetObjectSample", "download success.");
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
            }

            @Override
            public void onFailure(GetObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                // 请求异常
                if (clientExcepion != null) {
                    // 本地异常如网络异常等
                    clientExcepion.printStackTrace();
                }
                if (serviceException != null) {
                    // 服务异常
                    Log.e("ErrorCode", serviceException.getErrorCode());
                    Log.e("RequestId", serviceException.getRequestId());
                    Log.e("HostId", serviceException.getHostId());
                    Log.e("RawMessage", serviceException.getRawMessage());
                }
            }
        });
        return  file;
    }

    public void asyncGetObjectRangeSample() {

        GetObjectRequest get = new GetObjectRequest(testBucket, testObject);

        // 设置范围
        get.setRange(new Range(0, 99)); // 下载0到99共100个字节，文件范围从0开始计算

        OSSAsyncTask task = oss.asyncGetObject(get, new OSSCompletedCallback<GetObjectRequest, GetObjectResult>() {
            @Override
            public void onSuccess(GetObjectRequest request, GetObjectResult result) {
                // 请求成功
                InputStream inputStream = result.getObjectContent();

                byte[] buffer = new byte[2048];
                int len;

                try {
                    while ((len = inputStream.read(buffer)) != -1) {
                        // 处理下载的数据
                        Log.d("asyncGetObjectSample", "read length: " + len);
                    }
                    Log.d("asyncGetObjectSample", "download success.");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(GetObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                // 请求异常
                if (clientExcepion != null) {
                    // 本地异常如网络异常等
                    clientExcepion.printStackTrace();
                }
                if (serviceException != null) {
                    // 服务异常
                    Log.e("ErrorCode", serviceException.getErrorCode());
                    Log.e("RequestId", serviceException.getRequestId());
                    Log.e("HostId", serviceException.getHostId());
                    Log.e("RawMessage", serviceException.getRawMessage());
                }
            }
        });
    }

    //*****************************************************************//


    // 下载完成回调接口
    public interface DownloadStateListener {
        public void onFinish(List<String> urls);

        public void onFailed();
    }



    /**
     * 暂未提供设置
     */
    public void setDefaultExecutor() {

    }

    /**
     * 开始下载
     */
    public void startDownload() {
        // 首先检测path是否存在
        File downloadDirectory = new File(downloadPath );
        if (!downloadDirectory.exists()) {
            downloadDirectory.mkdirs();
        }

        for (final String url : listURL) {
            //捕获线程池拒绝执行异常
            try {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        downloadBitmap(url);
                    }
                }).start();


//                    // 线程放入线程池
//                    DEFAULT_TASK_EXECUTOR.execute(new Runnable() {
//
//                        @Override
//                        public void run() {
//                            downloadBitmap(url);
//                        }
//                    });


            } catch (RejectedExecutionException e) {
                e.printStackTrace();
                listener.onFailed();
            } catch (Exception e) {
                e.printStackTrace();
                listener.onFailed();
            }

        }

    }



    /**
     * Creates a constant cache file path given a target cache directory and an
     * image key.
     *
     * @param cacheDir
     * @param key
     * @return
     */
    public static String createFilePath(File cacheDir, String key) {
        try {
            // Use URLEncoder to ensure we have a valid filename, a tad hacky
            // but it will do for
            // this example
            return cacheDir.getAbsolutePath() + File.separator + CACHE_FILENAME_PREFIX + URLEncoder.encode(key.replace("*", ""), "UTF-8" );
        } catch (final UnsupportedEncodingException e) {

        }

        return null ;
    }


    /**
     * 统计下载个数
     */
    private void statDownloadNum() {
        synchronized (lock ) {
            size++;
            if (size == listURL .size()) {
                // 释放资源
               DEFAULT_TASK_EXECUTOR.shutdownNow();
                // 如果下载成功的个数与列表中 url个数一致，说明下载成功
                listener.onFinish(urls); // 下载成功回调

            }
        }
    }




}
