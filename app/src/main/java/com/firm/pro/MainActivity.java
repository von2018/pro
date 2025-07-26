package com.firm.pro;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.firm.pro.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private ActivityMainBinding binding;

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
//        setContentView(R.layout.activity_main);

        bottomNavigationView = binding.bottomNav;

        // 默认显示第一个Fragment
        if (savedInstanceState == null) {
            loadFragment(new ShortPlayFragment());
            bottomNavigationView.setSelectedItemId(R.id.nav_short_play);
        }

        // 设置导航项选择监听
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();
            if (itemId == R.id.nav_short_play) {
                selectedFragment = new ShortPlayFragment();
            } else if (itemId == R.id.nav_quiz) {
                selectedFragment = new QuizFragment();
            } else if (itemId == R.id.nav_welfare) {
                selectedFragment = new WelfareFragment();
            } else if (itemId == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
                return true;
            }
            return false;
        });
    }

    // 加载Fragment的方法
    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }


}