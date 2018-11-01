package com.example.santi.tallerfirebase;
import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class CreateUserActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    EditText nombre;
    EditText apellido;
    EditText correo;
    EditText password;
    Button registrar;
    Button galeria;
    Button camara;
    ImageView imagen ;
    final static int REQUEST_GALLERY = 1;
    final static int REQUEST_CAMERA = 2;
    final static int IMAGE_PICKER_REQUEST = 5;
    static final int REQUEST_IMAGE_CAPTURE = 6;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);
        nombre = (EditText)findViewById(R.id.txtNombre);
        apellido = (EditText)findViewById(R.id.txtApellido);
        correo = (EditText)findViewById(R.id.txtCorreo);
        password = (EditText)findViewById(R.id.txtPassword);
        registrar = (Button) findViewById(R.id.registrar);
        galeria = (Button)findViewById(R.id.btnGaleria);
        camara = (Button)findViewById(R.id.btnCamara);
        imagen = (ImageView)findViewById(R.id.imagen);
        galeria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestPermission((Activity) view.getContext(), Manifest.permission.READ_EXTERNAL_STORAGE, "Se necesita acceder a la galería", REQUEST_GALLERY);
                cargarImagenGaleria();
            }
        });
        camara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestPermission((Activity) view.getContext(), Manifest.permission.CAMERA, "Se necesita acceder a la camara", REQUEST_CAMERA);
                accederCamara();
            }
        });
        mAuth = FirebaseAuth.getInstance();
        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = correo.getText().toString();
                String passwordTxt = password.getText().toString();

                mAuth.createUserWithEmailAndPassword(email, passwordTxt)
                        .addOnCompleteListener(CreateUserActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(CreateUserActivity.this, "createUserWithEmail:onComplete:", Toast.LENGTH_SHORT).show();
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    if(user!=null){ //Update user Info
                                        UserProfileChangeRequest.Builder upcrb = new UserProfileChangeRequest.Builder();
                                        upcrb.setDisplayName(nombre.getText().toString()+" "+apellido.getText().toString());
                                        upcrb.setPhotoUri(Uri.parse(imagen.toString()));//fake uri, real one coming soon
                                        user.updateProfile(upcrb.build());
                                        startActivity(new Intent(CreateUserActivity.this, MapsActivity.class)); //o en el listener
                                    }
                                }
                                if (!task.isSuccessful()) {
                                    Toast.makeText(CreateUserActivity.this, "No se creo el usuario",
                                            Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
            }
        });
    }
    private void requestPermission(Activity context, String permiso, String explanation, int requestId ){
        if (ContextCompat.checkSelfPermission(context,permiso)!= PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?  
            if (ActivityCompat.shouldShowRequestPermissionRationale(context,permiso)) {
                Toast.makeText(context, explanation, Toast.LENGTH_LONG).show();
            }
            ActivityCompat.requestPermissions(context, new String[]{permiso}, requestId);
        }
    }
    private void cargarImagenGaleria(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            Intent pickImage = new Intent(Intent.ACTION_PICK);
            pickImage.setType("image/*");
            startActivityForResult(pickImage, IMAGE_PICKER_REQUEST);
        }
    }
    private void accederCamara() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case IMAGE_PICKER_REQUEST:
                if(resultCode == RESULT_OK){
                    try {
                        final Uri imageUri = data.getData();
                        final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        imagen.setImageBitmap(selectedImage);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            case REQUEST_IMAGE_CAPTURE:
                if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    imagen.setImageBitmap(imageBitmap);
                }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode){
            case  REQUEST_GALLERY: {
                cargarImagenGaleria();
                break;
            }
            case REQUEST_CAMERA: {
                accederCamara();
                break;
            }
        }
    }
}

