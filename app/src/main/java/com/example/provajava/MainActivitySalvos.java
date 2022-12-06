package com.example.provajava;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class MainActivitySalvos extends AppCompatActivity {

    Button btLogar;
    FirebaseAuth mAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    TextView txt;

    RecyclerView recyclerView;
    ArrayAdapter<Country> meuAdapter;
    ArrayList<Country> countryArrayList_ = new ArrayList<Country>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_salvos);

        btLogar = findViewById(R.id.buttonLogout);
        txt=findViewById(R.id.textView6);
        recyclerView = findViewById(R.id.recyclerView02);

        iniciarFirebase();

        //Sair
        btLogar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.getInstance().signOut();

                Intent i = new Intent(
                        MainActivitySalvos.this,
                        MainActivity.class
                );

                startActivity(i);
            }

        });

        //databaseReference = firebaseDatabase.getReference("Destinos");
        databaseReference.child("Destinos")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot != null) {
                            for(DataSnapshot objDataSnapshot : dataSnapshot.getChildren()){
                                Country a = objDataSnapshot.getValue(Country.class);
                                countryArrayList_.add(a);
                            }
                        }

                        setAdapter();
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }


    private void setAdapter() {

        RecyclerAdapter recyclerAdapter = new RecyclerAdapter(countryArrayList_, true);

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
        databaseReference = firebaseDatabase.getReference();
    }
}