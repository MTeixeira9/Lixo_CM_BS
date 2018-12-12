package com.example.android.buyshare;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.buyshare.Database.Lista;
import com.example.android.buyshare.Database.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NovaLista extends AppCompatActivity {

    private ArrayAdapter<String> mAdapter;
    private ListView mShoppingList;
    private EditText mItemEdit;
    private String userTlm;
    private DatabaseReference mDatabase;
    private ArrayList<String> produtos;


    private static final String msgErrLista = "Tem de dar um nome à Lista!";
    private static final String msgErrAddProd = "Tem de inserir um produto!";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nova_lista);

        getSupportActionBar().setTitle("Nova Lista");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //ir buscar quem estah autenticado
        userTlm = getIntent().getStringExtra("userTlm");

        mDatabase = FirebaseDatabase.getInstance().getReference("listas");
        produtos = new ArrayList<>();

        Button guardarLista = (Button) findViewById(R.id.guardarLista);
        guardarLista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                EditText nomeL = (EditText) findViewById(R.id.nomeL);
                String nomeLista = nomeL.getText().toString();


                String key = "";

                if (!nomeLista.equals("")){

                    Lista lista = new Lista(userTlm, nomeLista, produtos);
                    key = mDatabase.push().getKey();

                    mDatabase.child(key).setValue(lista);


                    Bundle b = new Bundle();
                    b.putStringArrayList("listaProdutos", produtos);

                    Intent i = new Intent(NovaLista.this, MinhasListas.class);
                    i.putExtra("userTlm", userTlm);
                    i.putExtras(b);


                    startActivity(i);
                    finish();
                }
                else{
                    Toast.makeText(getApplicationContext(), msgErrLista, Toast.LENGTH_LONG).show();
                }






            }
        });

        mItemEdit = (EditText) findViewById(R.id.produtoInserido);
        mShoppingList = (ListView) findViewById(R.id.listViewItems);
        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);

        mShoppingList.setAdapter(mAdapter);





        ImageButton addProduto = (ImageButton) findViewById(R.id.addProdButton);
        addProduto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String item = mItemEdit.getText().toString();
                if (!item.equals("")) {
                    produtos.add(item);
                    mAdapter.add(item);
                    mAdapter.notifyDataSetChanged();
                    mItemEdit.setText("");
                }
                else{
                    Toast.makeText(getApplicationContext(), msgErrAddProd, Toast.LENGTH_LONG).show();
                }





                // intent.putStringArrayListExtra("listaProdutos", produtos);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.novalista, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if(id == R.id.addMembros){
            Intent addMembros = new Intent(NovaLista.this, AdicionarMembrosNovaLista.class);
            startActivity(addMembros);

        }else if(id == R.id.verMembros){
            Intent amigos = new Intent(NovaLista.this, VerMembros.class);
            startActivity(amigos);

        }else if(id == R.id.finLista){
            Intent finLista = new Intent(NovaLista.this, AdicionarCustoL.class);
            startActivity(finLista);

        }
        return super.onOptionsItemSelected(item);
    }

}
