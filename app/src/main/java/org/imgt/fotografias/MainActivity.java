package org.imgt.fotografias;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private static final int SOLICITUD_PERMISO_ALMACENAMIENTO = 0; //NECESARIO PARA GUARDAR FOTO

    private ImageView imageView;
    private Uri uriFoto;
    final static int RESULTADO_GALERIA=2;
    final static int RESULTADO_FOTO=3;
    private Lugar lugar;

    private ImageView logocamara,logogaleria, eliminarfoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logocamara = findViewById(R.id.camara);
        logocamara.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){

                tomarFoto(null);

            }
        });

        logogaleria = findViewById(R.id.galeria);
        logogaleria.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                galeria(null);

            }
        });

        eliminarfoto = findViewById(R.id.eliminarfoto);
        eliminarfoto.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                eliminarFoto(null);
            }
        });
        
        lugar= new Lugar("");
        imageView= (ImageView) findViewById(R.id.foto);
        actualizarVistas();
    }

    public void galeria(View view){
        Intent intent= new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, RESULTADO_GALERIA);
    }

    private void tomarFoto(View view){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            uriFoto= Uri.fromFile(new File(
                    Environment.getExternalStorageDirectory()+
                            File.separator + "img_" +
                            (System.currentTimeMillis()/1000) +
                            ".jpg"));
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uriFoto);
            startActivityForResult(intent, RESULTADO_FOTO);

        }else{
            solicitarPermiso(Manifest.permission.READ_EXTERNAL_STORAGE, "Se necesita permiso de almacenamiento para poder usar la fotografía tomada por la cámara"
                    ,SOLICITUD_PERMISO_ALMACENAMIENTO,this);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent
            data) {

        if (requestCode==RESULTADO_GALERIA){ //Cuando cargamos foto desde la galería (pag.182)
            if(resultCode== Activity.RESULT_OK){
                lugar.setFoto(data.getDataString());
                ponerFoto(imageView, lugar.getFoto());

            }else{
                Toast.makeText(this, "Foto no cargada", Toast.LENGTH_SHORT).show();
            }
        }else if(requestCode==RESULTADO_FOTO){
            if(resultCode== Activity.RESULT_OK && lugar!=null && uriFoto!=null){
                lugar.setFoto(uriFoto.toString());
                ponerFoto(imageView, lugar.getFoto());

            }else{
                Toast.makeText(this, "Error en captura", Toast.LENGTH_SHORT).show();
            }
        }
    }

    protected void ponerFoto(ImageView imageView, String uri){
        if(uri !=null && !uri.isEmpty() && !uri.equals("null")){
            imageView.setImageURI(Uri.parse(uri));
        }else{
            imageView.setImageBitmap(null);
        }
    }


    public void eliminarFoto(View view) {
        lugar.setFoto(null);
        ponerFoto(imageView, null);
    }


    protected void actualizarVistas(){
        ponerFoto(imageView, lugar.getFoto());
    }

    public static void solicitarPermiso(final String permiso, String
            justificacion, final int requestCode, final Activity actividad) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(actividad,
                permiso)){
            new AlertDialog.Builder(actividad)
                    .setTitle("Solicitud de permiso")
                    .setMessage(justificacion)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            ActivityCompat.requestPermissions(actividad,
                                    new String[]{permiso}, requestCode);
                        }})
                    .show();
        } else {
            ActivityCompat.requestPermissions(actividad,
                    new String[]{permiso}, requestCode);
        }
    }
}
