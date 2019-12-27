package com.github.daggerok.akka

import akka.actor.typed.{ActorSystem, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import com.github.daggerok.akka.AkkaActorTyped.{ChangeGreeting, Message, SayHello, behavior}

object Main extends App {
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
        println("Change how greet is behave!")
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
