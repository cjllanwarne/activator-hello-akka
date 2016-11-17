package chrisl

import akka.actor.SupervisorStrategy.{Stop, _}
import akka.actor.{Actor, ActorLogging, OneForOneStrategy, Props, SupervisorStrategy, Terminated}

import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.Random
import chrisl.WatcherActor._
import com.sun.org.apache.xml.internal.security.Init

// Watch child actors. Associate exceptions with actors.
class WatcherActor(childCount: Int) extends Actor with ActorLogging {

  implicit val ec = context.dispatcher

  // Map of actor name -> exception message
  var noticedExceptions: Map[String, Option[String]] = init()

  override def receive = {
    case Terminated(actorRef) =>
      noticedExceptions += actorRef.path.name -> Some("unknown") // TODO: Can I find out the exception message here?
  }

  final val stopAndLogStrategy: SupervisorStrategy = {
    def stoppingDecider: Decider = {
      case e: Exception =>
        // TODO: Or, can I find out the actor name here?
        // noticedExceptions += name -> Some(e.getMessage)
        Stop
    }
    OneForOneStrategy()(stoppingDecider)
  }

  override def supervisorStrategy = stopAndLogStrategy


  def init() = {
    (0 until childCount) map { id =>
      val name = childName(id)
      val newActor = context.actorOf(props = ExceptionThrowerActor.props(id), name = name)
      context watch newActor
      context.system.scheduler.scheduleOnce(Random.nextInt(100) milliseconds, newActor, ExceptionThrowerActor.ThrowNow)
      name -> None
    } toMap
  }
}

object WatcherActor {
  def props(childCount: Int) = Props(new WatcherActor(childCount))
  def childName(id: Int) = s"exception-thrower-$id"
}
