#!/bin/sh

udp_port="5555"
remote_udp_port="28001"

socat udp4-listen:$udp_port,reuseaddr,fork udp:$SOCAT_PEERADDR:$remote_udp_port &


echo "finish udp port forward setting!"
