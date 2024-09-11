package com.learnasyouread.stories;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.core.splashscreen.SplashScreen;

import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.learnasyouread.stories.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashSet;
import java.util.Set;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private SplashScreen splashScreen; // Declare the SplashScreen instance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // For devices with SDK version 31 or higher, set the splash screen
            splashScreen = SplashScreen.installSplashScreen(this);
        } else {
            // Ensure the theme is set before super.onCreate
            setTheme(R.style.Base_Theme_HikayeIng);
        }
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView bottomNavigationView = binding.botNag;
        NavController navController = Navigation.findNavController(this, R.id.fragmentContainerView);

        Set<Integer> topLevelDestinations = new HashSet<>();
        topLevelDestinations.add(R.id.storiesListFragment);
        topLevelDestinations.add(R.id.textListOCRFragment);
        topLevelDestinations.add(R.id.textRecognitionStoryFragment);
        topLevelDestinations.add(R.id.savedWordsFragment);


        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (topLevelDestinations.contains(destination.getId())) {
                bottomNavigationView.setVisibility(View.VISIBLE);
            } else {
                bottomNavigationView.setVisibility(View.GONE);
            }
        });
    }
}
