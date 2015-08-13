package com.justinrmiller.communicator

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import com.justinrmiller.communicator.models.Users
import spray.can.Http
import scala.concurrent.ExecutionContext.Implicits.global

object Boot extends App {
  implicit val system = ActorSystem()

  val service = system.actorOf(Props[ApiServiceActor], "api-service")
  val userActor = system.actorOf(Props[UserActor], "users")
  val messageActor = system.actorOf(Props[ChatActor], "messages")

  val interface = system.settings.config getString "communicator.interface"
  val port = system.settings.config getInt "communicator.port"

  // ensure we're able to successfully establish a connection a cassandra
  // doesn't matter if the user isn't found
  val f = Users.getByUsername("nouserhere")
  f onSuccess {
    case t =>  IO(Http) ! Http.Bind(service, interface, port)
  }
  f onFailure {
    case t => println("Unable to connect to cassandra because of: " + t.getMessage)
  }
}