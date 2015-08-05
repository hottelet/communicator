package com.justinrmiller.communicator

import akka.actor.{ActorLogging, Actor}
import akka.pattern.pipe

import com.justinrmiller.communicator.models.{UserMessages, Users}

class UserActor extends Actor with ActorLogging {
  implicit def ec = context.dispatcher

  def receive = {
    case UserMessages.Get(username) =>
      val f = Users.getByUsername(username)

      f pipeTo sender

    case UserMessages.Store(user) =>
      val f = Users.store(user)

      f pipeTo sender
  }
}