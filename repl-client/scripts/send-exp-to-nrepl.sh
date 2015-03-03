#!/bin/sh
# send argument to remote nrepl server
# requires socat

PORT=5000
echo $1 | socat -t 4 - tcp:localhost:$PORT
