package tv.sonce.plchecker.checkservice;

import tv.sonce.exceptions.FileProcessingException;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public abstract class AbstractParticularFeatureChecker implements IParticularFeatureChecker {

    protected String FEATURE_NAME;

    protected Properties getProperties(final String PATH_TO_PROPERTY) {

        Properties properties = new Properties();

        try (FileReader fileReader = new FileReader(PATH_TO_PROPERTY)) {
            properties.load(fileReader);
            FEATURE_NAME = properties.getProperty("FEATURE_NAME");
        } catch (IOException e) {
            throw new FileProcessingException(e);
        }
        return properties;
    }
}
