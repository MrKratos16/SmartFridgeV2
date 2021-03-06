package net.ddns.smartfridge.smartfridgev2.modelo.adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import net.ddns.smartfridge.smartfridgev2.R;

import java.util.ArrayList;

/**
 * Clase para cargar un "comboBox" también llamado spinner en android
 */
public class CustomBaseAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<String> datos;//Para cargar los datos

    /**
     * Instantiates a new Custom base adapter.
     *
     * @param context the context
     * @param datos   the datos
     */
    public CustomBaseAdapter(Context context, ArrayList<String> datos){
        this.context = context;
        this.datos = datos;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 4;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View itemView = view;
        ViewHolderBase fila;

        if (view == null) {
            itemView = LayoutInflater.from(context).inflate(R.layout.fila_spinner, viewGroup, false);
            fila = new ViewHolderBase();
            fila.textView = (TextView) itemView.findViewById(R.id.tvSpinner);
            itemView.setTag(fila);
        } else {
            fila = (ViewHolderBase) itemView.getTag();
            fila.textView.setText(datos.get(i));
        }

        //fila.imageView.setImageBitmap();
        return itemView;
    }

    private static class ViewHolderBase{
        /**
         * The Text view.
         */
        TextView textView;
    }
}
