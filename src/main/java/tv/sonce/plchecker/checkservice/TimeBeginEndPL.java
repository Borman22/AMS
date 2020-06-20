package tv.sonce.plchecker.checkservice;

import tv.sonce.plchecker.PLKeeper;
import tv.sonce.plchecker.entity.Event;
import tv.sonce.utils.TimeCode;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class TimeBeginEndPL extends AbstractParticularFeatureChecker {

    private final String PATH_TO_PROPERTY = "./properties/PLChecker/checkerservice/TimeBeginEndPL.properties";

    @Override
    public Map<String, Integer> checkFeature(PLKeeper plKeeper) {

        Properties properties = getProperties(PATH_TO_PROPERTY);

        final String TIME_BEGIN_ERROR_MESSAGE = properties.getProperty("TIME_BEGIN_ERROR_MESSAGE");
        final String TIME_END_ERROR_MESSAGE = properties.getProperty("TIME_END_ERROR_MESSAGE");
        final int TIME_BEGIN_PL = TimeCode.delimitedStrToFrames(properties.getProperty("TIME_BEGIN_PL"));
        final int TIME_END_PL = TimeCode.delimitedStrToFrames(properties.getProperty("TIME_END_PL"));

        int numOfErrors = 0;

        Event firstEvent = plKeeper.getAllEventsList().get(0);
        Event lastEvent = plKeeper.getAllEventsList().get(plKeeper.getAllEventsList().size() - 1);

        if (firstEvent.getTime() != TIME_BEGIN_PL) {
            firstEvent.errors.add(TIME_BEGIN_ERROR_MESSAGE);
            numOfErrors++;
        }

        if (lastEvent.getTime() + lastEvent.getDuration() != TIME_END_PL) {
            lastEvent.errors.add(TIME_END_ERROR_MESSAGE);
            numOfErrors++;
        }

        Map<String, Integer> nameAndNumberOfErrors = new HashMap<>(1);
        nameAndNumberOfErrors.put(FEATURE_NAME, numOfErrors);
        return nameAndNumberOfErrors;
    }
}
