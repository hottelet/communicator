package com.justinrmiller.communicator.models

object MessageMessage {
  case class Receive(user1: String, user2: String, limit: Int)
  case class Send(message: Message)
}
