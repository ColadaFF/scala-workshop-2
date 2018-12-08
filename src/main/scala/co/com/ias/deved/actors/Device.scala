package co.com.ias.deved.actors

import akka.actor.{ Actor, ActorLogging, Props }

object Device {
  def props(): Props = Props(new Device)

  //response
  case class TemperatureRecorded(id: Long)
  case class RespondTemperature(id: Long, value: Option[Double])

  // request
  case class RecordTemperature(requestId: Long, value: Double)
  case class RequestLastTemperature(requestId: Long)

}

class Device() extends Actor with ActorLogging {

  import Device._

  override def preStart(): Unit = log.info("Device started")
  override def postStop(): Unit = log.info("Device stopped")

  var lastTemperatureReading: Option[Double] = None

  override def receive: Receive = {
    case RecordTemperature(requestId, value) => {
      lastTemperatureReading = Some(value)
      sender() ! TemperatureRecorded(requestId)
    }

    case RequestLastTemperature(id) => {
      sender() ! RespondTemperature(id, lastTemperatureReading)
    }
  }

  override def unhandled(message: Any): Unit = {
    log.info("unhandled, {}", message)
  }
}
