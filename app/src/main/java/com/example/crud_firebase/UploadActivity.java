package com.example.crud_firebase;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class UploadActivity extends AppCompatActivity {

    ImageView uploadImage;
    Button saveButton;
    EditText uploadNombre, uploadApellido, uploadFechaNacimiento;
    String imageURL;
    Uri uri;
    DatabaseReference databaseReference;
    int newId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        uploadImage = findViewById(R.id.uploadImage);
        uploadNombre = findViewById(R.id.uploadNombre);
        uploadApellido = findViewById(R.id.uploadApellido);
        uploadFechaNacimiento = findViewById(R.id.uploadFecha);
        saveButton = findViewById(R.id.saveButton);




        databaseReference = FirebaseDatabase.getInstance().getReference("Personas");

        // Obtener el último id y establecer el nuevo id autoincrementable
        Query query = databaseReference.orderByKey().limitToLast(1);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getChildrenCount() > 0) {
                    for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                        String id = childSnapshot.child("id").getValue(String.class);
                        newId = Integer.parseInt(id) + 1;
                    }
                } else {
                    // No hay ningún id en la base de datos
                    newId = 1;
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UploadActivity.this, error.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            uri = data.getData();
                            uploadImage.setImageURI(uri);
                        } else {
                            Toast.makeText(UploadActivity.this, "No Image Selected", Toast.LENGTH_SHORT).show();
                            uri = Uri.parse("https://firebasestorage.googleapis.com/v0/b/crud-firebase-d4364.appspot.com/o/Imagenes%2Fuser%20(3).png?alt=media&token=c3bdae87-c877-491c-9810-17110058d83a");
                            uploadImage.setImageURI(uri);
                        }
                    }
                }
        );

        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPicker = new Intent(Intent.ACTION_PICK);
                photoPicker.setType("image/*");
                activityResultLauncher.launch(photoPicker);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nombre = uploadNombre.getText().toString().trim();
                String apellido = uploadApellido.getText().toString().trim();
                String fechaNacimiento = uploadFechaNacimiento.getText().toString().trim();

                if (nombre.isEmpty()) {
                    uploadNombre.setError("Ingrese su nombre");
                } else if (apellido.isEmpty()) {
                    uploadApellido.setError("Ingrese su apellido");
                } else if (fechaNacimiento.isEmpty()) {
                    uploadFechaNacimiento.setError("Ingrese su fecha de nacimiento");
                } else if (!isValidDate(fechaNacimiento)) {
                    uploadFechaNacimiento.setError("Ingrese una fecha de nacimiento válida (dd/mm/yyyy)");
                } else {
                    saveData();
                }

            }
        });
    }
    private boolean isValidDate(String inputDate) {
        String dateFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.getDefault());
        sdf.setLenient(false);
        try {
            Date date = sdf.parse(inputDate);
            if (!inputDate.equals(sdf.format(date))) {
                return false;
            }
        } catch (ParseException e) {
            return false;
        }
        return true;
    }


    public void saveData() {
if(uri==null){
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle("Error")
            .setMessage("Por favor suba una imagen")
            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    // Acciones a realizar al presionar el botón "Aceptar"
                }
            });
    AlertDialog dialog = builder.create();
    dialog.show();

}else {


    // Obtiene una referencia a la base de datos de Firebase
    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Personas");

    // Crea un nuevo nodo para la persona y obtiene su ID único
    String personaId = String.valueOf(newId);

    // Obtiene una referencia al almacenamiento de Firebase
    StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("Imagenes").child(personaId);

    AlertDialog.Builder builder = new AlertDialog.Builder(UploadActivity.this);
    builder.setCancelable(false);
    builder.setView(R.layout.progress_layout);
    AlertDialog dialog = builder.create();
    dialog.show();

    // Sube la imagen al almacenamiento de Firebase
    storageRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
        @Override
        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

            // Obtiene la URL de descarga de la imagen
            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
            while (!uriTask.isComplete()) ;
            Uri urlImage = uriTask.getResult();
            imageURL = urlImage.toString();

            // Obtiene los datos de la persona desde los campos de entrada
            String nombre = uploadNombre.getText().toString();
            String apellido = uploadApellido.getText().toString();
            String fechaNacimiento = uploadFechaNacimiento.getText().toString();

            // Crea un objeto Persona con los datos
            DataClass persona = new DataClass(personaId, nombre, apellido, fechaNacimiento, imageURL);

            // Guarda la persona en la base de datos de Firebase
            dbRef.child(personaId).setValue(persona).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(UploadActivity.this, "Guardado Exitosamente", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(UploadActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                }
            });

            dialog.dismiss();
        }
    }).addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
            dialog.dismiss();
        }
    });
}
    }

    public void uploadData() {

            String nombre = uploadNombre.getText().toString();
            String apellido = uploadApellido.getText().toString();
            String fechaNacimiento = uploadFechaNacimiento.getText().toString();
            String id = String.valueOf(newId);

            DataClass dataClass = new DataClass(id, nombre, apellido, fechaNacimiento, imageURL);

            FirebaseDatabase.getInstance().getReference("Personas")
                    .push()
                    .setValue(dataClass)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(UploadActivity.this, "Guardado correctamente", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(UploadActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                        }
                    });

    }
}