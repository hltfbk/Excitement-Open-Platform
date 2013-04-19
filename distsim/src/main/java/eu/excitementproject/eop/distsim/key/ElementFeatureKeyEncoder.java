package eu.excitementproject.eop.distsim.key;

/**
 * Encoding and decoding of element and feature to a unique long representation
 * 
 * @author Meni Adler
 * @since 24/04/2012
 *
 * @param <T> The enum type of the feature label 
 * 
 */
public class ElementFeatureKeyEncoder<T> {
	
	/**
	 * @param featureLabelEncoder 
	 */
	public ElementFeatureKeyEncoder(EcnodableEnum<T> featureLabelEncoder) {
		this.featureLabelEncoder = featureLabelEncoder;
	}
	
	/**
	 * @param elementId a unique id of some element
	 * @param featureId a unique id of some feature
	 * @param featureLabel the label assigned to the feature
	 * @return the encoding of the element id, feature id and the feature label
	 * @throws EncodingException
	 */
	public long encode(int elementId, int featureId, EcnodableEnum<T> featureLabel) throws EncodingException {
		if (featureLabel.getEncodingBitsNum() > 1)
			throw new EncodingException("feature label should be encodable to 1 bit");		
        return IntsLongEncoder.encode(elementId, featureId, 31,31) | featureLabel.encode(1);
	}

	/**
	 * @param encoding an encoding of some element id, feature id and feature label 
	 * @return element id, encoded in the given encoding
	 * @throws EncodingException for a case of problematic encoding
	 */
	public int decodeElementId(long encoding) throws EncodingException {
		return IntsLongEncoder.decode(encoding, true, 31, 31);
	}
	
	/**
	 * @param encoding an encoding of some element id, feature id and feature label
	 * @return feature id, encoded in the given encoding
	 * @throws EncodingException for a case of problematic encoding
	 */
	public int decodeFeatureId(long encoding) throws EncodingException {
		return IntsLongEncoder.decode(encoding, false, 31, 31);
	}
	
	
	/**
	 * @param encoding an encoding of some element id, feature id and feature label
	 * @return feature label, encoded in the given encoding
	 * @throws EncodingException  for a case of problematic encoding
	 */
	public T decodeFeatureLable(long encoding) throws EncodingException {
		return featureLabelEncoder.decode((int)(encoding & 1)); 
	}

	EcnodableEnum<T> featureLabelEncoder;
}
