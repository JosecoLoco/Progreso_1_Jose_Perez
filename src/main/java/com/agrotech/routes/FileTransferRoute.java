package com.agrotech.routes;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;

public class FileTransferRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        from("file:C:/Users/Jose Perez/Desarrollo/evaluacion-transferencia-datos/SensData?noop=false")
            .routeId("transferSensData")
            .log(" Archivo detectado: ${header.CamelFileName}")
            .unmarshal().csv()
            .marshal().json(JsonLibrary.Jackson)
            .to("file:C:/Users/Jose Perez/Desarrollo/evaluacion-transferencia-datos/AgroAnalyzer?fileName=sensores.json")
            .log(" Archivo JSON enviado al módulo AgroAnalyzer");
    }
}

