package com.io7m.test

import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.SSLSocket

object ListCiphers {
  def main(args : Array[String]) : Unit = {
    val sf = SSLSocketFactory.getDefault()
    val so = sf.createSocket().asInstanceOf[SSLSocket]
    so.getEnabledCipherSuites().foreach(System.out.println)
  }
}