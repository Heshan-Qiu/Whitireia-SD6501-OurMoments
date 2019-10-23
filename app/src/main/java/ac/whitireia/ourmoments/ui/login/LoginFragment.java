package ac.whitireia.ourmoments.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

import ac.whitireia.ourmoments.R;
import ac.whitireia.ourmoments.ui.main.MainFragment;

import static android.app.Activity.RESULT_OK;

public class LoginFragment extends Fragment {

    private static final String LOG_TAG = LoginFragment.class.getName();
    private static final int RC_LOGIN = 1001;

    private FirebaseAuth mAuth;

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Initialiaze Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        return inflater.inflate(R.layout.login_fragment, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            List<AuthUI.IdpConfig> providers = Arrays.asList(
                    new AuthUI.IdpConfig.GoogleBuilder().build(),
                    new AuthUI.IdpConfig.EmailBuilder().build());

            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .setTheme(R.style.LoginTheme)
                            .setLogo(R.mipmap.ic_launcher)
                            .setIsSmartLockEnabled(false, true)
//                            .setTosAndPrivacyPolicyUrls("http://www.google.com", "http://www.google.com")
                            .build(),
                    RC_LOGIN);
        } else {
            startMainFragment();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_LOGIN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                startMainFragment();
            } else {
                /*
                  Sign in failed. If response is null the user canceled the sign-in flow using
                  the back button. Otherwise check response.getError().getErrorCode() and handle
                  the error.
                 */
                if (response == null) {
                    Toast.makeText(getContext(), "Login canceled.", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e(LOG_TAG, "Login failed", response.getError());
                }
            }
        }
    }

    private void startMainFragment() {
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commit();
    }
}
