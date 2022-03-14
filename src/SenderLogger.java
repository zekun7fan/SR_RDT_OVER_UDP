import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SenderLogger {

    private FileOutputStream seqnumLog;
    private FileOutputStream ackLog;
    private FileOutputStream nLog;
    private int timeStamp = 0;


    public SenderLogger() {
        try {
            Path p1 = Paths.get("seqnum.log");
            Files.deleteIfExists(p1);
            Files.createFile(p1);
            this.seqnumLog = new FileOutputStream("seqnum.log", true);
        } catch (IOException e) {
            System.err.println("fail to create seqnum.log");
        }
        try {
            Path p2 = Paths.get("ack.log");
            Files.deleteIfExists(p2);
            Files.createFile(p2);
            this.ackLog = new FileOutputStream("ack.log", true);
        } catch (IOException e) {
            System.err.println("fail to create ack.log");
        }
        try {
            Path p3 = Paths.get("N.log");
            Files.deleteIfExists(p3);
            Files.createFile(p3);
            this.nLog = new FileOutputStream("N.log", true);
        } catch (IOException e) {
            System.err.println("fail to create N.log");
        }
    }

    public void logWindowSize(Integer size, boolean update) throws IOException {
        String record = "t=" + timeStamp + " " + size;
        nLog.write(record.getBytes(StandardCharsets.UTF_8));
        nLog.write("\r\n".getBytes(StandardCharsets.UTF_8));
        if (update) {
            timeStamp++;
        }
    }

    public void logSeq(Integer seq, boolean update) throws IOException {
        String record = "t=" + timeStamp + " " + seq;
        seqnumLog.write(record.getBytes(StandardCharsets.UTF_8));
        seqnumLog.write("\r\n".getBytes(StandardCharsets.UTF_8));
        if (update) {
            timeStamp++;
        }
    }

    public void logSeqEOT(boolean update) throws IOException {
        String record = "t=" + timeStamp + " EOT";
        seqnumLog.write(record.getBytes(StandardCharsets.UTF_8));
        seqnumLog.write("\r\n".getBytes(StandardCharsets.UTF_8));
        if (update) {
            timeStamp++;
        }
    }

    public void logAck(Integer ack, boolean update) throws IOException {
        String record = "t=" + timeStamp + " " + ack;
        ackLog.write(record.getBytes(StandardCharsets.UTF_8));
        ackLog.write("\r\n".getBytes(StandardCharsets.UTF_8));
        if (update) {
            timeStamp++;
        }
    }

    public void logAckEOT(boolean update) throws IOException {
        String record = "t=" + timeStamp + " EOT";
        ackLog.write(record.getBytes(StandardCharsets.UTF_8));
        ackLog.write("\r\n".getBytes(StandardCharsets.UTF_8));
        if (update) {
            timeStamp++;
        }
    }
}
