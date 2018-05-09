package com.folder.health.monitor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.log4j.Logger;

/**
 * The Class FolderMonitor.
 * 
 * @author vivek.gupta
 */
public class FolderMonitor extends TimerTask {

  /** The logger. */
  final static Logger logger = Logger.getLogger(FolderMonitor.class);

  public static void main(String[] args) {

    Timer time = new Timer();

    FolderMonitor folderMonitor = new FolderMonitor();

    time.schedule(folderMonitor, 0, 300000);
  }

  /** The secured folder. */
  Path securedFolder = Paths.get(CommonConstants.SOURCE_FILE);

  /** The archive folder. */
  Path archiveFolder = Paths.get(CommonConstants.ARCHIVED_FILE);

  /**
   * Run.
   */
  @Override
  public void run() {

    logger.info("Monitoring Folder every 5 mins in FolderMonitor");

    long currentFolderSize = 0;

    try {
      currentFolderSize = getSize(securedFolder.toString());

      System.out.println("Current Size of Secured Folder is :" + currentFolderSize);

      if (currentFolderSize > 999999) {

        archiveOlderFiles(securedFolder.toString(), archiveFolder.toString());
      }

    } catch (Exception e) {
      logger.error("Exception in FolderMonitor");
    }
  }

  /**
   * Gets the secured folder size.
   *
   * @param securedFolderPath
   *          the secured folder path
   * @return the secured folder size
   */
  private long getSize(String securedFolderPath) {

    logger.info("Current secured folder size in FolderMonitor");

    long size = 0;

    File folder = new File(securedFolderPath);

    File[] listOfFiles = folder.listFiles();

    if (listOfFiles.length > 0) {

      for (File file : listOfFiles) {

        if (file.toString().contains(CommonConstants.BATCH)
            || file.toString().contains(CommonConstants.SH)) {

          file.delete();
          logger.info("File deleted :" + file.getName());
          continue;
        }
        if (file.isFile()) {

          size += file.length();
        }
      }
    }
    return size;

  }

  /**
   * Archive older files.
   *
   * @param securedFolderPath
   *          the secured folder path
   * @param archiveFolderPath
   *          the archive folder path
   */
  private void archiveOlderFiles(String securedFolderPath, String archiveFolderPath) {

    logger.info("Archiving Older Files in FolderMonitor");

    long folderSize = 0;

    File folder = new File(securedFolderPath);

    File[] listOfFiles = folder.listFiles();

    List<File> listOfArchiveFiles = new ArrayList<File>();
    Arrays.sort(listOfFiles, new Comparator<File>() {

      @Override
      public int compare(File file1, File file2) {

        return Long.valueOf(file2.lastModified()).compareTo(file1.lastModified());
      }
    });
    if (listOfFiles.length > 0) {

      for (File file : listOfFiles) {

        folderSize += file.length();

        if (folderSize > 900000) {

          listOfArchiveFiles.add(file);
        }
      }

      logger.info("No of files to be archived : " + listOfArchiveFiles.size());
      moveFilesToArchiveFolder(listOfArchiveFiles.toArray(), securedFolderPath, archiveFolderPath);
    }
  }

  /**
   * Move files to archive folder.
   *
   * @param listOfArchiveFiles
   *          the list of archive files
   * @param securedFolderPath
   *          the secured folder path
   * @param archiveFolderPath
   *          the archive folder path
   */
  private void moveFilesToArchiveFolder(Object[] listOfArchiveFiles, String securedFolderPath,
      String archiveFolderPath) {

    logger.info("Moving older files to archive folder in FolderMonitor");

    int archivedFilesCount = 0;

    try {
      for (int i = 0; i < listOfArchiveFiles.length; i++) {
        File sharedFolderFile = new File(listOfArchiveFiles[i].toString());
        File source = new File(securedFolderPath + "\\" + sharedFolderFile.getName());
        File destination = new File(archiveFolderPath + "\\" + sharedFolderFile.getName());
        Files.move(source.toPath(), destination.toPath(), StandardCopyOption.ATOMIC_MOVE);

        archivedFilesCount += 1;
        logger.info("No of files archived: " + archivedFilesCount);
      }
      logger.info("Total count of archived files : " + archivedFilesCount);
    } catch (IOException e) {
      logger.error("IOException() in FolderMonitor");
    } catch (Exception e) {
      logger.error("Exception() in FolderMonitor");
    }
  }

}
