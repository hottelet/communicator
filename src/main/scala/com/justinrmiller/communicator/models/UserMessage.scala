package com.justinrmiller.communicator.models

object UserMessage {
  case class Get(username: String)
  case class Store(user: User)
}