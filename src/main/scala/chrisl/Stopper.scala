package chrisl

import akka.actor.{Actor, ActorRef, Props}
import chrisl.Stopper.ThrowException

class Stopper(watcher: ActorRef) extends Actor {
  watcher ! StopWatcher.WatchMe
  override def receive = {
    case ThrowException => throw new RuntimeException("Oh No!")
  }
}

object Stopper {
  case object ThrowException
  def props(watcher: ActorRef) = Props(new Stopper(watcher))
}
