package com.example.cinesmart_taoufik;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cinesmart_taoufik.api.AuthApi;
import com.example.cinesmart_taoufik.models.AuthRequest;
import com.example.cinesmart_taoufik.models.AuthResponse;
import com.example.cinesmart_taoufik.utils.SessionManager;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private AuthApi authApi;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sessionManager = new SessionManager(this);
        
        if (sessionManager.getUserId() != null && sessionManager.getToken() != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }

        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        Button btnLogin = findViewById(R.id.btn_login);
        TextView tvGoToRegister = findViewById(R.id.tv_go_to_register);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:3000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        authApi = retrofit.create(AuthApi.class);

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            } else {
                loginUser(email, password);
            }
        });

        tvGoToRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }

    private void loginUser(String email, String password) {
        AuthRequest request = new AuthRequest(email, password);
        authApi.login(request).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AuthResponse authResponse = response.body();
                    
                    String id = authResponse.getUserId();
                    String token = authResponse.getToken();

                    if (id != null && token != null) {
                        sessionManager.saveSession(id, token);
                        
                        String name = authResponse.getUser() != null && authResponse.getUser().getName() != null ? authResponse.getUser().getName() : "Utilisateur";
                        Toast.makeText(LoginActivity.this, "Bienvenue " + name, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    } else {
                        Log.e("LoginError", "Champs manquants - ID: " + id + ", Token: " + (token != null ? "Présent" : "Nul"));
                        Toast.makeText(LoginActivity.this, "Données incomplètes : vérifiez les logs", Toast.LENGTH_LONG).show();
                    }
                } else {
                    String errorBody = "";
                    try {
                        if (response.errorBody() != null) errorBody = response.errorBody().string();
                    } catch (Exception e) { e.printStackTrace(); }
                    Log.e("LoginError", "Code: " + response.code() + " Error: " + errorBody);
                    Toast.makeText(LoginActivity.this, "Identifiants incorrects ou erreur serveur", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                Log.e("LoginError", t.getMessage());
                Toast.makeText(LoginActivity.this, "Erreur réseau : " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
