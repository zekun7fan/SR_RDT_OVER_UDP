import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ReceiverLogger {

    private FileOutputStream arrivalLog;

    public ReceiverLogger() {
        try {
            Path p1 = Paths.get("arrival.log");
            Files.deleteIfExists(p1);
            Files.createFile(p1);
            arrivalLog = new FileOutputStream("arrival.log", true);
        } catch (IOException e) {
            System.err.println("fail to create arrival.log");
        }
    }

    public void logSeq(Integer seq) throws IOException {
        arrivalLog.write(String.valueOf(seq).getBytes(StandardCharsets.UTF_8));
        arrivalLog.write("\r\n".getBytes(StandardCharsets.UTF_8));
    }

    public void logEOT() throws IOException {
        arrivalLog.write("EOT".getBytes(StandardCharsets.UTF_8));
        arrivalLog.write("\r\n".getBytes(StandardCharsets.UTF_8));
    }
}
