package chrisl

import akka.actor.ActorSystem
import akka.testkit.{TestActorRef, TestKit}
import org.scalatest.concurrent.Eventually
import org.scalatest.{FlatSpecLike, Matchers}
import scala.concurrent.duration._

class WatcherActorSpec extends TestKit(ActorSystem("TestSystem")) with FlatSpecLike with Matchers with Eventually {

  override implicit val patienceConfig = PatienceConfig(3.seconds)

  behavior of "WatcherActor"

  it should "associate uncaught exceptions in supervisees with the actorRef" in {

    val children = 100
    val testWatcherActor = TestActorRef(new WatcherActor(children))

    eventually {
      testWatcherActor.underlyingActor.noticedExceptions.size should be(children)
      testWatcherActor.underlyingActor.noticedExceptions foreach { _._2.isDefined should be(true) }
    }

    (0 until children) foreach { id =>
      testWatcherActor.underlyingActor.noticedExceptions(WatcherActor.childName(id)) should be(Some(ExceptionThrowerActor.errorMessage(id)))
    }
  }
}
