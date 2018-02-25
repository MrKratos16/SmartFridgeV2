package net.ddns.smartfridge.smartfridgev2.modelo.adaptadores;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import net.ddns.smartfridge.smartfridgev2.R;
import net.ddns.smartfridge.smartfridgev2.modelo.basico.ComponenteListaCompra;
import net.ddns.smartfridge.smartfridgev2.modelo.utiles.Dialogos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import cn.refactor.library.SmoothCheckBox;

/**
 * Created by Alberto on 10/02/2018.
 */
public class CustomArrayAdapterNuevaLista extends ArrayAdapter<ComponenteListaCompra> {
    private ArrayList<ComponenteListaCompra> productos;
    private ArrayList<ComponenteListaCompra> auxiliar;
    private ArrayList<ComponenteListaCompra> falseProducts; //ArrayList para almacenar los productos que se han deseleccionado con el checkbox
    private ArrayList<SmoothCheckBox> checkBoxes;
    private Dialogos dialogos;
    private Activity activity;
    private String modificacion;
    private int contador;//Para ver si hay elementos que están repetidos

    /**
     * Instantiates a new Custom array adapter nueva lista.
     *
     * @param context            the context
     * @param productosSugeridos the productos sugeridos
     * @param activity           the activity
     */
    public CustomArrayAdapterNuevaLista(@NonNull Context context, ArrayList<ComponenteListaCompra> productosSugeridos, Activity activity) {
        super(context, R.layout.fila_producto_nueva_lista, productosSugeridos);
        //Log.d("MECAGOENDIOS", "CustomArrayAdapterNuevaLista: " + productosSugeridos.size());
        if (productosSugeridos != null) {
            this.productos = productosSugeridos;

        } else {
            productosSugeridos = new ArrayList<>();
            this.productos = productosSugeridos;
        }
        this.auxiliar = new ArrayList<>();
        this.checkBoxes = new ArrayList<>();
        this.falseProducts = new ArrayList<>();
        dialogos = new Dialogos(context, activity);
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //Log.d("MECAGOENDIOS", "llamadas: " + llamada++);
        final String alimento = productos.get(position).getNombreElemento();
        //Log.d("MECAGOENDIOS", "getView: " + productos.size());
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.fila_producto_nueva_lista, parent, false);
        }
        TextView tvAlimentoSugerido = convertView.findViewById(R.id.tvNombreroductoNuevaLista);
        SmoothCheckBox scb = convertView.findViewById(R.id.smoothCheckBoxNuevaLista);
        scb.setVisibility(View.INVISIBLE);
        //Log.d("MECAGOENDIOS", "dimensión checkboxes antes de añadir: " + checkBoxes.size());
        //Añadimos el correspondiente checkbox al arrayList siempre y cuando sea único
        comprobarRepetidos(checkBoxes, scb);
        Log.d("MECAGOENDIOS", "dimensión checkboxes: " + checkBoxes.size());
        tvAlimentoSugerido.setText(alimento);
        //AQUI ESTA EL LISTENER DE LOS CHECKBOXES, SI SE CAMBIA A TRUE LO AÑADE A UN ARRAY LIST AUXILIAR Y SI SE CAMBIA A FALSE SE ELIMINA DE ESE ARRAYLIST AUXILIAR
        //TODO ESTO SE CONFIRMA CUANDO LLAMAMOS AL METODO DE CONFIRMAR CAMBIOS
        //AQUI PASA UNA COSA RARA QUE CUANDO PASA POR AQUI PASA 14 VECES
        scb.setOnCheckedChangeListener(new SmoothCheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SmoothCheckBox smoothCheckBox, boolean b) {
                if (b) {//Si está marcado a true el checkbox
                    //Se añade al auxiliar
                    comprobarRepetidosAlimentos(auxiliar, productos.get(position));
                    //Miramos si un alimento que estaba a false, se ha vuelto a poner a true para quitarlo del array correspondiente
                    for(int i=0;i<falseProducts.size();i++){
                        if(falseProducts.get(i).getNombreElemento().equals(productos.get(position)));
                        falseProducts.remove(productos.get(position));
                    }
                } else {
                    //Comprobamos que el elemento que queremos eliminar no está ya incluido en el arrayList. Si no está ni se mete en el array
                    comprobarRepetidosAlimentos(falseProducts, productos.get(position));
                }
            }
        });

        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                dialogos.dialogoModificarBorrar(alimento, CustomArrayAdapterNuevaLista.this, position);
                return false;
            }
        });

        return convertView;
    }


    /**
     * Add producto.
     *
     * @param producto the producto
     */
    public void addProducto(ComponenteListaCompra producto) {
        if(this.productos.contains(producto)){
            Toast.makeText(activity, "Ya está en la lista", Toast.LENGTH_SHORT).show();
        } else {
            productos.add(producto);
            this.notifyDataSetChanged();
        }
    }

    /**
     * Método para eliminar los alimentos que ha seleccionado el usuario
     */
    public void confirmarCambios() {

        //Recorremos el auxiliar y lo comparamos con cada objeto del array falseProducts
        for(int a=0; a < auxiliar.size(); a++){
            for (int f=0; f < falseProducts.size(); f++){
                //Si coinciden los elementos, se elimina del auxiliar
                if (auxiliar.get(a).getNombreElemento().equals(falseProducts.get(f).getNombreElemento())){
                    auxiliar.remove(falseProducts.get(f));
                }
            }
        }
        //Asignamos al arrayList llamado productos, el valor del arrayList auxiliar
        this.productos = auxiliar;
        Log.d("check", "longitud de productos: " + productos.size());
        checkBoxes.clear();
        this.notifyDataSetChanged();
    }

    /**
     * Gets lista final.
     *
     * @return the lista final
     */
    public ArrayList<ComponenteListaCompra> getListaFinal() {
        return this.productos;
    }

    /**
     * Metodo para mostrar los checkboxes gracias al arrayList de checkboxes
     */
    public void mostrarCheckboxes() {
        auxiliar = productos;
        Log.d("check", "longitud de productos: " + auxiliar.size());
        //Log.d("MECAGOENDIOS", "mostrarCheckboxes: " + checkBoxes.size());
        for (SmoothCheckBox item : this.checkBoxes) {
            item.setVisibility(View.VISIBLE);
            item.setChecked(true);
        }
    }

    /**
     * Metodo para ocultar los checkboxes gracias al arrayList de checkboxes
     */
    public void ocultarrCheckboxes() {
        for (SmoothCheckBox item : this.checkBoxes) {
            item.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Metodo para modificar el nombre de un alimento ya existente en el array
     *
     * @param position     the position
     * @param modificacion the modificacion
     */
    public void modificar(int position, String modificacion){
        if (modificacion != null) {
            productos.get(position).setNombreElemento(modificacion);
            notifyDataSetChanged();
        } else {
            productos.remove(position);
            notifyDataSetChanged();
        }
    }

    /**
     * Get size int.
     *
     * @return the int
     */
    public int getSize(){
        return productos.size();
    }

    /**
     * Comprobar repetidos.
     *
     * @param checkbox the checkbox
     * @param scb      the scb
     */
//Método para comprobar si hay algún checkbox repetido en la lista y no ponerlo
    public void comprobarRepetidos(ArrayList<SmoothCheckBox> checkbox, SmoothCheckBox scb){
        contador = 0;//Lo inicializamos
        for (SmoothCheckBox c : checkbox){
            //Si coinciden los valores, se incrementa el contador
            if(c==scb){
                contador++;
            }
        }
        //Si no ha encontrado ninguna coincidencia, se añade al arrayList
        if(contador==0){
            checkbox.add(scb);
        }
    }

    /**
     * Comprobar repetidos alimentos.
     *
     * @param aux        the aux
     * @param componente the componente
     */
//Método para comprobar si hay algún alimento repetido en la lista y no ponerlo
    public void comprobarRepetidosAlimentos(ArrayList<ComponenteListaCompra> aux, ComponenteListaCompra componente){
        contador=0;//Iniciamos el contador
        for (ComponenteListaCompra c : aux){
            //Recorremos el array viendo si coinciden los valores
            if(c.getNombreElemento().equals(componente.getNombreElemento())){
                contador++;
            }
        }
        //Si no hay coincidencias, se añade al arrayList correspondiente
        if(contador==0){
            aux.add(componente);
        }
    }

    /**
     * Método para ordenar el recycler view alfabeticamente
     *
     * @param az este int será un 1 o un -1 según el orden que queramos
     */
    public void sortRecyclerView(final int az){
        Collections.sort(productos, new Comparator<ComponenteListaCompra>() {
            @Override
            public int compare(ComponenteListaCompra v1, ComponenteListaCompra v2) {
                return v1.getNombreElemento().compareToIgnoreCase(v2.getNombreElemento()) * az;
            }
        });
        notifyDataSetChanged();
    }

    /**
     * Add productos varios.
     *
     * @param productos the productos
     */
    public void addProductosVarios(ArrayList<ComponenteListaCompra> productos){
        for (ComponenteListaCompra item: productos) {
            if (!this.productos.contains(item)) {
                this.productos.add(item);
            }
        }
        notifyDataSetChanged();
    }
}
