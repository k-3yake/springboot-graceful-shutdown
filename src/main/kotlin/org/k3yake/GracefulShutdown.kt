package org.k3yake

import org.apache.catalina.connector.Connector
import org.apache.tomcat.util.threads.ThreadPoolExecutor
import org.slf4j.LoggerFactory
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextClosedEvent
import java.util.concurrent.TimeUnit
/**
 * Created by katsuki-miyake on 18/05/14.
 */
class GracefulShutdown(val shutdownTimeout: Long, val unit: TimeUnit) : TomcatConnectorCustomizer, ApplicationListener<ContextClosedEvent> {

  @Volatile private var connector: Connector? = null

  override fun customize(connector: Connector) {
    this.connector = connector
  }

  override fun onApplicationEvent(event: ContextClosedEvent) {
    if (this.connector == null) {
      return
    }
    awaitTermination(this.connector!!)
  }

  fun awaitTermination(connector: Connector) {
    connector.pause()
    val executor = connector.protocolHandler.executor
    if (executor is ThreadPoolExecutor) {
      log.warn("Context closed. Going to await termination for $shutdownTimeout $unit.")
      try {
        executor.shutdown()
        if (!executor.awaitTermination(shutdownTimeout, unit)) {
          log.warn("Tomcat thread pool did not shut down gracefully within $shutdownTimeout $unit. Proceeding with forceful shutdown")
        }
      } catch (ex: InterruptedException) {
        Thread.currentThread().interrupt()
      }
    }
  }

  companion object {
    private val log = LoggerFactory.getLogger(GracefulShutdown::class.java)
  }
}