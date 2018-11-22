package ar.edu.utn.frsf.isi.dam.laboratorio05;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import ar.edu.utn.frsf.isi.dam.laboratorio05.modelo.MyDatabase;
import ar.edu.utn.frsf.isi.dam.laboratorio05.modelo.Reclamo;
import ar.edu.utn.frsf.isi.dam.laboratorio05.modelo.ReclamoDao;


/**
 * A simple {@link Fragment} subclass.
 */
public class FormularioBusquedaFragment extends Fragment {

    private ArrayAdapter<Reclamo.TipoReclamo> tipoReclamoAdapter;

    public FormularioBusquedaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_formulario_busqueda, container, false);

        final Spinner spTipoReclamo = (Spinner) v.findViewById(R.id.spTipoReclamo);
        final Button btnBuscarTipoReclamo = (Button) v.findViewById(R.id.btnBuscarTipoReclamo);

        tipoReclamoAdapter = new ArrayAdapter<>(
                getActivity(),
                android.R.layout.simple_spinner_dropdown_item,
                Reclamo.TipoReclamo.values()
        );
        tipoReclamoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTipoReclamo.setAdapter(tipoReclamoAdapter);
        spTipoReclamo.setSelection(0);

        btnBuscarTipoReclamo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tag = "mapaReclamos";
                Fragment fragment = getActivity().getSupportFragmentManager().findFragmentByTag(tag);
                if (fragment == null) {
                    fragment = new MapaFragment();
                }

                Bundle bundle = new Bundle();
                bundle.putInt("tipo_mapa", 5);
                bundle.putString("tipo_reclamo", spTipoReclamo.getSelectedItem().toString());
                fragment.setArguments(bundle);

                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.contenido, fragment)
                        .commit();
            }
        });

        // Inflate the layout for this fragment
        return v;
    }

}
