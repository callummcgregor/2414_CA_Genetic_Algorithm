package ga;

/**
 * A Cloneable subtype of Design to allow a new Design to be created with a existing Design's designvector
 *
 * @author dev1 & dev2
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
