package eu.excitementproject.eop.distsim.key;

/**
 * Number compression utility
 * 
 * @author Meni Adler
 * @since 26/12/2012
 *
 */
public class IntsLongEncoder {
	
	/**
	 * @param i1 first int value to be encoded
	 * @param i2 second int value to be encoded
	 * @param bits1 maximal number of bits for the first value encoding
	 * @param bits2 maximal number of bits for the second value encoding
	 * @return encoding of the two given int value to one long values
	 * @throws EncodingException  for a case of problematic encoding
	 */
	public static long encode(int i1, int i2, int bits1, int bits2) throws EncodingException {
		
		if (bits1 + bits2 > 63)
			throw new EncodingException((bits1 + bits2) + " bits cannot be encoded to long");
		if (i1 > Math.pow(2,bits1)-1 || i1 < (-1 * (Math.pow(2,bits1))))
			throw new EncodingException(i1 + " cannot be encoded to " + bits1 + " bits");
		if (i2 > Math.pow(2,bits2)-1 || i2 < (-1 * (Math.pow(2,bits2))))
			throw new EncodingException(i2 + " cannot be encoded to " + bits2 + " bits");
		
		return (((long)i1) << (63 - bits1)) | (((long)i2)  << (63 - bits1 - bits2));
	}

	/**
	 * @param encoding encoding of two int values
	 * @param bFirst indicates if the first or the second value should be decoded
	 * @param bits1  number of bits, assigned for the encoding of the first value
	 * @param bits2 number of bits, assigned for the encoding of the second value
	 * @return the decoded value
	 * @throws EncodingException  for a case of problematic encoding
	 */
	public static int decode(long encoding, boolean bFirst, int bits1, int bits2) throws EncodingException {
		
		if (bits1 > 31)
			throw new EncodingException(bits1 + " bits cannot be decoded to int");

		if (bFirst)
			return (int)(encoding >> (63 - bits1));
		else
			return (int)encoding >>  (63 - bits1 - bits2);
	}
	
	
	public static void main(String[] args) throws EncodingException {
		//DecimalFormat format = new DecimalFormat("################"); 
		//System.out.println(format.format(Math.pow(2,31)));
		long l = IntsLongEncoder.encode(17,654,20,23);
		System.out.println(l);
		System.out.println(IntsLongEncoder.decode(l,true,20,23));
		System.out.println(IntsLongEncoder.decode(l,false,20,23));
	}
}
