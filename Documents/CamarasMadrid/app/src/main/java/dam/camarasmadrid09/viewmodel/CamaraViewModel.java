package dam.camarasmadrid09.viewmodel;

import androidx.lifecycle.ViewModel;

import dam.camarasmadrid09.objetos.Camara;
import dam.camarasmadrid09.objetos.ListaCamaras;
/**
 * Grupo: 09
 * Alumnos:
 * - Víctor López Valero
 * - Pedro Gallego Madrid-Salvador
 */
public class CamaraViewModel extends ViewModel {
    private Camara camaraSeleccionada;
    private ListaCamaras listado, filtrada;
    private int posicionCamara;

    private String busqueda = "";
    public void setCamaraSeleccionada(Camara camara,int posicion, ListaCamaras listadoCamaras){
        posicionCamara = posicion;
        listado = listadoCamaras;
        camaraSeleccionada = camara;
    }

    public void desseleccionarCamara(){
        posicionCamara = -1;
        camaraSeleccionada = null;
    }

    public void setListaCamaras(ListaCamaras listadoCamaras){
        listado = listadoCamaras;
    }
    public void setListaFiltrada(ListaCamaras listadoCamaras){
        filtrada = listadoCamaras;
    }
    public ListaCamaras getListaCamaraSeleccionada() {
        return listado;
    }
    public ListaCamaras getListaCamaraFiltrada() {
        return filtrada;
    }
    public Camara getCamaraSeleccionada() {
        return camaraSeleccionada;
    }
    public int getPosicionCamara(){
        return posicionCamara;
    }

    public String getBusqueda(){return busqueda;};

    public void setBusqueda(String s){busqueda = s;}
}
