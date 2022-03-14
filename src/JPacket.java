import java.io.Serializable;

/**
 * transmitted packet model
 */
public class JPacket implements Serializable {

    private Integer type;
    private Integer seqnum;
    private Integer length;
    private String data;

    public JPacket(Integer type, Integer seqnum, Integer length, String data) {
        this.type = type;
        this.seqnum = seqnum;
        this.length = length;
        this.data = data;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getSeqnum() {
        return seqnum;
    }

    public void setSeqnum(Integer seqnum) {
        this.seqnum = seqnum;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
