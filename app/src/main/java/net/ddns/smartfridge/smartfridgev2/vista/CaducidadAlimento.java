package net.ddns.smartfridge.smartfridgev2.vista;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import net.ddns.smartfridge.smartfridgev2.R;
import net.ddns.smartfridge.smartfridgev2.modelo.Alimento_Codigo;
import net.ddns.smartfridge.smartfridgev2.modelo.escuchadores.CustomOnDragListener;
import net.ddns.smartfridge.smartfridgev2.modelo.escuchadores.CustomOnDragListener2;
import net.ddns.smartfridge.smartfridgev2.modelo.escuchadores.CustomOnLongClickListener;

public class CaducidadAlimento extends AppCompatActivity {
    private boolean ocupado = false;
    private Alimento_Codigo ac;//Para almacenar el objeto que recojamos el ConfirmarAlimentoActivity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        ac = ConfirmarAlimentoActivity.getAlimento();
        setContentView(R.layout.activity_caducidad_alimento);
        //Cogemos el drawable de la carpeta de recursos
        Drawable elemento = getResources().getDrawable(R.drawable.uno);
        //Convertimos el drawable a bitmap
        Bitmap bitmap = ((BitmapDrawable) elemento).getBitmap();
        //Hacemos ese bitmap redondeado
        RoundedBitmapDrawable redondo = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
        //Redondeamos los bordes
        redondo.setCornerRadius(bitmap.getHeight());
        //Cogemos la referencia al imageView
        ImageView iv1 = (ImageView)findViewById(R.id.ivCad1);
        iv1.setImageDrawable(redondo);
        ImageView iv2 = (ImageView)findViewById(R.id.ivCad2);
        iv2.setImageDrawable(redondo);
        /*ImageView iv3 = (ImageView)findViewById(R.id.ivCad3);
        iv3.setImageDrawable(redondo);*/
        ImageView iv4 = (ImageView)findViewById(R.id.ivCad4);
        iv4.setImageDrawable(redondo);
        ImageView iv5 = (ImageView)findViewById(R.id.ivCad5);
        iv5.setImageDrawable(redondo);
        /*ImageView iv6 = (ImageView)findViewById(R.id.ivCad6);
        iv6.setImageDrawable(redondo);*/
        ImageView iv7 = (ImageView)findViewById(R.id.ivCad7);
        iv7.setImageDrawable(redondo);
        ImageView iv8 = (ImageView)findViewById(R.id.ivCadMas);
        iv8.setImageDrawable(this.getResources().getDrawable(R.drawable.uno));
        ImageView dd = (ImageView)findViewById(R.id.ivDropZone);
        dd.setImageBitmap(ac.getImagen());
        cargarDragAndDrop();
    }

    private void cargarDragAndDrop(){
        findViewById(R.id.ivCad1).setOnLongClickListener(new CustomOnLongClickListener(1));
        findViewById(R.id.ivCad2).setOnLongClickListener(new CustomOnLongClickListener(2));
        findViewById(R.id.ivCad3).setOnLongClickListener(new CustomOnLongClickListener(3));
        findViewById(R.id.ivCad4).setOnLongClickListener(new CustomOnLongClickListener(4));
        findViewById(R.id.ivCad5).setOnLongClickListener(new CustomOnLongClickListener(5));
        findViewById(R.id.ivCad6).setOnLongClickListener(new CustomOnLongClickListener(6));
        findViewById(R.id.ivCad7).setOnLongClickListener(new CustomOnLongClickListener(7));
        findViewById(R.id.relativeLayout).setOnDragListener(new CustomOnDragListener((ImageView) findViewById(R.id.ivDropZone), (LinearLayout) findViewById(R.id.linearLayout), this));
        findViewById(R.id.linearLayout).setOnDragListener(new CustomOnDragListener2(this));
    }
}
