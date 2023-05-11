package dam.camarasmadrid09;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.clustering.ClusterManager;

import dam.camarasmadrid09.cluster.MyItem;
import dam.camarasmadrid09.databinding.ActivityMapsBinding;
import dam.camarasmadrid09.viewmodel.MapaViewModel;
/**
 * Grupo: 09
 * Alumnos:
 * - Víctor López Valero
 * - Pedro Gallego Madrid-Salvador
 */
public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener {

  private GoogleMap mMap;
  private ActivityMapsBinding binding;
  private CircleOptions circleOptions;
  private Circle circle;
  private MarkerOptions markerOpSeleccion;
  private Marker markerSeleccion;
  private Toolbar barraHerramientas;
  private ActionBar actionBar;
  private RadioGroup vistas;
  private ClusterManager<MyItem> clusterManager;

  private MapaViewModel modeViewModel;

  String[] allCoordenadas;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding = ActivityMapsBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());
    modeViewModel = new ViewModelProvider(this).get(MapaViewModel.class);
    // Obtain the SupportMapFragment and get notified when the map is ready to be used.
    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
            .findFragmentById(R.id.map);
    mapFragment.getMapAsync(this);


    barraHerramientas = findViewById(R.id.toolbar);
    vistas = findViewById(R.id.vistas);
    vistas.setVisibility(View.GONE);

    setSupportActionBar(barraHerramientas);
    actionBar = getSupportActionBar();
    actionBar.setDisplayHomeAsUpEnabled(true);

    binding.fab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if(vistas.getVisibility() == View.GONE)
          vistas.setVisibility(View.VISIBLE);
        else
          vistas.setVisibility(View.GONE);
      }
    });
  }

  @Override
  public void onMapReady(GoogleMap googleMap) {

    mMap = googleMap;
    String coordenada = getIntent().getStringExtra("coordenada");
    String nombre = getIntent().getStringExtra("nombre");
    allCoordenadas = getIntent().getStringArrayExtra("allCordenadas");
    String mostrar = getIntent().getStringExtra("mostrar");
    boolean mostrarUbicacion = getIntent().getBooleanExtra("mostrarUbicacion", false);
    String[] partes = coordenada.split(",");
    String longitud= partes[0].trim();
    String latitud = partes[1].trim();

    LatLng ubicacionSeleccionada = new LatLng(Double.parseDouble(latitud), Double.parseDouble(longitud));
    mMap.addMarker(new MarkerOptions().position(ubicacionSeleccionada).title(nombre));
    mMap.moveCamera(CameraUpdateFactory.newLatLng(ubicacionSeleccionada));
    markerOpSeleccion = new MarkerOptions();
    markerOpSeleccion.position(ubicacionSeleccionada);
    markerOpSeleccion.title(nombre);
    markerOpSeleccion.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

    if (modeViewModel.getModeMap() >= 0) mMap.setMapType(modeViewModel.getModeMap());

    markerSeleccion = mMap.addMarker(markerOpSeleccion);
    markerSeleccion.showInfoWindow();

    mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener(){
      @Override
      public boolean onMarkerClick(@NonNull Marker marker) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 17));
        return false;
      }
    });

    if(mostrarUbicacion){
      FusedLocationProviderClient clienteLocalizacion;
      clienteLocalizacion = LocationServices.getFusedLocationProviderClient(this);
      if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){}
      else {
        Task<Location> ultimaLocalizacion = clienteLocalizacion.getLastLocation();
        ultimaLocalizacion.addOnSuccessListener(this, new OnSuccessListener<Location>() {
          @Override
          public void onSuccess(Location localizacion) {
            if(localizacion != null){
              MarkerOptions markerOpLocation = new MarkerOptions();
              markerOpLocation.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
              markerOpLocation.position(new LatLng(localizacion.getLatitude(), localizacion.getLongitude()));
              mMap.addMarker(markerOpLocation);
              circleOptions = new CircleOptions()
                      .center(new LatLng(localizacion.getLatitude(), localizacion.getLongitude()))
                      .radius(1000).strokeColor(Color.BLACK);
              circle = mMap.addCircle(circleOptions);
              circle.setVisible(true);
            }
          }
        });
      }
    }

    //Añadir el resto de marcadores;
    switch (mostrar) {
      case "mostrarUna":
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(ubicacionSeleccionada)
                .zoom(17) // nivel de zoom a nivel de calles
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        break;
      case "mostrarTodas":
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for(int i = 0; i< allCoordenadas.length; i++){
          String[] div = allCoordenadas[i].split(",");
          String partelongitud= div[0].trim();
          String partelatitud = div[1].trim();
          LatLng latLng = new LatLng(Double.parseDouble(partelatitud), Double.parseDouble(partelongitud));
          mMap.addMarker(new MarkerOptions().position(latLng));
          builder.include(latLng);
        }
        // Crea el objeto LatLngBounds que contiene todos los marcadores
        LatLngBounds bounds = builder.build();
        // Crea un margen alrededor de los límites geográficos
        int padding = 50; // en píxeles
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        mMap.animateCamera(cu);
        break;
      case "mostrarAgrupacion":
        setUpClusterer();
        break;
      default:
        break;
    }
  }

  private void setUpClusterer() {

    // Initialize the manager with the context and the map.
    // (Activity extends context, so we can pass 'this' in the constructor.)
    clusterManager = new ClusterManager<MyItem>(this, mMap);

    // Point the map's listeners at the listeners implemented by the cluster
    // manager.
    final CameraPosition[] mPreviousCameraPosition = {null};
    mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
      @Override
      public void onCameraIdle() {
        CameraPosition position = mMap.getCameraPosition();
        if(mPreviousCameraPosition[0] == null || mPreviousCameraPosition[0].zoom != position.zoom) {
          mPreviousCameraPosition[0] = mMap.getCameraPosition();
          clusterManager.cluster();
        }
      }
    });
    mMap.setOnMarkerClickListener(clusterManager);

    LatLngBounds.Builder builder = new LatLngBounds.Builder();
    // Add cluster items (markers) to the cluster manager.
    for(int i = 0; i< allCoordenadas.length; i++){
      String[] div = allCoordenadas[i].split(",");
      String partelongitud= div[0].trim();
      String partelatitud = div[1].trim();
      builder.include(new LatLng(Double.parseDouble(partelatitud), Double.parseDouble(partelongitud)));
      clusterManager.addItem(new MyItem(Double.parseDouble(partelatitud), Double.parseDouble(partelongitud), "Title" + i, "Snippet" + i));
    }
    LatLngBounds bounds = builder.build();
    int padding = 50;
    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
    mMap.animateCamera(cu);
  }

  @Override
  public void onMapClick(@NonNull LatLng latLng) {

  }

  @Override
  public void onMapLongClick(@NonNull LatLng latLng) {

  }

  public void modeNormal (View view){
    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    modeViewModel.setModeMap(GoogleMap.MAP_TYPE_NORMAL);
  }

  public void modeSatellite (View view){
    mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
    modeViewModel.setModeMap(GoogleMap.MAP_TYPE_SATELLITE);
  }

  public void modeHybrid (View view){
    mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
    modeViewModel.setModeMap(GoogleMap.MAP_TYPE_HYBRID);
  }

  public void modeTerrain (View view){
    mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
    modeViewModel.setModeMap(GoogleMap.MAP_TYPE_TERRAIN);

  }

  @Override
  public boolean onSupportNavigateUp() {
    super.onBackPressed();
    return true;
  }

}