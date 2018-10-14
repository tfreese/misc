/**
 * Created: 07.04.2012
 */

package de.freese.sonstiges.maven;

import java.io.File;
import org.apache.maven.settings.Server;
import org.apache.maven.settings.Settings;
import org.apache.maven.settings.building.DefaultSettingsBuilderFactory;
import org.apache.maven.settings.building.DefaultSettingsBuildingRequest;
import org.apache.maven.settings.building.SettingsBuilder;
import org.apache.maven.settings.building.SettingsBuildingResult;

/**
 * @author Thomas Freese
 */
public class ShowMavenSettings
{
    /**
     * @param args String[]
     * @throws Exception Falls was schief geht.
     */
    public static void main(final String[] args) throws Exception
    {
        DefaultSettingsBuilderFactory settingsBuilderFactory = new DefaultSettingsBuilderFactory();
        SettingsBuilder settingsBuilder = settingsBuilderFactory.newInstance();

        // @formatter:off
		DefaultSettingsBuildingRequest settingsBuildingRequest = new DefaultSettingsBuildingRequest()
		        .setSystemProperties(System.getProperties())
				.setUserProperties(null)
				.setGlobalSettingsFile(new File(System.getenv("M2_HOME") + "/conf/settings.xml"))
				.setUserSettingsFile(new File(System.getProperty("user.home") + "/.m2/settings.xml"));
		// @formatter:on

        SettingsBuildingResult settingsBuildingResult = settingsBuilder.build(settingsBuildingRequest);

        Settings settings = settingsBuildingResult.getEffectiveSettings();

        for (Server server : settings.getServers())
        {
            System.out.println("Server = " + server.getId() + " " + server.getUsername() + "/" + server.getPassword());
        }

        System.out.println();

        // Passwörter entschlüsseln
        // Ausserhalb vom Plexus Container die Objekte über Reflection zusammenbasteln.
        // SecDispatcher securityDispatcher = new DefaultSecDispatcher();
        // Field cipherField = securityDispatcher.getClass().getDeclaredField("_cipher");
        // cipherField.setAccessible(true);
        // cipherField.set(securityDispatcher, new DefaultPlexusCipher());
        // ((DefaultSecDispatcher) securityDispatcher).setConfigurationFile(System.getProperty("user.home") + "/.m2/settings-security.xml");
        //
        // SettingsDecrypter settingsDecrypter = new DefaultSettingsDecrypter();
        // Field dispatcherField = settingsDecrypter.getClass().getDeclaredField("securityDispatcher");
        // dispatcherField.setAccessible(true);
        // dispatcherField.set(settingsDecrypter, securityDispatcher);
        //
        // SettingsDecryptionRequest decryptionRequest = new DefaultSettingsDecryptionRequest(settings);
        // SettingsDecryptionResult decryptionResult = settingsDecrypter.decrypt(decryptionRequest);
        //
        // for (Server server : decryptionResult.getServers())
        // {
        // System.out.println("Server = " + server.getId() + " " + server.getUsername() + "/" + server.getPassword());
        // }
    }
}
