import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

/**
 * utils for converting packet object to bytes or vice-verse
 */
public class SerializeUtils {

    /**
     * convert packet to bytes
     * @param packet
     * @return
     */
    public static byte[] toBytes(JPacket packet){

        byte[] tp = ByteBuffer.wrap(new byte[4]).putInt(packet.getType()).order(ByteOrder.LITTLE_ENDIAN).array();
        byte[] seq = ByteBuffer.wrap(new byte[4]).putInt(packet.getSeqnum()).order(ByteOrder.LITTLE_ENDIAN).array();
        byte[] len = ByteBuffer.wrap(new byte[4]).putInt(packet.getLength()).order(ByteOrder.LITTLE_ENDIAN).array();
        String data = packet.getData();
        int totalBytes = 12 + packet.getData().length();
        byte[] res = new byte[totalBytes];
        System.arraycopy(tp, 0, res, 0, 4);
        System.arraycopy(seq, 0, res, 4, 4);
        System.arraycopy(len, 0, res, 8, 4);
        System.arraycopy(data.getBytes(StandardCharsets.UTF_8), 0, res, 12, data.length());
        return res;

    }

    /**
     * convert bytes to packet
     * @param bytes
     * @param len
     * @return
     */
    public static JPacket toPacket(byte[] bytes, int len){

        int type = ByteBuffer.wrap(bytes, 0, 4).order(ByteOrder.BIG_ENDIAN).getInt();
        int seq = ByteBuffer.wrap(bytes, 4, 4).order(ByteOrder.BIG_ENDIAN).getInt();
        int length = ByteBuffer.wrap(bytes, 8, 4).order(ByteOrder.BIG_ENDIAN).getInt();
        String data = new String(bytes, 12, len - 12, StandardCharsets.UTF_8);
        return new JPacket(type, seq, length, data);

    }




}
