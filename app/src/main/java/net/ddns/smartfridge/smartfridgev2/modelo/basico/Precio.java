package net.ddns.smartfridge.smartfridgev2.modelo.basico;

import java.io.Serializable;

/**
 * Clase que representa los productos con los precios por supermercado
 */
public class Precio implements Serializable {
    private String nombreProducto;//El nombre del producto
    private double pvp;//El precio del producto
    private String supermercado;//El nombre del supermercado al que corresponde dicho precio

    /**
     * Constructor
     *
     * @param nombreProducto nombre del producto
     * @param pvp            precio del producto en la bbdd
     * @param supermercado   nombre del supermercado
     */
    public Precio(String nombreProducto, double pvp, String supermercado) {
        this.nombreProducto = nombreProducto;
        this.pvp = pvp;
        this.supermercado = supermercado;
    }

    /**
     * Gets nombre producto.
     *
     * @return the nombre producto
     */
    public String getNombreProducto() {
        return nombreProducto;
    }

    /**
     * Gets pvp.
     *
     * @return the pvp
     */
    public double getPvp() {
        return pvp;
    }

    /**
     * Gets supermercado.
     *
     * @return the supermercado
     */
    public String getSupermercado() {
        return supermercado;
    }

}
