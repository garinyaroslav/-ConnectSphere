#!/bin/sh
export DOCKERHOST=$(ip addr | grep -E "([0-9]{1,3}\.){3}[0-9]{1,3}" | grep -v 127.0.0.1 | awk '{ print $2 }' | cut -f1 -d'/' | head -n1)
echo "DOCKERHOST is set to: $DOCKERHOST"
docker-compose -f docker-compose.yml up
