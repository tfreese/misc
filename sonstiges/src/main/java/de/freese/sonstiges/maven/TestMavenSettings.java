/**
 * Created: 07.04.2012
 */

package de.freese.sonstiges.maven;

import java.io.File;
import java.lang.reflect.Field;

import org.apache.maven.settings.Server;
import org.apache.maven.settings.Settings;
import org.apache.maven.settings.building.DefaultSettingsBuilderFactory;
import org.apache.maven.settings.building.DefaultSettingsBuildingRequest;
import org.apache.maven.settings.building.SettingsBuilder;
import org.apache.maven.settings.building.SettingsBuildingResult;
import org.apache.maven.settings.crypto.DefaultSettingsDecrypter;
import org.apache.maven.settings.crypto.DefaultSettingsDecryptionRequest;
import org.apache.maven.settings.crypto.SettingsDecrypter;
import org.apache.maven.settings.crypto.SettingsDecryptionRequest;
import org.apache.maven.settings.crypto.SettingsDecryptionResult;
import org.sonatype.plexus.components.cipher.DefaultPlexusCipher;
import org.sonatype.plexus.components.sec.dispatcher.DefaultSecDispatcher;
import org.sonatype.plexus.components.sec.dispatcher.SecDispatcher;

/**
 * @author Thomas Freese
 */
public class TestMavenSettings
{
	/**
	 * @param args String[]
	 * @throws Exception Falls was schief geht.
	 */
	public static void main(final String[] args) throws Exception
	{
		DefaultSettingsBuilderFactory settingsBuilderFactory = new DefaultSettingsBuilderFactory();
		SettingsBuilder settingsBuilder = settingsBuilderFactory.newInstance();

		DefaultSettingsBuildingRequest settingsBuildingRequest =
				new DefaultSettingsBuildingRequest()
						.setSystemProperties(System.getProperties())
						.setUserProperties(null)
						.setGlobalSettingsFile(
								new File(System.getenv("M2_HOME") + "/conf/settings.xml"))
						.setUserSettingsFile(
								new File(System.getProperty("user.home") + "/.m2/settings.xml"));

		SettingsBuildingResult settingsBuildingResult =
				settingsBuilder.build(settingsBuildingRequest);

		Settings settings = settingsBuildingResult.getEffectiveSettings();

		for (Server server : settings.getServers())
		{
			System.out.println("Server = " + server.getId() + " " + server.getUsername() + "/"
					+ server.getPassword());
		}

		System.out.println();

		// Passwörter entschlüsseln
		// Ausserhalb vom Plexus Container die Pbjekte über Reflection zusammenbasteln.
		SecDispatcher securityDispatcher = new DefaultSecDispatcher();
		Field cipherField = securityDispatcher.getClass().getDeclaredField("_cipher");
		cipherField.setAccessible(true);
		cipherField.set(securityDispatcher, new DefaultPlexusCipher());
		((DefaultSecDispatcher) securityDispatcher).setConfigurationFile(System
				.getProperty("user.home") + "/.m2/settings-security.xml");

		SettingsDecrypter settingsDecrypter = new DefaultSettingsDecrypter();
		Field dispatcherField = settingsDecrypter.getClass().getDeclaredField("securityDispatcher");
		dispatcherField.setAccessible(true);
		dispatcherField.set(settingsDecrypter, securityDispatcher);

		SettingsDecryptionRequest decryptionRequest =
				new DefaultSettingsDecryptionRequest(settings);
		SettingsDecryptionResult decryptionResult = settingsDecrypter.decrypt(decryptionRequest);

		for (Server server : decryptionResult.getServers())
		{
			System.out.println("Server = " + server.getId() + " " + server.getUsername() + "/"
					+ server.getPassword());
		}
	}
}
