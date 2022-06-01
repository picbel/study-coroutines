package basic

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    println("Hello ")
    myWorld()
}

suspend fun myWorld(){
    delay(1000)
    println("World!")
}
