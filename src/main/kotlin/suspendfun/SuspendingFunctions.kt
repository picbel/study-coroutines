import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.system.measureTimeMillis

fun main() = runBlocking<Unit> {
    measureTimeMillis {
        println("시작")
        val result = asyncFunctions()
        println("첫번째 함수 호출")
        val result2 = asyncFunctions2()
        println("두번째 함수 호출")
        println("비동기 결과 합산 : ${result+result2}")
    }.also {
        println("실행 시간:$it")
    }
}

private suspend fun asyncFunctions() : Int {
    delay(1000L)
    return 3
}
private suspend fun asyncFunctions2() : Int {
    delay(1000L)
    return 7
}