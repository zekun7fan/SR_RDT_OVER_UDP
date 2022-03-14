JFLAGS = -d out -sourcepath src
JC = javac
.SUFFIXES: .java .class
.java.class:
		$(JC) $(JFLAGS) $*.java

# This uses the line continuation character (\) for readability
# You can list these all on a single line, separated by a space instead.
# If your version of make can't handle the leading tabs on each
# line, just remove them (these are also just added for readability).
CLASSES = \
		src/SerializeUtils.java\
		src/JPacket.java \
		src/MyTimer.java \
		src/SentPacket.java \
		src/SenderWindow.java \
		src/SenderLogger.java \
		src/ReceiverLogger.java \
        src/Sender.java \
        src/Receiver.java

default: classes



classes: $(CLASSES:.java=.class)

clean:
		$(RM) out/*.class out/*.log
