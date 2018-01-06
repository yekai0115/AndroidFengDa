package com.fengda.app.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.widget.ImageView;
import com.fengda.app.application.MyApplication;
import com.fengda.app.constant.MyConstant;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * Created by StormShadow on 2017/3/28.
 * Knowledge is power.
 */

public class ImgSetUtil {
    public static Date getYesterdayDate() {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return cal.getTime();
    }

    /**
     * 将Bitmap转换成指定大小
     */
    public static Bitmap adjustBitmapSize(Bitmap bitmap, int width, int height) {
        return Bitmap.createScaledBitmap(bitmap, width, height, true);
    }

    /**
     * Handle the bitmap and img_white_return the final path
     */
    public static String handleBitmapByAlbumIntent(Context context, Intent data) {
        Uri imageUri = data.getData();
        String imagePath = ImgSetUtil.getRealFilePath(context, imageUri);
        Bitmap bitmap = getAdjustCompressBitmapByPath(imagePath);
        String filePath="";
        if(null!=bitmap){
            String filename;
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat ("yyyyMMddHHmmss");
            filename = sdf.format(date);
            try{
                OutputStream fOut;
                FileUtils.makeDirs(MyConstant.IMG_PATH);
                File file = new File(MyConstant.IMG_PATH, filename + ".jpg");
                filePath = file.getAbsolutePath();
                fOut = new FileOutputStream(file);

                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                fOut.flush();
                fOut.close();

                MediaStore.Images.Media.insertImage(context.getContentResolver()
                        ,file.getAbsolutePath(), file.getName(), file.getName());

            }catch (Exception e) {
                e.printStackTrace();
            }
        }

        return filePath;
    }

    //测试
    public static String handleBitmapByAlbumIntent2(Context context, Intent data) {
        Uri imageUri =ImgSetUtil.geturi(context,data);
        String imagePath = ImgSetUtil.getAbsoluteImagePath(context, imageUri);
//        Bitmap bitmap = getAdjustCompressBitmapByPath(imagePath);
//
//        String filename;
//        Date date = new Date();
//        SimpleDateFormat sdf = new SimpleDateFormat ("yyyyMMddHHmmss");
//        filename = sdf.format(date);
//
//        String filePath="";
//        try{
//            OutputStream fOut;
//            FileUtils.makeDirs(MyConstants.IMG_PATH);
//            File file = new File(MyConstants.IMG_PATH, filename + ".jpg");
//            filePath = file.getAbsolutePath();
//            fOut = new FileOutputStream(file);
//
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
//            fOut.flush();
//            fOut.close();
//
//            MediaStore.Images.Media.insertImage(context.getContentResolver()
//                    ,file.getAbsolutePath(), file.getName(), file.getName());
//
//        }catch (Exception e) {
//            e.printStackTrace();
//        }
        return imagePath;
    }



    public static String handleBitmapByCameraIntent(Context context, Intent data) {
        Bitmap bitmap2 = (Bitmap) data.getExtras().get("data");
        Bitmap bitmap = getAdjustCompressBitmapByBitmap(bitmap2);

        String filename;
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat ("yyyyMMddHHmmss");
        filename = sdf.format(date);

        String filePath="";
        try{
            OutputStream fOut;
            FileUtils.makeDirs(MyConstant.IMG_PATH);
            File file = new File(MyConstant.IMG_PATH, filename + ".jpg");
            filePath = file.getAbsolutePath();
            fOut = new FileOutputStream(file);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.flush();
            fOut.close();

            MediaStore.Images.Media.insertImage(context.getContentResolver()
                    ,file.getAbsolutePath(), file.getName(), file.getName());

        }catch (Exception e) {
            e.printStackTrace();
        }
        return filePath;
    }

    public static Bitmap getAdjustCompressBitmapByBitmap(Bitmap bitmap) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inSampleSize = 4;
        options.inJustDecodeBounds = false;
        Bitmap bitmap1 = adjustBitmapSize(bitmap, MyConstant.DEF_IMG_W, MyConstant.DEF_IMG_H);
        return bitmap1;
    }

    public static Bitmap getAdjustCompressBitmapByPath(String path) {
        Bitmap bitmap1;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            options.inSampleSize = 4;
            options.inJustDecodeBounds = false;

            Bitmap bitmap = BitmapFactory.decodeFile(path, options);
             bitmap1 = adjustBitmapSize(bitmap, MyConstant.DEF_IMG_W, MyConstant.DEF_IMG_H);
        }catch (Exception e){
            return null;
        }
        return bitmap1;
    }

    public static String getAbsoluteImagePath(Context context, Uri uri) {
        // can post image
//        String [] proj ={MediaStore.Images.Media.DATA};
//        Cursor cursor = context.getContentResolver().query(
//                uri,
//                proj,       // Which columns to img_white_return
//                null,       // WHERE clause; which rows to img_white_return (all rows)
//                null,       // WHERE clause selection arguments (none)
//                null);      // Order-by clause (ascending by name)
//        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//        cursor.moveToFirst();
//        img_white_return cursor.getString(column_index);

        String res = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(uri, proj, null, null, null);
        if(cursor.moveToFirst()){;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }

    public static String getRealFilePath( final Context context, final Uri uri ) {
        if ( null == uri ) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if ( scheme == null )
            data = uri.getPath();
        else if ( ContentResolver.SCHEME_FILE.equals( scheme ) ) {
            data = uri.getPath();
        } else if ( ContentResolver.SCHEME_CONTENT.equals( scheme ) ) {
            Cursor cursor = context.getContentResolver().query( uri, new String[] { MediaStore.Images.ImageColumns.DATA }, null, null, null );
            if ( null != cursor ) {
                if ( cursor.moveToFirst() ) {
                    int index = cursor.getColumnIndex( MediaStore.Images.ImageColumns.DATA );
                    if ( index > -1 ) {
                        data = cursor.getString( index );
                    }
                }
                cursor.close();
            }
        }
        return data;
    }


    public static Uri geturi(Context context,Intent intent) {
        Uri uri = intent.getData();
        String type = intent.getType();
        if (uri.getScheme().equals("file") && (type.contains("image/"))) {
            String path = uri.getEncodedPath();
            if (path != null) {
                path = Uri.decode(path);
                ContentResolver cr = context.getContentResolver();
                StringBuffer buff = new StringBuffer();
                buff.append("(").append(MediaStore.Images.ImageColumns.DATA).append("=").append("'" + path + "'").append(")");
                Cursor cur = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        new String[] { MediaStore.Images.ImageColumns._ID },
                        buff.toString(), null, null);
                int index = 0;
                for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
                    index = cur.getColumnIndex(MediaStore.Images.ImageColumns._ID);
                    // set _id value
                    index = cur.getInt(index);
                }
                if (index == 0) {
                    // do nothing
                } else {
                    Uri uri_temp = Uri.parse("content://media/external/images/media/" + index);
                    if (uri_temp != null) {
                        uri = uri_temp;
                    }
                }
            }
        }
        return uri;
    }






    public static String md5(final String toEncrypt) {
        try {
            final MessageDigest digest = MessageDigest.getInstance("md5");
            digest.update(toEncrypt.getBytes());
            final byte[] bytes = digest.digest();
            final StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(String.format("%02X", bytes[i]));
            }
            return sb.toString().toLowerCase();
        } catch (Exception exc) {
            return "";
        }
    }

    public static String getImgKeyString(String userId) {
        java.text.DateFormat dateFormat = new SimpleDateFormat("yyyy/MMdd");
        String dateStr = dateFormat.format(new Date());

        long curTime = System.currentTimeMillis();
        Timestamp tsTemp = new Timestamp(curTime);
        String timeStamp =  tsTemp.toString();
        String timeStampMd5 = ImgSetUtil.md5(timeStamp);

        return dateStr + userId + "/" + timeStampMd5 + ".jpg";
    }

    /**
     *
     * @param userId
     * @return
     */
    public static String getZipKeyString(String userId) {
        java.text.DateFormat dateFormat = new SimpleDateFormat("yyyy/MMdd");
        String dateStr = dateFormat.format(new Date());

        long curTime = System.currentTimeMillis();
        Timestamp tsTemp = new Timestamp(curTime);
        String timeStamp =  tsTemp.toString();
        String timeStampMd5 = ImgSetUtil.md5(timeStamp);

        return dateStr + userId + "/" + timeStampMd5 + ".zip";
    }



    public static void setbitmap(final String imageUri, final ImageView imageView) throws IOException {
        // 显示网络上的图片
        final Bitmap bitmap;
        new AsyncTask<Void, Void, Bitmap>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Bitmap doInBackground(Void... params) {
                Bitmap imageBitmap = null;
                try {
                    URL myFileUrl = new URL(imageUri);
                    HttpURLConnection conn = (HttpURLConnection) myFileUrl
                            .openConnection();
                    conn.setDoInput(true);
                    conn.connect();
                    InputStream is = conn.getInputStream();
                    imageBitmap = BitmapFactory.decodeStream(is);
                    is.close();
                } catch (Exception e) {
                }
                return imageBitmap;
            }

            @Override
            protected void onPostExecute(Bitmap result) {
                super.onPostExecute(result);
                if (result == null) {
                } else {
                    imageView.setImageBitmap(result);
                }
            }
        }.executeOnExecutor(MyApplication.TASK_EXECUTOR);
    }

    public static String getImgKeyString() {
        java.text.DateFormat dateFormat = new SimpleDateFormat("yyyy/MMdd");
        String dateStr = dateFormat.format(new Date());

        long curTime = System.currentTimeMillis();
        Timestamp tsTemp = new Timestamp(curTime);
        String timeStamp =  tsTemp.toString();
        String timeStampMd5 = ImgSetUtil.md5(timeStamp);
        return dateStr + "/" + timeStampMd5 + ".jpg";
//        return dateStr + userId + "/" + timeStampMd5 + ".jpg";
    }
    /**
     * 根据图片的url路径获得Bitmap对象
     * @param url
     * @return
     */
    public static Bitmap returnBitmap(String url) {
        URL fileUrl = null;
        Bitmap bitmap = null;

        try {
            URL myFileUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) myFileUrl
                    .openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;

    }
}
