package dam.camarasmadrid09;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import dam.camarasmadrid09.viewmodel.MenuViewModel;

/**
 * Grupo: 09
 * Alumnos:
 * - Víctor López Valero
 * - Pedro Gallego Madrid-Salvador
 */
public class MainActivity extends AppCompatActivity{
    private MenuViewModel menuViewModel;
    private Menu menuOpciones;
    FragmentoListado listaFragmentos;

    private ActivityResultLauncher<String> lanzadorPeticionPermiso = registerForActivityResult(new ActivityResultContracts.RequestPermission(), esConcendido -> {
        if (esConcendido) {
            menuOpciones.getItem(2).setChecked(true);
            menuViewModel.setMostrarUbicacion(true);
        }
    });


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

        }
        menuViewModel = new ViewModelProvider(this).get(MenuViewModel.class);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.barraherramientas, menu);
        menuOpciones = menu;
        menuOpciones.getItem(3).setChecked(menuViewModel.getMostrarUbicacion());
        if(menuViewModel.getModoMostrar() == "mostrarUna")
            menuOpciones.getItem(4).setChecked(true);
        else if(menuViewModel.getModoMostrar() == "mostrarTodas")
            menuOpciones.getItem(5).setChecked(true);
        else
            menuOpciones.getItem(6).setChecked(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        listaFragmentos = new FragmentoListado();

        switch (id) {
            case R.id.download:
                return false;
            case R.id.mostrarUbicacion:
                boolean estado = item.isChecked();
                if(estado == true){
                    item.setChecked(false);
                    menuViewModel.setMostrarUbicacion(false);
                } else {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        item.setChecked(true);
                        menuViewModel.setMostrarUbicacion(true);
                    } else {
                        solicitarPermiso();
                    }
                }
                return false;
            case R.id.mostrarAgrupación:
                menuViewModel.setModoMostrar("mostrarAgrupacion");
                item.setChecked(true);
                return false;
            case R.id.mostrarTodas:
                menuViewModel.setModoMostrar("mostrarTodas");
                item.setChecked(true);
                return false;
            case R.id.mostrarUna:
                menuViewModel.setModoMostrar("mostrarUna");
                item.setChecked(true);
                return false;
            case R.id.order:
                return false; //NO SE HACE EN ESTA ACTIVIDAD
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void solicitarPermiso(){
        boolean showRationale = shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_FINE_LOCATION);
        if(!showRationale){
            final Activity actividad = this;
            new AlertDialog.Builder(actividad)
                    .setTitle("Solicitud de permiso de ubicación")
                    .setMessage("Seleccionó no volver a preguntar el permiso de ubicación. ¿Desea activarlo manualmente?")
                    . setNegativeButton("No acepto", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        }
                    })
                    .setPositiveButton("Acepto", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            Intent intent = new Intent();
                            intent.setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                            intent.setData(uri);
                            startActivity(intent);
                        }
                    })
                    .show();
        } else {
            final Activity actividad = this;
            new AlertDialog.Builder(actividad)
                    .setTitle("Solicitud de permiso de ubicación")
                    .setMessage("A continuación se le solicitará permiso para poder acceder a su ubicación.")
                    . setNegativeButton("No acepto", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            new AlertDialog.Builder(actividad)
                                    .setTitle("ATENCIÓN")
                                    .setMessage("No ha aceptado solicitud de permiso a su ubicación por lo que no podrá hacer uso de esta función.")
                                    .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                        }
                                    })
                                    .show();
                        }
                    })
                    .setPositiveButton("Acepto", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            lanzadorPeticionPermiso.launch(android.Manifest.permission.ACCESS_FINE_LOCATION);
                        }
                    })
                    .show();
        }

    }
}


