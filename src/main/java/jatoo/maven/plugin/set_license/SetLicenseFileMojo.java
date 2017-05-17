/*
 * Copyright (C) Cristian Sulea ( http://cristian.sulea.net )
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jatoo.maven.plugin.set_license;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

/**
 * The {@link Mojo} for <code>file</code> goal.
 * 
 * @author <a href="http://cristian.sulea.net" rel="author">Cristian Sulea</a>
 * @version 3.0, May 17, 2017
 */
@Mojo(name = "file")
public class SetLicenseFileMojo extends AbstractMojo {

  /** The license to be used, a sub-folder in the 'licenses' folder. */
  @Parameter
  private String license;

  /** The file containing the license to be copied. */
  @Parameter
  private File licenseFile;

  /** Project the plugin is called from. */
  @Component
  private MavenProject project;

  @Override
  public final void execute() throws MojoExecutionException, MojoFailureException {
    final Log log = getLog();

    final URL licenseResource;

    if (license == null) {

      if (licenseFile == null) {
        throw new MojoExecutionException("both parameters 'license' and 'licenseFile' are missing");
      }

      try {
        licenseResource = licenseFile.toURI().toURL();
      } catch (MalformedURLException e) {
        throw new MojoExecutionException("error getting license file (" + licenseFile + ")", e);
      }
    }

    else {
      try {
        licenseResource = getClass().getResource("licenses/" + license + "/LICENSE").toURI().toURL();
      } catch (MalformedURLException | URISyntaxException e) {
        throw new MojoExecutionException("error getting license (" + license + ")", e);
      }
    }

    log.info("setting license file:");
    log.info("from: " + licenseResource);
    log.info("to:   " + new File(project.getBasedir(), "LICENSE").getAbsolutePath());

    try {
      FileUtils.copyURLToFile(licenseResource, new File(project.getBasedir(), "LICENSE"));
    } catch (IOException e) {
      throw new MojoExecutionException("error copying license file", e);
    }
  }
}
