package tv.sonce.plchecker.checkservice;

import tv.sonce.plchecker.PLKeeper;
import tv.sonce.plchecker.entity.Event;
import tv.sonce.plchecker.entity.ProgramDescription;
import tv.sonce.utils.JsonReader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class EroticaPlashka extends AbstractParticularFeatureChecker {

    private final String PATH_TO_PROPERTY = "./properties/PLChecker/checkerservice/EroticaPlashka.properties";

    @Override
    public Map<String, Integer> checkFeature(PLKeeper plKeeper) {

        Properties properties = getProperties(PATH_TO_PROPERTY);

        final String PATH_TO_ALL_PROGRAMS_DESCRIPTION = properties.getProperty("PATH_TO_ALL_PROGRAMS_DESCRIPTION");
        final String ERROR_MESSAGE = properties.getProperty("ERROR_MESSAGE");
        final String PLASHKA = properties.getProperty("PLASHKA");
        final String PROGRAM_TYPE = properties.getProperty("PROGRAM_TYPE");

        JsonReader<ProgramDescription[]> jsonReader = new JsonReader<>(PATH_TO_ALL_PROGRAMS_DESCRIPTION, new ProgramDescription[]{});
        ProgramDescription[] allProgramsDescriptions = jsonReader.getContent();

        int numOfErrors = 0;

        for (List<Event> program : plKeeper.getAllProgramsList()) {
            if (findProgramType(program.get(0), allProgramsDescriptions).matches(PROGRAM_TYPE)) {
                if (!plKeeper.getAllEventsList().get(program.get(0).getNumberOfEvent() - 2).getName().toLowerCase().matches(PLASHKA)) {
                    plKeeper.getAllEventsList().get(program.get(0).getNumberOfEvent() - 2).errors.add(ERROR_MESSAGE);
                    numOfErrors++;
                }
            }
        }

        Map<String, Integer> nameAndNumberOfErrors = new HashMap<>(1);
        nameAndNumberOfErrors.put(FEATURE_NAME, numOfErrors);
        return nameAndNumberOfErrors;
    }

    private String findProgramType(Event event, ProgramDescription[] allProgramsDescriptions) {
        for (ProgramDescription currentProgramDescription : allProgramsDescriptions) {
            for (String currentProgramSynonym : currentProgramDescription.getProgramSynonyms()) {
                if (event.getCanonicalName().matches(currentProgramSynonym))
                    return currentProgramDescription.getProgramType();
            }
        }
        return "";
    }
}
