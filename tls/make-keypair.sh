#!/bin/sh

fatal()
{
  echo "make-keypair: fatal: $1" 1>&2
  exit 1
}

info()
{
  echo "make-keypair: info: $1" 1>&2
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

if [ $# -ne 1 ]
then
  fatal "usage: target"
fi

TARGET="$1"
shift

if [ -f "${TARGET}/key_store.jks" ]
then
  fatal "${TARGET}/key_store.jks already exists, please remove it if you want to replace the keys"
fi

keypair "${TARGET}"
