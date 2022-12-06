package com.example.provajava;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class MainActivity extends AppCompatActivity {

    EditText edEmail, edSenha;
    TextView criar, esqueci;
    Button btLogar;
    FirebaseAuth mAuth;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edEmail = findViewById(R.id.editTextEmailReset);
        edSenha = findViewById(R.id.txtInputSenha);
        btLogar = findViewById(R.id.buttonEntrar);
        criar = findViewById(R.id.textViewCriarConta);
        esqueci = findViewById(R.id.textViewEsqueci);
        progressBar = findViewById(R.id.progressBarLogin);

        mAuth= FirebaseAuth.getInstance();

        progressBar.setVisibility(View.INVISIBLE);

        btLogar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = edEmail.getText().toString();
                String senha = edSenha.getText().toString();

                if(logar())
                {
                    progressBar.setVisibility(View.VISIBLE);

                    mAuth.signInWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                btLogar.setEnabled(false);

                                Intent i = new Intent(
                                        MainActivity.this,
                                        MainActivityHome.class
                                );

                                startActivity(i);
                            }
                        }
                    });

                }
                //progressBar.setVisibility(View.GONE);

            }
        });

        esqueci.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(
                        MainActivity.this,
                        MainActivityRecuperarSenha.class
                );

                startActivity(i);
            }
        });

        criar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(
                        MainActivity.this,
                        MainActivityCriarConta.class
                );

                startActivity(i);
            }
        });
    }


    private boolean logar(){
        boolean erro = false;
        if(edEmail.getText().toString().trim().equals("")|| !Patterns.EMAIL_ADDRESS.matcher(edEmail.getText().toString()).matches()){
            edEmail.setError("Preencha corretamente.");
            edEmail.requestFocus();
            erro = true;
        }
        if(edSenha.getText().toString().trim().equals("")){
            edSenha.setError("Preencha.");
            edSenha.requestFocus();
            erro = true;
        }
        if(erro == false)
            return true;
        else
            return false;

    }
}