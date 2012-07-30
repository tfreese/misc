package de.freese.maven.plugin.trimrepo;

/*
 * Copyright 2001-2005 The Apache Software Foundation. Licensed under the Apache License, Version
 * 2.0 (the "License"); you may not use this file except in compliance with the License. You may
 * obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by
 * applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See
 * the License for the specific language governing permissions and limitations under the License.
 */

import java.io.File;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.settings.Server;
import org.apache.maven.settings.Settings;
import org.apache.maven.settings.crypto.DefaultSettingsDecryptionRequest;
import org.apache.maven.settings.crypto.SettingsDecrypter;
import org.apache.maven.settings.crypto.SettingsDecryptionRequest;
import org.apache.maven.settings.crypto.SettingsDecryptionResult;
import org.apache.maven.wagon.Wagon;
import org.apache.maven.wagon.authentication.AuthenticationInfo;
import org.apache.maven.wagon.repository.Repository;

/**
 * Goal which touches a timestamp file.<br>
 * ${prefix}-maven-plugin - for plugins from other sources
 * 
 * @goal cleanup
 * @phase validate
 */
public class MyMojo extends AbstractMojo
{
	// /**
	// * Location of the file.
	// *
	// * @parameter expression="${project.build.directory}" default-value="."
	// * @required
	// */
	// private File outputDirectory = null;

	// /**
	// * @parameter
	// * @required
	// */
	// private String host = null;
	//
	// /**
	// * @parameter default-value="/"
	// */
	// private String rootPath = null;

	/**
	 * The Maven project this plugin runs in.<br>
	 * 
	 * @component
	 */
	private Wagon wagon;

	/**
	 * The Maven project this plugin runs in.<br>
	 * 
	 * @parameter expression="${project}"
	 * @required
	 */
	private MavenProject project;

	/**
	 * <br>
	 * 
	 * @parameter expression="${settings}"
	 * @required
	 */
	private Settings settings;

	/**
	 * <br>
	 * 
	 * @component
	 * @required
	 */
	private SettingsDecrypter settingsDecrypter = null;

	/**
	 * Erstellt ein neues {@link MyMojo} Object.
	 */
	public MyMojo()
	{
		super();
	}

	/**
	 * @see org.apache.maven.plugin.AbstractMojo#execute()
	 */
	@Override
	public void execute() throws MojoExecutionException
	{
		if (this.project.getArtifact().isRelease())
		{
			getLog().info("Project is release, skipping cleanup.");
			return;
		}

		ArtifactRepository distributionRepository =
				this.project.getDistributionManagementArtifactRepository();
		String serverID = distributionRepository.getId();

		Server server = this.settings.getServer(serverID);

		String url = distributionRepository.getUrl();
		String username = server.getUsername();
		String password = server.getPassword();

		getLog().info("Server: " + serverID + " " + username + "/" + password + " @ " + url);

		// PW entschlüsseln.
		SettingsDecryptionRequest decryptionRequest =
				new DefaultSettingsDecryptionRequest(this.settings);
		SettingsDecryptionResult decryptionResult =
				this.settingsDecrypter.decrypt(decryptionRequest);

		for (Server s : decryptionResult.getServers())
		{
			if (s.getId().equals(serverID))
			{
				server = s;
			}
		}

		// username = server.getUsername();
		password = server.getPassword();
		// getLog().info("Server: " + serverID + " " + username + "/" + password);

		try
		{
			AuthenticationInfo authenticationInfo = new AuthenticationInfo();
			authenticationInfo.setUserName(username);
			authenticationInfo.setPassword(password);

			// Repository repository = new Repository(serverID, this.host + "/" + this.rootPath);
			Repository repository = new Repository(serverID, url);
			this.wagon.connect(repository, authenticationInfo);
			// this.wagon.connect(repository);

			this.wagon.put(new File("pom.xml"), "mypom.xml");
			this.wagon.get("mypom.xml", new File("./mypom.xml"));
		}
		catch (Exception ex)
		{
			getLog().error(ex);
		}

		try
		{
			this.wagon.disconnect();
		}
		catch (Exception ex)
		{
			getLog().error(ex);
		}

		// File f = this.outputDirectory;
		//
		// if (!f.exists())
		// {
		// f.mkdirs();
		// }
		//
		// File touch = new File(f, "touch.txt");
		//
		// FileWriter w = null;
		// try
		// {
		// w = new FileWriter(touch);
		//
		// w.write("touch.txt");
		// }
		// catch (IOException e)
		// {
		// throw new MojoExecutionException("Error creating file " + touch, e);
		// }
		// finally
		// {
		// if (w != null)
		// {
		// try
		// {
		// w.close();
		// }
		// catch (IOException e)
		// {
		// // ignore
		// }
		// }
		// }
	}
}
