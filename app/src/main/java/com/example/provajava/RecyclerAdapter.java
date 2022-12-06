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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Executors;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {


    ArrayList<Country> countryArrayList = new ArrayList<Country>();
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    boolean show = false;
    boolean salvos = true;

    //construtor
    public RecyclerAdapter(ArrayList<Country> countryArrayList, boolean salvos) {
        this.countryArrayList = countryArrayList;
        this.salvos = salvos;
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


                        databaseReference.
                                child("Destinos").
                                child(countryArrayList.get(getLayoutPosition()).getName()).
                                setValue(c);
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
                        String s = countryArrayList.get(getLayoutPosition()).getName();
                        databaseReference.child("Destinos").child(s).removeValue();
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

            countryArrayList.remove(layoutPosition);
            notifyItemRemoved(layoutPosition);
            notifyItemRangeChanged(layoutPosition, countryArrayList.size());

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
