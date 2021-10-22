package com.example.organizze.activity.activity.activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.organizze.R;
import com.example.organizze.activity.activity.config.ConfiguracaoFireBase;
import com.example.organizze.activity.activity.helper.Base64Custon;
import com.example.organizze.activity.activity.helper.DateCuston;
import com.example.organizze.activity.activity.model.Movimentacao;
import com.example.organizze.activity.activity.model.Usuario;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class ReceitasActivity extends AppCompatActivity {

    private TextInputEditText campoData, campoCategoria, campoDescricao;
    private EditText campoValor;
    private Movimentacao movimentacao;
    private DatabaseReference fireBaseRef = ConfiguracaoFireBase.getFirebaseDataBase();
    private FirebaseAuth autenticacao = ConfiguracaoFireBase.getFirebaseAutenticacao();
    private Double receitaTotal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receitas);

        ids();


        //Preenche o campo data com data atual
        campoData.setText(DateCuston.dataAtual());
        recuperarReceitaTotal();


    }

    public void salvarReceita(View view) {

        if (validarReceita()) {

            String data = campoData.getText().toString();
            Double valorRecuperado = Double.parseDouble(campoValor.getText().toString());
            movimentacao = new Movimentacao();
            movimentacao.setValor(valorRecuperado);
            movimentacao.setCategoria(campoCategoria.getText().toString());
            movimentacao.setDescricao(campoDescricao.getText().toString());
            movimentacao.setData(campoData.getText().toString());
            movimentacao.setTipo("d");


            Double receitaAtualizada = receitaTotal + valorRecuperado;
            atualizarReceita(receitaAtualizada);


            movimentacao.salvar(data);

            finish();

        }


    }

    public Boolean validarReceita() {

        String textValor = campoValor.getText().toString();
        String textData = campoData.getText().toString();
        String textCategoria = campoCategoria.getText().toString();
        String textDescricao = campoDescricao.getText().toString();

        if (!textValor.isEmpty()) {
            if (!textData.isEmpty()) {
                if (!textCategoria.isEmpty()) {
                    if (!textDescricao.isEmpty()) {
                        return true;

                    } else {
                        Toast.makeText(ReceitasActivity.this, "Preencha a descrição", Toast.LENGTH_SHORT).show();
                        return false;
                    }

                } else {
                    Toast.makeText(ReceitasActivity.this, "Preencha a categoria", Toast.LENGTH_SHORT).show();
                    return false;
                }

            } else {
                Toast.makeText(ReceitasActivity.this, "Preencha a data", Toast.LENGTH_SHORT).show();
                return false;
            }

        } else {
            Toast.makeText(ReceitasActivity.this, "Preencha o valor", Toast.LENGTH_SHORT).show();
            return false;
        }


    }

    public void recuperarReceitaTotal() {
        String emailUsuario = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custon.codificarBase64(emailUsuario);
        DatabaseReference usuarioRef = fireBaseRef.child("usuarios").child(idUsuario);

        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Usuario usuario = snapshot.getValue(Usuario.class);
                receitaTotal = usuario.getReceitaTotal();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

    public void atualizarReceita(Double receita) {
        String emailUsuario = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custon.codificarBase64(emailUsuario);
        DatabaseReference usuarioRef = fireBaseRef.child("usuarios").child(idUsuario);

        usuarioRef.child("receitaTotal").setValue(receita);
    }

    public void ids(){

        campoData=findViewById(R.id.editDataReceita);
        campoCategoria=findViewById(R.id.editCategoriaReceita);
        campoDescricao=findViewById(R.id.editDescricaoReceita);
        campoValor=findViewById(R.id.editValorReceita);

    }
}



