package gfo.processing

import io.micronaut.runtime.Micronaut
import io.micronaut.runtime.event.annotation.EventListener
import io.micronaut.runtime.server.event.ServerStartupEvent
import io.micronaut.scheduling.annotation.Async
import org.apache.camel.CamelContext
import org.apache.camel.impl.DefaultCamelContext
import org.apache.camel.support.SimpleRegistry

class Application {

    SqsS3Processor sqsS3Processor

    Application(SqsS3Processor sqsS3Processor)
    {
        this.sqsS3Processor = sqsS3Processor
    }

    @EventListener
    @Async
    public void onStartup(ServerStartupEvent event) {
        SimpleRegistry registry = new SimpleRegistry()
        CamelContext context = new DefaultCamelContext(registry)

        context.addRoutes(sqsS3Processor)
        context.start();
    }

    static void main(String[] args) {
        Micronaut.run(Application)
    }
}
