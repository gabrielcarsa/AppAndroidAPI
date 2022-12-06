package com.example.provajava;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {


    ArrayList<Country> countryArrayList;
    ArrayList<Country> countryArrayListCopia;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    boolean salvos = true;

    //construtor
    public RecyclerAdapter(ArrayList<Country> countryArrayList, boolean salvos) {
        this.countryArrayList = countryArrayList;
        this.salvos = salvos;
        //duplicar o array original no construtor para manipularmos a lista sem problemas.
        countryArrayListCopia = new ArrayList<>(countryArrayList);
    }

    //inner class do viewholder
    //funciona mais ou menos como a onCreate da MainActivity
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView mTextViewNome;
        TextView mTextViewRegiao;
        ImageView mImageView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextViewNome = itemView.findViewById(R.id.textViewNome);
            mTextViewRegiao = itemView.findViewById(R.id.textViewRegiao);
            mImageView = itemView.findViewById(R.id.imageView);

            itemView.setOnClickListener(this);
        }

       @Override
        public void onClick(View view) {

            iniciarFirebase();


            if (salvos == false){
                AlertDialog.Builder janela = new AlertDialog.Builder(view.getContext());
                janela.setTitle("Adicionar " + countryArrayList.get(getLayoutPosition()).getName() + "?");
                janela.setMessage("Deseja adicionar " + countryArrayList.get(getLayoutPosition()).getName() +
                        " a sua lista de destinos para viajar?");
                janela.setIcon(R.drawable.ic_travel);
                //janela.setNeutralButton("Botão ok", null);
                janela.setPositiveButton("Adicionar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        Country c = new Country(countryArrayList.get(getLayoutPosition()).getName(),
                                countryArrayList.get(getLayoutPosition()).getRegion(),
                                countryArrayList.get(getLayoutPosition()).getFlag());

                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        databaseReference.child("Destinos").child(user.getUid()).
                                child(countryArrayList.get(getLayoutPosition()).getName()).setValue(c);
                        Toast.makeText(view.getContext(), "Adicionado com sucesso ", Toast.LENGTH_SHORT).show();

                    }
                });

                janela.setNegativeButton("Cancelar", null);
                janela.show();
            }else{
                AlertDialog.Builder janela = new AlertDialog.Builder(view.getContext());
                janela.setTitle("Remover " + countryArrayList.get(getLayoutPosition()).getName() + "?");
                janela.setMessage("Deseja remover " + countryArrayList.get(getLayoutPosition()).getName() +
                        " de sua lista de destinos para viajar?");
                janela.setIcon(R.drawable.ic_delete);
                //janela.setNeutralButton("Botão ok", null);
                janela.setPositiveButton("Remover", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        removeAt(getLayoutPosition());
                        Toast.makeText(view.getContext(), "Removido com sucesso ", Toast.LENGTH_SHORT).show();
                    }

                });
                janela.setNegativeButton("Cancelar", null);
                janela.show();

            }

            //Toast.makeText(view.getContext(), "posição= " + getLayoutPosition(), Toast.LENGTH_SHORT).show();
            //removeAt(getLayoutPosition());
        }

        private void removeAt(int layoutPosition) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            Country f = countryArrayList.get(layoutPosition);

            //importante: sem esta linha o array não é atualizado corretamente
            //limpa o array para correta renderização da lista
            //após remoção, array será remontado com os valores restantes do firebase
            countryArrayList.clear();

            databaseReference.child("Destinos").child(user.getUid()).
                    child(f.getName()).
                    removeValue();

        }
    }


    @NonNull
    @Override
    public RecyclerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //método para inflar o xml dos itens
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_items, parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapter.MyViewHolder holder, int position) {
        //método para pegar o valor e jogar nas views da tela;
        String nome = countryArrayList.get(position).getName();
        String regiao = countryArrayList.get(position).getRegion();
        String imagem = countryArrayList.get(position).getFlag();


        holder.mTextViewNome.setText(nome);
        holder.mTextViewRegiao.setText(regiao);
        Picasso.get().load(imagem).into(holder.mImageView);
        System.out.println(imagem);
        //holder.mImageView.setImageResource();

    }

    public void filtrar(String text) {
        //limpando array que monta a lista ao buscar algum termo na searchView
        countryArrayList.clear();

        //digitou algo e apagou = trazer todos
        //lembrando que filmeArrayListCopia contém toda a informação original
        //(populado no construtor)
        if (text.isEmpty()) {
            countryArrayList.addAll(countryArrayListCopia);
        } else {
            //algum texto digitado na busca
            //converte para letra minúscula para não haver distinção
            text = text.toLowerCase();
            //percorre o array com os dados originais (todos os favoritos)
            for (Country item : countryArrayListCopia) {
                //caso, nos dados originais, exista o termo procurado, popule o array vazio com o item
                if (item.getName().toLowerCase().contains(text)) {
                    countryArrayList.add(item);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        //tamanho da lista de itens
        return countryArrayList.size();
    }

    private void iniciarFirebase() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }


}
