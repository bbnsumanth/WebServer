package in.Sample.Server

import java.io.PrintWriter
import java.net.{Socket, ServerSocket}
import java.util.concurrent.{Future => JavaFuture}
import java.util.concurrent.{ Callable, Executors, ExecutorService}
import scala.concurrent.{ExecutionContext, ExecutionContextExecutor, Future}
import scala.concurrent.ExecutionContext.Implicits.global

class Blockingcall7(count:Int) extends Callable[(Int,String)]{
  val counter = count
  def call: (Int, String) ={
    val thread =Thread.currentThread.getName()
    println(s"entered blocking call for request $counter on thread: $thread")
    Thread.sleep(5000)
    (counter,thread)
  }
}

object Handler7{
  def run(socket: Socket,counter:Int,handlerPool:ExecutorService) = {
    val message = (s"server up and running.serving this request:$counter on thread:${Thread.currentThread.getName()} "+"\n")

    println(s"handling request: $counter on thread : ${Thread.currentThread.getName()}")
    new PrintWriter(socket.getOutputStream(), true).println(message)
    socket.close()
    val res: JavaFuture[(Int, String)] = handlerPool.submit[(Int,String)](new Blockingcall7(counter))
    val result: (Int, String) = res.get()
    println(s"finished execution of request: ${result._1} using result coming from thread : ${result._2} on thread ${Thread.currentThread.getName()}")
  }


}

object SampleServer7 extends App{
  val text = "Server is up and running"
  val port = 4040
  val poolSize = 10
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
      Future(Handler7.run(clientSocket,request,handlerpool))(ec)
      requestCount += 1
    }
}
