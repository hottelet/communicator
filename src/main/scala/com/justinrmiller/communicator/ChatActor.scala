package com.justinrmiller.communicator

import akka.actor.{ActorLogging, Actor}
import com.justinrmiller.communicator.models.{MessageMessage, Messages}
import akka.pattern.pipe

class ChatActor extends Actor with ActorLogging {
  implicit def ec = context.dispatcher

  def receive = {
    case MessageMessage.Receive(user1, user2, limit) =>
      val f = Messages.getByFromTo(user1, user2, limit)

      f pipeTo sender

    case MessageMessage.Send(message) =>
      val f = Messages.store(message)

      f pipeTo sender
  }
}