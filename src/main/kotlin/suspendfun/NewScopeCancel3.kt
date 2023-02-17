import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.newFixedThreadPoolContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.lang.Exception
import kotlin.system.measureTimeMillis

/*
실행결과
시작
첫번째 함수 호출
두번째 함수 호출
asyncFunctions2
asyncFunctions
취소
asyncFunctions is alive

main 스코프가 아닌 다른 스코프에서 실행된 s1의 async는 결국 취소되지않았다
코루틴스코프의 취소나 예외는 같은 스코프에서만 전파된다
 */
fun main() = runBlocking<Unit> {
    val job = launch {
        println("시작")
        s1.async { asyncFunctions() }
        println("첫번째 함수 호출")
        async { asyncFunctions2() }
        println("두번째 함수 호출")


    }
    delay(1100)
    println("취소")
    job.cancelAndJoin()
    delay(2000)
}
val s1 = CoroutineScope(Dispatchers.Default)
private suspend fun asyncFunctions(): Int {
    delay(1000)
    println("asyncFunctions")
    delay(500)
    println("asyncFunctions is alive")
    return 3
}

private suspend fun asyncFunctions2(): Int {
    delay(1000)
    println("asyncFunctions2")
    delay(500)
    println("asyncFunctions2 is dead")
    return 7
}