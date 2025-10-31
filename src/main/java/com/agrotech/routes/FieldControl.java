package com.agrotech.routes;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.ProducerTemplate;

public class FieldControl extends RouteBuilder {
    @Override
    public void configure() {
        from("direct:solicitarLectura")
            .log("[RPC] Cliente - Enviando solicitud para sensor: ${body}")
            .setHeader("id_sensor", simple("${body}"))
            .bean(com.agrotech.services.ServicioAnalitica.class, "getUltimoValor")
            .log("[RPC] Cliente - Respuesta recibida: ${body}");
    }

    public static void main(String[] args) throws Exception {
        System.out.println("\n=== Cliente RPC - Iniciando ===");
        System.out.println("Este programa actúa como cliente RPC para consultar datos de sensores");
        System.out.println("------------------------------------------------------------");

        CamelContext context = new DefaultCamelContext();
        context.addRoutes(new FieldControl());
        context.start();

        ProducerTemplate template = context.createProducerTemplate();
        String[] sensores = {"S001", "S002", "S003"};
        
        for (String sensorId : sensores) {
            System.out.println("\n>> Realizando llamada RPC para sensor: " + sensorId + " <<");
            String response = template.requestBody("direct:solicitarLectura", sensorId, String.class);
            System.out.println("------------------------------------------------------------");
        }

        context.stop();
        System.out.println("\n=== Cliente RPC - Finalizado ===");
    }
}
