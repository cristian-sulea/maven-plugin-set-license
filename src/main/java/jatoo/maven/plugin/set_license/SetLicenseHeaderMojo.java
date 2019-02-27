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

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.DirectoryScanner;

/**
 * The {@link Mojo} for <code>header</code> goal.
 * 
 * @author <a href="http://cristian.sulea.net" rel="author">Cristian Sulea</a>
 * @version 4.0, February 27, 2019
 */
@Mojo(name = "header")
public class SetLicenseHeaderMojo extends AbstractMojo {

  /** To skip or not to skip. */
  @Parameter
  private boolean skip;

  /** The license to be used, a sub-folder in the 'licenses' folder. */
  @Parameter
  private String license;

  /** The text to be used for ${copyright} parameter. */
  @Parameter
  private String copyright;

  /** The file containing the text to be used as the header. */
  @Parameter
  private File licenseHeader;

  /** Default include patterns for directory scanner. */
  @Parameter(defaultValue = "src/**/*.java")
  private String[] includes;

  /** Project the plugin is called from. */
  @Component
  private MavenProject project;

  @Override
  public final void execute() throws MojoExecutionException, MojoFailureException {
    final Log log = getLog();

    if (skip) {
      log.info("Skipping...");
      return;
    }

    final String licenseHeaderText;

    if (license == null) {

      if (licenseHeader == null) {
        throw new MojoExecutionException("both parameters 'license' and 'licenseHeader' are missing");
      }

      try {
        licenseHeaderText = FileUtils.readFileToString(licenseHeader).trim();
      } catch (IOException e) {
        throw new MojoExecutionException("error reading license header file (" + licenseHeader + ")", e);
      }
    }

    else {

      if (copyright == null) {
        throw new MojoExecutionException("the parameter 'copyright' is missing");
      }

      try {
        licenseHeaderText = IOUtils.toString(getClass().getResource("licenses/" + license + "/HEADER")).replaceAll("\\$\\{copyright\\}", copyright).trim();
      } catch (IOException e) {
        throw new MojoExecutionException("error reading license (" + license + ") header", e);
      }
    }

    log.info("license header text: " + IOUtils.LINE_SEPARATOR + licenseHeaderText);

    final DirectoryScanner directoryScanner = new DirectoryScanner();
    directoryScanner.setIncludes(includes);
    directoryScanner.setBasedir(project.getBasedir());
    directoryScanner.scan();

    log.info("setting license header text on files:");

    for (String file : directoryScanner.getIncludedFiles()) {
      log.info(file);

      try {

        String fileContent = FileUtils.readFileToString(new File(project.getBasedir(), file));
        int index = fileContent.indexOf("package ");

        int indexComments = fileContent.indexOf("/**");
        if (indexComments >= 0 && indexComments <= index) {
          index = indexComments;
        }

        fileContent = fileContent.substring(index);

        File targetFolder = new File(project.getBuild().getDirectory());
        if (!targetFolder.exists() && !targetFolder.mkdirs()) {
          throw new MojoExecutionException("error creating target folder");
        }

        FileUtils.write(new File(project.getBasedir(), file), licenseHeaderText + IOUtils.LINE_SEPARATOR + IOUtils.LINE_SEPARATOR + fileContent);
      }

      catch (IOException e) {
        throw new MojoExecutionException("error writing license header in " + file, e);
      }

    }
  }
}
