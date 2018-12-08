package co.com.ias.deved.actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props, Terminated}

object DeviceManager {
  def props(): Props = Props(new DeviceManager)

  case class RequestTrackingDevice(groupId: String, deviceId:String)
}

class DeviceManager extends Actor with ActorLogging{

  import DeviceManager._

  var groupIdToActorRef = Map.empty[String, ActorRef]
  var actorRefToGroupId = Map.empty[ActorRef, String]


  override def receive: Receive = {
    case msg@RequestTrackingDevice(groupId, _) => {
      groupIdToActorRef.get(groupId) match {
        case Some(ref) => ref forward msg
        case None => {
          log.info("Creating device group {}", groupId)
          val groupActor = context.actorOf(DeviceGroup.props(groupId))
          context watch groupActor
          groupActor forward msg
          groupIdToActorRef += groupId -> groupActor
          actorRefToGroupId += groupActor -> groupId
        }
      }
    }
    case Terminated(groupAcTor) => {
      val groupId = actorRefToGroupId(groupAcTor)
      log.info("Device group for {}, terminated", groupId)
      groupIdToActorRef -= groupId
      actorRefToGroupId -= groupAcTor

    }
  }
}
