#!/bin/bash

#Run script for the server distributed as a part of 
#Assignment 1
#Computer Networks (CS 456)
#Number of parameters: 2
#Parameter:
#    $1: <req_code>
#    $2: <file_to_send>


#req_code=33
#file_to_send="send.txt"
#emulatorHostAddr=localhost
#emulatorPort=40000
#localReceivePort=40001
#fileToWrite=rec.txt

#Uncomment/update exactly one of the following commands depending on implementation

cd out
#For Java implementation
#java receiver $emulatorHostAddr $emulatorPort $localReceivePort $fileToWrite

java Receiver $1 $2 $3 $4


