
package in.Sample.Server

import java.io.PrintWriter
import java.net.{Socket, ServerSocket}
import java.util.concurrent.{Future => JavaFuture}
import java.util.concurrent.{ Callable, Executors, ExecutorService}
import scala.concurrent.{ExecutionContext, ExecutionContextExecutor, Future}
import scala.concurrent.ExecutionContext.Implicits.global

object BlockingCall8{
  def blocking(counter:Int) ={
    val thread =Thread.currentThread.getName()
    println(s"entered blocking call for request $counter on thread: $thread")
    Thread.sleep(50000)
    (counter,thread)
  }
}


object Handler8 {
  def run(socket: Socket,counter:Int,handlerPool:ExecutorService) = {
    val message = (s"server up and running.serving this request:$counter on thread:${Thread.currentThread.getName()} "+"\n")

    println(s"handling request: $counter on thread : ${Thread.currentThread.getName()}")
    new PrintWriter(socket.getOutputStream(), true).println(message)
    socket.close()
    val fut = Future(BlockingCall8.blocking(counter))
    fut.foreach(x =>  println(s"finished execution of request: ${x._1} using result coming from thread : ${x._2} on thread ${Thread.currentThread.getName()}"))
  }


}

object SampleServer8 extends App{
  val text = "Server is up and running"
  val port = 4040
  val poolSize = 5
  val handleerPoolSize = 2
  val serverSocket: ServerSocket = new ServerSocket(port)
  val handlerpool: ExecutorService = Executors.newFixedThreadPool(handleerPoolSize)
  implicit val ec: ExecutionContextExecutor = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(poolSize))

  println(s"Listening at port $port")
  var requestCount = 1
  //this code will be running in loop as long as the app is running
  while (true) {
    println(s"waiting for request number: $requestCount on thread:${Thread.currentThread.getName()} ")
    //this is very imp => if we passed requestCountdirectly to future body,its value might get changed by the time body is executed
    val request = requestCount
    val clientSocket: Socket = serverSocket.accept()
    println(s"request received,making a future call to process this request request number $requestCount")
    Future(Handler8.run(clientSocket,request,handlerpool))(ec)
    requestCount += 1
  }
}
