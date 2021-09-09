package com.levent.rindex.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.levent.rindex.R;
import com.levent.rindex.ccs.SavedSettings;
import com.levent.rindex.models.SingleResponse;
import com.levent.rindex.models.Token;
import com.levent.rindex.models.User;
import com.levent.rindex.services.AccountService;
import com.levent.rindex.services.BackgroundLocationService;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    TextView mailField;
    TextView passField;
    private TextToSpeech tts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button loginButton = findViewById(R.id.login);
        mailField = findViewById(R.id.usermail);
        passField = findViewById(R.id.userpass);

        loadFromCache();
        askForSpeakingMode();

        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.FOREGROUND_SERVICE},1001);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryLogin(mailField.getText().toString(),passField.getText().toString());
            }
        });

    }

    private void askForSpeakingMode(){
        tts = new TextToSpeech(LoginActivity.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status == TextToSpeech.SUCCESS){
                    int result = tts.setLanguage(new Locale("tr", "TR"));
                    if(result == TextToSpeech.LANG_NOT_SUPPORTED){
                        String text = getString(R.string.enable_tts);
                        Toast.makeText(LoginActivity.this,"No support for device language!",Toast.LENGTH_SHORT).show();
                        tts.speak(text,TextToSpeech.QUEUE_ADD,null);
                    }
                }
            }
            
        });
    }

    private void loadFromCache(){
        mailField.setText(SavedSettings.get("User_Mail",LoginActivity.this));
        passField.setText(SavedSettings.get("User_Password",LoginActivity.this));
    }

    private void tryLogin(String mail,String password){
        AccountService accountService = new AccountService();
        Call<SingleResponse<Token>> call = accountService.login(mail,password,SavedSettings.get("Notification_Token",LoginActivity.this));
        call.enqueue(new Callback<SingleResponse<Token>>() {
            @Override
            public void onResponse(Call<SingleResponse<Token>> call, Response<SingleResponse<Token>> response) {
                if(response.isSuccessful()){
                    SavedSettings.set("Auth_Token","Bearer "+response.body().data.token,LoginActivity.this);
                    Log.e("Set Token!","Bearer: "+response.body().data.token);
                    showRememberMeDialog(response.body().data.user);
                }
                else{
                    Snackbar.make(findViewById(R.id.linearLayout),"Giriş Başarısız!",Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<SingleResponse<Token>> call, Throwable t) {
                Snackbar.make(findViewById(R.id.linearLayout),"Giriş Başarısız!",Snackbar.LENGTH_LONG).show();
                try {
                    throw t;
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        });
    }

    private void showRememberMeDialog(User user){
        new MaterialAlertDialogBuilder(LoginActivity.this)
                .setTitle("Hey!")
                .setMessage(getString((R.string.remind_me)))
                .setPositiveButton(getString(R.string.save), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SavedSettings.set("User_Mail",mailField.getText().toString(),LoginActivity.this);
                        SavedSettings.set("User_Password",passField.getText().toString(),LoginActivity.this);
                        startApplication(user);
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startApplication(user);
                    }
                })
                .show()
        .setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                startApplication(user);
            }
        });
    }

    private void startApplication(User startUser){
        Intent intent = new Intent(this,MainActivity.class);
        intent.putExtra("User",startUser);
        startActivity(intent);
    }
}