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
