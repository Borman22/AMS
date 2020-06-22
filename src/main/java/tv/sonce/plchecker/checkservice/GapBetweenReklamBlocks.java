package tv.sonce.plchecker.checkservice;

import tv.sonce.plchecker.PLKeeper;
import tv.sonce.plchecker.entity.Event;
import tv.sonce.utils.TimeCode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

// Проверяет, что между рекламами не меньше 20 минут (точнее 19 минут 55 секунд - договорились с Ирой)
public class GapBetweenReklamBlocks extends AbstractParticularFeatureChecker {

    private final String PATH_TO_PROPERTY = "./properties/PLChecker/checkerservice/GapBetweenReklamBlocks.properties";

    @Override
    public Map<String, Integer> checkFeature(PLKeeper plKeeper) {

        Properties properties = getProperties(PATH_TO_PROPERTY);

        final String ERROR_MESSAGE_FIRST = properties.getProperty("ERROR_MESSAGE_FIRST");
        final String ERROR_MESSAGE_LAST = properties.getProperty("ERROR_MESSAGE_LAST");
        final TimeCode GAP_BETWEEN_REKLAM_BLOCKS = new TimeCode(properties.getProperty("GAP_BETWEEN_REKLAM_BLOCKS"));

        int numOfErrors = 0;

        List<List<Event>> reklamBloksList = plKeeper.getAllReklamBloksList();
        for (int i = 0; i < reklamBloksList.size() - 1; i++) {
            Event nextBlockFirstBumper = reklamBloksList.get(i + 1).get(0);
            Event previousBlockLastBumper = reklamBloksList.get(i).get(reklamBloksList.get(i).size() - 1);
            int difference = TimeCode.TCDifferenceConsideringMidnight(nextBlockFirstBumper.getTime(), previousBlockLastBumper.getTime() + previousBlockLastBumper.getDuration());
            if (difference < GAP_BETWEEN_REKLAM_BLOCKS.getFrames()) {
                previousBlockLastBumper.errors.add(ERROR_MESSAGE_FIRST + TimeCode.framesToDelimitedStr(difference));
                nextBlockFirstBumper.errors.add(ERROR_MESSAGE_LAST + TimeCode.framesToDelimitedStr(difference));
                numOfErrors++;
            }
        }

        Map<String, Integer> nameAndNumberOfErrors = new HashMap<>(1);
        nameAndNumberOfErrors.put(FEATURE_NAME, numOfErrors);
        return nameAndNumberOfErrors;
    }
}
