package tv.sonce.plchecker.checkservice;

import tv.sonce.plchecker.PLKeeper;
import tv.sonce.plchecker.entity.Event;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PgmAmount extends AbstractParticularFeatureChecker {

    private final String PATH_TO_PROPERTY = "./properties/PLChecker/checkerservice/PgmAmount.properties";

    @Override
    public Map<String, Integer> checkFeature(PLKeeper plKeeper) {

        Properties properties = getProperties(PATH_TO_PROPERTY);

        final String PGM = properties.getProperty("PGM");
        final String ERROR_MESSAGE = properties.getProperty("ERROR_MESSAGE");

        int numOfErrors = 0;

        for (Event event : plKeeper.getAllEventsList()) {
            int numOfPgm = 0;
            for (String format : event.getFormat()) {
                if (format.matches(PGM))
                    numOfPgm++;
            }
            if (numOfPgm != 1) {
                event.errors.add(ERROR_MESSAGE);
                numOfErrors++;
            }
        }

        Map<String, Integer> nameAndNumberOfErrors = new HashMap<>(1);
        nameAndNumberOfErrors.put(FEATURE_NAME, numOfErrors);
        return nameAndNumberOfErrors;
    }
}
