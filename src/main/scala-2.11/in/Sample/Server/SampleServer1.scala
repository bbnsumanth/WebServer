package in.Sample.Server

import java.io.PrintWriter
import java.net.{ServerSocket, Socket}

/**
 * Created by sumanth_bbn on 23/12/15.
 */
object SampleServer1 extends App{
    /**
    val text =
    <HTML>
      <HEAD>
        <TITLE>Hello world </TITLE>
      </HEAD>
      <BODY LANG="en-US" BGCOLOR="#e6e6ff" DIR="LTR">
        <P ALIGN="CENTER"> <FONT FACE="Arial, sans-serif" SIZE="6">Goodbye, World!</FONT> </P>
      </BODY>
    </HTML>
      **/
    val text = "Server is up and running"
    val port = 4040
    /**
     * A server socket waits for requests to coming over the network. It performs some operation
     * based on that request, and then possibly returns a result to the requester.
     * this will bind the created socket obj to the mentioned port by calling bind method.
     * bind method actually need socketAddress(InetSocketAddress) obj.InetAdress contains host and port
     * default queue length is 50,when a request comes when this queue is full,that request will be refused
     *
     */
    val serverSocket: ServerSocket = new ServerSocket(port)
    println(s"Listening at port $port")
    var count = 0
    //this code will be running in loop as long as the app is running
    while (true) {
      println(s"entered loop for $count")
      /**
       * A client socket is an endpoint for communication from client.
       * we get this from server socket obj which waits for request from a client.
       * accept method on ServerSocket is BLOCKING,it will BLOCK this thread until it got a request and  until a connection is made
       */
      val clientSocket: Socket = serverSocket.accept()
      //this code will execute when the thread got unblocked with a request
      println("got a request")
      Thread.sleep(5000)
      //if we got a request,write the response through the same socket
      new PrintWriter(clientSocket.getOutputStream(), true).println(text)
      println(s"served request $count")
      //close the socket after writing out response
      clientSocket.close()
      count += 1
    }
    //loop will be started again,wait for a request,if got the request, respond and get into loop again.
    //when we made multiple requests before we could respond to a previous request,these will be queued by ServerSocket
}
