package co.com.ias.deved

import akka.actor.{ ActorRef, ActorSystem }
import akka.testkit.{ TestKit, TestProbe }
import co.com.ias.deved.actors.Device
import org.scalatest.{ BeforeAndAfterAll, Matchers, WordSpec, WordSpecLike }
import Device._

class IotSpec(_system: ActorSystem)
  extends TestKit(_system)
  with Matchers
  with WordSpecLike
  with BeforeAndAfterAll {

  import Device._

  def this() = this(ActorSystem("IotSystem"))

  override def afterAll(): Unit = shutdown(_system)

  "Device responds with empty temperature value" in {
    val probe = TestProbe()

    val device = _system.actorOf(Device.props())

    device.tell(RequestLastTemperature(1), probe.ref)

    val response = probe.expectMsgType[RespondTemperature]

    response.value should ===(None)
  }

  "Device updates last state" in {
    val probe = TestProbe()

    val device = _system.actorOf(Device.props())

    device.tell(RecordTemperature(1, 1.5), probe.ref)

    val response1 = probe.expectMsgType[TemperatureRecorded]

    response1.id should ===(1)

    device.tell(RequestLastTemperature(2), probe.ref)

    val response2 = probe.expectMsgType[RespondTemperature]

    response2.value should ===(Some(1.5))
    response2.id should ===(2)
  }

}
