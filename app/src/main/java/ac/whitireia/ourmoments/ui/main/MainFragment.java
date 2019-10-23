package ac.whitireia.ourmoments.ui.main;

import androidx.core.content.FileProvider;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.IdpResponse;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import ac.whitireia.ourmoments.R;
import ac.whitireia.ourmoments.ui.image.ImageFragment;

import static android.app.Activity.RESULT_OK;

public class MainFragment extends Fragment implements View.OnClickListener {

    private static final String LOG_TAG = MainFragment.class.getName();
    private static final int RC_CAMERA = 1002;
    private static final int RC_ALBUM = 1003;

    private File cameraFile;
    private int imageWidth;
    private int imageHeight;

    private boolean image1Setted;
    private boolean image2Setted;
    private boolean mergeEnable;

    private String image1Path;
    private String image2Path;
    private String mergePath;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        image1Path = getPathForResource(R.drawable.sample1);
        image2Path = getPathForResource(R.drawable.sample2);
        mergePath = getPathForResource(R.drawable.sample3);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_fragment, container, false);
    }

    private String getPathForResource(int resourceId) {
        return Uri.parse("android.resource://" + Objects.requireNonNull(R.class.getPackage()).getName() + "/" + resourceId).toString();
    }

    private void calculateImageDimensions() {
        Point screenSize = new Point();
        requireActivity().getWindowManager().getDefaultDisplay().getSize(screenSize);
        imageHeight = screenSize.y / 6;
        imageWidth = imageHeight * 4 / 3;
    }

    private void initializeImages() {
        View view = getView();

        if (view != null) {
            ImageView imageView1 = view.findViewById(R.id.imageView1);
            ImageView imageView2 = view.findViewById(R.id.imageView2);
            ImageView imageView3 = view.findViewById(R.id.imageView3);

            if (image1Path == null)
                Glide.with(view).load(R.drawable.sample1).override(imageWidth, imageHeight).fitCenter().into(imageView1);
            else
                Glide.with(view).load(image1Path).override(imageWidth, imageHeight).fitCenter().into(imageView1);
            if (image2Path == null)
                Glide.with(view).load(R.drawable.sample2).override(imageWidth, imageHeight).fitCenter().into(imageView2);
            else
                Glide.with(view).load(image2Path).override(imageWidth, imageHeight).fitCenter().into(imageView2);
            if (mergePath == null)
                Glide.with(view).load(R.drawable.sample3).override(imageWidth, imageHeight).fitCenter().into(imageView3);
            else
                Glide.with(view).load(mergePath).override(imageWidth, imageHeight).fitCenter().into(imageView3);

            imageView1.setOnClickListener(this);
            imageView2.setOnClickListener(this);
            imageView3.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View view) {
        String path = null;
        switch (view.getId()) {
            case R.id.imageView1:
                path = image1Path;
                break;
            case R.id.imageView2:
                path = image2Path;
                break;
            case R.id.imageView3:
                path = mergePath;
                break;
            default:
                break;
        }

        if (path != null)
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, ImageFragment.newInstance(path))
                    .addToBackStack(MainFragment.class.getName())
                    .commit();
    }

    private void clearAllImages() {
        image1Path = getPathForResource(R.drawable.sample1);
        image2Path = getPathForResource(R.drawable.sample2);
        mergePath = getPathForResource(R.drawable.sample3);
        image1Setted = false;
        image2Setted = false;
        mergeEnable = false;

        View view = getView();
        if (view != null) {
            ImageView imageView1 = view.findViewById(R.id.imageView1);
            ImageView imageView2 = view.findViewById(R.id.imageView2);
            ImageView imageView3 = view.findViewById(R.id.imageView3);

            Glide.with(view).load(R.drawable.sample1).override(imageWidth, imageHeight).fitCenter().into(imageView1);
            Glide.with(view).load(R.drawable.sample2).override(imageWidth, imageHeight).fitCenter().into(imageView2);
            Glide.with(view).load(R.drawable.sample3).override(imageWidth, imageHeight).fitCenter().into(imageView3);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        calculateImageDimensions();
        initializeImages();

        Button selectChoose = view.findViewById(R.id.buttonChoose);
        selectChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(requireActivity(), view);
                popupMenu.inflate(R.menu.select_menu);

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.menu_camera:
                                startCameraActivity();
                                break;
                            case R.id.menu_album:
                                startAlbumActivity();
                                break;
                            case R.id.menu_clear:
                                clearAllImages();
                                break;
                            default:
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });
    }

    private void startAlbumActivity() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, RC_ALBUM);
    }

    private void startCameraActivity() {
        try {
            cameraFile = createImageFile();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Can't create image file.", e);
            return;
        }

        Uri photoURI = FileProvider.getUriForFile(requireActivity(),
                "ac.whitireia.ourmoments.fileprovider", cameraFile);
        startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, photoURI),
                RC_CAMERA);
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(new Date());
        String pictureFile = "ourmoments_" + timeStamp + "_";
        File storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(pictureFile, ".jpg", storageDir);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IdpResponse response = IdpResponse.fromResultIntent(data);
        if (resultCode == RESULT_OK) {
            if (requestCode == RC_CAMERA) {
                Toast.makeText(getContext(), "Capture image successfully.", Toast.LENGTH_LONG).show();
                handleImageResult(cameraFile.getAbsolutePath());
            } else if (requestCode == RC_ALBUM) {
                Toast.makeText(getContext(), "Choose image successfully.", Toast.LENGTH_LONG).show();
                Uri uri = data.getData();
                try {
                    handleImageResult(getUriPath(uri));
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Handle image error.", e);
                }
            }
        } else if (response != null)
            Log.e(LOG_TAG, "Capture image error", response.getError());
    }

    private String getUriPath(Uri uri) throws IOException {
        InputStream inputStream = getContext().getContentResolver().openInputStream(uri);
        File file = createImageFile();
        OutputStream outputStream = new FileOutputStream(file);
        byte[] buffer = new byte[1024];
        int length;

        while ((length = inputStream.read(buffer)) > 0)
            outputStream.write(buffer, 0, length);

        outputStream.close();
        inputStream.close();

        return file.getAbsolutePath();
    }

    private void handleImageResult(String path) {
        ImageView imageView = null;
        View view = getView();

        if (view != null) {
            if (!image1Setted) {
                imageView = view.findViewById(R.id.imageView1);
                image1Path = path;
                image1Setted = true;
                image2Setted = false;
            } else if (!image2Setted) {
                imageView = view.findViewById(R.id.imageView2);
                image2Path = path;
                image2Setted = true;
                image1Setted = false;
                mergeEnable = true;
            }
        }
        if (imageView != null)
            Glide.with(getView()).load(path).override(imageWidth, imageHeight).fitCenter().into(imageView);
    }
}
