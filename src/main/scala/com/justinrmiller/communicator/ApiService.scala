package com.justinrmiller.communicator

import com.datastax.driver.core.utils.UUIDs
import com.justinrmiller.communicator.models.{UserMessage, User, MessageMessage, Message}
import com.justinrmiller.communicator.utils.Marshalling
import org.joda.time.DateTime

import scala.concurrent.duration._
import scala.util.{Failure, Success}

import akka.actor.{ActorLogging, ActorSelection, Actor}
import akka.pattern.ask
import akka.util.Timeout

import spray.routing.{HttpService}
import spray.http.StatusCodes._

import spray.json._

class ApiServiceActor extends Actor with ApiService with ActorLogging {
  def actorRefFactory = context
  def receive = runRoute(serviceRoute)
}

case class MessageSubmission (
  from_user: String,
  to_user: String,
  text: String
)

object JsonConversions extends DefaultJsonProtocol with Marshalling {
  import com.justinrmiller.communicator.models._

  implicit val userFormat = jsonFormat4(User)
  implicit val messageFormat = jsonFormat5(Message)
  implicit val messageSubmissionFormat = jsonFormat3(MessageSubmission)
}

trait ApiService extends HttpService {
  implicit val ec = actorRefFactory.dispatcher
  implicit val timeout = Timeout(1.seconds)

  def userActor = actorRefFactory.actorSelection("/user/users")
  def messageActor = actorRefFactory.actorSelection("/user/messages")

  def Ask(a: ActorSelection, msg: Any) = a.ask(msg)

  val serviceRoute = {
    import spray.httpx.SprayJsonSupport._
    import JsonConversions._

    val users = pathPrefix("users") {
      post {
        entity(as[User]) { req =>
          onComplete(userActor.ask(UserMessage.Store(req)).mapTo[Option[User]]) {
            case Success(Some(res)) => complete(res)
            case Success(None) => complete(Conflict)
            case Failure(t) => failWith(t)
          }
        }
      } ~
        path(Segment) { username =>
          get {
            onComplete(userActor.ask(UserMessage.Get(username)).mapTo[Option[User]]) {
              case Success(Some(res)) => complete(res)
              case Success(None) => complete(NotFound)
              case Failure(t) => failWith(t)
            }
          }
        }
    }

    val messages = path("messages") {
      import spray.httpx.SprayJsonSupport._
      post {
        entity(as[MessageSubmission]) { req =>
          val message = Message(
            UUIDs.random(),
            req.from_user,
            req.to_user,
            DateTime.now(),
            req.text
          )
          onComplete(messageActor.ask(MessageMessage.Send(message)).mapTo[Option[Message]]) {
            case Success(Some(res)) => complete(res)
            case Success(None) => complete(Conflict)
            case Failure(t) => failWith(t)
          }
        }
      } ~
      get {
        parameterMap { pm =>
          if (!(pm.contains("from") && pm.contains("to"))) {
            failWith(new Exception("Missing from and/or to"))
          } else {
            val limit : Int = if (pm.contains("limit")) pm("limit").toInt else 5

            onComplete(messageActor.ask(MessageMessage.Receive(pm("from"), pm("to"), limit)).mapTo[Seq[Message]]) {
              case Success(res) => complete(res)
              case Failure(t) => failWith(t)
            }
          }
        }
      }
    }

    pathPrefix("api") {
      users ~ messages
    }
  }
}