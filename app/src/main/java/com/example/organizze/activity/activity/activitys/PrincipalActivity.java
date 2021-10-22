package com.example.organizze.activity.activity.activitys;

import android.content.Intent;
import android.os.Bundle;


import com.example.organizze.activity.activity.adapter.AdapterMovimentacao;
import com.example.organizze.activity.activity.config.ConfiguracaoFireBase;
import com.example.organizze.activity.activity.helper.Base64Custon;
import com.example.organizze.activity.activity.model.Movimentacao;
import com.example.organizze.activity.activity.model.Usuario;
import com.example.organizze.databinding.ActivityPrincipalBinding;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.organizze.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class PrincipalActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityPrincipalBinding binding;
    private TextView textoSaudacao, textoSaldo;
    private MaterialCalendarView calendarView ;
    private FirebaseAuth autenticacao = ConfiguracaoFireBase.getFirebaseAutenticacao();
    private DatabaseReference fireBaseRef = ConfiguracaoFireBase.getFirebaseDataBase();
    private Double despesaTotal = 0.0;
    private Double receitaTotal = 0.0;
    private Double resumoTotal = 0.0;
    private DatabaseReference usuarioRef;
    private ValueEventListener valueEventListenerUsuario;
    private RecyclerView recyclerMovimentos;
    private AdapterMovimentacao adapterMovimentacao;
    private List<Movimentacao> movimentacaoList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityPrincipalBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.toolbar.setTitle("Organizze");
        setSupportActionBar(binding.toolbar);
        textoSaldo = findViewById(R.id.textSaldo);
        textoSaudacao = findViewById(R.id.textSaudacao);
        calendarView = findViewById(R.id.calendarView);
        recyclerMovimentos = findViewById(R.id.recyclerMovimentos);
        configuraCalendarView();


        //Configurar adpter
        adapterMovimentacao = new AdapterMovimentacao(movimentacaoList, this);

        //Configurar recycler
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerMovimentos.setLayoutManager(layoutManager );
        recyclerMovimentos.setHasFixedSize(true);
       recyclerMovimentos.setAdapter( adapterMovimentacao);


    }

    @Override
    protected void onStart() {
        super.onStart();
        recuperarResumo();
    }

    public  void recuperarResumo(){

        String emailUsuario = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custon.codificarBase64(emailUsuario);
        usuarioRef = fireBaseRef.child("usuarios").child(idUsuario);

       valueEventListenerUsuario =  usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Usuario usuario = snapshot.getValue(Usuario.class);

                despesaTotal = usuario.getDespesaTotal();
                receitaTotal = usuario.getReceitaTotal();
                resumoTotal = receitaTotal - despesaTotal;

                DecimalFormat decimalFormat = new DecimalFormat("0.##");
                String resultadoFormatado = decimalFormat.format( resumoTotal);


                textoSaudacao.setText("Olá," + usuario.getNome());
                textoSaldo.setText("R$ " + resultadoFormatado);


            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

            }
        });

    }


    //exibir menu

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_principal,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menuSair:
                autenticacao.signOut();
                startActivity(new Intent(this, MainActivity.class));
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void adcionarDespesa(View view){
        startActivity(new Intent(this, DespesasActivity.class));

    }

    public void adcionarReceita(View view){
        startActivity(new Intent(this, ReceitasActivity.class));


    }

    public void configuraCalendarView(){

        CharSequence meses [] = {"Janeiro", "Fevereiro","Março","Abril","Maio","Junho","Julho","Agosto","Setembro","Agosto","Setembro","Outubro","Novembro","Dezembro"};
        calendarView.setTitleMonths(meses);
        calendarView.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {

            }
        });


    }

    @Override
    protected void onStop() {
        super.onStop();
        usuarioRef.removeEventListener(valueEventListenerUsuario);
    }
}