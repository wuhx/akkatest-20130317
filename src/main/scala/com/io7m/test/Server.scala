package com.io7m.test

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import akka.actor.Props
import akka.actor.ActorLogging
import akka.actor.Actor
import akka.event.LoggingReceive

final class ServerActor extends Actor with ActorLogging {

  override def receive : PartialFunction[Any, Unit] = LoggingReceive {
    case m => this.unhandled(m)
  }

}

final class Server {

  private val config = ConfigFactory.parseString("""
akka.loglevel                              = DEBUG
akka.log-config-on-start                   = on
akka.actor.provider                        = "akka.remote.RemoteActorRefProvider"
akka.actor.serialize-message               = on
akka.actor.debug.lifecycle                 = on
akka.actor.debug.receive                   = on
akka.remote.log-received-messages          = on
akka.remote.log-sent-messages              = on
akka.remote.transport                      = "akka.remote.netty.NettyRemoteTransport"
akka.remote.netty.hostname                 = "127.0.0.1"
akka.remote.netty.port                     = 9000
akka.remote.netty.ssl.enable               = on
# akka.remote.netty.ssl.protocol             = "TLSv1"
akka.remote.netty.ssl.key-store            = tls/server/key_store.jks
akka.remote.netty.ssl.key-store-password   = 12345678
akka.remote.netty.ssl.trust-store          = tls/server/trust_store.jks
akka.remote.netty.ssl.trust-store-password = 12345678
# akka.remote.netty.ssl.enabled-algorithms   = ["TLS_RSA_WITH_AES_256_CBC_SHA"]
""")

  private val system =
    ActorSystem("test-server", this.config)

  private val act =
    this.system.actorOf(Props[ServerActor], "server")
}

final object Server {
  def main(args : Array[String]) : Unit = {
    val c = new Server()
  }
}
