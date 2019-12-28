package com.github.daggerok.akka

import java.time.ZonedDateTime

import akka.actor.{ActorLogging, ActorSystem, Props}
import akka.persistence.PersistentActor

import scala.collection.mutable

object Main extends App {

  // protocol
  sealed trait Message

  // commands
  final case class RegisterCitizen(citizenId: String, date: ZonedDateTime = ZonedDateTime.now()) extends Message
  final case class RegisterCandidate(candidateId: String, date: ZonedDateTime = ZonedDateTime.now()) extends Message
  final case class Vote(citizenId: String, candidateId: String, date: ZonedDateTime = ZonedDateTime.now()) extends Message
  final case class GracefulShutdown(date: ZonedDateTime = ZonedDateTime.now()) extends Message

  // events
  final case class CitizenRegistered(citizenId: String, date: ZonedDateTime = ZonedDateTime.now()) extends Message
  final case class CandidateRegistered(candidateId: String, date: ZonedDateTime = ZonedDateTime.now()) extends Message
  final case class Voted(citizenId: String, candidateId: String, date: ZonedDateTime = ZonedDateTime.now()) extends Message
  final case class Rejected(reason: String) extends Message

  // entity
  class Voting extends PersistentActor with ActorLogging {

    private val citizens: mutable.Set[String] = mutable.Set.empty
    private val candidates: mutable.Set[String] = mutable.Set.empty
    private val votes: mutable.Map[String, mutable.Set[String]] = mutable.Map.empty

    override def persistenceId: String = "Voting-December-2019"

    override def receiveCommand: Receive = {
      case cmd: GracefulShutdown =>
        log.info("{}", cmd)
        context.stop(self)
        context.system.terminate()

      case cmd @ RegisterCitizen(citizenId, _) if citizens.contains(citizenId) =>
        val res = Rejected(s"Citizen $citizenId already registered.")
        log.info("{} => {}", cmd, res)
      case cmd @ RegisterCitizen(citizenId, date) =>
        persist(CitizenRegistered(citizenId, date)) { evt =>
          citizens.add(citizenId)
          log.info("cmd {} => evt {}", cmd, evt)
        }

      case cmd @ RegisterCandidate(candidateId, _) if candidates.contains(candidateId) =>
        val res = Rejected(s"Candidate $candidateId already registered.")
        log.info("{} => {}", cmd, res)
      case cmd @ RegisterCandidate(candidateId, date) =>
        persist(CandidateRegistered(candidateId, date)) { evt =>
          candidates.add(candidateId)
          votes.put(candidateId, mutable.Set.empty)
          log.info("cmd {} => evt {}", cmd, evt)
        }

      case cmd @ Vote(citizenId, _, _) if !citizens.contains(citizenId) =>
        val res = Rejected(s"Citizen $citizenId is not registered.")
        log.info("{} => {}", cmd, res)
      case cmd @ Vote(_, candidateId, _) if !candidates.contains(candidateId) =>
        val res = Rejected(s"Candidate $candidateId is not registered.")
        log.info("{} => {}", cmd, res)
      case cmd @ Vote(citizenId, _, _) if votes.exists(e => e._2.contains(citizenId)) =>
        val res = Rejected(s"Citizen $citizenId already voted.")
        log.info("{} => {}", cmd, res)
      case cmd @ Vote(citizenId, candidateId, date) =>
        persist(Voted(citizenId, candidateId, date)) { evt =>
          votes(candidateId).add(citizenId)
          log.info("cmd {} => evt {}", cmd, evt)
        }
    }

    override def receiveRecover: Receive = {
      case evt @ CitizenRegistered(citizenId, _) =>
        log.info("recover {}", evt)
        citizens.add(citizenId)
      case evt @ CandidateRegistered(candidateId, _) =>
        log.info("recover {}", evt)
        candidates.add(candidateId)
        votes.put(candidateId, mutable.Set.empty)
      case evt @ Voted(citizenId, candidateId, date) =>
        log.info("recover {}", evt)
        votes(candidateId).add(citizenId)
    }
  }
  // run
  val system = ActorSystem("AkkaPersistenceSystem")
  val voting = system.actorOf(Props[Voting], "voting")
  // stop
  voting ! RegisterCitizen("Max")
  voting ! RegisterCandidate("Max")
  voting ! Vote("Max", "Max")
  voting ! new GracefulShutdown
}
