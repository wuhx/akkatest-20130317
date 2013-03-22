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

export_key()
{
  info "exporting private key"

  NAME="$1"
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
  fatal "usage: name"
fi

export_key "$1"
