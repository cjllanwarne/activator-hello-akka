package chrisl

import akka.actor.ActorSystem
import akka.testkit.{TestActorRef, TestKit}
import org.scalatest.concurrent.Eventually
import org.scalatest.{FlatSpecLike, Matchers}
import scala.concurrent.duration._

class WatcherActorSpec extends TestKit(ActorSystem("TestSystem")) with FlatSpecLike with Matchers with Eventually {

  override implicit val patienceConfig = PatienceConfig(3.seconds)

  behavior of "WatcherActor"

  it should "eventually succeed" in {

    val testWatcherActor = TestActorRef(new WatcherActor())

    eventually {
      // Eventually it should get a success...
      testWatcherActor.underlyingActor.success should be(true)
    }
  }
}
