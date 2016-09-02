/*
 * Copyright (C) Cristian Sulea ( http://cristian.sulea.net )
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package jatoo.maven.plugin.set_license;

import java.io.File;
import java.io.IOException;

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
 * @version 2.0.0, September 2, 2016
 */
@Mojo(name = "file")
public class SetLicenseFileMojo extends AbstractMojo {

  /** The file containing the license to be copied. */
  @Parameter
  private File licenseFile;

  /** Project the plugin is called from. */
  @Component
  private MavenProject project;

  @Override
  public final void execute() throws MojoExecutionException, MojoFailureException {
    final Log log = getLog();

    if (licenseFile == null) {
      throw new MojoExecutionException("The parameter 'licenseFile' is missing.");
    }

    log.info("setting license file:");
    log.info("from: " + licenseFile.getAbsolutePath());
    log.info("to:   " + new File(project.getBasedir(), "LICENSE").getAbsolutePath());

    try {
      FileUtils.copyFile(licenseFile, new File(project.getBasedir(), "LICENSE"));
    }
    catch (IOException e) {
      throw new MojoExecutionException("error copying license file", e);
    }
  }
}
