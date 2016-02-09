import Maps as mp

import socket
socket.getaddrinfo('localhost', 8080)

map_handler = mp.Maps()

print map_handler.get_industry_index([33.533478, 73.140807])