package com.github.daggerok.akka

import java.util.Date

import akka.actor.{ActorLogging, ActorSystem, Props}
import akka.persistence.PersistentActor

object Main extends App {
  // protocol
  sealed trait Message
  final case class SaveInvoice(recipient: String, date: Date, balance: BigDecimal) extends Message
  final case class InvoiceSaved(id: BigInt, recipient: String, date: Date, balance: BigDecimal) extends Message
  // entity
  class Accountant extends PersistentActor with ActorLogging {
    private var id: BigInt = 1
    private var total: BigDecimal = 0

    override def persistenceId: String = "Accountant-123"

    override def receiveCommand: Receive = {
      case SaveInvoice(recipient, date, balance) =>
        persist(InvoiceSaved(id, recipient, date, balance)) { e =>
          id = (id + 1) % 3
          total += balance
          log.info("receive: {} => {}", e, total)
        }
    }

    override def receiveRecover: Receive = {
        case InvoiceSaved(id, _, _, balance) =>
          this.id = id
          total += balance
          log.info("recover: InvoiceSaved(id={}, balance={}) => {}", id, balance, total)
    }
  }
  // run
  val system = ActorSystem("AkkaPersistenceSystem")
  val accountant = system.actorOf(Props[Accountant], "accountant")
  accountant ! SaveInvoice("Max Inc", new Date, 1234)
  accountant ! SaveInvoice("Max Inc", new Date, 4321)
  accountant ! SaveInvoice("Other Inc", new Date, 4444)
  // stop
  Thread.sleep(3333)
  system.terminate()
}
