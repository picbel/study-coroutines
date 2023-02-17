import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.system.measureTimeMillis


// 글로벌 스코프로 async는 사용하지 말자
// async를 묶어서 사용하는 suspend로 한번더 묶고 coroutineScope로 스코프를 분리한다

/*
 하면 안되는 이유 글로벌 스코프로 async를 하면 익셉션이 발생했을때 코루틴 스코프에 취소 전파가 되지않는다
 예를들어 메인 스코프 안에서 async가 작성되거나 코루틴스코프를 생성하여 하는경우엔 메인스코프안에 자식 스코프이기때문에 익셉션으로 취소시에 async에도 취소가 전파가 되지만
 글로벌 스코프로 async를 작성하면 메인 스코프에 별개의 스코프에서 실행되기때문에 익셥션을 이용한 취소 전파가 되지 않기때문입니다.
 */

fun main() = runBlocking<Unit> {
    measureTimeMillis {
        println("시작")
        val resultSum = resultSum()
        println("resultSum = $resultSum")
    }.also {
        println("실행 시간:$it")
    }
}

private suspend fun resultSum(): Int = coroutineScope {
    val a = async { asyncFunctions() }
    val b = async { asyncFunctions2() }
    awaitAll(a, b).sum().also {
        println("비동기 결과 합산 : $it")
    }
}

private suspend fun asyncFunctions(): Int {
    delay(1000L)
    return 3
}

private suspend fun asyncFunctions2(): Int {
    delay(1000L)
    return 7
}