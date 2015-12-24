val hello = new Thread(new Runnable {
  def run() {
    println("hello world")
  }
})
hello.start()

