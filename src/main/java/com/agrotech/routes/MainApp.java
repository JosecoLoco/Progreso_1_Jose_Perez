package com.agrotech.routes;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.ProducerTemplate;
import com.agrotech.services.ServicioAnalitica;

public class MainApp {
    public static void main(String[] args) throws Exception {
        CamelContext context = new DefaultCamelContext();
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() {
                from("direct:solicitarLectura")
                    .log("\n[RPC] Cliente - Iniciando llamada RPC")
                    .log("[RPC] Cliente - Solicitando datos del sensor: ${body}")
                    .setHeader("id_sensor", simple("${body}"))
                    .bean(ServicioAnalitica.class, "getUltimoValor")
                    .log("[RPC] Cliente - Respuesta recibida: ${body}");
            }
        });

        context.start();
        ProducerTemplate template = context.createProducerTemplate();
        
        System.out.println("\n=== Simulación de Llamadas RPC ===");
        System.out.println("Este programa simula llamadas RPC entre componentes usando Apache Camel");
        System.out.println("------------------------------------------------------------");
        
        String[] sensores = {"S001", "S002", "S003", "S004"};
        for (String sensorId : sensores) {
            System.out.println("\n>> Iniciando nueva solicitud RPC <<");
            String response = template.requestBody("direct:solicitarLectura", sensorId, String.class);
            System.out.println("------------------------------------------------------------");
        }
        
        context.stop();
    }
}
