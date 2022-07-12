package com.example.weather;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {
    EditText txtCiudad, txtCP;
    TextView tvConsulta;
    private final String url = "https://api.openweathermap.org/data/2.5/weather";
    private final String appid = "eedec0fab45e1d6f3de8aa7e038114e4";
    DecimalFormat df = new DecimalFormat("#.##");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtCiudad = findViewById(R.id.txtCiudad);
        txtCP = findViewById(R.id.txtCP);
        tvConsulta = findViewById(R.id.tvConsulta);
    }

    public void getClimaDetalle(View view) {
        String tempUrl = "";
        String ciudad = txtCiudad.getText().toString().trim();
        String cp = txtCP.getText().toString().trim();
        if (ciudad.equals("")) {
            tvConsulta.setText("El campo de ciudad no puede quedar vacío.");
        } else {
            if (!cp.equals("")) {
                tempUrl = url + "?q=" + ciudad + "," + cp + "&appid=" + appid;
            } else {
                tempUrl = url + "?q=" + ciudad + "&appid=" + appid;
            }
            StringRequest stringRequest = new StringRequest(Request.Method.POST, tempUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    //Log.d("response", response);
                    String output = "";
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        JSONArray jsonArray = jsonResponse.getJSONArray("weather");
                        JSONObject jsonObjectClima = jsonArray.getJSONObject(0);
                        String descripcion = jsonObjectClima.getString("description");
                        JSONObject jsonObjectMain = jsonResponse.getJSONObject("main");
                        double temp = jsonObjectMain.getDouble("temp") - 273.15;
                        double sensacion = jsonObjectMain.getDouble("feels_like") - 273.15;
                        float presion = jsonObjectMain.getInt("pressure");
                        int humedad = jsonObjectMain.getInt("humidity");
                        JSONObject jsonObjectWind = jsonResponse.getJSONObject("wind");
                        String viento = jsonObjectWind.getString("speed");
                        JSONObject jsonObjectClouds = jsonResponse.getJSONObject("clouds");
                        String nubes = jsonObjectClouds.getString("all");
                        JSONObject jsonObjectSys = jsonResponse.getJSONObject("sys");
                        String pais = jsonObjectSys.getString("country");
                        String cp = jsonResponse.getString("name");
                        tvConsulta.setTextColor(Color.rgb(255, 255, 255));
                        output += "El clima actual de " + cp + " (" + pais + ")" +
                                "\nTemperatura: " + df.format(temp) + "°C" +
                                "\nSensación Térmica: " + df.format(sensacion) + "°C" +
                                "\nHumedad: " + humedad + "%" +
                                "\nDescripción: " + descripcion +
                                "\nVelocidad del Viento: " + viento + "m/s (metros por segundo)" +
                                "\nNubosidad: " + nubes + "%" +
                                "\nPresión: " + presion + "hPa";
                        tvConsulta.setText(output);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), error.toString().trim(), Toast.LENGTH_SHORT).show();
                }
            });
            RequestQueue rq = Volley.newRequestQueue(getApplicationContext());
            rq.add(stringRequest);
        }

    }
}