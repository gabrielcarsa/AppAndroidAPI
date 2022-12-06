package com.example.provajava;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivityRecuperarSenha extends AppCompatActivity {

    FirebaseAuth mAuth;
    EditText email;
    Button reset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_recuperar_senha);

        email = findViewById(R.id.editTextEmailReset);
        reset = findViewById(R.id.buttonReset);

        mAuth = FirebaseAuth.getInstance();

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String em = email.getText().toString();

                if(logar()){
                    mAuth.sendPasswordResetEmail(em).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                                Intent i = new Intent(
                                        MainActivityRecuperarSenha.this,
                                        MainActivity.class
                                );

                                startActivity(i);
                            }else{
                                Toast.makeText(getApplicationContext(), "Erro", Toast.LENGTH_LONG).show();

                            }
                        }
                    });
                }
            }
        });
    }
    private boolean logar(){
        boolean erro = false;
        if(email.getText().toString().trim().equals("")|| !Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()){
            email.setError("Preencha corretamente.");
            email.requestFocus();
            erro = true;
        }

        if(erro == false)
            return true;
        else
            return false;

    }
}