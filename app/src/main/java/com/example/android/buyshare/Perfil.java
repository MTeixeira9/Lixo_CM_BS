package com.example.android.buyshare;

import android.content.Intent;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Perfil extends AppCompatActivity {


    private ArrayAdapter<String> mAdapter;
    private TextView nomeTV, pwdTV, nTlm_TV, email_TV;
    private ImageView image;
    private String userTlm;
    private DatabaseReference mDatabaseUsers, mDatabaseFotos;
    private StorageReference mStorage;
    private ImageView imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        getSupportActionBar().setTitle("Meu Perfil");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Button alteraDados = (Button) findViewById(R.id.alterarDados);

        userTlm = getIntent().getStringExtra("userTlm");

        nomeTV = findViewById(R.id.nome_perfil);
        pwdTV = findViewById(R.id.pwd_perfil);
        nTlm_TV = findViewById(R.id.nTlm_perfil);
        email_TV = findViewById(R.id.email_perfil);
        imageView = findViewById(R.id.imageView_Perfil);

        //BASE DE DADOS
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference("users");
        mStorage = FirebaseStorage.getInstance().getReference("upload");

        mDatabaseFotos = FirebaseDatabase.getInstance().getReference("upload");

        /**
         * PREENCHER FOTO
         */
        Query q2 = mDatabaseFotos.child(userTlm);
        q2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {

                    String name = String.valueOf(dataSnapshot.child("name").getValue());
                    String url = String.valueOf(dataSnapshot.child("imageUrl").getValue());

                    // Reference to an image file in Cloud Storage
                    StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(name);
                   // Toast.makeText(getApplicationContext(), storageReference.url+"", Toast.LENGTH_LONG).show();

                    // Load the image using Glide
                    Glide.with(Perfil.this)
                            .load(url)
                            .into((ImageView) findViewById(R.id.imageView_Perfil));
                }
            //}

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        /**
         * PREENCHER DADOS DA PESSOA
         */
        Query q = mDatabaseUsers.orderByChild("numeroTlm").equalTo(userTlm);
        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {

                    //Valores da base de dados
                    String nTelemovel = String.valueOf(singleSnapshot.child("numeroTlm").getValue());
                    String nome = String.valueOf(singleSnapshot.child("nome").getValue());
                    String pass = String.valueOf(singleSnapshot.child("password").getValue());
                    String email = String.valueOf(singleSnapshot.child("email").getValue());

                    nomeTV.setText(nome);
                    pwdTV.setText(pass);
                    nTlm_TV.setText(nTelemovel);
                    email_TV.setText(email);


                    //String url = mDatabase2.child(userTlm).child("imageUrl").toString();
                    //mStorage.child(url);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        alteraDados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Perfil.this, EditPerfil.class);
                i.putExtra("userTlm", userTlm);
                startActivity(i);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(Perfil.this, MinhasListas.class);
        i.putExtra("userTlm", userTlm);
        startActivity(i);
    }

}
