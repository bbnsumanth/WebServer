package in.Sample.Server

import java.io.PrintWriter
import java.net.{Socket, ServerSocket}
import java.util.concurrent.{Executors, ExecutorService}
import scala.concurrent.ExecutionContext.Implicits.global


import scala.concurrent.{ExecutionContextExecutor, ExecutionContext, Future}

object BlockingCall6{
  def blocking(counter:Int) ={
    val thread =Thread.currentThread.getName()
    println(s"entered blocking call for request $counter on thread: $thread")
    Thread.sleep(5000)
    (counter,thread)
  }
}
class Handler6(socket: Socket,counter:Int) extends Runnable {

  def message = (s"server up and running.serving this request:$counter on thread:${Thread.currentThread.getName()} "+"\n")
  def run() {
    println(s"handling request for $counter on thread : ${Thread.currentThread.getName()}")
    new PrintWriter(socket.getOutputStream(), true).println(message)
    socket.close()
    //implicit execution context uses thread pool sixe equals to cores on machine
    val fut = Future(BlockingCall6.blocking(counter))
    fut.foreach(x =>  println(s"finished execution of request: ${x._1} using result coming from thread : ${x._2} on thread ${Thread.currentThread.getName()}"))

  }
}

object SampleServer6 extends App{
  val text = "Server is up and running"
  val port = 4040
  val poolSize = 5
  val serverSocket: ServerSocket = new ServerSocket(port)
  val pool: ExecutorService = Executors.newFixedThreadPool(poolSize)
  //implicit val ec: ExecutionContextExecutor = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(2))


  println(s"Listening at port $port")
  var requestCount = 1
  //this code will be running in loop as long as the app is running
  try{
    while (true) {
      println(s"waiting for request number: $requestCount")
      val clientSocket: Socket = serverSocket.accept()
      println(s"request received,spawning a new thread for this request number $requestCount")
      //this server can handle max of 5 requests concurently
      pool.execute(new Handler6(clientSocket,requestCount))
      requestCount += 1
    }
  }finally {
    pool.shutdown()
  }




}
