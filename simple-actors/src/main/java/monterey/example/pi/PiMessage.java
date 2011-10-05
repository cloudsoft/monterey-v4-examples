package monterey.example.pi;

import java.io.Serializable;

public final class PiMessage implements Serializable {

    public final int start;
    public final int noElements;

    PiMessage(int start, int noElements) {
        this.start = start;
        this.noElements = noElements;
    }

}
