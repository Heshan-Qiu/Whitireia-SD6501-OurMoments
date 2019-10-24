package ac.whitireia.ourmoments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Environment;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utils {

    public static final String PREFERENCES_NAME = "Our Moments Preferences";

    public static boolean isNotificationEnable(Activity activity) {
        SharedPreferences preferences = activity.getApplicationContext().getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        boolean notification = preferences.getBoolean("notification", true);
        return notification;
    }

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

    public static File convertBitmapToFile(@NonNull Bitmap bitmap, Context context) throws IOException {
        File file = Utils.createImageFile(context);
        FileOutputStream outputStream = new FileOutputStream(file);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        outputStream.flush();
        outputStream.close();
        return file;
    }

    public static Bitmap createHorizontalMergedImage(String path1, String path2) {
        Bitmap image1 = BitmapFactory.decodeFile(path1);
        Bitmap image2 = BitmapFactory.decodeFile(path2);

        int width = image1.getWidth() + image2.getWidth();
        int height = image1.getHeight() > image2.getHeight() ? image1.getHeight() : image2.getHeight();
        Bitmap mergedImage = Bitmap.createBitmap(width, height, image1.getConfig());

        Canvas canvas = new Canvas(mergedImage);
        canvas.drawBitmap(image1, 0, 0, null);
        canvas.drawBitmap(image2, image1.getWidth(), 0, null);

        return mergedImage;
    }

    public static Bitmap createVerticalMergedImage(String path1, String path2) {
        Bitmap image1 = BitmapFactory.decodeFile(path1);
        Bitmap image2 = BitmapFactory.decodeFile(path2);

        int width = image1.getWidth() > image2.getWidth() ? image1.getWidth() : image2.getWidth();
        int height = image1.getHeight() + image2.getHeight();
        Bitmap mergedImage = Bitmap.createBitmap(width, height, image1.getConfig());

        Canvas canvas = new Canvas(mergedImage);
        canvas.drawBitmap(image1, 0, 0, null);
        canvas.drawBitmap(image2, 0, image1.getHeight(), null);

        return mergedImage;
    }

}
