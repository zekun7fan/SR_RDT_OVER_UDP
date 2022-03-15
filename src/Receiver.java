import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Receiver {

    // Emulator socket address
    private final InetSocketAddress nEmulatorAddr;

    // socket for receiving data from Emulator
    private DatagramSocket receiveSocket;

    // socket for sending data to Emulator
    private DatagramSocket sendSocket;

    // stream for writing received data into specific file
    private final FileOutputStream fos;

    // DatagramPacket for buffering received data from Emulator
    private final DatagramPacket rPacket;

    // an array for buffering received packet, whose length is 10
    private final JPacket[] recWindow;

    // the index/location where the packet with next smallest seqnum will be buffered
    private int headIndex;

    // the next smallest seqnum
    private int nextSeq;

    // logger
    private ReceiverLogger logger;


    public Receiver(String nEmulatorHost, String nEmulatorPort, String localReceivePort, String fileToWrite) throws IOException {

        this.nEmulatorAddr = new InetSocketAddress(nEmulatorHost, Integer.parseInt(nEmulatorPort));
        try {
            this.receiveSocket = new DatagramSocket(Integer.parseInt(localReceivePort));
            this.sendSocket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        this.rPacket = new DatagramPacket(new byte[2048], 0, 2048);
        Path path = Paths.get(fileToWrite);
        Files.deleteIfExists(path);
        Files.createFile(path);
        this.fos = new FileOutputStream(fileToWrite);
        this.recWindow = new JPacket[10];
        this.headIndex = 0;
        this.nextSeq = 0;
        this.logger = new ReceiverLogger();
    }

    /**
     * receive data from Emulator, handler received data according to its type
     *
     * @throws IOException
     */
    public void listen() throws IOException {
        while (true) {
            receiveSocket.receive(rPacket);
            byte[] bytes = rPacket.getData();
            JPacket packet = SerializeUtils.toPacket(bytes, rPacket.getLength());
            Integer type = packet.getType();
            // data
            if (type == 1) {
                Integer seqnum = packet.getSeqnum();
                logger.logSeq(seqnum);
                if (isInRecWindow(seqnum)) {
                    // ack this packet
                    JPacket ack = new JPacket(0, seqnum, 0, "");
                    byte[] ackBytes = SerializeUtils.toBytes(ack);
                    sendSocket.send(new DatagramPacket(ackBytes, ackBytes.length, nEmulatorAddr));
                    // buffer this packet
                    buffer(packet);
                } else if (isInLast10Seq(seqnum)) {
                    // ack this old packet and discard
                    JPacket oldAck = new JPacket(0, seqnum, 0, "");
                    byte[] oldAckBytes = SerializeUtils.toBytes(oldAck);
                    sendSocket.send(new DatagramPacket(oldAckBytes, oldAckBytes.length, nEmulatorAddr));
                }
                // EOT
            } else if (type == 2) {
                // send EOT and close this receiver
                logger.logEOT();
                JPacket EOTPacket = new JPacket(2, 0, 0, "");
                byte[] EOTbytes = SerializeUtils.toBytes(EOTPacket);
                sendSocket.send(new DatagramPacket(EOTbytes, EOTbytes.length, nEmulatorAddr));
                close();
                break;
            }

        }
    }

    /**
     * check whether this seqnum is in receiver window
     *
     * @param seq seq
     * @return
     */
    private boolean isInRecWindow(Integer seq) {
        int head = nextSeq;
        int tail = (nextSeq + 9) % 32;
        if (head < tail) {
            return seq >= head && seq <= tail;
        } else {
            return (seq >= head && seq <= 31) || (seq >= 0 && seq <= tail);
        }
    }


    /**
     * check whether this seqnum is in last 10 seqnum
     *
     * @param seq seq
     * @return
     */
    private boolean isInLast10Seq(Integer seq) {
        int head = nextSeq;
        if (head >= 10) {
            return seq >= head - 10 && seq <= head - 1;
        } else {
            return (seq >= 0 && seq <= head - 1) || (seq >= (32 - (10 - head)) && seq <= 31);
        }
    }

    /**
     * close receiver
     *
     * @throws IOException
     */
    private void close() throws IOException {
        fos.close();
        sendSocket.close();
        receiveSocket.close();

    }

    /**
     * buffer new arriving packet
     *
     * @param packet packet
     * @throws IOException
     */
    private void buffer(JPacket packet) throws IOException {
        // calculate the array index for buffering this packet
        Integer seqnum = packet.getSeqnum();
        // if seqnum > nextSeq, the offset is seqnum - nextSeq
        // otherwise, the offset is (31-nextSeq) + (seqnum-0)
        int offset = seqnum >= nextSeq ? seqnum - nextSeq : (31 - nextSeq) + seqnum;
        int index = (headIndex + offset) % 10;
        // whether this packet has already been buffered
        boolean buffered = false;
        if (recWindow[index] == null) {
            recWindow[index] = packet;
            buffered = true;
        }
        if (!buffered) {
            return;
        }
        // write in-order packets to specific file
        int deliveryNum = 0;
        for (int i = 0; i < 10; i++) {
            int idx = (headIndex + i) % 10;
            JPacket bufferedPacket = recWindow[idx];
            if (bufferedPacket == null) {
                break;
            } else {
                fos.write(bufferedPacket.getData().getBytes(StandardCharsets.UTF_8));
                recWindow[idx] = null;
                deliveryNum++;
            }
        }
        // update headIndex and nextSeq
        headIndex = (headIndex + deliveryNum) % 10;
        nextSeq = (nextSeq + deliveryNum) % 32;
    }

    public static void main(String[] args) throws IOException {
        Receiver receiver = new Receiver(args[0], args[1], args[2], args[3]);
        receiver.listen();
        System.exit(0);
    }


}
