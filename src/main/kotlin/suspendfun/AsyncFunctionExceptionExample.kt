package suspendfun

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

fun main() = runBlocking<Unit> {
    try {
        val sum = failedConcurrentSum()
        println("출력이 안되어야 함 result : $sum")
    } catch(e: ArithmeticException) {
        println("main 함수 캐치")
    }
}

suspend fun failedConcurrentSum(): Int = coroutineScope {
    val one = async<Int> {
        try {
            delay(Long.MAX_VALUE)
            42
        } finally {
            println("첫번째 async finally")
        }
    }
    val two = async<Int> {
        println("Exception 발생")
        throw ArithmeticException()
    }
    one.await() + two.await()
}