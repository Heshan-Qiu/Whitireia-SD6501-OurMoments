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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import ac.whitireia.ourmoments.MainActivity;
import ac.whitireia.ourmoments.R;

public class MergeFragment extends Fragment {

    private static final String IMAGE1 = "image1";
    private static final String IMAGE2 = "image2";

    private String image1Path;
    private String image2Path;

    private Bitmap mergedBitmap;

    private boolean horizontal;
    private boolean vertical;

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

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.findItem(R.id.menu_transform).setVisible(true);
        menu.findItem(R.id.menu_swap).setVisible(true);
        menu.findItem(R.id.menu_save).setVisible(true);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem item = menu.findItem(R.id.menu_swap);
        if (horizontal)
            item.setIcon(R.drawable.ic_swap_horiz_black_24dp);
        else if (vertical)
            item.setIcon(R.drawable.ic_swap_vert_black_24dp);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_transform:
                handleMenuTransform();
                break;
            case R.id.menu_swap:
                handleMenuSwap();
                break;
            case R.id.menu_save:
                break;
            default:
                break;
        }

        return false;
    }

    private void handleMenuSwap() {
        View view = getView();

        String tempPath = image1Path;
        image1Path = image2Path;
        image2Path = tempPath;

        if (horizontal) {
            mergedBitmap = createHorizontalMergedImage(image1Path, image2Path);
            Glide.with(view)
                    .load(mergedBitmap)
                    .fitCenter()
                    .into((ImageView) view.findViewById(R.id.ivMerge));
        } else if (vertical) {
            mergedBitmap = createVerticalMergedImage(image1Path, image2Path);
            Glide.with(view)
                    .load(mergedBitmap)
                    .fitCenter()
                    .into((ImageView) view.findViewById(R.id.ivMerge));
        }
    }

    private void handleMenuTransform() {
        View view = getView();

        if (horizontal) {
            mergedBitmap = createVerticalMergedImage(image1Path, image2Path);
            Glide.with(view)
                    .load(mergedBitmap)
                    .fitCenter()
                    .into((ImageView) view.findViewById(R.id.ivMerge));
        } else if (vertical) {
            mergedBitmap = createHorizontalMergedImage(image1Path, image2Path);
            Glide.with(view)
                    .load(mergedBitmap)
                    .fitCenter()
                    .into((ImageView) view.findViewById(R.id.ivMerge));
        }

        requireActivity().invalidateOptionsMenu();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((MainActivity) requireActivity()).showBackArrow();
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

        horizontal = true;
        vertical = false;

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

        vertical = true;
        horizontal = false;

        return mergedImage;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((MainActivity) requireActivity()).hideBackArrow();
    }
}
