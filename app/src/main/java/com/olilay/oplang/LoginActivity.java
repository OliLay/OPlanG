package com.olilay.oplang;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;

/**
 * Created by Oliver Layer on 20.08.2015.
 */
public class LoginActivity extends AppCompatActivity {

    private EditText tb_LoginName;
    private EditText tb_LoginPassword;
    private CheckBox cb_AutoLogin;
    private TextView txt_LoginFailure;
    private ProgressBar pg_Login;
    private Button btn_Login;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tb_LoginName = (EditText)findViewById(R.id.tb_LoginName);
        tb_LoginPassword = (EditText)findViewById(R.id.tb_LoginPassword);
        cb_AutoLogin = (CheckBox)findViewById(R.id.cb_AutoLogin);
        txt_LoginFailure = (TextView)findViewById(R.id.txt_LoginFailure);
        pg_Login = (ProgressBar)findViewById(R.id.pg_Login);
        btn_Login = (Button)findViewById(R.id.btn_Login);

        readLoginData();
    }

    public void onButtonLoginClicked(View v)
    {
        Login(true);
    }


    private void Login(boolean checkForTeacher)
    {
        txt_LoginFailure.setVisibility(View.INVISIBLE);
        pg_Login.setVisibility(View.VISIBLE);
        btn_Login.setEnabled(false);

        if(cb_AutoLogin.isChecked())
        {
            LoginHelper.saveLogin(this, tb_LoginName.getText().toString(), tb_LoginPassword.getText().toString());
        }
        else
        {
            LoginHelper.deleteLogin(this);
        }

        user = new User(tb_LoginName.getText().toString(), this);
        user.createSession(tb_LoginPassword.getText().toString(), checkForTeacher);
    }


    private void readLoginData()
    {
        List<String> data = LoginHelper.getLogin(this);

        if(data != null)
        {
            tb_LoginName.setText(data.get(0));
            tb_LoginPassword.setText(data.get(1));
            cb_AutoLogin.setChecked(true);

            Login(false);
        }
    }

    public void onLoginCompleted()
    {
        pg_Login.setVisibility(View.INVISIBLE);
        btn_Login.setEnabled(true);
        user.setIsTeacher(Settings.getIsTeacher(this));

        Intent intent = new Intent(this, PlanActivity.class);
        intent.putExtra("Cookie", user.getSession().getCookie());
        intent.putExtra("wholeName", user.getWholeName());
        intent.putExtra("givenName", user.getGivenName());
        intent.putExtra("lastName", user.getLastName());
        intent.putExtra("isTeacher", user.isTeacher());

        startActivity(intent);
    }

    public void onLoginFailed()
    {
        txt_LoginFailure.setVisibility(View.VISIBLE);
        pg_Login.setVisibility(View.INVISIBLE);
        btn_Login.setEnabled(true);
    }
}
