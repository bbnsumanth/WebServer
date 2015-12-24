package in.Sample.Server

import java.io.PrintWriter
import java.net.{Socket, ServerSocket}
import java.util.concurrent
import java.util.concurrent.{Callable, Executors, ExecutorService}

class Blockingcall4(count:Int) extends Runnable{
  val counter = count
  def run ={
    val thread =Thread.currentThread.getName()
    println(s"entered blocking call for request $counter on thread: $thread")
    Thread.sleep(5000)
    println(s"finished blocking call for request $counter on thread: $thread")

  }
}

class Handler4(socket: Socket,counter:Int,handlerPool:ExecutorService) extends Runnable {

  def message = (s"server up and running.serving this request:$counter on thread:${Thread.currentThread.getName()} "+"\n")

  def run() {
    println(s"handling request for $counter on thread ${Thread.currentThread.getName()}")
    new PrintWriter(socket.getOutputStream(), true).println(message)
    socket.close()
    handlerPool.execute(new Blockingcall4(counter))
  }
}

object SampleServer4 extends App{
  val text = "Server is up and running"
  val port = 4040
  val poolSize = 10
  val handlerPoolSize = 4
  val serverSocket: ServerSocket = new ServerSocket(port)
  val pool: ExecutorService = Executors.newFixedThreadPool(poolSize)
  val handlerpool: ExecutorService = Executors.newFixedThreadPool(handlerPoolSize)

  println(s"Listening at port $port")
  var requestCount = 1
  //this code will be running in loop as long as the app is running
  try{
    while (true) {
      println(s"waiting for request number: $requestCount")
      val clientSocket: Socket = serverSocket.accept()
      println(s"request received,spawning a new thread for this request number $requestCount")
      //this server can handle max of 5 requests concurently
      pool.execute(new Handler4(clientSocket,requestCount,handlerpool))
      requestCount += 1
    }
  }finally {
    pool.shutdown()
  }




}
