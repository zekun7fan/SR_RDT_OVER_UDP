import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;


/**
 * timer associated with sent but not acked packet
 */
class MyTimer {

    private Timer timer;

    private final Integer timeout;

    private boolean needRetransmit;

    private final Integer index;

    private final SenderWindow senderWindow;

    public MyTimer(Integer timeout, Integer index, SenderWindow senderWindow) {
        this.timeout = timeout;
        this.needRetransmit = false;
        this.timer = new Timer();
        this.index = index;
        this.senderWindow = senderWindow;
    }

    /**
     * start timer
     */
    public void start() {
        setTimer();
    }

    /**
     * reset timer
     */
    public void reset() {
        setTimer();
    }


    /**
     * set timer and start scheduled task
     */
    private void setTimer() {
        this.timer = new Timer();
        needRetransmit = true;
        this.timer.schedule(new TimerTask() {
            @Override
            public void run() {
                // notify the sender window a timeout event occur
                try {
                    senderWindow.retransmit(index);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, timeout);
    }

    /**
     * cancel timer
     */
    public void cancel() {
        needRetransmit = false;
        timer.cancel();
        timer.purge();
        this.timer = null;
    }

    public boolean isNeedRetransmit() {
        return needRetransmit;
    }

    public void setNeedRetransmit(boolean needRetransmit) {
        this.needRetransmit = needRetransmit;
    }
}
