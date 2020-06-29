package tv.sonce.plchecker.checkservice;

import tv.sonce.plchecker.PLKeeper;
import tv.sonce.plchecker.entity.Event;
import tv.sonce.plchecker.entity.ProgramDescription;
import tv.sonce.utils.JsonReader;
import tv.sonce.utils.TimeCode;

import java.util.*;

public class Sklerotic extends AbstractParticularFeatureChecker {

    private final String PATH_TO_PROPERTY = "./properties/PLChecker/checkerservice/Sklerotic.properties";

    @Override
    public Map<String, Integer> checkFeature(PLKeeper plKeeper) {

        Properties properties = getProperties(PATH_TO_PROPERTY);

        final String PATH_TO_ALL_PROGRAMS_DESCRIPTION = properties.getProperty("PATH_TO_ALL_PROGRAMS_DESCRIPTION");
        final String ERROR_MESSAGE = properties.getProperty("ERROR_MESSAGE");
        final String ERROR_MESSAGE_SUPERFLUOUS_SKLEROTIC = properties.getProperty("ERROR_MESSAGE_SUPERFLUOUS_SKLEROTIC");
        final String SKLEROTIC_TEMPLATE = properties.getProperty("SKLEROTIC_TEMPLATE");
        final String EMPTY_SKLEROTIC = properties.getProperty("EMPTY_SKLEROTIC");
        final TimeCode MIN_SUBCLIP_LENGTH_FOR_SKLEROTIC = new TimeCode(properties.getProperty("MIN_SUBCLIP_LENGTH_FOR_SKLEROTIC"));

        JsonReader<ProgramDescription[]> jsonReader = new JsonReader<>(PATH_TO_ALL_PROGRAMS_DESCRIPTION, new ProgramDescription[]{});
        ProgramDescription[] allProgramsDescriptions = jsonReader.getContent();

        int numOfErrors = 0;


        List<List<Event>> allProgramList = plKeeper.getAllProgramsList();
        for (int i = 0; i < allProgramList.size() - 1; i++) {
            String skleroticName = findSklerotic(allProgramList.get(i + 1).get(0), allProgramsDescriptions);

            Event lastSubclip = allProgramList.get(i).get(allProgramList.get(i).size() - 1);

            // Если длинна субклипа маленькая или склеротика не существует, то убедимся, что никакой склеротик не стоит
            if (lastSubclip.getDuration() < MIN_SUBCLIP_LENGTH_FOR_SKLEROTIC.getFrames() || skleroticName.equals(EMPTY_SKLEROTIC)) {
                if(howMuchIsThereSklerotic(SKLEROTIC_TEMPLATE, lastSubclip.getFormat()) != 0) {
                    lastSubclip.errors.add(ERROR_MESSAGE_SUPERFLUOUS_SKLEROTIC);
                    numOfErrors++;
                }
            } else { // Если длинна субклипа большая и склеротик существует, то убедимся, что он стоит
                if (howMuchIsThereSklerotic(skleroticName, lastSubclip.getFormat()) != 1 || howMuchIsThereSklerotic(SKLEROTIC_TEMPLATE, lastSubclip.getFormat()) != 1) {
                    lastSubclip.errors.add(ERROR_MESSAGE + skleroticName);
                    numOfErrors++;
                }
            }
        }

        Map<String, Integer> nameAndNumberOfErrors = new HashMap<>(1);
        nameAndNumberOfErrors.put(FEATURE_NAME, numOfErrors);
        return nameAndNumberOfErrors;
    }

    private int howMuchIsThereSklerotic(String skleroticName, String[] formats) {
        return (int) Arrays.stream(formats).filter(currentFormat -> currentFormat.matches(skleroticName)).count();
    }

    private String findSklerotic(Event event, ProgramDescription[] allProgramsDescriptions) {
        for (ProgramDescription currentProgramDescription : allProgramsDescriptions) {
            for (String currentProgramSynonym : currentProgramDescription.getProgramSynonyms()) {
                if (event.getCanonicalName().matches(currentProgramSynonym))
                    return currentProgramDescription.getSkleroticName();
            }
        }
        return "";
    }
}
