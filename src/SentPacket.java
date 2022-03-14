class SentPacket {
    // associated timer
    private final MyTimer myTimer;
    // sent but not acked packet
    private final JPacket packet;
    // whether this packet has been acked
    private boolean received;

    public SentPacket(JPacket packet, Integer timeout, Integer index, SenderWindow senderWindow) {
        this.packet = packet;
        this.myTimer = new MyTimer(timeout, index, senderWindow);
        this.received = false;
    }

    public MyTimer getMyTimer() {
        return myTimer;
    }

    public JPacket getPacket() {
        return packet;
    }

    public boolean isReceived() {
        return received;
    }

    public void setReceived(boolean received) {
        this.received = received;
    }
}
