package com.example.darius.taller_uber;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PaymentActivity extends AppCompatActivity {

    CheckBox visaCheck;
    Button confirmar, omitir;
    TextView numero;
    LinearLayout inputLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        load_layout_elements();
        configure_layout_elements();
    }

    private void load_layout_elements() {
        visaCheck = (CheckBox) findViewById(R.id.visaCheck);
        confirmar = (Button) findViewById(R.id.confirmar);
        omitir = (Button) findViewById(R.id.omitir);
        numero = (TextView) findViewById(R.id.input_code);
        inputLayout = (LinearLayout) findViewById(R.id.input_layout);
    }

    private void configure_layout_elements() {
        visaCheck.setOnCheckedChangeListener(
            new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (visaCheck.isChecked()) {
                        inputLayout.setVisibility(View.VISIBLE);
                    } else {
                        inputLayout.setVisibility(View.GONE);
                    }
                }
            });

        confirmar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if (!visaCheck.isChecked()){
                    Snackbar.make(view, "Seleccione un método de pago.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                } else {
                    //TODO mejorar la validacion
                    //TODO hacer logica de transmision de metodo de pago al app server
                    startActivity( new Intent(PaymentActivity.this, MainActivity.class));
                }
            }
        });

        omitir.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                startActivity( new Intent(PaymentActivity.this, MainActivity.class));
            }
        });
    }
}
