local:
socat tcp4-listen:28001,fork,reuseaddr tcp4:192.168.1.1:5252 &  
socat -d -d -b100000 tcp4-listen:28002,fork,reuseaddr udp4-listen:5555 &


remote:
socat tcp6-listen:28001,fork,reuseaddr tcp4:192.168.1.1:5252 &  
socat -d -d -b100000 tcp6-listen:28002,fork,reuseaddr udp4-listen:5555 &


option defaultroute 0
