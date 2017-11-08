package com.example.darius.taller_uber;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONException;
import org.json.JSONObject;

public class PaymentActivity extends AppCompatActivity implements URL_local{

    CheckBox visaCheck;
    Button confirmar;
    EditText numero, expMonth, expYear, ccvv;
    GridLayout inputLayout;
    private RequestQueue queue;

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
        numero = (EditText) findViewById(R.id.input_code);
        inputLayout = (GridLayout) findViewById(R.id.input_layout);
        expMonth = (EditText) findViewById(R.id.expiration_month);
        expYear = (EditText) findViewById(R.id.expiration_year);
        ccvv = (EditText) findViewById(R.id.ccvv);
        queue = Volley.newRequestQueue(this);
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
                    sendSignUpRequest();
                }
            }
        });
    }

    private void sendSignUpRequest() {
        try {
            final JSONObject passenger_json = new JSONObject();
            final JSONObject pago_json = new JSONObject();

            passenger_json.put("type", getIntent().getStringExtra("type"));
            passenger_json.put("name", getIntent().getStringExtra("name"));
            passenger_json.put("last_name", getIntent().getStringExtra("last_name"));
            passenger_json.put("mail", getIntent().getStringExtra("mail"));

            pago_json.put("expiration_month", expMonth.getText().toString());
            pago_json.put("expiration_year", expYear.getText().toString());
            pago_json.put("method","card");
            pago_json.put("type","visa");
            pago_json.put("number",numero.getText().toString());
            pago_json.put("ccvv",ccvv.getText().toString());

            passenger_json.put("credenciales",pago_json);

            Comunicador comunicador =
                new Comunicador(FirebaseAuth.getInstance().getCurrentUser(),this);
            comunicador.requestFree(new onSignUpSuccess(), new onSignUpFailure(),
                url_user,passenger_json,Request.Method.POST);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class onSignUpSuccess extends RequestHandler {
        @Override
        public void run() {
            startActivity(new Intent(PaymentActivity.this, MainActivity.class));
        }
    }

    private class onSignUpFailure extends RequestHandler {
        @Override
        public void run() {
            //TODO implementar
        }
    }
}
