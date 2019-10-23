package ac.whitireia.ourmoments;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import ac.whitireia.ourmoments.ui.login.LoginFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        findViewById(R.id.container).getBackground().setAlpha(60);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, LoginFragment.newInstance())
                    .commitNow();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_profile:
                break;
            case R.id.menu_logout:
                logout();
                break;
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                break;
        }

        return false;
    }

    private void logout() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        // user is now signed out
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.container, LoginFragment.newInstance())
                                .commit();
                    }
                });
    }

    public void showBackArrow() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void hideBackArrow() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

}
