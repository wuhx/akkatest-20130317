package com.io7m.test

import akka.actor.Actor
import akka.event.Logging.LoggerInitialized
import akka.event.Logging.InitializeLogger
import akka.event.Logging.Error
import akka.event.Logging.Warning
import akka.event.Logging.Info
import akka.event.Logging.Debug

class LogListener extends Actor {

  override def receive : PartialFunction[Any, Unit] = {
    case InitializeLogger(_)                      => sender ! LoggerInitialized
    case Error(cause, source, log_class, message) => LogListener.print("error", source, log_class, message)
    case Warning(source, log_class, message)      => LogListener.print("warning", source, log_class, message)
    case Info(source, log_class, message)         => LogListener.print("info", source, log_class, message)
    case Debug(source, log_class, message)        => LogListener.print("debug", source, log_class, message)
  }

}

object LogListener {

  def print(
    prefix : String,
    source : String,
    log_class : Class[_],
    message : Any) : Unit = {
    val buffer = new StringBuilder()
    buffer.append(prefix)
    buffer.append(": ")
    buffer.append(source)
    buffer.append(": ")
    buffer.append(message)
    System.err.println(buffer.toString())
  }

}
