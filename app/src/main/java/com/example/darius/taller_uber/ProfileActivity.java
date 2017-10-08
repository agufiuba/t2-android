package com.example.darius.taller_uber;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class ProfileActivity extends AppCompatActivity {

    private TextView nombre;
    private TextView apellido;
    private TextView lbl_nombre;
    private TextView lbl_apellido;
    private FirebaseAuth mAuth;
    private FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        load_layout_elements();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        configure_layout_elements();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

    /**
     * load_layout_elements
     * Carga los elementos de la interfaz y los asocia a un atributo.
     */
    private void load_layout_elements() {
        nombre = (TextView) findViewById(R.id.nombre_edit);
        apellido = (TextView) findViewById(R.id.apellido_edit);
        lbl_nombre = (TextView) findViewById(R.id.nombre_label);
        lbl_apellido = (TextView) findViewById(R.id.apellido_label);

    }

    private void configure_layout_elements() {
        lbl_nombre.setText(get_user_last_name());
        lbl_apellido.setText(get_user_first_name());
        nombre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popup_edittext("Nombre");
            }
        });
        apellido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popup_edittext("Apellido");
            }
        });
    }

    private void popup_edittext(final String titulo){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(titulo);
        final EditText input = new EditText(this);
        alert.setView(input);

        alert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String inputString = input.getText().toString();
                if (titulo.equals("Nombre") && inputString.length() != 0){
                    edit_user_last_name(inputString);
                    lbl_nombre.setText(inputString);
                }
                if (titulo.equals("Apellido") && inputString.length() != 0) {
                    edit_user_first_name(inputString);
                    lbl_apellido.setText(inputString);
                }
            }
        });

        alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //Nada
            }
        });
        alert.show();
    }

    private void edit_user_first_name(String firstName){
        String lastName;
        String new_name;

        lastName = get_user_last_name();
        new_name = lastName + " " + firstName;
        update_profile_name(new_name);
    }

    private void edit_user_last_name(String lastName){
        String firstName;
        String new_name;

        firstName = get_user_first_name();
        new_name = lastName + " " + firstName;
        update_profile_name(new_name);
    }

    private void update_profile_name(String new_name){
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
            .setDisplayName(new_name)
            .build();
        user.updateProfile(profileUpdates);
        
    }

    private String get_user_first_name(){
        String name;
        int i;

        name = user.getDisplayName();
        i = name.indexOf(" ");
        return name.substring(i+1,name.length());
    }

    private String get_user_last_name(){
        String name;
        int i;

        name = user.getDisplayName();
        i = name.indexOf(" ");
        return name.substring(0,i);
    }
}
