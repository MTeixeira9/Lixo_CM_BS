package com.example.android.buyshare;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android.buyshare.Database.Upload;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.regex.Pattern;

public class EditPerfil extends AppCompatActivity {

    private String userTlm;
    private Query q;
    private EditText nomeET, passwordET, conf_PasswET, emailET;
    private String nome, password, conf_Passw, email;
    private static final int RESULT_LOAD_IMAGE = 1;
    private DatabaseReference mDatabase, mDatabaseUpload;
    private StorageReference mStorageRefUpload;
    private StorageTask mUploadTask;
    private Uri mImageUri;
    private static final String MSG_PASSES_ERRO = "As palavras passe não coicidem";
    private static final String MSG_INV_EMAIL_ERRO = "Tem que inserir um email válido";
    private int nAlteracoes;

    public static final Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_perfil);

        getSupportActionBar().setTitle("Editar Dados");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        userTlm = getIntent().getStringExtra("userTlm");

        //BASE DE DADOS
        mDatabase = FirebaseDatabase.getInstance().getReference("users");

        mDatabaseUpload = FirebaseDatabase.getInstance().getReference("upload");
        mStorageRefUpload = FirebaseStorage.getInstance().getReference("upload");

        nomeET = findViewById(R.id.nome_perfil);
        passwordET = findViewById(R.id.pass_edit);
        conf_PasswET = findViewById(R.id.conf_pwd_edit);
        emailET = findViewById(R.id.email_edit);

        q = mDatabase.orderByChild("numeroTlm").equalTo(userTlm);

        Button alterar = findViewById(R.id.button_load_image);
        alterar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, RESULT_LOAD_IMAGE);

            }
        });
/*
        Button upload = findViewById(R.id.uploadFoto);
        upload.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mUploadTask != null && mUploadTask.isInProgress()) {
                    Toast.makeText(getApplicationContext(), "Upload in progress", Toast.LENGTH_SHORT).show();
                } else {
                    uploadFile();
                }
            }
        });
*/
        Button guardarDados = findViewById(R.id.guardarDados);
        guardarDados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                /**
                 * PREENCHER FOTO
                 */
               /* Query q2 = mDatabaseUpload.child(userTlm);
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
                        Glide.with(EditPerfil.this)
                                .load(url)
                                .into((ImageView) findViewById(R.id.imageView_EditPerfil));
                    }
                    //}

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
*/





                nome = nomeET.getText().toString();
                password = passwordET.getText().toString();
                conf_Passw = conf_PasswET.getText().toString();
                email = emailET.getText().toString();
                nAlteracoes = 0;

                if (!nome.equals("") || !password.equals("") || !conf_Passw.equals("") || !email.equals("")) {

                    q.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {

                                if (!nome.equals("")) {
                                    mDatabase.child(userTlm).child("nome").setValue(nome);
                                    nAlteracoes++;
                                }

                                if (!email.equals("")) {
                                    if (!isValidEmail(email)) {
                                        emailET.setError(MSG_INV_EMAIL_ERRO);
                                    } else {
                                        mDatabase.child(userTlm).child("email").setValue(email);
                                        nAlteracoes++;
                                    }
                                }

                                if (!password.equals("")) {
                                    if (!password.equals(conf_Passw)) {
                                        passwordET.setError(MSG_PASSES_ERRO);
                                    } else {
                                        mDatabase.child(userTlm).child("password").setValue(password);
                                        nAlteracoes++;
                                    }
                                }
                            }

                            if (nAlteracoes > 0) {
                                Intent i = new Intent(EditPerfil.this, Perfil.class);
                                i.putExtra("userTlm", userTlm);
                                finish();
                                startActivity(i);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.e("TAG", "onCancelled", databaseError.toException());
                        }
                    });

                } else {
                    Toast.makeText(getApplicationContext(), "Falta inserir dados", Toast.LENGTH_LONG).show();
                }

                if (mUploadTask != null && mUploadTask.isInProgress()) {
                    Toast.makeText(getApplicationContext(), "Upload in progress", Toast.LENGTH_SHORT).show();
                } else {
                    uploadFile();
                }
            }

        });
    }

    public static boolean isValidEmail(String email) {
        return EMAIL_ADDRESS_PATTERN.matcher(email).matches();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        ImageView imageView = findViewById(R.id.imageView_Perfil);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();

            Picasso.get().load(mImageUri).into(imageView);
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFile() {

        if (mImageUri != null) {
            final StorageReference fileReference = mStorageRefUpload.child(System.currentTimeMillis()
                    + "." + getFileExtension(mImageUri));

            mUploadTask = fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    // mProgressBar.setProgress(0);
                                }
                            }, 5);

                            StorageReference islandRef = fileReference.child("gs://cmbuyshare-5f07c.appspot.com/upload");

                            String uploadId = mDatabaseUpload.push().getKey();
                            Upload upload = new Upload(fileReference.getName(),
                                    islandRef + "/" + fileReference.getName(), uploadId);

                            mDatabaseUpload.child(userTlm).setValue(upload);

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            //mProgressBar.setProgress((int) progress);
                        }
                    });
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
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
        Intent i = new Intent(EditPerfil.this, Perfil.class);
        i.putExtra("userTlm", userTlm);
        startActivity(i);
    }
}
