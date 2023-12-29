
public class ClientThreadBase extends Thread {

    protected volatile Thread blinker;

    @Override
    public void start() {
        this.blinker = new Thread(this);
        this.blinker.start();
    }

    public void killThread() {
        this.blinker = null;
    }
}
