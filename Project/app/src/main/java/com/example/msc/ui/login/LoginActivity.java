package com.example.msc.ui.login;

import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.msc.MainActivity;
import com.example.msc.R;
import com.example.msc.persistence.MyDatabaseAccessor;
import com.example.msc.persistence.entities.User;
import com.example.msc.ui.recovery.AccountRecoveryActivity;
import com.example.msc.ui.registration.RegistrationActivity;
import com.example.msc.ui.settings.SettingsActivity;
import com.example.msc.util.BackgroundTask;
import com.example.msc.util.Encryptor;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;

import java.util.concurrent.ExecutionException;

public class LoginActivity extends AppCompatActivity {
    private CallbackManager callbackManager = CallbackManager.Factory.create();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final Button login = findViewById(R.id.login), register = findViewById(R.id.register);
        final EditText username = findViewById(R.id.username), password = findViewById(R.id.password);
        final LoginButton FBLoginButton = findViewById(R.id.login_button);
        final AccessToken accessToken = AccessToken.getCurrentAccessToken();
        final TextView credentialRecovery = findViewById(R.id.forgot_pwd);

        credentialRecovery.setOnClickListener(listener -> startActivity(new Intent(this, AccountRecoveryActivity.class)));

        login.setOnClickListener(view -> {
            final String user = username.getText().toString().trim(), pwd = password.getText().toString().trim();

            if (user.isEmpty() || pwd.isEmpty()) {
                Toast.makeText(this, "Fill form...", Toast.LENGTH_LONG).show();

                return;
            }

            String loginQuery = "";

            try {
                loginQuery = new BackgroundTask<>(() -> {
                    final User usr = MyDatabaseAccessor.getInstance(this.getApplicationContext()).getUserDao().getUserByUsername(user);

                    return usr != null && user.equals(usr.getUsername()) && pwd.equals(Encryptor.decrypt(usr.getPassword())) ? user : "";
                }, this.findViewById(R.id.loading), login, register, username, password).execute().get();
            } catch (final ExecutionException | InterruptedException exception) {
                exception.printStackTrace();
            }

            if (loginQuery.isEmpty())
                Toast.makeText(this, "LoginFailed", Toast.LENGTH_LONG).show();
            else {
                SettingsActivity.U_NAME = loginQuery;
                SettingsActivity.APP_LOGIN = true;
                Toast.makeText(this, "Welcome: " + loginQuery, Toast.LENGTH_LONG).show();
                startActivity(new Intent(this, MainActivity.class));
            }
        });
        register.setOnClickListener(listener -> startActivity(new Intent(this, RegistrationActivity .class)));
        FBLoginButton.setReadPermissions("email");
        FBLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(com.facebook.login.LoginResult loginResult) {
                final GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), (object, response) -> {
                    try {
                        SettingsActivity.U_NAME = object.getString("name");
                        SettingsActivity.APP_LOGIN = false;

                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    } catch(final JSONException exception) {
                        exception.printStackTrace();
                    }
                });
                Bundle parameters = new Bundle();

                parameters.putString("fields", "id,name,email,gender,birthday");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });

        if(accessToken != null && !accessToken.isExpired())
            startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onResume() {
        super.onResume();

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancelAll();

    }
}
