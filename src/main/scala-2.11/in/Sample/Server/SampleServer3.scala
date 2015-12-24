package in.Sample.Server

import java.io.PrintWriter
import java.net.{Socket, ServerSocket}
import java.util.concurrent.{Executors, ExecutorService}

class Handler3(socket: Socket,counter:Int) extends Runnable {
  def message = ("server up and running in "+Thread.currentThread.getName() + s" for request $counter"+"\n")
  def blocking = Thread.sleep(5000)
  def run() {
    println(s"handling request for $counter")
    new PrintWriter(socket.getOutputStream(), true).println(message)
    socket.close()
    blocking

  }
}

object SampleServer3 extends App{
  val text = "Server is up and running"
  val port = 4040
  val poolSize = 5
  val serverSocket: ServerSocket = new ServerSocket(port)
  val pool: ExecutorService = Executors.newFixedThreadPool(poolSize)
  println(s"Listening at port $port")
  var count = 1
  //this code will be running in loop as long as the app is running
  try{
    while (true) {
      println(s"entered loop for $count")
      val clientSocket: Socket = serverSocket.accept()
      println(s"spawing a new thread for this reuest number $count")
      //this server can handle max of 5 requests concurently
      pool.execute(new Handler3(clientSocket,count))
      count += 1
    }
  }finally {
    pool.shutdown()
  }




}
