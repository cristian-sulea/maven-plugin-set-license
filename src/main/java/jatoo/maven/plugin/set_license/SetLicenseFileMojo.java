package jatoo.maven.plugin.set_license;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * The {@link Mojo} for <code>file</code> goal.
 * 
 * @author <a href="http://cristian.sulea.net" rel="author">Cristian Sulea</a>
 * @version 0.1.0, September 1, 2016
 */
@Mojo(name = "file")
public class SetLicenseFileMojo extends AbstractMojo {

  /** The file containing the license to be copied. */
  @Parameter
  private File licenseFile;

  @Override
  public final void execute() throws MojoExecutionException, MojoFailureException {
    final Log log = getLog();

    if (licenseFile == null) {
      throw new MojoExecutionException("The parameter 'licenseFile' is missing.");
    }

    log.info("setting license file:");
    log.info(licenseFile.getAbsolutePath());

    try {
      FileUtils.copyFile(licenseFile, new File("LICENSE"));
    }
    catch (IOException e) {
      throw new MojoExecutionException("error copying license file", e);
    }
  }
}
