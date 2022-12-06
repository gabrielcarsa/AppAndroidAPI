package com.example.provajava;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivityCriarConta extends AppCompatActivity {

    FirebaseAuth mAuth;
    EditText nome, email, senha;
    Button criar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_criar_conta);

        mAuth= FirebaseAuth.getInstance();

        email = findViewById(R.id.editTextCriarEmail);
        senha = findViewById(R.id.editTextCriarSenha);
        criar = findViewById(R.id.buttonCriar);
        nome = findViewById(R.id.editTextNome);

        criar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String em = email.getText().toString();
                String se = senha.getText().toString();

                if(logar()){
                    mAuth.createUserWithEmailAndPassword(em, se).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(getApplicationContext(), "Usuário " + "criado com sucesso", Toast.LENGTH_LONG).show();

                                Intent i = new Intent(
                                        MainActivityCriarConta.this,
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
        if(senha.getText().toString().trim().equals("")){
            senha.setError("Preencha.");
            senha.requestFocus();
            erro = true;
        }
        if(nome.getText().toString().trim().equals("")){
            nome.setError("Preencha.");
            nome.requestFocus();
            erro = true;
        }
        if(erro == false)
            return true;
        else
            return false;

    }
}