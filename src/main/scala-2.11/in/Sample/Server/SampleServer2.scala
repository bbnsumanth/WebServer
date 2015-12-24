package in.Sample.Server

import java.io.PrintWriter
import java.net.{Socket, ServerSocket}

class Handler2(socket: Socket,counter:Int) extends Runnable {
  def message = ("server up and running in "+Thread.currentThread.getName() + s" for request $counter"+"\n")
  def run() {
    println(s"handling request for $counter")
    new PrintWriter(socket.getOutputStream(), true).println(message)
    socket.close()
    Thread.sleep(5000)
  }
}


object SampleServer2 extends App{
  val text = "Server is up and running"
  val port = 4040
  val serverSocket: ServerSocket = new ServerSocket(port)
  println(s"Listening at port $port")
  var count = 1
  //this code will be running in loop as long as the app is running
  while (true) {
    println(s"entered loop for $count")
    val clientSocket: Socket = serverSocket.accept()
    println(s"spawing a new thread for this reuest number $count")
    //create a new thread thread for this request
    val thread = new Thread(new Handler2(clientSocket,count))
    //start the thread
    thread.start()
    //now this thread won't wait until the response is send,this can now handle another request.
    count += 1
  }



 }
