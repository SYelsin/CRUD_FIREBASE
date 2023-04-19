package com.example.crud_firebase;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class DetailsActivity extends AppCompatActivity {

    TextView detailDesc, detailTitle, detailLang;
    ImageView detailImage;
    FloatingActionButton deleteButton, editButton;
    String key = "";
    String apellido, nombre, fecha;
    int id;
    String imageUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);


        detailImage = findViewById(R.id.detailImage);
        detailTitle = findViewById(R.id.detailTitle);
        deleteButton = findViewById(R.id.deleteButton);
        editButton = findViewById(R.id.editButton);
        detailLang = findViewById(R.id.detailLang);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            //detailDesc.setText(bundle.getString("Apellido"));
            id = bundle.getInt("Id");
            detailTitle.setText(bundle.getString("Nombre")+" "+bundle.getString("Apellido"));
            nombre = bundle.getString("Nombre");
            apellido = bundle.getString("Apellido");
            fecha = bundle.getString("Fecha");
            detailLang.setText("Fecha N: "+bundle.getString("Fecha"));
            key = bundle.getString("Key");
            imageUrl = bundle.getString("Imagen");
            Glide.with(this).load(bundle.getString("Imagen")).into(detailImage);
        }
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Personas");
                FirebaseStorage storage = FirebaseStorage.getInstance();

                StorageReference storageReference = storage.getReferenceFromUrl(imageUrl);
                storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        reference.child(key).removeValue();
                        Toast.makeText(DetailsActivity.this, "Eliminado correctamente", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    }
                });
            }
        });
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailsActivity.this, UpdateActivity.class)
                        .putExtra("Nombre", nombre)
                        .putExtra("Apellido", apellido)
                        .putExtra("Fecha", fecha)
                        .putExtra("Image", imageUrl)
                        .putExtra("Key", key);
                startActivity(intent);
            }
        });
    }
}