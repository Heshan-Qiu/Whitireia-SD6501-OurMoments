package ac.whitireia.ourmoments.ui.image;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import ac.whitireia.ourmoments.MainActivity;
import ac.whitireia.ourmoments.R;

public class ImageFragment extends Fragment {

    private static final String LOG_TAG = ImageFragment.class.getName();
    private static final String FILE_PATH = "filepath";

    public static ImageFragment newInstance(String filePath) {
        ImageFragment fragment = new ImageFragment();
        Bundle args = new Bundle();
        args.putString(FILE_PATH, filePath);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((MainActivity) requireActivity()).showBackArrow();
        return inflater.inflate(R.layout.image_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            Glide.with(view).load(args.getString(FILE_PATH)).fitCenter().into((ImageView) view.findViewById(R.id.imageView));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((MainActivity) requireActivity()).hideBackArrow();
    }
}
