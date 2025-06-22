package com.example.translate;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.example.translate.adapters.ViewPagerAdapter;
import com.example.translate.fragments.ImageFragment;
import com.example.translate.fragments.TextFragment;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int PERMISSION_REQUEST_CODE = 100;

    private String[] permissions = {
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            Log.d(TAG, "onCreate started");
            setContentView(R.layout.activity_main);
            Log.d(TAG, "Layout set successfully");

            checkPermissions();
            setupViewPager();

            Log.d(TAG, "onCreate completed successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage(), e);
            Toast.makeText(this, "Error loading app: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void setupViewPager() {
        try {
            Log.d(TAG, "Setting up ViewPager");

            TabLayout tabLayout = findViewById(R.id.tabLayout);
            ViewPager viewPager = findViewById(R.id.viewPager);

            if (tabLayout == null || viewPager == null) {
                Log.e(TAG, "TabLayout or ViewPager not found in layout");
                return;
            }

            ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

            // Add fragments
            try {
                adapter.addFragment(new TextFragment(), getString(R.string.translate_text));
                adapter.addFragment(new ImageFragment(), getString(R.string.translate_image));
                Log.d(TAG, "Fragments added to adapter");
            } catch (Exception e) {
                Log.e(TAG, "Error adding fragments: " + e.getMessage(), e);
                adapter.addFragment(new TextFragment(), "Text");
                adapter.addFragment(new ImageFragment(), "Image");
            }

            viewPager.setAdapter(adapter);
            tabLayout.setupWithViewPager(viewPager);

            // Set icons AFTER setupWithViewPager
            try {
                if (tabLayout.getTabCount() >= 2) {
                    TabLayout.Tab tab0 = tabLayout.getTabAt(0);
                    TabLayout.Tab tab1 = tabLayout.getTabAt(1);

                    if (tab0 != null) {
                        tab0.setIcon(R.drawable.ic_text_fields);
                        // Remove tint setting as we're using white icons on teal background
                    }
                    if (tab1 != null) {
                        tab1.setIcon(R.drawable.ic_image_placeholder);
                    }

                    // Verify icons are loaded
                    Log.d(TAG, "Tab 0 icon: " + (tab0 != null && tab0.getIcon() != null));
                    Log.d(TAG, "Tab 1 icon: " + (tab1 != null && tab1.getIcon() != null));
                }
            } catch (Exception e) {
                Log.e(TAG, "Error setting tab icons: " + e.getMessage(), e);
            }

            Log.d(TAG, "ViewPager setup completed");
        } catch (Exception e) {
            Log.e(TAG, "Error setting up ViewPager: " + e.getMessage(), e);
            Toast.makeText(this, "Error setting up interface", Toast.LENGTH_LONG).show();
        }
    }

    private void checkPermissions() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                boolean allPermissionsGranted = true;
                for (String permission : permissions) {
                    if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                        allPermissionsGranted = false;
                        break;
                    }
                }

                if (!allPermissionsGranted) {
                    ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
                }
                Log.d(TAG, "Permissions checked");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error checking permissions: " + e.getMessage(), e);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        try {
            if (requestCode == PERMISSION_REQUEST_CODE) {
                boolean allGranted = true;
                for (int result : grantResults) {
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        allGranted = false;
                        break;
                    }
                }

                if (!allGranted) {
                    Toast.makeText(this, "Some features may not work without permissions", Toast.LENGTH_LONG).show();
                }
                Log.d(TAG, "Permission results processed");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error processing permission results: " + e.getMessage(), e);
        }
    }
}