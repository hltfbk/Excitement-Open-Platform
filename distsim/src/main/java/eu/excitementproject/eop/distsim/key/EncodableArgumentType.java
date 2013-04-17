package eu.excitementproject.eop.distsim.key;

import eu.excitementproject.eop.distsim.domains.ArgumentType;


/**
 * An implementation of EcnodableEnum for ArgumentType value
 * 
 * @author Meni Adler
 * @since 24/04/2012
 *
 */
public class EncodableArgumentType implements EcnodableEnum<ArgumentType> {

	public EncodableArgumentType(ArgumentType value) {
		this.value = value;
	}

	public EncodableArgumentType(int encoding) throws EncodingException {
		this.value = decode(encoding);
	}

	
	/* (non-Javadoc)
	 * @see org.excitement.distsim.items.EcnodableEnum#get()
	 */
	@Override
	public ArgumentType get() {
		return value;
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.items.EcnodableEnum#encode(int)
	 */
	@Override
	public int encode(int bitsNum) throws EncodingException {
		if (bitsNum < 1)
			throw new EncodingException("ArgumentType requires one bit for encoding");
		return (value == ArgumentType.X ? 0 : 1);
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.items.EcnodableEnum#decode(int, int)
	 */
	@Override
	public ArgumentType decode(int encoding) throws EncodingException {
		if (encoding == 0)
			return ArgumentType.X;
		else {
			if (encoding == 1)
				return ArgumentType.Y;
			else
				throw new EncodingException("ilegal encoding value: " + encoding);
		}
		
	}
	
	/* (non-Javadoc)
	 * @see org.excitement.key.EcnodableEnum#getEncodingBitsNum()
	 */
	@Override
	public int getEncodingBitsNum() {
		return 1;
	}
	
	protected ArgumentType value;
}
