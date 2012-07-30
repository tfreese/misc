package util;

import java.io.IOException;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;

/**
 * @author Thomas Freese
 */
public class Utilities
{
	/**
	 * @return {@link AWSCredentials}
	 */
	public AWSCredentials getCreds()
	{
		try
		{
			return new PropertiesCredentials(getClass().getClassLoader().getResourceAsStream(
					"AwsCredentials.properties"));
		}
		catch (IOException ex)
		{
			throw new AmazonServiceException(ex.getMessage());
		}
	}
}
