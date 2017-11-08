package com.example.darius.taller_uber;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class JSONTests {

    private String[] setFromJson(JSONObject jsonObject){
        String[] options;
        JSONArray arr;
        List<String> _options = new ArrayList<String>();
        try {
            arr = jsonObject.getJSONArray("parameters");

            for(int i = 0; i < arr.length(); i++){
                _options.add(arr.getString(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        options =(String[])_options.toArray();
        return options;
    }

    @Test
    public void addition_isCorrect() throws Exception {
        JSONObject jsonObject = new JSONObject();
        String cs[] = {"Negro", "Amarillo", "Azul", "Rojo",
            "Gris", "Blanco", "Verde"};
        JSONArray cs2 = new JSONArray();
        jsonObject.put("parameters",cs);
        String prueba[] = setFromJson(jsonObject);
    }
}