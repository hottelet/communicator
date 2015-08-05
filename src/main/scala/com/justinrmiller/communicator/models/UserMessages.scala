package com.justinrmiller.communicator.models

object UserMessages {
  case class Get(username: String)
  case class Store(user: User)
}