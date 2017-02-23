package chrisl

import java.util.concurrent.atomic.AtomicInteger
import akka.actor.{Actor, ActorLogging, ActorRef, Props, SupervisorStrategy, Terminated}
import chrisl.ExceptionThrowerActor.DoWork
import scala.concurrent.duration._
import scala.language.postfixOps

class WatcherActor extends Actor with ActorLogging {

  log.info(s"Watcher actor starting up...")
  implicit val ec = context.dispatcher
  override def supervisorStrategy = SupervisorStrategy.stoppingStrategy

  val workerNumber = new AtomicInteger(0)
  var worker: ActorRef = resetWorker
  worker ! DoWork
  var success: Boolean = false

  override def receive = {
    case Terminated(actorRef) =>
      log.info(s"Work failed by actor ${workerNumber.get}. Making another")
      worker = resetWorker
      worker ! DoWork
    case ExceptionThrowerActor.WorkDone =>
      log.info(s"Work completed by actor ${workerNumber.get}")
      success = true
  }

  def resetWorker: ActorRef = {
    def workerName = "worker-" + workerNumber.incrementAndGet()
    val worker = context.actorOf(props = ExceptionThrowerActor.props, name = workerName)
    context.watch(worker)
    worker
  }
}

object WatcherActor {
  def props = Props(new WatcherActor)
}
