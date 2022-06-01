package basic

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking



fun main() = runBlocking {
        repeat(100_000){
            launch {
                delay(1000)
                println(".")
            }
        }
}

//fun main() = runBlocking {
//    repeat(100_000){
//        thread {
//            Thread.sleep(1000)
//            println(".")
//        }
//    }
//}