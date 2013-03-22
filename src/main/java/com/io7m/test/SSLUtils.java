package com.io7m.test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

/**
 * Functions for dealing with the unpleasant JSSE interface.
 */

public final class SSLUtils
{
  public static final class KeystoreDescription
  {
    public final String name;
    public final String password;

    public KeystoreDescription(
      final String name,
      final String password)
    {
      this.name = name;
      this.password = password;
    }
  }

  public static final class TruststoreDescription
  {
    public final String name;
    public final String password;

    public TruststoreDescription(
      final String name,
      final String password)
    {
      this.name = name;
      this.password = password;
    }
  }

  public static SSLSocket clientSocket(
    final String remote_address,
    final int remote_port,
    final KeystoreDescription ks_description,
    final TruststoreDescription ts_description)
    throws NoSuchAlgorithmException,
      CertificateException,
      FileNotFoundException,
      IOException,
      KeyStoreException,
      UnrecoverableKeyException,
      KeyManagementException
  {
    FileInputStream ks_stream = null;
    FileInputStream ts_stream = null;

    try {
      // Load keystore.
      final KeyStore key_store = KeyStore.getInstance("JKS");
      ks_stream = new FileInputStream(ks_description.name);
      key_store.load(ks_stream, ks_description.password.toCharArray());
      ks_stream.close();

      // Load truststore.
      final KeyStore trust_store = KeyStore.getInstance("JKS");
      ts_stream = new FileInputStream(ts_description.name);
      trust_store.load(ts_stream, ts_description.password.toCharArray());
      ts_stream.close();

      // "Initialise a KeyManagerFactory object to encapsulate the underlying keystore".
      final KeyManagerFactory key_manager_factory =
        KeyManagerFactory.getInstance("SunX509");
      key_manager_factory.init(
        key_store,
        ks_description.password.toCharArray());

      // Initialise a TrustManagerFactory with the truststore.
      final TrustManagerFactory trust_manager_factory =
        TrustManagerFactory.getInstance("SunX509");
      trust_manager_factory.init(trust_store);
      final TrustManager[] trust_manager =
        trust_manager_factory.getTrustManagers();

      // Initialise a new SSL context with the above.
      final SSLContext ssl_context = SSLContext.getInstance("TLS");
      ssl_context.init(
        key_manager_factory.getKeyManagers(),
        trust_manager,
        new SecureRandom());
      final SSLSocketFactory socket_factory = ssl_context.getSocketFactory();

      final InetAddress actual_address =
        InetAddress.getByName(remote_address);
      final SSLSocket socket =
        (SSLSocket) socket_factory.createSocket(actual_address, remote_port);
      socket.setNeedClientAuth(true);

      return socket;
    } finally {
      if (ks_stream != null) {
        ks_stream.close();
      }
      if (ts_stream != null) {
        ts_stream.close();
      }
    }
  }

  public static SSLServerSocket serverSocket(
    final String local_address,
    final int local_port,
    final KeystoreDescription ks_description,
    final TruststoreDescription ts_description)
    throws NoSuchAlgorithmException,
      CertificateException,
      FileNotFoundException,
      IOException,
      KeyStoreException,
      UnrecoverableKeyException,
      KeyManagementException
  {
    FileInputStream ts_stream = null;
    FileInputStream ks_stream = null;

    try {
      // Load keystore.
      final KeyStore key_store = KeyStore.getInstance("JKS");
      ks_stream = new FileInputStream(ks_description.name);
      key_store.load(ks_stream, ks_description.password.toCharArray());
      ks_stream.close();

      // Load truststore.
      final KeyStore trust_store = KeyStore.getInstance("JKS");
      ts_stream = new FileInputStream(ts_description.name);
      trust_store.load(ts_stream, ts_description.password.toCharArray());
      ts_stream.close();

      // "Initialise a KeyManagerFactory object to encapsulate the underlying keystore".
      final KeyManagerFactory key_manager_factory =
        KeyManagerFactory.getInstance("SunX509");
      key_manager_factory.init(
        key_store,
        ks_description.password.toCharArray());

      // Initialise a TrustManagerFactory with the truststore.
      final TrustManagerFactory trust_manager_factory =
        TrustManagerFactory.getInstance("SunX509");
      trust_manager_factory.init(trust_store);
      final TrustManager[] trust_manager =
        trust_manager_factory.getTrustManagers();

      // Initialise a new SSL context with the above.
      final SSLContext ssl_context = SSLContext.getInstance("TLS");
      ssl_context.init(
        key_manager_factory.getKeyManagers(),
        trust_manager,
        new SecureRandom());
      final SSLServerSocketFactory socket_factory =
        ssl_context.getServerSocketFactory();

      final InetAddress actual_address = InetAddress.getByName(local_address);
      final SSLServerSocket socket =
        (SSLServerSocket) socket_factory.createServerSocket(
          local_port,
          100,
          actual_address);
      socket.setNeedClientAuth(false);
      socket.setReuseAddress(true);

      return socket;
    } finally {
      if (ks_stream != null) {
        ks_stream.close();
      }
      if (ts_stream != null) {
        ts_stream.close();
      }
    }
  }
}
