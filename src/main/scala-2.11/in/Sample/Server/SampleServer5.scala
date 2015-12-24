package in.Sample.Server

import java.io.PrintWriter
import java.net.{Socket, ServerSocket}
import java.util.concurrent
import java.util.concurrent.{Future, Callable, Executors, ExecutorService}

class Blockingcall5(count:Int) extends Callable[(Int,String)]{
  val counter = count
  def call: (Int, String) ={
    val thread =Thread.currentThread.getName()
    println(s"entered blocking call for request $counter on thread: $thread")
    Thread.sleep(5000)
    (counter,thread)
  }
}

class Handler5(socket: Socket,counter:Int,handlerPool:ExecutorService) extends Runnable {

  def message = (s"server up and running.serving this request:$counter on thread:${Thread.currentThread.getName()} "+"\n")

  def run() {
    println(s"handling request: $counter on thread : ${Thread.currentThread.getName()}")
    new PrintWriter(socket.getOutputStream(), true).println(message)
    socket.close()
    val res: Future[(Int, String)] = handlerPool.submit[(Int,String)](new Blockingcall5(counter))
    val result = res.get()
    println(s"finished execution of request: ${result._1} using result coming from thread : ${result._2} on thread ${Thread.currentThread.getName()}")
  }
}

object SampleServer5 extends App{
  val text = "Server is up and running"
  val port = 4040
  val poolSize = 20
  val handleerPoolSize = 5
  val serverSocket: ServerSocket = new ServerSocket(port)
  val pool: ExecutorService = Executors.newFixedThreadPool(poolSize)
  val handlerpool: ExecutorService = Executors.newFixedThreadPool(handleerPoolSize)

  println(s"Listening at port $port")
  var requestCount = 1
  //this code will be running in loop as long as the app is running
  try{
    while (true) {
      println(s"waiting for request number: $requestCount")
      val clientSocket: Socket = serverSocket.accept()
      println(s"request received,spawning a new thread for this request number $requestCount")
      //this server can handle max of 5 requests concurently
      pool.execute(new Handler5(clientSocket,requestCount,handlerpool))
      requestCount += 1
    }
  }finally {
    pool.shutdown()
  }




}
