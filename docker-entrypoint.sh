#!/bin/sh
set -eu

if [ -d /data ]; then
  chown spring:spring /data
fi

exec gosu spring "$@"
