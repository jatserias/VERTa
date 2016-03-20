package mt;


/***
 * 
 * Given a distance Matrix betwen two sentences build the best alignment
 * @author jordi
 *
 */
public interface AlignmentBuilder {


    void build(boolean reversed, SentenceAlignment res, final DistanceMatrix d);

}
