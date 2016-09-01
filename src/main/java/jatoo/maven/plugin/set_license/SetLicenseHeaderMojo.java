package jatoo.maven.plugin.set_license;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.util.DirectoryScanner;

/**
 * The {@link Mojo} for <code>header</code> goal.
 * 
 * @author <a href="http://cristian.sulea.net" rel="author">Cristian Sulea</a>
 * @version 0.1.2, September 1, 2016
 */
@Mojo(name = "header")
public class SetLicenseHeaderMojo extends AbstractMojo {

  /** The file containing the text to be used as the header. */
  @Parameter
  private File licenseHeader;

  /** Default include patterns for directory scanner. */
  @Parameter(defaultValue = "src/**/*.java")
  private String[] includes;

  @Override
  public final void execute() throws MojoExecutionException, MojoFailureException {
    final Log log = getLog();

    if (licenseHeader == null) {
      throw new MojoExecutionException("The parameter 'licenseHeader' is missing.");
    }

    final String licenseHeaderText;
    try {
      licenseHeaderText = FileUtils.readFileToString(licenseHeader).trim();
    }
    catch (IOException e) {
      throw new MojoExecutionException("error reading license header", e);
    }

    log.info("license header text: " + IOUtils.LINE_SEPARATOR + licenseHeaderText);

    final DirectoryScanner directoryScanner = new DirectoryScanner();
    directoryScanner.setIncludes(includes);
    directoryScanner.setBasedir(new File(new File("pom.xml").getAbsolutePath()).getParentFile());
    directoryScanner.scan();

    for (String file : directoryScanner.getIncludedFiles()) {
      log.info(file);

      try {

        String fileContent = FileUtils.readFileToString(new File(file));
        if (!file.endsWith("package-info.java")) {
          fileContent = fileContent.substring(fileContent.indexOf("package "));
        }

        File targetFolder = new File("target");
        if (!targetFolder.exists() && !targetFolder.mkdirs()) {
          throw new MojoExecutionException("error creating target folder");
        }

        FileUtils.write(new File(file), licenseHeaderText + IOUtils.LINE_SEPARATOR + IOUtils.LINE_SEPARATOR + fileContent);
      }

      catch (IOException e) {
        throw new MojoExecutionException("error writing license header in " + file, e);
      }

    }
  }
}
