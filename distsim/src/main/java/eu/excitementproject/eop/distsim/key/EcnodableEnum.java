package eu.excitementproject.eop.distsim.key;

/**
 * An interface for enum type state, with encoding/decoding
 *   
 * @author Meni Adler
 * @since 24/04/2012
 *
 * @param <T> the enum type of the encoded value
 * 
 */
public interface EcnodableEnum<T> {
	/**
	 * @return the enum value
	 */
	T get();
	
	/**
	 * @param bitsNum number of bits for the encoding
	 * @return encoding of the enum value to at most bitsNum bits
	 * @throws EncodingException if the enum type cannot be encoded to bitsNum bits
	 */
	int encode(int bitsNum) throws EncodingException;
	
	/**
	 * @param encoding
	 * @return decoding of the given encoding to enum value
	 * @throws EncodingException
	 */
	T decode(int encoding)  throws EncodingException;
	
	
	/**
	 * @return the minimal number of bits required for the encoding
	 */
	int getEncodingBitsNum();
}
