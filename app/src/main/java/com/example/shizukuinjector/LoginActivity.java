package com.example.shizukuinjector;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText etAccessKey;
    private Button btnLogin;
    private static final String CORRECT_KEY = "KILLUA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etAccessKey = findViewById(R.id.etAccessKey);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(v -> {
            String enteredKey = etAccessKey.getText().toString().trim();

            if (enteredKey.equals(CORRECT_KEY)) {
                // Login bem-sucedido
                Toast.makeText(this, "Acesso concedido!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); // Fecha a tela de login para não voltar nela
            } else {
                // Chave incorreta
                Toast.makeText(this, "Chave de acesso incorreta!", Toast.LENGTH_SHORT).show();
                etAccessKey.setError("Chave inválida");
            }
        });
    }
}
