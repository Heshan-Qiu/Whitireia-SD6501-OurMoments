package ac.whitireia.ourmoments.ui.merge;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import ac.whitireia.ourmoments.R;

public class MergeFragment extends Fragment {

    private static final String IMAGE1 = "image1";
    private static final String IMAGE2 = "image2";

    private String image1Path;
    private String image2Path;

    private Bitmap mergedBitmap;

    public static MergeFragment newInstance(String image1, String image2) {
        MergeFragment fragment = new MergeFragment();
        Bundle args = new Bundle();
        args.putString(IMAGE1, image1);
        args.putString(IMAGE2, image2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            image1Path = getArguments().getString(IMAGE1);
            image2Path = getArguments().getString(IMAGE2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.merge_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mergedBitmap = createHorizontalMergedImage(image1Path, image2Path);
        Glide.with(view)
                .load(mergedBitmap)
                .fitCenter()
                .into((ImageView) view.findViewById(R.id.ivMerge));
    }

    private Bitmap createHorizontalMergedImage(String path1, String path2) {
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

    private Bitmap createVerticalMergedImage(String path1, String path2) {
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
