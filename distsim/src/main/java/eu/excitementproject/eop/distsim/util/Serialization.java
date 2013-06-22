package eu.excitementproject.eop.distsim.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.apache.commons.codec.binary.Base64;

/**
 * Serialization utils: serialization of a given object into a string, and deserialization of the string into the original object
 * 
 * @author Meni Adler
 * @since 24/07/2012
 *
 */
public class Serialization {

	/**
	 * Serialize a given object to a string
	 * 
	 * @param obj an object to be serialized
	 * @return string serialization of the given object
	 * @throws SerializationException for any problem with the serialization process
	 */
	public static <T  extends Serializable> String serialize(T obj) throws SerializationException {
		try {
		    ByteArrayOutputStream bos = new ByteArrayOutputStream();
		    ObjectOutputStream os = new ObjectOutputStream(bos);
		    os.writeObject(obj);
		    String ret = new String(Base64.encodeBase64(bos.toByteArray()));
		    os.close();
		    return ret;
		} catch (Exception e) {
			throw new SerializationException(e);
		}
	  }


	/**
	 * Construct an object according to a given string serialization
	 * 
	 * @param serialization a serialization of some object
	 * @return a constructed object, according to the given serialization
	 * @throws SerializationException for any problem with the deserialization process
	 */
	public static <T extends Serializable> T deserialize(String serialization) throws SerializationException {
		try {
			if (serialization.isEmpty())
				throw new SerializationException("empty data was given for deserialization");
		    ByteArrayInputStream bis = new ByteArrayInputStream(Base64.decodeBase64(serialization.getBytes()));
		    ObjectInputStream oInputStream = new ObjectInputStream(bis);
		    @SuppressWarnings("unchecked")
			T ret = (T)oInputStream.readObject();
		    oInputStream.close();
		    return ret;
		} catch (Exception e) {
			throw new SerializationException(e);
		}
	}

}
