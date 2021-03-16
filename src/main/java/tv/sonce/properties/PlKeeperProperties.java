package tv.sonce.properties;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import tv.sonce.exceptions.FileNotFoundExceptionRT;
import tv.sonce.exceptions.ReadingFileExceptionRT;

public class PlKeeperProperties {

  private static PlKeeperProperties instance;

  public final String PATH_TO_NOT_CONSIDERED_A_PROGRAM;

  public final String PATH_TO_PL;
  public final int MAX_NUM_OF_PROGRAMS;
  public final int MAX_REKLAMA_DURATION;
  public final int MIN_EVENTS_IN_REKLAM_BLOCK;
  public final String TELEMAGAZIN_BUMPER_TEMPLATE;
  public final String TELEMAGAZIN_TEMPLATE;
  public final String[] REDUNDANT_EVENT_TEMPLATE;
  public final int TC_DEVIATION;
  // Tag Names
  public final String EVENT;
  public final String DATE;
  public final String TIME;
  public final String DURATION;
  public final String TC_IN;
  public final String TC_OUT;
  public final String ASSET_ID;
  public final String NAME;
  public final String FORMAT;
  public final String STATUS;

  public static PlKeeperProperties getInstance(final String PATH_TO_PROPERTY) {
    if (instance == null) {
      instance = new PlKeeperProperties(PATH_TO_PROPERTY);
    }
    return instance;
  }

  private PlKeeperProperties(final String PATH_TO_PROPERTY) {
    Properties properties = new Properties();
    try (FileReader fileReader = new FileReader(PATH_TO_PROPERTY)) {
      properties.load(fileReader);
    } catch (FileNotFoundException e) {
      throw new FileNotFoundExceptionRT(e.getMessage());
    } catch (IOException e) {
      throw new ReadingFileExceptionRT(e.getMessage());
    }

    PATH_TO_PL = properties.getProperty("PATH_TO_PL");
    MAX_NUM_OF_PROGRAMS = Integer.parseInt(properties.getProperty("MAX_NUM_OF_PROGRAMS"));
    MAX_REKLAMA_DURATION = Integer.parseInt(properties.getProperty("MAX_REKLAMA_DURATION"));
    MIN_EVENTS_IN_REKLAM_BLOCK = Integer
        .parseInt(properties.getProperty("MIN_EVENTS_IN_REKLAM_BLOCK"));
    PATH_TO_NOT_CONSIDERED_A_PROGRAM = properties.getProperty("PATH_TO_NOT_CONSIDERED_A_PROGRAM");
    TC_DEVIATION = Integer.parseInt(properties.getProperty("TC_DEVIATION"));

    TELEMAGAZIN_BUMPER_TEMPLATE = properties.getProperty("TELEMAGAZIN_BUMPER_TEMPLATE");
    TELEMAGAZIN_TEMPLATE = properties.getProperty("TELEMAGAZIN_TEMPLATE");
    REDUNDANT_EVENT_TEMPLATE = properties.getProperty("REDUNDANT_EVENT_TEMPLATE").split("&");

    EVENT = properties.getProperty("EVENT");
    DATE = properties.getProperty("DATE");
    TIME = properties.getProperty("TIME");
    DURATION = properties.getProperty("DURATION");
    TC_IN = properties.getProperty("TC_IN");
    TC_OUT = properties.getProperty("TC_OUT");
    ASSET_ID = properties.getProperty("ASSET_ID");
    NAME = properties.getProperty("NAME");
    FORMAT = properties.getProperty("FORMAT");
    STATUS = properties.getProperty("STATUS");
  }
}
