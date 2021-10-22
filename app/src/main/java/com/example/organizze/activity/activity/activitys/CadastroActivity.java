package com.example.organizze.activity.activity.activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.organizze.R;
import com.example.organizze.activity.activity.config.ConfiguracaoFireBase;
import com.example.organizze.activity.activity.helper.Base64Custon;
import com.example.organizze.activity.activity.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class CadastroActivity extends AppCompatActivity {

    private EditText campoNome, campoEmail, campoSenha;
    private Button buttonCadastrar;
    private FirebaseAuth autenticacao;
    private Usuario usuario;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        //Mudar titulo da toolBar
        //getSupportActionBar().setTitle("Cadastro");



        ids();

        buttonCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String textoNome = campoNome.getText().toString();
                String textoEmail = campoEmail.getText().toString();
                String textoSenha = campoSenha.getText().toString();

                //Validação

                if (!textoNome.isEmpty()) {
                    if (!textoEmail.isEmpty()) {
                        if (!textoSenha.isEmpty()) {

                            usuario = new Usuario();
                            usuario.setNome(textoNome);
                            usuario.setEmail(textoEmail);
                            usuario.setSenha(textoSenha);
                            cadastrarUsuario();

                        } else {
                            Toast.makeText(CadastroActivity.this, "Preencha a senha", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(CadastroActivity.this, "Preencha o Email", Toast.LENGTH_SHORT).show();

                    }


                } else {
                    Toast.makeText(CadastroActivity.this, "Preencha o nome", Toast.LENGTH_SHORT).show();

                }


            }
        });


    }

    public void cadastrarUsuario() {

        autenticacao = ConfiguracaoFireBase.getFirebaseAutenticacao();

        autenticacao.createUserWithEmailAndPassword(
                usuario.getEmail(),
                usuario.getSenha()
        ).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> task) {
                if (task.isSuccessful()) {


                    String idUsuario = Base64Custon.codificarBase64(usuario.getEmail());
                    usuario.setIdUsuario(idUsuario);
                    usuario.salvar();
                    finish();

                    Toast.makeText(CadastroActivity.this, "Sucesso ao cadastrar", Toast.LENGTH_SHORT).show();


                } else {

                    String exececao = "";
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e) {
                        exececao = "Digite uma senha mais forte";

                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        exececao = "Por favor, digite uma emial válido";
                    } catch (FirebaseAuthUserCollisionException e) {
                        exececao = "Usuario já cadastrado";
                    } catch (Exception e) {
                        exececao = "Erro ao cadastrar: " + e.getMessage();
                        e.printStackTrace();
                    }

                    Toast.makeText(CadastroActivity.this, exececao, Toast.LENGTH_SHORT).show();
                }

            }
        });


    }


    public void ids() {

        campoNome = findViewById(R.id.editNome);
        campoEmail = findViewById(R.id.editEmail);
        campoSenha = findViewById(R.id.editSenha);
        buttonCadastrar = findViewById(R.id.buttonCadastrar);

    }

}