import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.coroutines.newFixedThreadPoolContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import java.lang.Exception
import java.net.HttpRetryException
import kotlin.system.measureTimeMillis


fun main() = runBlocking<Unit> {
    try {
        worstEx1()
    } catch (_ : Throwable) {}
    try {
        worstEx2()
    } catch (_ : Throwable) {}
    ex1()
    ex2()
    bestEx()
    bestEx2()
}

suspend fun worstEx1() = runBlocking {
    this.launch {
        val job = launch {
            try { // 코루틴 내부에서 try catch
                delay(500L)
            } catch (e: Exception) {
                e.printStackTrace() // delay 중단자에서 취소익셉션을 여기서 캐치해버린다
            }
            println("worstEx1 Coroutine 1 finished")
        }
        delay(300L)
        job.cancel()
    }
}

/*
    코루틴 내부에서 try catch하지 않고 예외를 launch 밖에서 잡을려 시도
    launch는 예외를 즉시 부모코루틴으로 전파시킨다
    async는 예외를 즉시 전파시키진 않고 작업을 확인할때 결과 혹은 예외를 작업확인 시점에 전달한다
 */
suspend fun worstEx2() = runBlocking {
    this.launch {  // 4. 예외가 처리되지 않고 root 코루틴까지 예외가 전파됨(이 시점에 앱 크래시 발생)

        try {
            launch {  // 3. 여전히 예외가 처리되지 않았으므로 이 코루틴으로도 예외가 전파됨
                launch {  // 2. 현재 코루틴으로 예외가 전파됨(propagation)
                    throw Exception()  // 1. 예외 발생
                }
            }
        } catch (e: Exception) {
            println("Caught Exception: $e")
        }
    }
}


suspend fun ex1() = runBlocking {
    this.launch { // 2. CancellationException이 부모 Scope까지 제대로 전파되어
        val job = launch {
            try {
                delay(500L)
            } catch (e: HttpRetryException) {  // 1. CancellationException이 잡히지 않으므로
                e.printStackTrace()
            }
            println("Coroutine 1 finished")  // 3. 이 라인의 작업을 실행하지 않는다.
        }
        delay(300L)
        job.cancel()
    }
    delay(500)
}

suspend fun ex2() = runBlocking {
    this.launch {  // 2. CancellationException이 부모 Scope까지 제대로 전파되도록 한다.
        val job = launch {
            try {
                delay(500L)
            } catch (e: Exception) {
                if (e is CancellationException) {
                    throw e  // 1. CancellationException일 경우 예외를 다시 던져
                }
                e.printStackTrace()
            }
            println("Coroutine 1 finished")
        }
        delay(300L)
        job.cancel()
    }
    delay(500)
}

// 익셉션 핸들러를 이용하여 해당 스코프의 코루틴이 실패할시 모든 코루틴을 취소한다
suspend fun bestEx() = runBlocking {
    val handler = CoroutineExceptionHandler { _, throwable ->
        println("bestEx Caught exception: $throwable")
    }

// + 연산자를 통해 2개의 CoroutineContext를 합쳐서 CoroutineScope에 적용
    CoroutineScope(handler).launch {
        launch {
            delay(300L)
            throw Exception("bestEx Coroutine 1 failed")
        }
        launch {
            delay(400L)
            println("bestEx Coroutine 2 finished")
        }
    }
    delay(500)
}


// supervisorScope는 해당 스코프의 코루틴이 하나 실패하더라도 다른 코루틴들에는 영향을 미치치않게 할 수 있다
suspend fun bestEx2() = runBlocking {
    val handler = CoroutineExceptionHandler { _, throwable ->
        println("bestEx2 Caught exception: $throwable")
    }

    CoroutineScope(handler).launch {
        supervisorScope {  // 자식 코루틴들을 supervisorScope 내부에 넣는다.
            launch {
                delay(300L)
                throw Exception("bestEx2 Coroutine 1 failed")
            }
            launch {
                delay(400L)
                println("bestEx2 Coroutine 2 finished")
            }
        }
    }
    delay(500)
}

