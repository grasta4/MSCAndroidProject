package com.example.msc.ui.registration;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.msc.persistence.MyDatabaseAccessor;
import com.example.msc.persistence.dao.UserDao;
import com.example.msc.persistence.entities.User;
import com.example.msc.R;
import com.example.msc.ui.login.LoginActivity;
import com.example.msc.ui.util.Validator;
import com.example.msc.util.BackgroundTask;
import java.util.concurrent.ExecutionException;

public class RegistrationActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        final Intent moveToLogin = new Intent(RegistrationActivity.this, LoginActivity.class);
        final Button signUp = findViewById(R.id.sign_up);
        final EditText username = findViewById(R.id.username), password = findViewById(R.id.password), confirmPassword = findViewById(R.id.confirm_password), email = findViewById(R.id.email);
        final TextView signIn = findViewById(R.id.sign_in);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View listener) {
                final String user = username.getText().toString().trim(), pwd = password.getText().toString().trim(), c_pwd = confirmPassword.getText().toString().trim(), mail = email.getText().toString().trim(), errorMessage = Validator.validateAll(user, pwd, c_pwd, mail);

                if (errorMessage != null) {
                    Toast.makeText(RegistrationActivity.this, errorMessage, Toast.LENGTH_SHORT).show();

                    return;
                }

                String registrationQuery = "";

                try {
                    registrationQuery = new BackgroundTask<>(() -> {
                        final UserDao userDao = MyDatabaseAccessor.getInstance(RegistrationActivity.this.getApplicationContext()).getUserDao();
                        final User usr = userDao.getUserByUsername(user);

                        if (usr != null && usr.getUsername().equals(user))
                            return "Username already in use...";
                        else if (userDao.AddUser(new User(user, pwd, mail, System.currentTimeMillis())) > 0)
                            return "";

                        return "Error: cannot register...";
                    }, RegistrationActivity.this.findViewById(R.id.progress_bar), signUp, username, password, confirmPassword, email, signIn).execute().get();
                } catch (final ExecutionException | InterruptedException exception) {
                    exception.printStackTrace();
                }

                if (registrationQuery.isEmpty()) {
                    Toast.makeText(RegistrationActivity.this, "Registration successful!", Toast.LENGTH_LONG).show();
                    RegistrationActivity.this.startActivity(moveToLogin);
                } else
                    Toast.makeText(RegistrationActivity.this, registrationQuery, Toast.LENGTH_LONG).show();
            }
        });
        signIn.setOnClickListener(listener -> startActivity(moveToLogin));
    }
}
