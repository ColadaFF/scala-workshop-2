package co.com.ias.deved.actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props}

object DeviceGroup {
  def props(groupId: String) = Props(new DeviceGroup(groupId))

  case class RequestDevices(requestId: Long)
  case class ReplyDeviceList(requestId: Long, ids: Set[String])
}

class DeviceGroup(groupId: String) extends Actor with ActorLogging {

  import DeviceManager._

  var deviceIdToActorRef = Map.empty[String, ActorRef]
  var actorRefToDeviceId = Map.empty[ActorRef, String]

  override def preStart(): Unit = log.info("Started group {}", groupId)

  override def receive: Receive = {
    case msg@RequestTrackingDevice(_, deviceId) => {
      deviceIdToActorRef.get(deviceId) match  {
        case Some(ref) => ref forward msg
        case None => {
          log.info("Creating device group {}", groupId)
          val deviceActor = context.actorOf(DeviceGroup.props(groupId))
          context watch deviceActor
          deviceActor forward msg
          deviceIdToActorRef += deviceId -> deviceActor
          actorRefToDeviceId += deviceActor -> deviceId
        }
      }
    }
  }
}
