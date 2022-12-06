package com.example.provajava;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SearchView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;


public class MainActivityHome extends AppCompatActivity {

    //campo de busca
    SearchView searchView;

    Button btMudarTela;

    FirebaseAuth mAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    RecyclerAdapter recyclerAdapter;
    RecyclerView recyclerView;
    ArrayList<Country> countryArrayList_ = new ArrayList<Country>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_home);

        searchView = findViewById(R.id.searchView01);
        btMudarTela = findViewById(R.id.buttonTelaCadastrar);
        mAuth= FirebaseAuth.getInstance();
        recyclerView = findViewById(R.id.recyclerView01);
        Handler handler = new Handler();

        //searchView aberto
        searchView.setIconified(false);
        //retira o foco automático e fecha o teclado ao iniciar a aplicação
        searchView.clearFocus();

        Tarefa tarefa = new Tarefa();
        tarefa.execute("https://restcountries.com/v2/all");
        iniciarFirebase();

        //ao término do método setInto, com o arraylist populado, montar RecyclerView e Adapter
        setAdapter();

        btMudarTela.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(
                        MainActivityHome.this,
                        MainActivitySalvos.class
                );

                startActivity(i);
            }
        });

        /*
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            //método para submit
            @Override
            public boolean onQueryTextSubmit(String s) {
                try {
                    //executamos novamente o método setInfo(), agora com o termo da busca
                    //isto vai gerar uma nova consulta com uma nova URL e
                    //um novo conjunto de dados no arraylist
                    setInfo(s);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //"avisar" o adapter para atualização da RecyclerView
                recyclerAdapter.notifyDataSetChanged();

                return true;
            }

            //método ao alterar o texto - similar ao TextWatcher
            @Override
            public boolean onQueryTextChange(String s) {
                //considerar que aqui cada caractere digitado = uma requisição na API
                //a rotina abaixo espera a digitação do usuário para as requisições (400 milisegundos)

                handler.removeCallbacksAndMessages(null);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // código igual ao submit acima:
                        try {
                            setInfo(s);
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        recyclerAdapter.notifyDataSetChanged();
                    }
                }, 400);

                return true;
            }
        });*/

    }

    private void setInfo(String busca) throws ExecutionException, InterruptedException {

        //Obs: busca = 3 caracteres no mínimo. Regra da API.

        //se o campo de busca for alterado e limpo na sequência, considerar valor padrão "movies"
        if (busca.trim().equals(""))
            busca = "";

        //limpa o array
        countryArrayList_.clear();

        //monta a url de busca com a key
        //String url = "https://www.omdbapi.com/?s=" + busca + "&apikey=fce85cc5";

        //executa a classe DownloadDados, que retorna o array populado e popula o array local
        //get() = executando de forma assíncrona, ou seja, aguarda a consulta e os dados
        //para popular o array

        //countryArrayList_.addAll(new Tarefa().execute(url).get());


    }

    private class Tarefa extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... strings) {
            String retorno = Conexao.getDados(strings[0]);
            return retorno;
        }

        @Override
        protected void onPostExecute(String s) {

           try {
                JSONArray jsonArray = new JSONArray(s);
                JSONObject jsonObject = null;

                for (int i = 0;i < jsonArray.length();i++){
                    jsonObject = jsonArray.getJSONObject(i);
                    countryArrayList_.add(new Country(jsonObject.getString("name"), jsonObject.getString("region"), jsonObject.getJSONObject("flags").getString("png")));

                }
            }catch (Exception e){
                e.printStackTrace();
            }

            setAdapter();
        }
    }

    private void setAdapter() {
        RecyclerAdapter recyclerAdapter = new RecyclerAdapter(countryArrayList_, false);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());

        //início exemplo de swipe para excluir um item
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(
                0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT | ItemTouchHelper.UP | ItemTouchHelper.DOWN) {

            @Override
            public boolean onMove(RecyclerView recyclerView,
                                  RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                //remove o item da lista e atualiza o RecyclerView
                int position = viewHolder.getAdapterPosition();
                countryArrayList_.remove(position);
                recyclerAdapter.notifyDataSetChanged();

            }

        };

        ItemTouchHelper touchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        touchHelper.attachToRecyclerView(recyclerView);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(recyclerAdapter);

    }

    private void iniciarFirebase() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.setPersistenceEnabled(true);
        databaseReference = firebaseDatabase.getReference();
    }
}