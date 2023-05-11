package dam.camarasmadrid09.objetos;

import android.util.Log;

import java.util.ArrayList;
/**
 * Grupo: 09
 * Alumnos:
 * - Víctor López Valero
 * - Pedro Gallego Madrid-Salvador
 */
public class ListaCamaras {
  private ArrayList<Camara> listaCamaras;

  public ListaCamaras() {
    listaCamaras = new ArrayList<>();
  }

  public void addCamara(Camara camara) {
    listaCamaras.add(camara);
  }

  public ArrayList<Camara> getListaCamaras() {
    return listaCamaras;
  }

  public void setListaCamaras(ArrayList<Camara> lista){
    listaCamaras = lista;
  }
  public ArrayList<String> getNombreCamaras() {
    ArrayList<String> camaras = new ArrayList<>();
    for (Camara camara: listaCamaras){
      camaras.add(camara.getNombre());
    }
    return camaras;
  }
  public String[] getAllCoordenadas() {
    String[] coordenadas = new String[listaCamaras.size()]; // Inicializar el arreglo con elementos
    for (int i = 0; i < listaCamaras.size(); i++) {
      coordenadas[i] = listaCamaras.get(i).getCoordenadas();
    }
    return coordenadas;
  }


  public ArrayList<Camara> filter(String searchTerm) {
    ArrayList<Camara> filteredList = new ArrayList<>();

      for (Camara camara : listaCamaras) {
        if (camara.getNombre().toLowerCase().contains(searchTerm.toLowerCase())) {
          filteredList.add(camara);
        }
      }

    return filteredList;
  }

}