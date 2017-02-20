import java.util.concurrent.CancellationException
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.ExecutionException

object Main extends LazyLogging {
  def main(args: Array[String]): Unit = {
    val executor = new ScheduledThreadPoolExecutor(1)
    val errorHandler = new ScheduledThreadPoolExecutor(1)

    val runnable: Runnable = new Runnable {
      val exceptionsMap = new ConcurrentHashMap[Class[_ <: Throwable], Int]()
      val z = new AtomicInteger(1)
      override def run(): Unit = try {
        if (z.get % 3 == 0) {
          println(z.incrementAndGet())
          throw new IllegalStateException
        } else {
          println("Hello")
          println(z.incrementAndGet())
        }
      } catch {
        case t: Throwable =>
          logger.error("Exception in task", t)
          exceptionsMap.put(t.getClass, exceptionsMap.getOrDefault(t.getClass, 1) + 1)
          if (exceptionsMap.get(t.getClass) > 5) {
            throw new IllegalStateException(s"Exception of type ${t.getClass} was thrown more then 5 times. Ending!")
          }
      }
    }

    val future = executor.scheduleAtFixedRate(runnable, 0, 1, TimeUnit.SECONDS)
    errorHandler.execute(() =>
      try {
        future.get()
      } catch {
        case e: InterruptedException => logger.error("Scheduled execution was interrupted", e)
        case e: CancellationException => logger.error("Watcher thread has been cancelled", e)
        case e: ExecutionException =>
          logger.error(s"Uncaught exception in scheduled execution", e.getCause)
          future.cancel(true)
      })
  }
}
