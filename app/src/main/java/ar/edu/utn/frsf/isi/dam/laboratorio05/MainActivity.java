package ar.edu.utn.frsf.isi.dam.laboratorio05;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

public class MainActivity extends AppCompatActivity implements
        FragmentManager.OnBackStackChangedListener,
        NuevoReclamoFragment.OnNuevoLugarListener,
        MapaFragment.OnMapaListener {
    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private boolean access_fine_location_permission = false;
    private boolean audio_record_audio_permission = false;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final int AUDIO_PERMISSION_REQUEST_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportFragmentManager().addOnBackStackChangedListener(this);
        //Handle when activity is recreated like on orientation Change
        shouldDisplayHomeUp();
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        navView = (NavigationView)findViewById(R.id.navview);
        BienvenidoFragment fragmentInicio = new BienvenidoFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.contenido, fragmentInicio)
                .commit();

        navView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {

                        boolean fragmentTransaction = false;
                        Fragment fragment = null;
                        String tag = "";
                        switch (menuItem.getItemId()) {
                            case R.id.optNuevoReclamo:
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO)
                                            != PackageManager.PERMISSION_GRANTED) {
                                        ActivityCompat.requestPermissions(
                                                MainActivity.this,
                                                new String[]{Manifest.permission.RECORD_AUDIO},
                                                AUDIO_PERMISSION_REQUEST_CODE
                                        );
                                    } else {
                                        Log.d("LAB06", "audio permission already granted!!");
                                        audio_record_audio_permission = true;
                                    }
                                }

                                if (audio_record_audio_permission) {
                                    tag = "nuevoReclamoFragment";
                                    fragment =  getSupportFragmentManager().findFragmentByTag(tag);
                                    if(fragment==null) {
                                        fragment = new NuevoReclamoFragment();
                                        ((NuevoReclamoFragment) fragment).setListener(MainActivity.this);
                                    }

                                    fragmentTransaction = true;
                                }

                                break;

                            case R.id.optListaReclamo:
                                tag="listaReclamos";
                                fragment =  getSupportFragmentManager().findFragmentByTag(tag);
                                if(fragment==null) fragment = new ListaReclamosFragment();
                                fragmentTransaction = true;
                                break;

                            case R.id.optVerMapa:
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                                            != PackageManager.PERMISSION_GRANTED) {
                                        ActivityCompat.requestPermissions(
                                                MainActivity.this,
                                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                                LOCATION_PERMISSION_REQUEST_CODE
                                        );
                                    } else {
                                        Log.d("LAB05", "permission already granted !!");
                                        access_fine_location_permission = true;
                                    }
                                }

                                if (access_fine_location_permission) {
                                    tag = "mapaReclamos";
                                    fragment = getSupportFragmentManager().findFragmentByTag(tag);
                                    if (fragment == null) {
                                        fragment = new MapaFragment();
                                        ((MapaFragment) fragment).setListener(MainActivity.this);
                                    }

                                    Bundle bundle = new Bundle();
                                    bundle.putInt("tipo_mapa", 2);
                                    fragment.setArguments(bundle);

                                    fragmentTransaction = true;
                                }

                                break;

                            case R.id.optHeatMap:
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                                            != PackageManager.PERMISSION_GRANTED) {
                                        ActivityCompat.requestPermissions(
                                                MainActivity.this,
                                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                                LOCATION_PERMISSION_REQUEST_CODE
                                        );
                                    } else {
                                        Log.d("LAB05", "permission already granted !!");
                                        access_fine_location_permission = true;
                                    }
                                }

                                if (access_fine_location_permission) {
                                    tag = "mapaReclamos";
                                    fragment = getSupportFragmentManager().findFragmentByTag(tag);
                                    if (fragment == null) {
                                        fragment = new MapaFragment();
                                        ((MapaFragment) fragment).setListener(MainActivity.this);
                                    }

                                    Bundle bundle = new Bundle();
                                    bundle.putInt("tipo_mapa", 4);
                                    fragment.setArguments(bundle);

                                    fragmentTransaction = true;
                                }

                                break;

                            case R.id.optFormularioBusqueda:
                                tag = "formularioBusqueda";
                                fragment = getSupportFragmentManager().findFragmentByTag(tag);
                                if (fragment == null) {
                                    fragment = new FormularioBusquedaFragment();
                                }
                                fragmentTransaction = true;

                                break;
                        }

                        if(fragmentTransaction) {
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.contenido, fragment,tag)
                                    .addToBackStack(null)
                                    .commit();

                            menuItem.setChecked(true);

                            getSupportActionBar().setTitle(menuItem.getTitle());
                        }

                        drawerLayout.closeDrawers();

                        return true;
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackStackChanged() {
        shouldDisplayHomeUp();
    }

    public void shouldDisplayHomeUp(){
        //Enable Up button only  if there are entries in the back stack
        boolean canback = getSupportFragmentManager().getBackStackEntryCount()>0;
        getSupportActionBar().setDisplayHomeAsUpEnabled(canback);
    }

    @Override
    public void coordenadasSeleccionadas(LatLng c) {
        String tag = "nuevoReclamoFragment";
        Fragment fragment =  getSupportFragmentManager().findFragmentByTag(tag);
        if(fragment==null) {
            fragment = new NuevoReclamoFragment();
            //((NuevoReclamoFragment) fragment).setListener(listenerReclamo);
            ((NuevoReclamoFragment) fragment).setListener(MainActivity.this);
        }

        Bundle bundle = new Bundle();
        bundle.putString("latLng",c.latitude+";"+c.longitude);
        fragment.setArguments(bundle);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.contenido, fragment,tag)
                .commit();
    }


    @Override
    public void obtenerCoordenadas() {
        Log.d("LAB05", "obtener coordenadas");

        String tag = "mapaReclamos";
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        if (fragment == null) {
            fragment = new MapaFragment();
            ((MapaFragment) fragment).setListener(MainActivity.this);
        }

        Bundle bundle = new Bundle();
        bundle.putInt("tipo_mapa", 1);
        fragment.setArguments(bundle);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.contenido, fragment, tag)
                .commit();
    }

    @Override
    public void onRequestPermissionsResult (int requestCode, String permission[], int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    access_fine_location_permission = true;
                }

                break;
            }

            case AUDIO_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    audio_record_audio_permission = true;
                }

                break;
            }
        }
    }
}
