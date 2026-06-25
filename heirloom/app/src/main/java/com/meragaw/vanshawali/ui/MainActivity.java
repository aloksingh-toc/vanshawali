package com.meragaw.vanshawali.ui;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.meragaw.vanshawali.R;
import com.meragaw.vanshawali.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private NavController navController;

    // Tab IDs that show the bottom nav
    private static final int[] BOTTOM_NAV_DESTINATIONS = {
        R.id.nav_home,
        R.id.nav_tree,
        R.id.nav_photos,
        R.id.nav_search,
        R.id.nav_profile
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Edge-to-edge: let the app draw behind status/nav bars
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set up NavController
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
        }

        // Wire bottom nav to nav controller
        BottomNavigationView bottomNav = binding.bottomNav;
        NavigationUI.setupWithNavController(bottomNav, navController);

        // Hide bottom nav on pushed screens (Notifications, Profile detail)
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            boolean showNav = false;
            for (int id : BOTTOM_NAV_DESTINATIONS) {
                if (destination.getId() == id) { showNav = true; break; }
            }
            bottomNav.setVisibility(showNav
                ? android.view.View.VISIBLE
                : android.view.View.GONE);
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp() || super.onSupportNavigateUp();
    }
}
