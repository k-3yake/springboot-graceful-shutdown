package org.k3yake

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import javax.annotation.PostConstruct

/**
 * Created by katsuki-miyake on 18/04/28.
 */
@ControllerAdvice
@RestController
class SystemApi {
    private var redy = false;

    @ExceptionHandler(IllegalStateException::class)
    fun handleEntityNotFound(ex: IllegalStateException): ResponseEntity<Any> {
        return ResponseEntity(ex.message!!, HttpStatus.SERVICE_UNAVAILABLE)
    }

    @GetMapping("/ready")
    fun reday():String {
        if(redy) {
            return "Reday!!"
        }else {
            throw IllegalStateException("Not Ready!")
        }
    }

    @GetMapping("/long-time-response")
    fun longTimeResponse():String {
      println("called long-time-response")
      Thread.sleep(30 * 1000)
      println("return called long-time-response")
      return "OK"
    }


}