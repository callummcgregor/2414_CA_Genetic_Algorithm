package ga;

import java.util.EventObject;

/**
 * @author dev1 & dev2
 * @version 1.0
 * Event fired in case of a MemberThread finishing it's Design's evolutions
 */
public class EvolutionsComplete extends EventObject {
    public EvolutionsComplete(Object source) {
        super(source);
    }
}
