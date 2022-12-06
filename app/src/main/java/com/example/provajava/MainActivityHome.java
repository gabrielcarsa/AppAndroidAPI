package com.example.provajava;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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



public class MainActivityHome extends AppCompatActivity {

    //campo de busca
    SearchView searchView;

    Button btMudarTela;

    FirebaseAuth mAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    RecyclerView recyclerView;
    ArrayAdapter<Country> meuAdapter;
    ArrayList<Country> countryArrayList_ = new ArrayList<Country>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_home);

        searchView = findViewById(R.id.searchView01);
        btMudarTela = findViewById(R.id.buttonTelaCadastrar);
        mAuth= FirebaseAuth.getInstance();
        recyclerView = findViewById(R.id.recyclerView01);


        Tarefa tarefa = new Tarefa();
        tarefa.execute("https://restcountries.com/v2/all");
        iniciarFirebase();

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