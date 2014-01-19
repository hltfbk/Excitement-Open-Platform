
package eu.excitementproject.eop.core;

import java.io.File;

import static org.junit.Assert.*;
import org.junit.Test;

import eu.excitementproject.eop.common.configuration.NameValueTable;
import eu.excitementproject.eop.common.utilities.configuration.ImplCommonConfig;

public class CommonConfigTest {

	@Test
	public void test() {
		
		try {
			
			File f = new File("./src/test/resources/example_of_configuration_file.xml");
			ImplCommonConfig commonConfig = new ImplCommonConfig(f);
			
			NameValueTable nameValueTable = commonConfig.getSection("core.MyEDA");
			assertTrue(nameValueTable.getString("someOption").equals("PhoneticDistanceComponent,EditDistanceComponent"));
			
			nameValueTable = commonConfig.getSection("PhoneticDistanceComponent");
			assertTrue(nameValueTable.getDouble("beta").doubleValue() == 0.1);
			
			nameValueTable = commonConfig.getSubSection("PhoneticDistanceComponent", "instance1");
			assertTrue(nameValueTable.getDouble("consonantScore").doubleValue() == 1.0);
			
			nameValueTable = commonConfig.getSubSection("PhoneticDistanceComponent", "instance2");
			assertTrue(nameValueTable.getDouble("consonantScore").doubleValue() == 0.6);
			
			
			
		}catch(Exception e) {
			System.err.println(e.getMessage());
		}
		
	}

}
