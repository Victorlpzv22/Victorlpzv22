package dam.camarasmadrid09.viewmodel;

import androidx.lifecycle.ViewModel;

public class MenuViewModel extends ViewModel {
  private boolean mostrarUbicacion = false;
  private String modoMostrar = "mostrarUna";

  public void setMostrarUbicacion (boolean b){mostrarUbicacion = b;}

  public boolean getMostrarUbicacion(){return mostrarUbicacion;}

  public void setModoMostrar (String s){modoMostrar = s;}

  public String getModoMostrar (){return modoMostrar;}

}
