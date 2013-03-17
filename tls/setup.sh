#!/bin/sh

fatal()
{
  echo "setup: fatal: $1" 1>&2
  exit 1
}

info()
{
  echo "setup: info: $1" 1>&2
}

keypair()
{
  info "generating keypair"

  NAME="$1"
  PASS=`head -n 1 "${NAME}/password"` || fatal "could not read ${NAME}/password"
  DNAME=`head -n 1 "${NAME}/dname"`   || fatal "could not read ${NAME}/dname"

  keytool                              \
    -genkeypair                        \
    -storepass  "${PASS}"              \
    -keypass    "${PASS}"              \
    -dname      "${DNAME}"             \
    -alias      "${NAME}"              \
    -keystore   "${NAME}/key_store.jks" || fatal "could not generate keypair for ${NAME}"

  info "exporting certificate"
  keytool                              \
    -export                            \
    -storepass "${PASS}"               \
    -keypass   "${PASS}"               \
    -alias     "${NAME}"               \
    -keystore  "${NAME}/key_store.jks" \
    -rfc                               \
    -file      "${NAME}/cert.pem" || fatal "could not export certificate for ${NAME}"
}

import()
{
  FROM="$1"
  TO="$2"
  PASS=`head -n 1 "${TO}/password"` || fatal "could not read ${TO}/password"

  info "importing ${FROM} certificate to ${TO}'s trust store"

  keytool                              \
    -noprompt                          \
    -import                            \
    -storepass "${PASS}"               \
    -keypass   "${PASS}"               \
    -alias     "${FROM}"               \
    -file      "${FROM}/cert.pem"      \
    -keystore  "${TO}/trust_store.jks" || fatal "could not import certificate"
}

keypair "server"
keypair "client"

import "server" "client"
import "client" "server"
