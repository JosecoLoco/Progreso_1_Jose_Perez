package com.agrotech.db;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.conversions.Bson;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class MongoManager {
    private static final Logger logger = LoggerFactory.getLogger(MongoManager.class);
    private static MongoClient client;
    private static MongoDatabase database;
    private static MongoCollection<Document> collection;
    private static final String DEFAULT_URI = "mongodb://localhost:27017";
    private static final String DB_NAME = "agrodb";
    private static final String COLL_NAME = "lecturas";

    public static void init() {
        String uri = System.getenv("MONGO_URI");
        if (uri == null || uri.isBlank()) {
            uri = DEFAULT_URI;
        }
        logger.info("Conectando a MongoDB en: {}", uri);
        client = MongoClients.create(uri);
        database = client.getDatabase(DB_NAME);
        collection = database.getCollection(COLL_NAME);
        // crear índices si es necesario
        collection.createIndex(Indexes.ascending("id_sensor", "fecha"));
        logger.info("MongoDB inicializado (db: {}, collection: {})", DB_NAME, COLL_NAME);
    }

    public static void insertLectura(String idSensor, String fecha, double humedad, double temperatura) {
        try {
            Document doc = new Document("id_sensor", idSensor)
                    .append("fecha", fecha)
                    .append("humedad", humedad)
                    .append("temperatura", temperatura);
            collection.insertOne(doc);
            logger.debug("Insertada lectura en MongoDB para {}", idSensor);
        } catch (Exception e) {
            logger.error("Error insertando en MongoDB: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public static List<Document> getLatestBySensor() {
        // Usamos agregación simple: ordenar por id_sensor y fecha desc, luego tomar el primer por sensor
        // Implementación simple: obtener distinct sensors y buscar latest per sensor (suffices para demo)
        List<Document> result = new ArrayList<>();
        try {
            List<String> sensors = collection.distinct("id_sensor", String.class).into(new ArrayList<>());
            for (String s : sensors) {
                Document doc = collection.find(Filters.eq("id_sensor", s))
                        .sort(Sorts.descending("fecha"))
                        .limit(1)
                        .projection(Projections.include("id_sensor", "fecha", "humedad", "temperatura"))
                        .first();
                if (doc != null) result.add(doc);
            }
            return result;
        } catch (Exception e) {
            logger.error("Error obteniendo últimas lecturas de MongoDB: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public static void close() {
        if (client != null) client.close();
    }
}
