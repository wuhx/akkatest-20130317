package com.io7m.test

object SimpleServer {

  def main(args : Array[String]) : Unit = {
    val sock = SSLUtils.serverSocket(
      "127.0.0.1",
      9000,
      new SSLUtils.KeystoreDescription("tls/server/key_store.jks", "12345678"),
      new SSLUtils.TruststoreDescription("tls/server/trust_store.jks", "12345678"))

    val client = sock.accept()
    val stream = client.getInputStream()
    stream.read()
    client.close()
  }

}