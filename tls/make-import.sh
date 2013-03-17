
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

if [ $# -ne 2 ]
then
  fatal "usage: import-from import-to"
fi

IMPORT_FROM="$1"
shift
IMPORT_TO="$1"
shift

import "${IMPORT_FROM}" "${IMPORT_TO}"
