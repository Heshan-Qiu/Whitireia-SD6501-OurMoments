package ac.whitireia.ourmoments.ui.merge;

import android.app.ActionBar;
import android.app.Notification;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import ac.whitireia.ourmoments.MainActivity;
import ac.whitireia.ourmoments.R;
import ac.whitireia.ourmoments.Utils;
import ac.whitireia.ourmoments.ui.main.MainFragment;

public class MergeFragment extends Fragment {

    private static final String LOG_TAG = MergeFragment.class.getName();

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
                handleMenuSave();
                break;
            default:
                break;
        }

        return false;
    }

    private void handleMenuSave() {
        FragmentManager manager = requireActivity().getSupportFragmentManager();
        MainFragment fragment = (MainFragment) manager.findFragmentByTag(MainFragment.class.getName());
        fragment.setMergeComplete(true);
        try {
            fragment.setMergePath(Utils.convertBitmapToFile(mergedBitmap, requireContext()).getAbsolutePath());
        } catch (IOException e) {
            Log.e(LOG_TAG, "Convert bitmap to jpeg file error: ", e);
        }
        manager.popBackStack();

        if (Utils.isNotificationEnable(requireActivity())) {
            Notification notification = new NotificationCompat.Builder(requireContext(), "OurMomentsChannel")
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setContentTitle("OurMoments")
                    .setContentText("Our moments merged successfully.")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .build();
            NotificationManagerCompat.from(requireContext()).notify(1, notification);
        }
    }

    private void handleMenuSwap() {
        View view = getView();

        String tempPath = image1Path;
        image1Path = image2Path;
        image2Path = tempPath;

        if (horizontal) {
            mergedBitmap = Utils.createHorizontalMergedImage(image1Path, image2Path);
            setHorizontalFlags();
            Glide.with(view)
                    .load(mergedBitmap)
                    .fitCenter()
                    .into((ImageView) view.findViewById(R.id.ivMerge));
        } else if (vertical) {
            mergedBitmap = Utils.createVerticalMergedImage(image1Path, image2Path);
            setVerticalFlags();
            Glide.with(view)
                    .load(mergedBitmap)
                    .fitCenter()
                    .into((ImageView) view.findViewById(R.id.ivMerge));
        }
    }

    private void handleMenuTransform() {
        View view = getView();

        if (horizontal) {
            mergedBitmap = Utils.createVerticalMergedImage(image1Path, image2Path);
            setVerticalFlags();
            Glide.with(view)
                    .load(mergedBitmap)
                    .fitCenter()
                    .into((ImageView) view.findViewById(R.id.ivMerge));
        } else if (vertical) {
            mergedBitmap = Utils.createHorizontalMergedImage(image1Path, image2Path);
            setHorizontalFlags();
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

        mergedBitmap = Utils.createHorizontalMergedImage(image1Path, image2Path);
        setHorizontalFlags();
        Glide.with(view)
                .load(mergedBitmap)
                .fitCenter()
                .into((ImageView) view.findViewById(R.id.ivMerge));
    }

    private void setHorizontalFlags() {
        horizontal = true;
        vertical = false;
    }

    private void setVerticalFlags() {
        vertical = true;
        horizontal = false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((MainActivity) requireActivity()).hideBackArrow();
    }
}
