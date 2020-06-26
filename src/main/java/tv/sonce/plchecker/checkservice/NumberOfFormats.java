package tv.sonce.plchecker.checkservice;

import tv.sonce.plchecker.PLKeeper;
import tv.sonce.plchecker.entity.Event;

import java.util.*;

public class NumberOfFormats extends AbstractParticularFeatureChecker {

    private final String PATH_TO_PROPERTY = "./properties/PLChecker/checkerservice/NumberOfFormats.properties";

    @Override
    public Map<String, Integer> checkFeature(PLKeeper plKeeper) {

        Properties properties = getProperties(PATH_TO_PROPERTY);

        final String ERROR_MESSAGE = properties.getProperty("ERROR_MESSAGE");

        final String[] EXCLUDE_FORMATS_AT_NOT_A_PROGRAM = properties.getProperty("EXCLUDE_FORMATS_AT_NOT_A_PROGRAM").split("&");
        final String[] EXCLUDE_FORMATS_ON_NOT_THE_LAST_SUBCLIP = properties.getProperty("EXCLUDE_FORMATS_ON_NOT_THE_LAST_SUBCLIP").split("&");
        final String[] EXCLUDE_FORMATS_AT_LAST_SUBCLIP = properties.getProperty("EXCLUDE_FORMATS_AT_LAST_SUBCLIP").split("&");

        final int NUMBER_OF_FORMATS_AT_NOT_A_PROGRAM = Integer.parseInt(properties.getProperty("NUMBER_OF_FORMATS_AT_NOT_A_PROGRAM"));
        final int NUMBER_OF_FORMATS_ON_NOT_THE_LAST_SUBCLIP = Integer.parseInt(properties.getProperty("NUMBER_OF_FORMATS_ON_NOT_THE_LAST_SUBCLIP"));
        final int NUMBER_OF_FORMATS_AT_LAST_SUBCLIP = Integer.parseInt(properties.getProperty("NUMBER_OF_FORMATS_AT_LAST_SUBCLIP"));


        int numOfErrors = 0;

        // допустимое количество форматов на рекламах = 1 (PGM), если не считать свечку и лого.
        // допустимое количество на программах = 1 (PGM), если не считать свечку, лого, знак круг, Ticker (бегучка).
        // допустимое количество на последнем субклипе программы = 1 (PGM), если исключить свечку, лого, знак круг,  Ticker (бегучка), склеротик

        // Получим отдельные списки програм, реклам и последних субклипов програм.
        List<Event> programsWithoutLastSubclip = new ArrayList<>();
        List<Event> lastSubclips = new ArrayList<>();
        List<Event> notAProgram = new ArrayList<>(plKeeper.getAllEventsList());

        for (List<Event> program : plKeeper.getAllProgramsList()) {
            programsWithoutLastSubclip.addAll(program);
            lastSubclips.add(program.get(program.size() - 1));
            notAProgram.removeAll(program);
        }
        programsWithoutLastSubclip.removeAll(lastSubclips);

        numOfErrors += checkEachEventFromList(programsWithoutLastSubclip, EXCLUDE_FORMATS_ON_NOT_THE_LAST_SUBCLIP, NUMBER_OF_FORMATS_ON_NOT_THE_LAST_SUBCLIP, ERROR_MESSAGE);
        numOfErrors += checkEachEventFromList(lastSubclips, EXCLUDE_FORMATS_AT_LAST_SUBCLIP, NUMBER_OF_FORMATS_AT_LAST_SUBCLIP, ERROR_MESSAGE);
        numOfErrors += checkEachEventFromList(notAProgram, EXCLUDE_FORMATS_AT_NOT_A_PROGRAM, NUMBER_OF_FORMATS_AT_NOT_A_PROGRAM, ERROR_MESSAGE);

        Map<String, Integer> nameAndNumberOfErrors = new HashMap<>(1);
        nameAndNumberOfErrors.put(FEATURE_NAME, numOfErrors);
        return nameAndNumberOfErrors;
    }

    private int checkEachEventFromList(List<Event> eventList, String[] excludedFormats, int expectedNumberOfFormats, String errorMessage) {
        int numOfErrors = 0;
        for (Event event : eventList) {
            if (numberOfFormatsAfterExcluding(event.getFormat(), excludedFormats) != expectedNumberOfFormats) {
                event.errors.add(errorMessage);
                numOfErrors++;
            }
        }
        return numOfErrors;
    }

    private int numberOfFormatsAfterExcluding(String[] formats, String[] excludedFormats) {
        List<String> formatsList = new ArrayList<>(Arrays.asList(formats));
        for (String format : formats) {
            for (String excludedFormatRegex : excludedFormats) {
                if (format.matches(excludedFormatRegex))
                    formatsList.remove(format);
            }
        }
        return formatsList.size();
    }
}
