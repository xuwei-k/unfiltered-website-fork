import unfiltered.netty
import unfiltered.netty.websockets._

object example_netty_websockets {

locally {
// #example1
object Main {
  def main(args: Array[String]) {
    var sockets = new scala.collection.mutable.ListBuffer[WebSocket]()
    WebSocketServer("/", 8080) {
      case Open(s) => sockets += s
      case Message(s, Text(str)) => sockets foreach(_.send(str.reverse))
      case Close(s) => sockets -= s
      case Error(s, e) => println("error %s" format e.getMessage)
    }
  }
}
// #example1
}

type X =
// #example2
PartialFunction[netty.RequestBinding, PartialFunction[netty.websocket.SocketCallback => Unit]]
// #example2


// #example3
netty.websockets.Planify({
  case GET(Path("/")) => {
    case Open(socket) => ???
    case Message(s, Text(str)) => ???
    case _ => ???
  }
})
// #example3


object tweets {
  def onTweet(f: String => Unit): Unit = ???
}
// #example4
object Main {
  def main(args: Array[String]) {
    WebSocketServer("/twttr", 8080) {
      case Open(s) => tweets onTweet { twt =>
        s.send(twt)
      }
    }
  }
}
// #example4


// #example5
unfiltered.netty.Server.http(8080)
  .handler(netty.websockets.Planify({
    case Path("/foo") => {
      case Open(socket) =>
        socket.send("push!")
    }
  })
  // onPass overrides default connection closing on invalid websocket requests
  // and sends control flow  upstream to the next ChannelHandler
  .onPass(_.fireChannelRead(_)))
  .handler(unfiltered.netty.channel.Planify({
    case Path("/bar") =>
      ???
  }))
// #example5

}
