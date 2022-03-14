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
#emulatorHostAddr=localhost
#emulatorPort=40001
#localReceivePort=40000
#timeout=1000
#fileToTransfer=send.txt

#Uncomment/update exactly one of the following commands depending on your implementation

cd out
#For Java implementation
#java sender $emulatorHostAddr $emulatorPort $localReceivePort $timeout $fileToTransfer

java Sender $1 $2 $3 $4 $5


