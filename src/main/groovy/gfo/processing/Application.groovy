package gfo.processing

import io.micronaut.context.annotation.Value
import io.micronaut.runtime.Micronaut
import io.micronaut.runtime.event.annotation.EventListener
import io.micronaut.runtime.server.event.ServerStartupEvent
import io.micronaut.scheduling.annotation.Async

import groovy.transform.CompileStatic

import org.apache.camel.CamelContext
import org.apache.camel.impl.DefaultCamelContext
import org.apache.camel.support.SimpleRegistry

class Application {



    ExtractJson extractJson


    Application(ExtractJson extractJson)
    {
        this.extractJson = extractJson
    }

    @EventListener
    @Async
    public void onStartup(ServerStartupEvent event) {
        SimpleRegistry registry = new SimpleRegistry()
        CamelContext context = new DefaultCamelContext(registry)

        context.addRoutes(extractJson)
        context.start();
    }

    static void main(String[] args) {
        Micronaut.run(Application)
    }
}
