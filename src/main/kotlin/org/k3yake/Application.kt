package org.k3yake

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import java.util.concurrent.TimeUnit
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory
import org.springframework.boot.web.server.WebServerFactoryCustomizer
import sun.awt.image.SurfaceManager.setManager
import org.apache.catalina.session.StandardManager
import org.apache.catalina.Manager




/**
 * Created by katsuki-miyake on 18/03/04.
 */
@SpringBootApplication
open class Application {

    @Value("\${catalina.threadpool.execution.timeout.seconds}")
    var shutdownTimeoutSeconds: Long = 30

    @Bean
    fun gracefulShutdown(): GracefulShutdown {
        return GracefulShutdown(shutdownTimeoutSeconds, TimeUnit.SECONDS)
    }

    @Bean
    fun tomcatCustomizer(): WebServerFactoryCustomizer<TomcatServletWebServerFactory> {
        return WebServerFactoryCustomizer { container ->
            if (container is TomcatServletWebServerFactory) {
                container.addConnectorCustomizers(gracefulShutdown())
            }
        }

/*
        return EmbeddedServletContainerCustomizer { container ->
            if (container is TomcatEmbeddedServletContainerFactory) {
                container.addConnectorCustomizers(gracefulShutdown())
            }
        }
*/
    }
}

fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}


