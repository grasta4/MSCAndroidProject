package com.example.msc.ui.recovery;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.example.msc.R;
import com.example.msc.persistence.MyDatabaseAccessor;
import com.example.msc.persistence.entities.User;
import com.example.msc.ui.recovery.util.GMailSender;
import com.example.msc.ui.util.Validator;
import com.example.msc.util.BackgroundTask;

public class AccountRecoveryActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_recovery);

        final Button send = findViewById(R.id.send);
        final EditText user = findViewById(R.id.username);

        send.setOnClickListener(listener -> {
            final String str = user.getText().toString().trim();
            String errorMessage = Validator.validateUsername(str);

            if(errorMessage == null)
                try {
                    errorMessage = new BackgroundTask<>(() -> {
                        final User usr = MyDatabaseAccessor.getInstance(getApplicationContext()).getUserDao().getUserByUsername(str);

                        if(usr == null || !usr.getUsername().equals(str))
                            return "Error! Username not found...";

                        new Thread(() -> new GMailSender().sendMail("Account recovery", "Username: " + str + "\nPassword: " + usr.getPassword() + "\n", usr.getEmail())).start();

                        return "";
                    }, findViewById(R.id.progress_bar), send, user).execute().get();
                } catch (final Exception e) {
                    e.printStackTrace();
                }

            Toast.makeText(AccountRecoveryActivity.this, errorMessage == null || errorMessage.isEmpty() ? "You credentials have been sent to the email associated with your username..." : errorMessage, Toast.LENGTH_LONG).show();
        });
    }
}
