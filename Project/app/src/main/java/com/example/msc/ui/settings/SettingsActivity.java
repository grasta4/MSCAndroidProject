package com.example.msc.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;
import com.example.msc.MainActivity;
import com.example.msc.R;

public class SettingsActivity extends AppCompatActivity {
    public static String U_NAME = "";
    private static boolean fragmentPresent = false;
    public static boolean APP_LOGIN = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ((TextView)findViewById(R.id.username)).setText(U_NAME);

        final Button button = findViewById(R.id.change_password), backButton = findViewById(R.id.back);
        button.setEnabled(APP_LOGIN);

        backButton.setOnClickListener(view -> startActivity(new Intent(this, MainActivity.class)));
        button.setOnClickListener(view -> {
            final FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

            fragmentTransaction.replace(R.id.fragment, !fragmentPresent ? new PasswordChangeFragment() : new BlankFragment());
            fragmentTransaction.commit();
            button.setText(fragmentPresent ? getString(R.string.done) : getString(R.string.change_pwd));

            fragmentPresent = !fragmentPresent;
        });
    }
}