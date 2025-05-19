package com.example.agrishop;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtils {
    public static File getFileFromUri(Context context, Uri uri) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            File file = new File(context.getCacheDir(), getFileName(context, uri));
            OutputStream outputStream = new FileOutputStream(file);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.close();
            inputStream.close();
            return file;
        } catch (Exception e) {
            Log.e("FileUtils", "Error getting file from URI", e);
            return null;
        }
    }

    private static String getFileName(Context context, Uri uri) {
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            String name = cursor.getString(nameIndex);
            cursor.close();
            return name;
        }
        return "temp_file";
    }
}
