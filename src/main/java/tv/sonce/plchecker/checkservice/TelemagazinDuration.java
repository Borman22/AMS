package tv.sonce.plchecker.checkservice;

import tv.sonce.plchecker.PLKeeper;
import tv.sonce.plchecker.entity.Event;
import tv.sonce.utils.TimeCode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

// Проверяет, что длительность телемагазина не меньше N минут
public class TelemagazinDuration extends AbstractParticularFeatureChecker {

    private final String PATH_TO_PROPERTY = "./properties/PLChecker/checkerservice/TelemagazinDuration.properties";

    @Override
    public Map<String, Integer> checkFeature(PLKeeper plKeeper) {

        Properties properties = getProperties(PATH_TO_PROPERTY);

        final String ERROR_MESSAGE = properties.getProperty("ERROR_MESSAGE");
        final int TELEMAGAZIN_DURATION = Integer.parseInt(properties.getProperty("TELEMAGAZIN_DURATION"));

        int numOfErrors = 0;

        for (List<Event> telemagazinBlock : plKeeper.getAllTelemagazinBloksList()) {
            if (TimeCode.TCDifferenceConsideringMidnight(telemagazinBlock.get(telemagazinBlock.size() - 1).getTime()
                    + telemagazinBlock.get(telemagazinBlock.size() - 1).getDuration(), telemagazinBlock.get(0).getTime()) < TELEMAGAZIN_DURATION) {
                for (Event event : telemagazinBlock) {
                    event.errors.add(ERROR_MESSAGE);
                }
                numOfErrors++;
            }
        }

        Map<String, Integer> nameAndNumberOfErrors = new HashMap<>(1);
        nameAndNumberOfErrors.put(FEATURE_NAME, numOfErrors);
        return nameAndNumberOfErrors;
    }

    private boolean isThisABumper(String bumperName, String[] allBumpers) {
        bumperName = bumperName.toLowerCase();
        for (String currentBumper : allBumpers) {
            if (bumperName.matches(currentBumper))
                return true;
        }

        return false;
    }
}
