import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

class SenderWindow {
    // max window size
    private final Integer MAX_SIZE = 10;
    // array for buffered packet
    private final SentPacket[] window = new SentPacket[MAX_SIZE];
    // current sender window size
    private Integer size;
    // number of buffed packet
    private Integer num;
    // the index of the first sent but not acked packet in this array
    private Integer headIndex;
    // timeout interval
    private final Integer timeout;
    // socket for retransmitting old packet to Emulator
    private DatagramSocket sendSocket;
    // address of Emulator
    private final InetSocketAddress nEmulatorAddr;
    // logger
    private SenderLogger logger;


    public SenderWindow(Integer timeout, InetSocketAddress nEmulatorAddr, SenderLogger logger) {
        this.timeout = timeout;
        this.headIndex = 0;
        this.num = 0;
        this.size = 1;
        this.nEmulatorAddr = nEmulatorAddr;
        try {
            this.sendSocket = new DatagramSocket();
        } catch (SocketException e) {
            System.err.println("can not create sender window's send socket");
            return;
        }
        this.logger = logger;
        try {
            this.logger.logWindowSize(1, true);
        } catch (IOException e) {
            System.err.println("error occur when log the initial size of sender window");
        }
    }


    /**
     * attempt to increase size, up tp 10
     *
     */
    public void increaseSize() throws IOException {
        if (size < MAX_SIZE) {
            size++;
            logger.logWindowSize(size, true);
        }
    }

    /**
     * handle received ack
     * attempt to advance sender window base
     * attempt to increase window size if new ack arrives
     * return available slot number for sender to send new packet
     *
     * @param ack
     * @return number of available slots for sending new packets
     */
    public int receiveAck(Integer ack) throws IOException {
        // check whether receive new ack
        // find buffered packet whose seqnum is equal to this ack
        // if this packet has been received before, then this ack is duplicate, just return
        // if not received before, then mark this packed as received, cancel its associated timer
        boolean receiveNewAck = false;
        for (int i = 0; i < num; i++) {
            SentPacket sentPacket = window[(i + headIndex) % MAX_SIZE];
            if (sentPacket != null && sentPacket.getPacket().getSeqnum().equals(ack) && !sentPacket.isReceived()) {
                sentPacket.getMyTimer().cancel();
                sentPacket.setReceived(true);
                receiveNewAck = true;
                break;
            }
        }
        // duplicate ack, return
        if (!receiveNewAck) {
            return 0;
        }
        // attempt to advance sender window to the next sent but not acked packet with smallest seqnum
        int receivedNum = 0;
        for (int i = 0; i < num; i++) {
            SentPacket sentPacket = window[(i + headIndex) % MAX_SIZE];
            if (sentPacket.isReceived()) {
                window[(i + headIndex) % MAX_SIZE] = null;
                receivedNum++;
            } else {
                break;
            }
        }
        // update
        headIndex = (headIndex + receivedNum) % MAX_SIZE;
        num -= receivedNum;
        // received new ack, attempt to increase sender window size because new ack arrived
        increaseSize();
        // check whether there are some already timed out packet, if there are some, retransmit them
        retransmitReenteredPacked();
        return size > num ? size - num : 0;
    }


    /**
     * find packed which are already timed out and reenter sender window again
     * if there are, retransmit them
     */
    private void retransmitReenteredPacked() throws IOException {
        for (int i = 0; i < num && i < size; i++) {
            SentPacket sentPacket = window[(i + headIndex) % MAX_SIZE];
            if (sentPacket != null && sentPacket.getMyTimer().isNeedRetransmit()) {
                JPacket packet = sentPacket.getPacket();
                retransmit(packet);
                sentPacket.getMyTimer().reset();
                logger.logSeq(packet.getSeqnum(), true);
            }
        }
    }

    /**
     * whether sender window has no buffered packet
     *
     * @return
     */
    public boolean isEmpty() {
        return num == 0;
    }


    /**
     * close associated resources
     */
    protected void close() {
        sendSocket.close();
    }


    /**
     * buffer new sent but not acked packet, and start its timer
     *
     * @param packet
     */
    public void buffer(JPacket packet) {
        if (num >= size){
            return;
        }
        int index = (headIndex + num) % MAX_SIZE;
        if (window[index] != null){
            return;
        }
        SentPacket sentPacket = new SentPacket(packet, timeout, index, this);
        window[index] = sentPacket;
        sentPacket.getMyTimer().start();
        num++;
    }


    /**
     * retransmit packet according to its index
     *
     * @param index
     */
    public synchronized void retransmit(int index) throws IOException {
        size = 1;
        SentPacket sentPacket = window[index];
        if (sentPacket == null){
            return;
        }
        if (headIndex == index) {
            // retransmit packet and reset timer
            JPacket packet = sentPacket.getPacket();
            retransmit(packet);
            sentPacket.getMyTimer().reset();
            logger.logWindowSize(1, false);
            logger.logSeq(packet.getSeqnum(), true);
        } else {
            // delay retransmit, mark this packet need to be retransmitted later
            sentPacket.getMyTimer().cancel();
            sentPacket.getMyTimer().setNeedRetransmit(true);
            logger.logWindowSize(1, true);
        }
    }


    /**
     * send timed out packet to Emulator
     *
     * @param packet
     */
    private void retransmit(JPacket packet) {
        byte[] bytes = SerializeUtils.toBytes(packet);
        try {
            sendSocket.send(new DatagramPacket(bytes, bytes.length, nEmulatorAddr));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
