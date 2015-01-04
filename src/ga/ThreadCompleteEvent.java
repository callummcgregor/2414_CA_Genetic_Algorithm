package ga;

import java.util.EventObject;

/**
 * Event fired once termination criteria has been met and current Thread finished its processing
 *
 * @author dev1 & dev2
 */
public class ThreadCompleteEvent extends EventObject {
    public ThreadCompleteEvent(Object source) {
        super(source);
    }
}
