package com.github.daggerok.akka

import java.time.ZonedDateTime

import akka.actor.{ActorLogging, ActorSystem, Props}
import akka.persistence.PersistentActor

object Main extends App {
  // protocol
  sealed trait Message
  final case class BulkInvoices(invoices: Seq[Invoice]) extends Message
  final case class Invoice(recipient: String, date: ZonedDateTime, balance: BigDecimal) extends Message
  final case class InvoiceSaved(id: BigInt, recipient: String, date: ZonedDateTime, balance: BigDecimal) extends Message
  // entity
  class Accountant extends PersistentActor with ActorLogging {
    private var id: BigInt = 1
    private var total: BigDecimal = 0

    override def persistenceId: String = "Accountant-123"

    override def receiveCommand: Receive = {
      case BulkInvoices(invoices) =>
        val ids = 0 to invoices.size
        val events = invoices.zip(ids).map { pair =>
          val invoice = pair._1
          val id = pair._2
          InvoiceSaved(id, invoice.recipient, invoice.date, invoice.balance)
        }
        persistAll(events) { invoice =>
          id = (id + 1) % 3
          total += invoice.balance
          log.info("receive: {} => {}", invoice, total)
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
  val invoices = for (i <- 1 to 3) yield Invoice(s"Recipient$i", ZonedDateTime.now(), BigDecimal(i * 100))
  accountant ! BulkInvoices(invoices)
  // stop
  Thread.sleep(1111)
  system.terminate()
}
