package com.example.cinesmart_taoufik;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cinesmart_taoufik.api.AuthApi;
import com.example.cinesmart_taoufik.models.AuthRequest;
import com.example.cinesmart_taoufik.models.AuthResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPassword;
    private AuthApi authApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etName = findViewById(R.id.et_register_name);
        etEmail = findViewById(R.id.et_register_email);
        etPassword = findViewById(R.id.et_register_password);
        Button btnRegister = findViewById(R.id.btn_register);
        TextView tvGoToLogin = findViewById(R.id.tv_go_to_login);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:3000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        authApi = retrofit.create(AuthApi.class);

        btnRegister.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            } else {
                registerUser(name, email, password);
            }
        });

        tvGoToLogin.setOnClickListener(v -> finish());
    }

    private void registerUser(String name, String email, String password) {
        AuthRequest request = new AuthRequest(email, password, name);
        authApi.register(request).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, "Compte créé ! Connectez-vous.", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    try {
                        // On essaie de lire le message d'erreur envoyé par le serveur Node.js
                        String errorBody = response.errorBody().string();
                        // Si le serveur a renvoyé { "error": "message" }, on pourrait le parser, 
                        // sinon on affiche le corps brut pour déboguer.
                        Toast.makeText(RegisterActivity.this, "Erreur : " + errorBody, Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(RegisterActivity.this, "Erreur inconnue : " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, "Échec connexion : " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
