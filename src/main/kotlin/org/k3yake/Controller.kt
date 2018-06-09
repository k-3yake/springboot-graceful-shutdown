package org.k3yake

import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler


/**
 * Created by katsuki-miyake on 18/02/15.
 */
@ControllerAdvice
@RestController
class Controller: ResponseEntityExceptionHandler() {

    @GetMapping("/test")
    fun createCity(@RequestParam id:String):String {
        println("START id[${id}]")
        Thread.sleep(5 * 1000)
        println("END id[${id}]")
        return "${id} OK"
    }
}
