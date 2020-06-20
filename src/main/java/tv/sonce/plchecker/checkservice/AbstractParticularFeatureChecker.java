package tv.sonce.plchecker.checkservice;

import tv.sonce.exceptions.FileNotFoundExceptionRT;
import tv.sonce.exceptions.ReadingFileExceptionRT;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public abstract class AbstractParticularFeatureChecker implements IParticularFeatureChecker {

    protected String FEATURE_NAME;

    protected Properties getProperties(final String PATH_TO_PROPERTY) {

        Properties properties = new Properties();

        try {
            properties.load(new FileReader(PATH_TO_PROPERTY));
            FEATURE_NAME = properties.getProperty("FEATURE_NAME");
        } catch (FileNotFoundException e) {
            throw new FileNotFoundExceptionRT(e.getMessage());
        } catch (IOException e) {
            throw new ReadingFileExceptionRT(e.getMessage());
        }
        return properties;
    }
}
