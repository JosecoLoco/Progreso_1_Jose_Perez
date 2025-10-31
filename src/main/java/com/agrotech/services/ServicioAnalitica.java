package com.agrotech.services;

import org.apache.camel.Header;
import org.json.JSONArray;
import org.json.JSONObject;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ServicioAnalitica {
    private static final String SENSORES_FILE = "AgroAnalyzer/sensores.json";

    public String getUltimoValor(@Header("id_sensor") String id) throws Exception {
        System.out.println("\n[RPC] Servicio Analítica - Buscando datos del sensor: " + id);
        System.out.println("[RPC] Leyendo archivo: " + SENSORES_FILE);
        
        String jsonContent = Files.readString(Paths.get(SENSORES_FILE));
        JSONArray sensores = new JSONArray(jsonContent);
        
        System.out.println("[RPC] Total de sensores en archivo: " + sensores.length());
        
        for (int i = 0; i < sensores.length(); i++) {
            JSONObject sensor = sensores.getJSONObject(i);
            if (sensor.getString("id_sensor").equals(id)) {
                String resultado = String.format(
                    "{\"id\":\"%s\",\"humedad\":%s,\"temperatura\":%s,\"fecha\":\"%s\"}",
                    sensor.getString("id_sensor"),
                    sensor.getString("humedad"),
                    sensor.getString("temperatura"),
                    sensor.getString("fecha")
                );
                System.out.println("[RPC] Sensor encontrado -> " + resultado);
                return resultado;
            }
        }
        
        return String.format(
            "{\"error\":\"Sensor %s no encontrado\"}",
            id
        );
    }
}