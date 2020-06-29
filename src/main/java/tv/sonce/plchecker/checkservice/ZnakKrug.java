package tv.sonce.plchecker.checkservice;

import tv.sonce.plchecker.PLKeeper;
import tv.sonce.plchecker.entity.Event;
import tv.sonce.plchecker.entity.ProgramDescription;
import tv.sonce.utils.JsonReader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class ZnakKrug extends AbstractParticularFeatureChecker {

    private final String PATH_TO_PROPERTY = "./properties/PLChecker/checkerservice/ZnakKrug.properties";

    @Override
    public Map<String, Integer> checkFeature(PLKeeper plKeeper) {

        Properties properties = getProperties(PATH_TO_PROPERTY);

        final String PATH_TO_ALL_PROGRAMS_DESCRIPTION = properties.getProperty("PATH_TO_ALL_PROGRAMS_DESCRIPTION");
        final String ERROR_MESSAGE = properties.getProperty("ERROR_MESSAGE");
        final String COMMON_ZNAK_KRUG_TEMPLATE = properties.getProperty("COMMON_ZNAK_KRUG_TEMPLATE");

        final String ZNAK_KRUG_12_FORMAT = properties.getProperty("ZNAK_KRUG_12_FORMAT");
        final String ZNAK_KRUG_16_FORMAT = properties.getProperty("ZNAK_KRUG_16_FORMAT");
        final String ZNAK_KRUG_18_FORMAT = properties.getProperty("ZNAK_KRUG_18_FORMAT");

        final String ZNAK_KRUG_12_TEMPLATE = properties.getProperty("ZNAK_KRUG_12_TEMPLATE");
        final String ZNAK_KRUG_16_TEMPLATE = properties.getProperty("ZNAK_KRUG_16_TEMPLATE");
        final String ZNAK_KRUG_18_TEMPLATE = properties.getProperty("ZNAK_KRUG_18_TEMPLATE");

        JsonReader<ProgramDescription[]> jsonReader = new JsonReader<>(PATH_TO_ALL_PROGRAMS_DESCRIPTION, new ProgramDescription[]{});
        ProgramDescription[] allProgramsDescriptions = jsonReader.getContent();

        int numOfErrors = 0;

        for (List<Event> currentProgram : plKeeper.getAllProgramsList()) {
            String znakKrug = findZnakKrug(currentProgram.get(0), allProgramsDescriptions);

            // если такой программы нет в списке всех программ, то попытаться угадать знак круг [TODO или посмотреть в excel]

            if (znakKrug == null)
                znakKrug = findPossibleZnakKrug(currentProgram.get(0).getCanonicalName(), ZNAK_KRUG_12_FORMAT, ZNAK_KRUG_12_TEMPLATE.split("&"));
            if (znakKrug == null)
                znakKrug = findPossibleZnakKrug(currentProgram.get(0).getCanonicalName(), ZNAK_KRUG_16_FORMAT, ZNAK_KRUG_16_TEMPLATE.split("&"));
            if (znakKrug == null)
                znakKrug = findPossibleZnakKrug(currentProgram.get(0).getCanonicalName(), ZNAK_KRUG_18_FORMAT, ZNAK_KRUG_18_TEMPLATE.split("&"));
            if (znakKrug == null)
                znakKrug = "";

            if (!znakKrug.equals("")) {
                for (Event subclip : currentProgram) {
                    if (howManyIsThereZnakKrug(znakKrug, subclip.getFormat()) != 1 || howManyIsThereZnakKrug(COMMON_ZNAK_KRUG_TEMPLATE, subclip.getFormat()) != 1) {
                        subclip.errors.add(ERROR_MESSAGE);
                        numOfErrors++;
                    }
                }
            } else {
                // если знак круг не найден, то убедиться, что его нет ни на каком субклипе
                for (Event subclip : currentProgram) {
                    if (howManyIsThereZnakKrug(COMMON_ZNAK_KRUG_TEMPLATE, subclip.getFormat()) != 0) {
                        subclip.errors.add(ERROR_MESSAGE);
                        numOfErrors++;
                    }
                }
            }
        }

        Map<String, Integer> nameAndNumberOfErrors = new HashMap<>(1);
        nameAndNumberOfErrors.put(FEATURE_NAME, numOfErrors);
        return nameAndNumberOfErrors;
    }

    private String findPossibleZnakKrug(String subclipName, String znakKrug, String[] templates) {
        for (String currentRegexp : templates) {
            if (subclipName.matches(currentRegexp))
                return znakKrug;
        }
        return null;
    }

    private int howManyIsThereZnakKrug(String znakKrug, String[] formats) {
        int numberOfZnakKrug = 0;
        for (String currentFromat : formats) {
            if (currentFromat.matches(znakKrug))
                numberOfZnakKrug++;
        }
        return numberOfZnakKrug;
    }

    private String findZnakKrug(Event event, ProgramDescription[] allProgramsDescriptions) {
        for (ProgramDescription currentProgramDescription : allProgramsDescriptions) {
            for (String currentProgramSynonym : currentProgramDescription.getProgramSynonyms()) {
                if (event.getCanonicalName().matches(currentProgramSynonym))
                    return currentProgramDescription.getZnakKrug();
            }
        }
        return null; // если такой программы вообще нет в списке
    }
}
