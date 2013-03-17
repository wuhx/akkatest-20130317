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

export()
{
  NAME="$1"

  info "exporting ${NAME}'s private key to ${NAME}/key.pem"

  PASS=`head -n 1 "${NAME}/password"` || fatal "could not read ${NAME}/password"

  java \
    -jar exportprivatekey.zip \
    "${NAME}/key_store.jks"   \
    JKS                       \
    "${PASS}"                 \
    "${NAME}"                 \
    "${NAME}/key.pem"
}

if [ $# -ne 1 ]
then
  fatal "usage: target"
fi

TARGET="$1"
shift

export "${TARGET}"
