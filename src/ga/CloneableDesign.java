package ga;

/**
 * @author dev1 & dev2
 * @version 1.0
 * A cloneable subtype of Design allowing
 */
public class CloneableDesign extends Design implements Cloneable{

    public CloneableDesign(Problem prob){
        super(prob);
    }

    @Override
    public Design clone() {
        try {
            return (Design) super.clone();
        } catch (Exception e) {
            return null; //TODO: improve
        }
    }
}
