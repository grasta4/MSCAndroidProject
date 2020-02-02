package com.example.msc.ui.settings;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.msc.R;
import com.example.msc.persistence.MyDatabaseAccessor;
import com.example.msc.persistence.dao.UserDao;
import com.example.msc.persistence.entities.User;
import com.example.msc.util.BackgroundTask;
import com.example.msc.util.Encryptor;
import java.util.concurrent.ExecutionException;

public class PasswordChangeFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_password_change, container, false);
        final Button confirm = rootView.findViewById(R.id.confirm);
        final EditText pwd = rootView.findViewById(R.id.password), confirm_pwd = rootView.findViewById(R.id.confirm_password);

        confirm.setOnClickListener((view) -> {
            final String pwd_str = pwd.getText().toString().trim();

            if(pwd_str.equals(confirm_pwd.getText().toString().trim())) {
                String updatePwdQuery = "";

                try {
                    updatePwdQuery = new BackgroundTask<>(() -> {
                        final UserDao userDao = MyDatabaseAccessor.getInstance(getContext()).getUserDao();
                        final User usr = userDao.getUserByUsername(SettingsActivity.U_NAME);

                        if(usr == null)
                            return "User not found...";

                        return userDao.UpdateUser(new User(usr.getUsername(), Encryptor.encrypt(pwd_str), usr.getEmail(), usr.getRegistered())) > 0 ? "" : "Database error";
                    }, rootView.findViewById(R.id.progress_bar), confirm, pwd, confirm_pwd).execute().get();
                } catch (final ExecutionException | InterruptedException exception) {
                    exception.printStackTrace();
                }

                Toast.makeText(getContext(), (updatePwdQuery.isEmpty()) ? "Password changed!" : updatePwdQuery, Toast.LENGTH_LONG).show();
            }
        });

        return rootView;
    }
}
