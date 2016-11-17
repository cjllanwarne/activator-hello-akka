package chrisl

import akka.actor.{Actor, ActorLogging, Props}
import chrisl.ExceptionThrowerActor._

class ExceptionThrowerActor(id: Int) extends Actor with ActorLogging {

  override def receive = {
    case ThrowNow =>
      throw new RuntimeException(errorMessage(id))
  }
}

object ExceptionThrowerActor {
  case object ThrowNow
  def props(id: Int): Props = Props(new ExceptionThrowerActor(id))
  def errorMessage(id: Int) = s"Boom! $id"
}
