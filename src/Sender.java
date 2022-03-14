import java.io.FileInputStream;
import java.io.IOException;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;


public class Sender {

    // socket for receiving data from Emulator
    private DatagramSocket receiveSocket;

    // socket for sending new packet to Emulator
    private DatagramSocket sendSocket;

    // address of Emulator
    private InetSocketAddress nEmulatorAddr;

    // DatagramPacket for buffering received data from Emulator
    private DatagramPacket rPacket;

    // bytes array for buffering data read from file to be sent
    private byte[] sbuf;

    // stream for read file to be sent
    private FileInputStream fis = null;

    // global seqnum for next packet
    private int globalNextSeq = 0;

    // sender window, which buffer sent but not acked packet
    private SenderWindow senderWindow = null;

    // whether sender has receiver EOT from receiver
    private boolean receiveEOT;

    // whether sender has sent EOT to receiver
    private boolean sendEOT;

    private SenderLogger logger;

    public Sender(String nEmulatorHost, String nEmulatorPort, String localReceivePort, String timeout, String fileToTransfer) {
        SenderLogger senderLogger = new SenderLogger();
        try {
            this.nEmulatorAddr = new InetSocketAddress(nEmulatorHost, Integer.parseInt(nEmulatorPort));
            this.senderWindow = new SenderWindow(Integer.parseInt(timeout), this.nEmulatorAddr, senderLogger);
        } catch (NumberFormatException e) {
            System.err.println("can not parse string to integer");
            return;
        }
        try {
            this.receiveSocket = new DatagramSocket(Integer.parseInt(localReceivePort));
            this.sendSocket = new DatagramSocket();
        } catch (SocketException e) {
            System.err.println("can not create datagram socket");
            return;
        }
        this.sbuf = new byte[500];
        this.rPacket = new DatagramPacket(new byte[2048], 0, 2048);
        try {
            if (!Files.exists(Paths.get(fileToTransfer))){
                System.err.println("file to transfer does not exist");
                return;
            }
            this.fis = new FileInputStream(fileToTransfer);
        } catch (IOException e) {
            System.err.println("can not create file to transfer or associated file input stream");
            return;
        }
        this.receiveEOT = false;
        this.sendEOT = false;
        this.logger = senderLogger;
    }

    /**
     * send new packet to nEmulator, up to 500 bytes
     *
     * @throws IOException
     */
    public void sendNewPacket() throws IOException {
        int read = fis.read(sbuf);
        JPacket packet;
        // still some bytes to send
        if (read != -1) {
            // send
            String data = new String(sbuf, 0, read);
            packet = new JPacket(1, (globalNextSeq % 32), data.length(), data);
            logger.logSeq(packet.getSeqnum(), true);
            byte[] bytes = SerializeUtils.toBytes(packet);
            sendSocket.send(new DatagramPacket(bytes, bytes.length, nEmulatorAddr));
            // buffer
            senderWindow.buffer(packet);
            // update
            globalNextSeq++;
            // no more bytes, send EOT
        } else if (!sendEOT) {
            // send
            packet = new JPacket(2, (globalNextSeq % 32), 0, "");
            logger.logSeqEOT(true);
            byte[] bytes = SerializeUtils.toBytes(packet);
            sendSocket.send(new DatagramPacket(bytes, bytes.length, nEmulatorAddr));
            sendEOT = true;
            // update
            globalNextSeq++;
        }
    }

    /**
     * receive data from receiver, handler this data according to its type
     *
     * @throws IOException
     */
    public void listen() throws IOException {
        while (true) {
            receiveSocket.receive(rPacket);
            byte[] bytes = rPacket.getData();
            JPacket packet = SerializeUtils.toPacket(bytes, rPacket.getLength());
            Integer type = packet.getType();
            // sack
            if (type == 0) {
                logger.logAck(packet.getSeqnum(), true);
                int num = senderWindow.receiveAck(packet.getSeqnum());
                for (int i = 0; i < num; i++) {
                    sendNewPacket();
                }
                if (sendEOT && receiveEOT && senderWindow.isEmpty()) {
                    close();
                    break;
                }
                // EOT
            } else if (type == 2) {
                logger.logAckEOT(true);
                receiveEOT = true;
                if (senderWindow.isEmpty()) {
                    close();
                    break;
                }
            }
        }
    }

    /**
     * close associated opened resources, sender program exits
     *
     * @throws IOException
     */
    private void close() throws IOException {
        fis.close();
        sendSocket.close();
        receiveSocket.close();
        senderWindow.close();
    }

    public static void main(String[] args) throws IOException {
        Sender sender = new Sender(args[0], args[1], args[2], args[3], args[4]);
        sender.sendNewPacket();
        sender.listen();
    }
}


