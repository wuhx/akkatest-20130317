#!/bin/sh
exec openssl s_client -pause -msg -debug -key client/key.pem -connect 127.0.0.1:9000
