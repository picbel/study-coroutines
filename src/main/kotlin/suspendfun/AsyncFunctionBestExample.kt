import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.system.measureTimeMillis


// 글로벌 스코프로 async는 사용하지 말자
// async를 묶어서 사용하는 suspend로 한번더 묶고 coroutineScope로 스코프를 분리한다
fun main() = runBlocking<Unit> {
    measureTimeMillis {
        println("시작")
        val resultSum = resultSum()
        println("resultSum = $resultSum")
    }.also {
        println("실행 시간:$it")
    }
}

private suspend fun resultSum() : Int = coroutineScope {
    val a  = async { asyncFunctions() }
    val b = async { asyncFunctions2() }
    awaitAll(a, b).sum().also {
        println("비동기 결과 합산 : $it")
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