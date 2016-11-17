package chrisl

import akka.actor.{Actor, ActorLogging, Props}
import chrisl.ExceptionThrowerActor._

class ExceptionThrowerActor extends Actor with ActorLogging {
  val r = scala.util.Random

  override def receive = {
    case DoWork =>
      val i = r.nextInt(100)
      if (i < 95)
        throw new RuntimeException("I don't want to see this in my output or logs!")
      else
        sender ! WorkDone
  }
}

object ExceptionThrowerActor {
  case object DoWork
  case object WorkDone
  def props: Props = Props(new ExceptionThrowerActor)
}
