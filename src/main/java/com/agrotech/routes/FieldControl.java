package com.agrotech.routes;

import com.agrotech.db.MongoManager;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class FieldControl {
    private static final Logger logger = LoggerFactory.getLogger(FieldControl.class);

    public static void main(String[] args) {
        try {
            MongoManager.init();
            List<Document> latest = MongoManager.getLatestBySensor();

            System.out.println("Lecturas más recientes por sensor (desde MongoDB):");
            System.out.println("------------------------------------------------");
            System.out.printf("%-10s %-12s %-8s %-12s%n", "ID Sensor", "Fecha", "Humedad", "Temperatura");
            System.out.println("------------------------------------------------");
            for (Document d : latest) {
                String idSensor = d.getString("id_sensor");
                String fecha = d.getString("fecha");
                Double humedad = d.getDouble("humedad");
                Double temperatura = d.getDouble("temperatura");
                System.out.printf("%-10s %-12s %-8.1f %-12.1f%n", idSensor, fecha, humedad != null ? humedad : 0.0, temperatura != null ? temperatura : 0.0);
            }
            System.out.println("------------------------------------------------");
        } catch (Exception e) {
            logger.error("Error en FieldControl: {}", e.getMessage(), e);
        } finally {
            MongoManager.close();
        }
    }
}