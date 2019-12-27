package com.github.daggerok.akka

import akka.actor.typed._
import akka.actor.typed.scaladsl._
import akka.util.Timeout

import scala.concurrent.Future

object AskPattern extends App {
  // protocol
  final case class Hello(greet: String)
  final case class SayHello(name: String, replyTo: ActorRef[Hello])
  // behaviour
  def behavior(greeting: String): Behavior[SayHello] =
    Behaviors.receiveMessage {
      case SayHello(name, replyTo) =>
        println(s"$greeting, $name!")
        replyTo ! Hello(s"$greeting, $name!")
        Behaviors.same
    }
  // system
  val greeter: ActorSystem[SayHello] = ActorSystem(behavior("Превед"), "hololo")
  // ask pattern
  import akka.actor.typed.scaladsl.AskPattern._
  import scala.concurrent.duration._
  implicit val timeout: Timeout = Timeout(2.seconds)
  implicit val scheduler: Scheduler = greeter.scheduler
  val result: Future[Hello] =
    // greeter ? (replyTo => SayHello("Akka Typed", replyTo)) // same:
    greeter.ask((replyTo: ActorRef[Hello]) => SayHello("Akka Typed", replyTo))
  // stop
  greeter.terminate()
}
