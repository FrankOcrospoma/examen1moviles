package com.example.examen1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemLongClickListener {


    Spinner spEjercicio;
    EditText txtTiempo, txtCalorias;
    MaterialButton btnAgregar, btnCompartir;
    ListView lvdatos;

    //Variables para el almacenamiento de los datos y listado
    Set<String> ejercicios;
    ArrayAdapter<String> adapter;

    //Constantes para administrar el almacenamiento de los datos
    private static final String PREF_NAME = "MisPreferencias";
    private static final String TASK_KEY = "Tareas";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Enlazar los controles XML con los controles Java
        txtTiempo = findViewById(R.id.txtTiempo);
        txtCalorias = findViewById(R.id.txtCalorias);
        btnAgregar = findViewById(R.id.btnAgregar);
        btnCompartir = findViewById(R.id.btnCompartir);
        spEjercicio = findViewById(R.id.spEjercicio);

        lvdatos = findViewById(R.id.lvdatos);

        btnAgregar.setOnClickListener(this);
        btnCompartir.setOnClickListener(this);

        cargarEjercicios();

        //Inicializar la variable de almacenamiento
        ejercicios = new HashSet<>();

        //Cargar las ejercicios guardadas en el almacen de datos
        SharedPreferences preferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        ejercicios = new LinkedHashSet<>(preferences.getStringSet(TASK_KEY, new LinkedHashSet<>()));

        //Configurar el adaptador para mostrar el listado
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>(ejercicios));
        lvdatos.setAdapter(adapter);

        //Implementar el botón agregar
        btnAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tiempo = txtTiempo.getText().toString();
                String calorias = txtCalorias.getText().toString();
                String ejerci = spEjercicio.getSelectedItem().toString();
                if (tiempo.isEmpty()){
                    Snackbar.make(v, "Debe ingresar el tiempo", Snackbar.LENGTH_LONG).show();
                    return;
                }
                if (calorias.isEmpty()){
                    Snackbar.make(v, "Debe ingresar las calorias", Snackbar.LENGTH_LONG).show();
                    return;
                }
                String item = ejerci + " | Tiempo dedicado:" + tiempo + " | Calorias quemadas:" + calorias;
                //Agregar un nuevo item a la variable ejercicios
                ejercicios.add(item);

                //Limpiar el adaptador
                adapter.clear();

                //Agregar las tareas al adaptador
                adapter.addAll(ejercicios);

                //Notificar al adapter que tiene nuevos elementos y debe refrescar el listado
                adapter.notifyDataSetChanged();

                //Almacenar la tarea en SharedPreferences
                guardarTarea();

                //Limpiar la caja de texto para agregar otra ejercicio
                txtCalorias.setText("");
                txtTiempo.setText("");

                txtTiempo.requestFocus();

            }
        });

        //Implementar el botón compartir por WhatsApp
        btnCompartir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Preparar el intent para abrir WhatsApp
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.setPackage("com.whatsapp");
                intent.putExtra(Intent.EXTRA_TEXT, textoTareasFormato());

                //Abrir el intent
                try {
                    startActivity(intent);
                }catch (ActivityNotFoundException e){
                    Snackbar.make(v, "Su dispositivo no tiene instalado WhatsApp", Snackbar.LENGTH_LONG).show();
                }

            }
        });



    }

    private void cargarEjercicios() {
        String [] ejers = {"Carrera","Bicicleta","Futbol","Natacion","Tenis"};
        spEjercicio.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,ejers));
    }

    private String textoTareasFormato() {
        String usuario = " *Frank Ocrospoma Ugaz:*\n";
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(usuario);
        for(String tarea: ejercicios){
            stringBuilder.append(tarea).append("\n");

        }
        return  stringBuilder.toString();
    }

    private void guardarTarea() {
        SharedPreferences preferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putStringSet(TASK_KEY, ejercicios);
        editor.apply();
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        return false;
    }
}