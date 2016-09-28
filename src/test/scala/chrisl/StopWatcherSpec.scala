package chrisl

import akka.actor.SupervisorStrategy.Stop
import akka.actor.{ActorRef, ActorSystem, Kill, PoisonPill}
import akka.testkit.{TestActorRef, TestKit}
import org.scalatest.concurrent.Eventually
import org.scalatest.{FlatSpecLike, Matchers}
import scala.concurrent.duration._

class StopWatcherSpec extends TestKit(ActorSystem("TestSystem")) with FlatSpecLike with Matchers with Eventually {

  behavior of "StopWatcher"

  override implicit val patienceConfig = PatienceConfig(3.seconds)

  val actorDeathMethods: List[(String, ActorRef => Unit)] = List(
    ("self_stopped", (a: ActorRef) => a ! Stop),
    ("externally_stopped", (a: ActorRef) => system.stop(a)),
    ("poison_pilled", (a: ActorRef) => a ! PoisonPill),
    ("killed", (a: ActorRef) => a ! Kill),
    ("throw_exception", (a: ActorRef) => a ! Stopper.ThrowException)
  )

  actorDeathMethods foreach { case (name, killSwitch) =>
    it should s"detect $name" in {

      val watcher = TestActorRef(new StopWatcher())
      val stopper = TestActorRef(new Stopper(watcher))

      eventually { watcher.underlyingActor.watching should be(true) }

      killSwitch(stopper)

      eventually { watcher.underlyingActor.spottedTheStop should be(true) }

    }
  }
}
