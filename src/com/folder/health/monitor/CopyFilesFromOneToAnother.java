package com.folder.health.monitor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.log4j.Logger;

/**
 * The Class CopyFilesFromOneToAnother.
 * 
 * @author vivek.gupta
 */
public class CopyFilesFromOneToAnother extends TimerTask {

  /** The logger. */
  final static Logger logger = Logger.getLogger(CopyFilesFromOneToAnother.class);

  public static void main(String[] args) throws InterruptedException {
    Timer time = new Timer();
    CopyFilesFromOneToAnother copyFilesFromOneToAnother = new CopyFilesFromOneToAnother();
    time.schedule(copyFilesFromOneToAnother, 0, 120000);
    for (int i = 0; i <= 2; i++) {
      logger.info("Copying Files Start in CopyFilesFromOneToAnother");
      Thread.sleep(2000);
      if (i == 2) {
        logger.info("Copying Files Terminates in CopyFilesFromOneToAnother");
        System.exit(0);
      }
    }
  }

  /** The temp folder. */
  Path tempFolder = Paths.get(CommonConstants.SOURCE_FILE);

  /** The secured folder. */
  Path securedFolder = Paths.get(CommonConstants.DESTINTION_FILE);

  /*
   * (non-Javadoc)
   * 
   * @see java.util.TimerTask#run()
   */
  @Override
  public void run() {
    logger.info("Copying Files every 2 mins in CopyFilesFromOneToAnother");
    File folder = new File(tempFolder.toString());
    File[] listOfFiles = folder.listFiles();

    try {
      if (listOfFiles.length > 0) {

        for (File file : listOfFiles) {

          File source = new File(tempFolder + "\\" + file.getName());

          File destination = new File(securedFolder + "\\" + file.getName());

          Files.copy(source.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
      }

    } catch (IOException e) {
      logger.error("IOException in CopyFilesFromOneToAnother");
    } catch (Exception ex) {
      logger.error("Exception in CopyFilesFromOneToAnother");
    }
  }

}
