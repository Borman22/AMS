package tv.sonce.plchecker.checkservice;

import tv.sonce.plchecker.PLKeeper;
import tv.sonce.plchecker.entity.Event;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class LogoAmount extends AbstractParticularFeatureChecker {

    private final String PATH_TO_PROPERTY = "./properties/PLChecker/checkerservice/LogoAmount.properties";

    @Override
    public Map<String, Integer> checkFeature(PLKeeper plKeeper) {

        Properties properties = getProperties(PATH_TO_PROPERTY);

        final String LOGO_V2 = properties.getProperty("LOGO_V2");
        final String LOGO_TRAUR = properties.getProperty("LOGO_TRAUR");
        final String ERROR_MESSAGE = properties.getProperty("ERROR_MESSAGE");
        final String WARNING_MESSAGE = properties.getProperty("WARNING_MESSAGE");

        int numOfErrors = 0;

        for (Event event : plKeeper.getAllEventsList()) {
            int numOfLogo = 0;
            for (String format : event.getFormat()) {
                if (format.matches(LOGO_V2) || format.matches(LOGO_TRAUR))
                    numOfLogo++;
            }
            if (numOfLogo == 1) {
                event.errors.add(WARNING_MESSAGE);
                numOfErrors++;
            }
            if (numOfLogo > 1) {
                event.errors.add(ERROR_MESSAGE);
                numOfErrors++;
            }
        }

        Map<String, Integer> nameAndNumberOfErrors = new HashMap<>(1);
        nameAndNumberOfErrors.put(FEATURE_NAME, numOfErrors);
        return nameAndNumberOfErrors;
    }
}
