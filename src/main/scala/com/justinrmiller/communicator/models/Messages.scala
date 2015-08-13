package com.justinrmiller.communicator.models

import java.util.UUID

import com.justinrmiller.communicator.connector.CassandraConnector

import com.websudos.phantom.CassandraTable
import com.websudos.phantom.dsl._

import scala.concurrent.Future

case class Message (
  id: UUID,
  from_user: String,
  to_user: String,
  submitted: DateTime,
  text: String
)

class Messages extends CassandraTable[Messages, Message] {
  object id extends UUIDColumn(this) with PartitionKey[UUID]
  object from_user extends StringColumn(this) with PrimaryKey[String]
  object to_user extends StringColumn(this) with PrimaryKey[String]
  object submitted extends DateTimeColumn(this) with ClusteringOrder[DateTime] with Descending with PrimaryKey[DateTime]
  object text extends StringColumn(this)

  def fromRow(row: Row): Message = {
    Message(
      id(row),
      from_user(row),
      to_user(row),
      submitted(row),
      text(row)
    )
  }
}

object Messages extends Messages with CassandraConnector {

  def store(message: Message): Future[Option[Message]] = {
    insert
      .value(_.id, message.id)
      .value(_.from_user, message.from_user)
      .value(_.to_user, message.to_user)
      .value(_.submitted, message.submitted)
      .value(_.text, message.text)
      .future().flatMap {
        rows => Future.successful(Some(message))
    }
  }

  def getByFromTo(user1: String, user2: String, limit: Int): Future[Seq[Message]] = {
    select
      .where(_.from_user in List(user1, user2))
      .and(_.to_user in List(user1, user2))
      .orderBy(_.submitted.descending)
      .limit(limit)
      .fetch()
  }

}
