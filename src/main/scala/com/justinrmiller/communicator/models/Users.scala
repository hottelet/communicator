package com.justinrmiller.communicator.models

import com.justinrmiller.communicator.connector.CassandraConnector
import com.websudos.phantom.dsl._
import scala.concurrent.Future

case class User(username: String, firstName: String, lastName: String, email: String)

class Users extends CassandraTable[Users, User] {

  object username extends StringColumn(this) with PartitionKey[String]
  object first_name extends StringColumn(this)
  object last_name extends StringColumn(this)
  object email extends StringColumn(this)

  def fromRow(row: Row): User = {
    User(
      username(row),
      first_name(row),
      last_name(row),
      email(row)
    )
  }
}

object Users extends Users with CassandraConnector {

  def store(user: User): Future[Option[User]] = {
    insert.value(_.username, user.username)
          .value(_.email, user.email)
          .value(_.first_name, user.firstName)
          .value(_.last_name, user.lastName)
          .future().flatMap {
            rows => {
              for {
                one <- getByUsername(user.username)
              } yield one
            }
          }
  }

  def getByUsername(username: String): Future[Option[User]] = {
    select.where(_.username eqs username).one()
  }

}
