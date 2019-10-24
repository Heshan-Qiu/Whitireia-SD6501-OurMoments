package ac.whitireia.ourmoments;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utils {

    public static File createImageFile(Context context) throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(new Date());
        String pictureFile = "ourmoments_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(pictureFile, ".jpg", storageDir);
    }

    public static String getUriPath(Uri uri, Context context) throws IOException {
        InputStream inputStream = context.getContentResolver().openInputStream(uri);
        File file = createImageFile(context);
        OutputStream outputStream = new FileOutputStream(file);
        byte[] buffer = new byte[1024];
        int length;

        while ((length = inputStream.read(buffer)) > 0)
            outputStream.write(buffer, 0, length);

        outputStream.close();
        inputStream.close();

        return file.getAbsolutePath();
    }
}
