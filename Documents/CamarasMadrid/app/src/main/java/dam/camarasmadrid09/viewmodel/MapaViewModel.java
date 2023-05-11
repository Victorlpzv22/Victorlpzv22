package dam.camarasmadrid09.viewmodel;

import androidx.lifecycle.ViewModel;
/**
 * Grupo: 09
 * Alumnos:
 * - Víctor López Valero
 * - Pedro Gallego Madrid-Salvador
 */
public class MapaViewModel extends ViewModel {
    private int modeMap = -1;

    public void setModeMap ( int modeMap){
      this.modeMap = modeMap;
    }

    public int getModeMap (){
      return modeMap;
    }
}
