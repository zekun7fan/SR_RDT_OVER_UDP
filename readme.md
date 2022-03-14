# 1.how to start

### step1: run `make clean` to delete class files, log files and text files.

### step2: run `make` to compile the program

### step3: place the file to be transfer under the `out` directory 

### step4: run the shell script in the following order and pass according arguments


#### 4.1.`emulator.sh`

##### this script is optional, if didn't run this script, then Sender and Receiver should be configured to be directly connected

* recPortFromSender : emulator's receiving UDP port number in the forward (Sender) direction
* receiverAddr : Receiver's network address
* receiverPort : Receiver's receiving UDP port number
* recPortFromReceiver : emulator's receiving UDP port number in the backward (Receiver) direction
* senderAddr : Sender's network address
* senderPort : Sender's receiving UDP port number
* maxDelay : maximum delay of the link in units of millisecond
* discardProb : JPacket discard probability
* verbose-mode : set to 1, the network emulator will output its internal processing, one per line


#### 4.2.`receiver.sh`
* emulatorHostAddr : hostname for the network emulator
* emulatorPort : UDP port number used by the link emulator to receive ACKs from the Receiver
* localReceivePort : UDP port number used by the Receiver to receive data from the emulator
* fileToWrite : name of the file into which the received data is written


#### 4.3.`sender.sh`
* emulatorHostAddr : host address of the network emulator
* emulatorPort : UDP port number used by the emulator to receive data from the Sender
* localReceivePort : UDP port number used by the Sender to receive SACKs from the emulator
* timeout : timeout interval in units of millisecond
* fileToTransfer : name of the file to be transferred



# 2.test results


