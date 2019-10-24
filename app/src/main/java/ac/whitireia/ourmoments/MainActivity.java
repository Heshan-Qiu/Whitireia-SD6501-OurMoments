package ac.whitireia.ourmoments;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import ac.whitireia.ourmoments.ui.about.AboutFragment;
import ac.whitireia.ourmoments.ui.login.LoginFragment;
import ac.whitireia.ourmoments.ui.main.MainFragment;

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
            case R.id.menu_settings:
                break;
            case R.id.menu_about:
                showAboutDialog();
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

    private void showAboutDialog() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("about");
        if (prev != null)
            transaction.remove(prev);
        transaction.addToBackStack(null);

        AboutFragment fragment = AboutFragment.newInstance();
        fragment.show(transaction, "dialog");
    }

    private void logout() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(getString(R.string.logout_title));
        alert.setMessage(getString(R.string.logout_message));
        alert.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AuthUI.getInstance()
                        .signOut(MainActivity.this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                // user is now signed out
                                getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.container, LoginFragment.newInstance())
                                        .commit();
                            }
                        });
            }
        });
        alert.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alert.show();
    }

    public void showBackArrow() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void hideBackArrow() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

}
