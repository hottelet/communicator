package com.justinrmiller.communicator

import com.justinrmiller.communicator.models.{UserMessages, User}

import scala.concurrent.duration._
import scala.util.{Failure, Success}

import akka.actor.{ActorLogging, ActorSelection, Actor}
import akka.pattern.ask
import akka.util.Timeout

import spray.routing.HttpService
import spray.http.StatusCodes._

class ApiServiceActor extends Actor with ApiService with ActorLogging {
  def actorRefFactory = context
  def receive = runRoute(serviceRoute)
}

import spray.json.DefaultJsonProtocol

object JsonConversions extends DefaultJsonProtocol {
  import com.justinrmiller.communicator.models._

  implicit val userFormat = jsonFormat4(User)
}

trait ApiService extends HttpService {
  implicit val ec = actorRefFactory.dispatcher
  implicit val timeout = Timeout(1.seconds)

  def userActor = actorRefFactory.actorSelection("/user/users")

  def Ask(a: ActorSelection, msg: Any) = a.ask(msg)

  val serviceRoute = {
    import spray.httpx.SprayJsonSupport._
    import JsonConversions._

    pathPrefix("api" / "users") {
      post {
        entity(as[User]) { req =>
          onComplete(userActor.ask(UserMessages.Store(req)).mapTo[Option[User]]) {
            case Success(Some(res)) => complete(res)
            case Success(None)      => complete(Conflict)
            case Failure(t)         => failWith(t)
          }
        }
      } ~
      path(Segment) { username =>
        get {
          onComplete(userActor.ask(UserMessages.Get(username)).mapTo[Option[User]]) {
            case Success(Some(res)) => complete(res)
            case Success(None)      => complete(NotFound)
            case Failure(t)         => failWith(t)
          }
        }
      }
    }
  }
}