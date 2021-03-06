package tv.sonce.plchecker.checkservice;

import tv.sonce.plchecker.PLKeeper;
import tv.sonce.plchecker.entity.Event;
import tv.sonce.utils.TimeCode;

import java.util.*;

// Длительность рекламы в течении часа не должна привышать 9 минут
public class ReklamaDurationInHour extends AbstractParticularFeatureChecker {

    private final String PATH_TO_PROPERTY = "./properties/PLChecker/checkerservice/ReklamaDurationInHour.properties";

    @Override
    public Map<String, Integer> checkFeature(PLKeeper plKeeper) {

        Properties properties = getProperties(PATH_TO_PROPERTY);

        final String ERROR_MESSAGE = properties.getProperty("ERROR_MESSAGE");
        final TimeCode REKLAMA_DURATION_IN_HOUR = new TimeCode(properties.getProperty("REKLAMA_DURATION_IN_HOUR"));
        final TimeCode ONE_HOUR = new TimeCode("01:00:00:00");

        int numOfErrors = 0;

        int tempHour = -1;
        int tempDuration = 0;
        List<Event> reklamEventsInCurrentHour = new ArrayList<>(50);
        for (List<Event> reklamBlock : plKeeper.getAllReklamBloksList()) {
            for (Event reklamaEvent : reklamBlock) {
                if (reklamaEvent.getTime() / ONE_HOUR.getFrames() != tempHour) {
                    if (tempDuration > REKLAMA_DURATION_IN_HOUR.getFrames()) {
                        for (Event event : reklamEventsInCurrentHour) {
                            event.errors.add(ERROR_MESSAGE + TimeCode.framesToDelimitedStr(tempDuration));
                        }
                        numOfErrors++;
                    }
                    tempDuration = reklamaEvent.getDuration();
                    tempHour = reklamaEvent.getTime() / ONE_HOUR.getFrames();
                    reklamEventsInCurrentHour = new ArrayList<>(50);
                    reklamEventsInCurrentHour.add(reklamaEvent);
                } else {
                    tempDuration += reklamaEvent.getDuration();
                    reklamEventsInCurrentHour.add(reklamaEvent);
                }
            }
        }

        // проверка для последнего рекламного блока
        if (tempDuration > REKLAMA_DURATION_IN_HOUR.getFrames()) {
            for (Event event : reklamEventsInCurrentHour) {
                event.errors.add(ERROR_MESSAGE + TimeCode.framesToDelimitedStr(tempDuration));
            }
            numOfErrors++;
        }

        Map<String, Integer> nameAndNumberOfErrors = new HashMap<>(1);
        nameAndNumberOfErrors.put(FEATURE_NAME, numOfErrors);
        return nameAndNumberOfErrors;
    }
}
