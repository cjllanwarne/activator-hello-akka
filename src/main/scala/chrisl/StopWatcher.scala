package chrisl

import akka.actor.{Actor, Terminated}
import chrisl.StopWatcher.WatchMe

class StopWatcher extends Actor {

  var spottedTheStop: Boolean = false
  var watching: Boolean = false

  override def receive: Actor.Receive = {
    case WatchMe =>
      context.watch(sender)
      watching = true
    case Terminated(actor) =>
      spottedTheStop = true
  }

}

object StopWatcher {
  case object WatchMe
}
