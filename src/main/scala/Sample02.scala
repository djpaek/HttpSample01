import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory

import scala.io.StdIn

object Sample02 extends App {
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  val config = ConfigFactory.load()
  val host = config.getString("http.host")
  val port = config.getInt("http.port")


  val route = {
    get {
      path("") {
        complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "Hello World"))
      } ~
      path("crash"){
        sys.error("Crash!!")
      }
    }
  }

  val bindingFuture = Http().bindAndHandle(route, host, port)

  println(s"Server online at http://$host:$port/\nPress RETURN to stop...")
  StdIn.readLine() // let it run until user presses return
  bindingFuture
    .flatMap(_.unbind()) // trigger unbinding from the port
    .onComplete(_ => system.terminate()) // and shutdown when done

}
