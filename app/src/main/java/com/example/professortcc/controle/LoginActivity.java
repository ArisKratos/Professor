package com.example.professortcc.controle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.professortcc.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText aliasEmailProf, aliasSenhaProf;
    private Button aliasBtnLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().setTitle("IFNotification: Professor");


        aliasEmailProf = findViewById(R.id.editEmailProf);
        aliasSenhaProf = findViewById(R.id.editSenhaProf);
        aliasBtnLogin = findViewById(R.id.buttonCadastrarProf);
        aliasBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String email = aliasEmailProf.getText().toString();
                String senha = aliasSenhaProf.getText().toString();

                if (email == null || email.isEmpty() || senha == null || senha.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Email, nome e senha devem ser preenchidos", Toast.LENGTH_SHORT).show();
                    return;
                }

                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, senha)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.i("ERRO", String.valueOf(task.isSuccessful()));

                                if(task.isSuccessful()) {
                                    Intent intent = new Intent(getApplicationContext(), ProfActivity.class);

                                    startActivity(intent);
                                }
                                else  {
                                    Toast.makeText(LoginActivity.this, "Authentication failed." ,Toast.LENGTH_SHORT).show();
                                }

                                aliasEmailProf.setText("");
                                aliasSenhaProf.setText("");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                Log.i("Teste" ,  e.getMessage());

                            }
                        });

            }
        });
    }
}
