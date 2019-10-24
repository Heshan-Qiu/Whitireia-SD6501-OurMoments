package ac.whitireia.ourmoments.ui.setting;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

import ac.whitireia.ourmoments.R;
import ac.whitireia.ourmoments.Utils;
import ac.whitireia.ourmoments.ui.main.MainFragment;

public class SettingsFragment extends Fragment {

    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.settings_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final SharedPreferences preferences = requireActivity().getApplicationContext().getSharedPreferences(Utils.PREFERENCES_NAME, Context.MODE_PRIVATE);
        String name = preferences.getString("name", null);
        boolean watermark = preferences.getBoolean("watermark", false);
        boolean notification = preferences.getBoolean("notification", true);

        final EditText editText = view.findViewById(R.id.etName);
        editText.setText(name);
        final Switch wSwitch = view.findViewById(R.id.switchWatermark);
        wSwitch.setChecked(watermark);
        final Switch nSwitch = view.findViewById(R.id.switchNotification);
        nSwitch.setChecked(notification);

        ((Button) view.findViewById(R.id.buttonOK)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("name", editText.getText().toString());
                editor.putBoolean("watermark", wSwitch.isChecked());
                editor.putBoolean("notification", nSwitch.isChecked());
                editor.apply();

                showMainFragment();
            }
        });

        ((Button) view.findViewById(R.id.buttonCancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMainFragment();
            }
        });
    }

    private void showMainFragment() {
        FragmentManager manager = requireActivity().getSupportFragmentManager();
        MainFragment fragment = (MainFragment) manager.findFragmentByTag(MainFragment.class.getName());
        if (fragment == null) {
            fragment = MainFragment.newInstance();
            manager.beginTransaction().add(fragment, MainFragment.class.getName());
        }
        manager.beginTransaction().replace(R.id.container, fragment).commit();
    }
}
