package tv.sonce.plchecker.checkservice;

import tv.sonce.plchecker.PLKeeper;
import tv.sonce.plchecker.entity.Event;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

// Проверяет, что реклама не перелазит на другой час
public class ReklamaInsideHour extends AbstractParticularFeatureChecker {

    private final String PATH_TO_PROPERTY = "./properties/PLChecker/checkerservice/ReklamaInsideHour.properties";

    @Override
    public Map<String, Integer> checkFeature(PLKeeper plKeeper) {

        Properties properties = getProperties(PATH_TO_PROPERTY);

        final String ERROR_MESSAGE = properties.getProperty("ERROR_MESSAGE");
        final int FRAMES_IN_HOUR = Integer.parseInt(properties.getProperty("FRAMES_IN_HOUR"));

        int numOfErrors = 0;

        for (List<Event> reklamBlock : plKeeper.getAllReklamBloksList()) {
            if (reklamBlock.get(0).getTime() / FRAMES_IN_HOUR != reklamBlock.get(reklamBlock.size() - 1).getTime() / FRAMES_IN_HOUR) {
                for (Event reklama : reklamBlock) {
                    reklama.errors.add(ERROR_MESSAGE);
                }
                numOfErrors++;
            }
        }

        Map<String, Integer> nameAndNumberOfErrors = new HashMap<>(1);
        nameAndNumberOfErrors.put(FEATURE_NAME, numOfErrors);
        return nameAndNumberOfErrors;
    }
}
