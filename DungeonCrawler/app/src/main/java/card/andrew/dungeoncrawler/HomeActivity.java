package card.andrew.dungeoncrawler;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar; // Added Toolbar import
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import card.andrew.dungeoncrawler.databinding.ActivityHomeBinding;

public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Setup Toolbar as ActionBar
        Toolbar toolbar = findViewById(R.id.toolbar); // Assuming R.id.toolbar is the ID of your Toolbar in activity_home.xml
        setSupportActionBar(toolbar);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_leaderboard)
                .build();
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_home);
        NavController navController = getNavController(fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

    @NonNull
    private NavController getNavController(Fragment fragment) {
        if (!(fragment instanceof NavHostFragment)) {
            throw new IllegalStateException("The fragment with ID R.id.nav_host_fragment_activity_home (" + R.id.nav_host_fragment_activity_home + ") is not a NavHostFragment. " +
                    "Check your layout (activity_home.xml) and ensure the FragmentContainerView with this ID " +
                    "has android:name=\"androidx.navigation.fragment.NavHostFragment\". " +
                    "Actual fragment type: " + (fragment != null ? fragment.getClass().getName() : "null"));
        }
        NavHostFragment navHostFragment = (NavHostFragment) fragment;
        NavController navController = navHostFragment.getNavController();
        return navController;
    }

}