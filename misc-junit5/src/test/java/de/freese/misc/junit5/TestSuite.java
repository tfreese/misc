/****/
package de.freese.misc.junit5;

import org.junit.platform.runner.JUnitPlatform;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.SuiteDisplayName;
import org.junit.runner.RunWith;

/***
 * @author Thomas Freese
 */
@RunWith(JUnitPlatform.class)
@SuiteDisplayName("JUnit Platform Suite Demo")
@SelectPackages("de.freese.misc.junit5")
public class TestSuite
{
    /**
     * Erstellt ein neues {@link TestSuite} Object.
     */
    public TestSuite()
    {
        super();
    }
}
