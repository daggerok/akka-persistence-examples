package com.github.daggerok.akka

import akka.actor.typed.{ActorSystem, Behavior}
import akka.actor.typed.scaladsl.Behaviors

object AkkaActorTyped extends App {
  // protocol
  sealed trait Message
  final case class SayHello(name: String) extends Message
  final case class ChangeGreeting(greet: String) extends Message
  // behaviour
  def behavior(greeting: String): Behavior[Message] =
    Behaviors.receiveMessage {
      case SayHello(name) =>
        println(s"$greeting, $name!")
        Behaviors.same
      case ChangeGreeting(greet: String) =>
        println(s"Change how greet is behave from $greeting to $greet!")
        behavior(greet)
    }
  // run
  val greeter = ActorSystem[Message](behavior("Hello"), "AkkaTyped")
  greeter ! SayHello("Max")
  greeter ! ChangeGreeting("Привет")
  greeter ! SayHello("Макс")
  // stop
  greeter.terminate()
}
