package com.fengda.app.utils;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import com.fengda.app.common.CreateFile;
import com.fengda.app.constant.MyConstant;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileUtils {

    public final static String FILE_EXTENSION_SEPARATOR = ".";

    private FileUtils() {
        throw new AssertionError();
    }

    public static String getRealPathFromURI(Context context, Uri contentURI) {
        String result;
        Cursor cursor = context.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    public static boolean saveBitmap(Context context, Bitmap bitmap) {
        String filename;
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat ("yyyyMMddHHmmss");
        filename = sdf.format(date);

        try{
            OutputStream fOut;
            FileUtils.makeDirs(MyConstant.IMG_PATH);
            File file = new File(CreateFile.QXCODE_PATH, filename + ".jpg");
            fOut = new FileOutputStream(file);

            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();

            MediaStore.Images.Media.insertImage(context.getContentResolver()
                    ,file.getAbsolutePath(), file.getName(), file.getName());
            return true;

        }catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Handle the bitmap and img_white_return the final path
     * @param context
     * @param path
     * @return
     */
    public static String handleBitmapByPath(Context context, String path) {
        Bitmap bitmap = getAdjustCompressBitmapByPath(path);

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

    /**
     * 等比压缩图片
     *
     * @param bitmap
     * @param width
     * @param height
     * @return
     */
    public static Bitmap zoomBitmap(Bitmap bitmap, int width, int height) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) width / w);
        float scaleHeight = ((float) height / h);
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
        return newbmp;
    }
    public static Bitmap decodeFile(String fPath) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        opts.inDither = false; // Disable Dithering mode
        opts.inPurgeable = true; // Tell to gc that whether it needs free
        opts.inInputShareable = true; // Which kind of reference will be used to
        BitmapFactory.decodeFile(fPath, opts);
        final int REQUIRED_SIZE = 200;
        int scale = 1;
        if (opts.outHeight > REQUIRED_SIZE || opts.outWidth > REQUIRED_SIZE) {
            final int heightRatio = Math.round((float) opts.outHeight
                    / (float) REQUIRED_SIZE);
            final int widthRatio = Math.round((float) opts.outWidth
                    / (float) REQUIRED_SIZE);
            scale = heightRatio < widthRatio ? heightRatio : widthRatio;//
        }
        Log.i("scale", "scal ="+ scale);
        opts.inJustDecodeBounds = false;
        opts.inSampleSize = scale;
        Bitmap bm = BitmapFactory.decodeFile(fPath, opts).copy(Bitmap.Config.ARGB_8888, false);
        return bm;
    }


    public static Bitmap getAdjustCompressBitmapByPath(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inSampleSize = 4;
        options.inJustDecodeBounds = false;

        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        Bitmap bitmap1 = ImgSetUtil.adjustBitmapSize(bitmap, MyConstant.DEF_IMG_W, MyConstant.DEF_IMG_H);

        return bitmap1;
    }

    public static String saveBitmapRetPath(Context context, Bitmap bitmap) {
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

    public static String compressAndSaveBitmap(Context context, Bitmap bitmap1, int width, int height) {
        Bitmap tBitmap = ImgSetUtil.adjustBitmapSize(bitmap1, width, height);

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

            tBitmap.compress(Bitmap.CompressFormat.JPEG, 90, fOut);
            fOut.flush();
            fOut.close();

            MediaStore.Images.Media.insertImage(context.getContentResolver()
                    ,file.getAbsolutePath(), file.getName(), file.getName());

        }catch (Exception e) {
            e.printStackTrace();
        }
        return filePath;
    }

    public static StringBuilder readFile(String filePath, String charsetName) {
        File file = new File(filePath);
        StringBuilder fileContent = new StringBuilder("");
        if (file == null || !file.isFile()) {
            return null;
        }

        BufferedReader reader = null;
        try {
            InputStreamReader is = new InputStreamReader(new FileInputStream(file), charsetName);
            reader = new BufferedReader(is);
            String line = null;
            while ((line = reader.readLine()) != null) {
                if (!fileContent.toString().equals("")) {
                    fileContent.append("\r\n");
                }
                fileContent.append(line);
            }
            return fileContent;
        } catch (IOException e) {
            throw new RuntimeException("IOException occurred. ", e);
        } finally {
            IOUtils.close(reader);
        }
    }

    public static boolean writeFile(String filePath, String content, boolean append) {
        if (StringUtils.isEmpty(content)) {
            return false;
        }

        FileWriter fileWriter = null;
        try {
            makeDirs(filePath);
            fileWriter = new FileWriter(filePath, append);
            fileWriter.write(content);
            return true;
        } catch (IOException e) {
            throw new RuntimeException("IOException occurred. ", e);
        } finally {
            IOUtils.close(fileWriter);
        }
    }

    public static boolean writeFile(String filePath, String content) {
        return writeFile(filePath, content, false);
    }

    public static boolean writeFile(String filePath, InputStream stream) {
        return writeFile(filePath, stream, false);
    }

    public static boolean writeFile(String filePath, InputStream stream, boolean append) {
        return writeFile(filePath != null ? new File(filePath) : null, stream, append);
    }

    public static boolean writeFile(File file, InputStream stream) {
        return writeFile(file, stream, false);
    }

    public static boolean writeFile(File file, InputStream stream, boolean append) {
        OutputStream o = null;
        try {
            makeDirs(file.getAbsolutePath());
            o = new FileOutputStream(file, append);
            byte data[] = new byte[1024];
            int length = -1;
            while ((length = stream.read(data)) != -1) {
                o.write(data, 0, length);
            }
            o.flush();
            return true;
        } catch (FileNotFoundException e) {
            throw new RuntimeException("FileNotFoundException occurred. ", e);
        } catch (IOException e) {
            throw new RuntimeException("IOException occurred. ", e);
        } finally {
            IOUtils.close(o);
            IOUtils.close(stream);
        }
    }

    public static void moveFile(String sourceFilePath, String destFilePath) {
        if (TextUtils.isEmpty(sourceFilePath) || TextUtils.isEmpty(destFilePath)) {
            throw new RuntimeException("Both sourceFilePath and destFilePath cannot be null.");
        }
        moveFile(new File(sourceFilePath), new File(destFilePath));
    }

    public static void moveFile(File srcFile, File destFile) {
        boolean rename = srcFile.renameTo(destFile);
        if (!rename) {
            copyFile(srcFile.getAbsolutePath(), destFile.getAbsolutePath());
            deleteFile(srcFile.getAbsolutePath());
        }
    }

    public static boolean copyFile(String sourceFilePath, String destFilePath) {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(sourceFilePath);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("FileNotFoundException occurred. ", e);
        }
        return writeFile(destFilePath, inputStream);
    }

    public static List<String> readFileToList(String filePath, String charsetName) {
        File file = new File(filePath);
        List<String> fileContent = new ArrayList<String>();
        if (file == null || !file.isFile()) {
            return null;
        }

        BufferedReader reader = null;
        try {
            InputStreamReader is = new InputStreamReader(new FileInputStream(file), charsetName);
            reader = new BufferedReader(is);
            String line = null;
            while ((line = reader.readLine()) != null) {
                fileContent.add(line);
            }
            return fileContent;
        } catch (IOException e) {
            throw new RuntimeException("IOException occurred. ", e);
        } finally {
            IOUtils.close(reader);
        }
    }

    public static String getFileNameWithoutExtension(String filePath) {
        if (StringUtils.isEmpty(filePath)) {
            return filePath;
        }

        int extenPosi = filePath.lastIndexOf(FILE_EXTENSION_SEPARATOR);
        int filePosi = filePath.lastIndexOf(File.separator);
        if (filePosi == -1) {
            return (extenPosi == -1 ? filePath : filePath.substring(0, extenPosi));
        }
        if (extenPosi == -1) {
            return filePath.substring(filePosi + 1);
        }
        return (filePosi < extenPosi ? filePath.substring(filePosi + 1, extenPosi) : filePath.substring(filePosi + 1));
    }

    public static String getFileName(String filePath) {
        if (StringUtils.isEmpty(filePath)) {
            return filePath;
        }

        int filePosi = filePath.lastIndexOf(File.separator);
        return (filePosi == -1) ? filePath : filePath.substring(filePosi + 1);
    }

    public static String getFolderName(String filePath) {

        if (StringUtils.isEmpty(filePath)) {
            return filePath;
        }

        int filePosi = filePath.lastIndexOf(File.separator);
        return (filePosi == -1) ? "" : filePath.substring(0, filePosi);
    }

    public static String getFileExtension(String filePath) {
        if (StringUtils.isBlank(filePath)) {
            return filePath;
        }

        int extenPosi = filePath.lastIndexOf(FILE_EXTENSION_SEPARATOR);
        int filePosi = filePath.lastIndexOf(File.separator);
        if (extenPosi == -1) {
            return "";
        }
        return (filePosi >= extenPosi) ? "" : filePath.substring(extenPosi + 1);
    }

    public static boolean makeDirs(String filePath) {
        String folderName = getFolderName(filePath);
        if (StringUtils.isEmpty(folderName)) {
            return false;
        }

        File folder = new File(folderName);
        return (folder.exists() && folder.isDirectory()) ? true : folder.mkdirs();
    }

    public static boolean makeFolders(String filePath) {
        return makeDirs(filePath);
    }

    public static boolean isFileExist(String filePath) {
        if (StringUtils.isBlank(filePath)) {
            return false;
        }

        File file = new File(filePath);
        return (file.exists() && file.isFile());
    }

    public static boolean isFolderExist(String directoryPath) {
        if (StringUtils.isBlank(directoryPath)) {
            return false;
        }

        File dire = new File(directoryPath);
        return (dire.exists() && dire.isDirectory());
    }

    public static boolean deleteFile(String path) {
        if (StringUtils.isBlank(path)) {
            return true;
        }

        File file = new File(path);
        if (!file.exists()) {
            return true;
        }
        if (file.isFile()) {
            return file.delete();
        }
        if (!file.isDirectory()) {
            return false;
        }
        for (File f : file.listFiles()) {
            if (f.isFile()) {
                f.delete();
            } else if (f.isDirectory()) {
                deleteFile(f.getAbsolutePath());
            }
        }
        return file.delete();
    }

    public static long getFileSize(String path) {
        if (StringUtils.isBlank(path)) {
            return -1;
        }

        File file = new File(path);
        return (file.exists() && file.isFile() ? file.length() : -1);
    }




    public static void saveImageToGallery(Context context, Bitmap bmp) {
        // 首先保存图片
        File appDir = new File(Environment.getExternalStorageDirectory(), "Boohee");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 其次把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    file.getAbsolutePath(), fileName, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // 最后通知图库更新
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + Environment.getExternalStorageDirectory())));
    }


    public static String getDiskCacheDir(Context context) {
        String cachePath = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return cachePath;
    }

    public static  File getCacheFile(File parent, String child) {
        // 创建File对象，用于存储图片
        File file = new File(parent, child);

        if (file.exists()) {
            file.delete();
        }
        createOrExistsFile(file);
        return file;
    }

    /**
     * 根据文件路径获取文件
     *
     * @param filePath 文件路径
     * @return 文件
     */
    public static File getFileByPath(String filePath) {
        return StringUtils.isBlank(filePath) ? null : new File(filePath);
    }

    /**
     * 判断目录是否存在，不存在则判断是否创建成功
     *
     * @param dirPath 文件路径
     * @return {@code true}: 存在或创建成功<br>{@code false}: 不存在或创建失败
     */
    public static boolean createOrExistsDir(String dirPath) {
        return createOrExistsDir(getFileByPath(dirPath));
    }

    /**
     * 判断目录是否存在，不存在则判断是否创建成功
     *
     * @param file 文件
     * @return {@code true}: 存在或创建成功<br>{@code false}: 不存在或创建失败
     */
    public static boolean createOrExistsDir(File file) {
        // 如果存在，是目录则返回true，是文件则返回false，不存在则返回是否创建成功
        return file != null && (file.exists() ? file.isDirectory() : file.mkdirs());
    }

    /**
     * 判断文件是否存在，不存在则判断是否创建成功
     *
     * @param filePath 文件路径
     * @return {@code true}: 存在或创建成功<br>{@code false}: 不存在或创建失败
     */
    public static boolean createOrExistsFile(String filePath) {
        return createOrExistsFile(getFileByPath(filePath));
    }

    /**
     * 判断文件是否存在，不存在则判断是否创建成功
     *
     * @param file 文件
     * @return {@code true}: 存在或创建成功<br>{@code false}: 不存在或创建失败
     */
    public static boolean createOrExistsFile(File file) {
        if (file == null) return false;
        // 如果存在，是文件则返回true，是目录则返回false
        if (file.exists()) return file.isFile();
        if (!createOrExistsDir(file.getParentFile())) return false;
        try {
            return file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean isCreateSrcDir = false;//是否创建源目录 在这里的话需要说明下。如果需要
    /**
     *
     * [对指定路径下文件的压缩处理]<BR>
     * [功能详细描述]
     *
     * @param src 径地址
     * @param archive 指定到压缩文件夹的路径
     * @param comment 描述
     * @throws FileNotFoundException 文件没有找到异常
     * @throws IOException IO输入异常
     */
    public static void writeByApacheZipOutputStream(List<File>  src,
                                              String archive, String comment) throws FileNotFoundException,
            IOException
    {
     //   Log.e(TAG, "writeByApacheZipOutputStream");
        //----压缩文件：
        FileOutputStream f = new FileOutputStream(archive);
        //使用指定校验和创建输出流
        CheckedOutputStream csum = new CheckedOutputStream(f, new CRC32());
        ZipOutputStream zos = new ZipOutputStream(csum);
        //支持中文
    //    zos.setEncoding("GBK");
        BufferedOutputStream out = new BufferedOutputStream(zos);
        //设置压缩包注释
        zos.setComment(comment);
        //启用压缩
        zos.setMethod(ZipOutputStream.DEFLATED);
        //压缩级别为最强压缩，但时间要花得多一点
        zos.setLevel(Deflater.BEST_COMPRESSION);
        // 如果为单个文件的压缩在这里修改
        for (int i = 0; i < src.size(); i++)
        {
     //       Log.e(TAG, "src["+i+"] is "+src[i]);
            File srcFile = src.get(i);
            if (!srcFile.exists()
                    || (srcFile.isDirectory() && srcFile.list().length == 0))
            {
        //        Log.e(TAG, "!srcFile.exists()");
                throw new FileNotFoundException(
                        "File must exist and ZIP file must have at least one entry.");
            }
            String strSrcString = src.get(i).getAbsolutePath();
            //获取压缩源所在父目录
            strSrcString = strSrcString.replaceAll("////", "/");
            String prefixDir = null;
            if (srcFile.isFile())
            {
                prefixDir = strSrcString.substring(0, strSrcString
                        .lastIndexOf("/") + 1);
            }
            else
            {
                prefixDir = (strSrcString.replaceAll("/$", "") + "/");
            }
            //如果不是根目录
            if (prefixDir.indexOf("/") != (prefixDir.length() - 1)
                    && isCreateSrcDir)
            {
                prefixDir = prefixDir.replaceAll("[^/]+/$", "");
            }
            //开始压缩
            writeRecursive(zos, out, srcFile, prefixDir);
        }

        out.close();
        // 注：校验和要在流关闭后才准备，一定要放在流被关闭后使用
    //    Log.e(TAG, "Checksum: " + csum.getChecksum().getValue());
        @SuppressWarnings("unused")
        BufferedInputStream bi;
    }

    /**
     *
     * [递归压缩
     *
     * 使用 org.apache.tools.zip.ZipOutputStream 类进行压缩，它的好处就是支持中文路径， 而Java类库中的
     * java.util.zip.ZipOutputStream 压缩中文文件名时压缩包会出现乱码。 使用 apache 中的这个类与 java
     * 类库中的用法是一新的，只是能设置编码方式了。]<BR>
     * [功能详细描述]
     *
     * @param zos
     * @param bo
     * @param srcFile
     * @param prefixDir
     * @throws IOException
     * @throws FileNotFoundException
     */
    private static void writeRecursive(ZipOutputStream zos,
                                       BufferedOutputStream bo, File srcFile, String prefixDir)
            throws IOException, FileNotFoundException
    {
     //   Log.e(TAG, "writeRecursive");
        ZipEntry zipEntry;
        String filePath = srcFile.getAbsolutePath().replaceAll("////", "/")
                .replaceAll("//", "/");
        if (srcFile.isDirectory())
        {
            filePath = filePath.replaceAll("/$", "") + "/";
        }
        String entryName = filePath.replace(prefixDir, "").replaceAll("/$", "");
        if (srcFile.isDirectory())
        {
            if (!"".equals(entryName))
            {
        //        Log.e(TAG, "正在创建目录 - " + srcFile.getAbsolutePath() + " entryName=" + entryName);
                //如果是目录，则需要在写目录后面加上 /
                zipEntry = new ZipEntry(entryName + "/");
                zos.putNextEntry(zipEntry);
            }
            File srcFiles[] = srcFile.listFiles();
            for (int i = 0; i < srcFiles.length; i++)
            {
                writeRecursive(zos, bo, srcFiles[i], prefixDir);
            }
        }
        else
        {
        //    Log.e(TAG,"正在写文件 - " + srcFile.getAbsolutePath() + " entryName=" + entryName );
            BufferedInputStream bi = new BufferedInputStream(
                    new FileInputStream(srcFile));
            //开始写入新的ZIP文件条目并将流定位到条目数据的开始处
            zipEntry = new ZipEntry(entryName);
            zos.putNextEntry(zipEntry);
            byte[] buffer = new byte[1024];
            int readCount = bi.read(buffer);
            while (readCount != -1)
            {
                bo.write(buffer, 0, readCount);
                readCount = bi.read(buffer);
            }
            //注，在使用缓冲流写压缩文件时，一个条件完后一定要刷新一把，不
            //然可能有的内容就会存入到后面条目中去了
            bo.flush();
            //文件读完后关闭
            bi.close();
        }
    }


    /**
     * 删除文件
     * @param file 文件
     * @return {@code true}: 删除成功<br>{@code false}: 删除失败
     */
    public static boolean deleteFile(File file) {
        return file != null && (!file.exists() || file.isFile() && file.delete());
    }


    /**
     *
     * @param context
     * @param fileName
     * @return
     */
    public static String readAssets(Context context, String fileName)
    {
        InputStream is = null;
        String content = null;
        try
        {
            is = context.getAssets().open(fileName);
            if (is != null)
            {

                byte[] buffer = new byte[1024];
                ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
                while (true)
                {
                    int readLength = is.read(buffer);
                    if (readLength == -1) break;
                    arrayOutputStream.write(buffer, 0, readLength);
                }
                is.close();
                arrayOutputStream.close();
                content = new String(arrayOutputStream.toByteArray());

            }
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            content = null;
        }
        finally
        {
            try
            {
                if (is != null) is.close();
            }
            catch (IOException ioe)
            {
                ioe.printStackTrace();
            }
        }
        return content;
    }


    /**
     * 复制assets目录下的指定文件到指定路径
     */
    public static boolean copyAssetsFile(Context context, String assetsFile,
                                         String destFile) throws IOException {
        InputStream is = context.getAssets().open(assetsFile);
        if (null == is) {
            return false;
        }

        File dest = new File(destFile);
        if (dest.exists()) {
            dest.delete();
        }

        FileOutputStream os = new FileOutputStream(dest);
        byte[] buf = new byte[1024];
        int length = -1;
        while ((length = is.read(buf)) != -1) {
            os.write(buf, 0, length);
        }
        os.flush();
        os.close();
        is.close();

        return true;
    }


}