package ac.biu.nlp.nlp.engineml.classifiers;
import java.util.Map;

/**
 * Similar to {@link TrainableClassifier}, but assumes that the underlying model is:
 * <BR>
 * w1*x1+w2*x2+ ... + wn*xn <= b ===> true 
 * w1*x1+w2*x2+ ... + wn*xn > b ===> false
 * <P>
 * 
 * Thread-safety: Please read the comment about thread-safety in {@link TrainableClassifier}.<BR>
 * As for the methods added in this interface: the "get" methods, i.e.,
 * {@link #getWeights()}, {@link #getThreshold()} and {@link #getProduct(Map)},
 * are thread-safe.
 * 
 * 
 * @author Asher Stern
 * @since Jun 16, 2011
 *
 */
public interface LinearTrainableStorableClassifier extends LinearTrainableClassifier, TrainableStorableClassifier
{

}
