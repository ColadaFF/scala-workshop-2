package co.com.ias.deved.actors

import akka.actor.ActorSystem

import scala.io.StdIn

object IotApp extends App {
  val system = ActorSystem("IotApp")

  try {
    val deviceActor = system.actorOf(Device.props(), "device-1")
    StdIn.readLine()
  } finally {
    system.terminate()
  }

}
