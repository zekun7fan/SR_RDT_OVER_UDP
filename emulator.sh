#!/bin/bash

#Run script for client distributed as part of
#Assignment 1
#Computer Networks (CS 456)
#Number of parameters: 5
#Parameter:
#    $1: <server_address>
#    $2: <n_port>
#    $3: <mode>
#    $4: <req_code>
#    $5: <file_received>

#server_address="localhost"
#n_port=53999
#mode="A"
#req_code=33
#file_received="rec.txt"
#recPortFromSender=
#receiverAddr=
#receiverPort
#recPortFromReceiver
#senderAddr
#senderPort
#maxDelay
#discardProb
#verbose-mode

#Uncomment/update exactly one of the following commands depending on your implementation

#For Java implementation
#python3 network_emulator.py $recPortFromSender $receiverAddr $receiverPort $recPortFromReceiver $senderAddr $senderPort $maxDelay $discardProb $verbose-mode

python3 network_emulator.py $1 $2 $3 $4 $5 $6 $7 $8 $9



