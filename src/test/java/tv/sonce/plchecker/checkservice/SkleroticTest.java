package tv.sonce.plchecker.checkservice;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import tv.sonce.plchecker.PLKeeper;
import tv.sonce.plchecker.entity.Event;
import tv.sonce.utils.TimeCode;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SkleroticTest {

    private static final String PATH_TO_PROPERTY = "./properties/PLChecker/checkerservice/Sklerotic.properties";

    private static Sklerotic sklerotic;

    private PLKeeper plKeeperMock;
    private List<List<Event>> allProgramsList;

    private static String FEATURE_NAME;
    private static String MIN_SUBCLIP_LENGTH_FOR_SKLEROTIC_STR;

    private final String SKLEROTIC = "Dali HS-CriminalMinds";
    private final String PROGRAM = "HS-CriminalMinds";
    private final String PROGRAM_WITHOUT_SKLEROTIC = "PR-Oliver";
    private final String SKLEROTIC_INCORRECT = "Dali HS-CSI NewYork";


    @BeforeClass
    public static void initClass() throws IOException {
        Properties properties = new Properties();
        properties.load(new FileReader(PATH_TO_PROPERTY));
        FEATURE_NAME = properties.getProperty("FEATURE_NAME");
        MIN_SUBCLIP_LENGTH_FOR_SKLEROTIC_STR = properties.getProperty("MIN_SUBCLIP_LENGTH_FOR_SKLEROTIC");
        sklerotic = new Sklerotic();
    }

    @Before
    public void init() {
        allProgramsList = new ArrayList<>();
        plKeeperMock = mock(PLKeeper.class);
        when(plKeeperMock.getAllProgramsList()).thenReturn(allProgramsList);
    }

    @Test
    public void durationEqMinLegth() {
        TimeCode MIN_SUBCLIP_LENGTH_FOR_SKLEROTIC = new TimeCode(MIN_SUBCLIP_LENGTH_FOR_SKLEROTIC_STR);

        List<Event> program1 = new ArrayList<>();
        List<Event> program2 = new ArrayList<>();

        Event eventForProgram1 = new Event(1, "", "00:00:00:00", MIN_SUBCLIP_LENGTH_FOR_SKLEROTIC.getDelimitedStr(), null, null, "0", "", SKLEROTIC, "");
        Event eventForProgram2 = new Event(2, "", "00:10:00:00", "00:10:00:00", null, null, "0", PROGRAM, "", "");

        program1.add(eventForProgram1);
        program2.add(eventForProgram2);

        allProgramsList.add(program1);
        allProgramsList.add(program2);

        Map<String, Integer> result = sklerotic.checkFeature(plKeeperMock);
        assertEquals(0, result.get(FEATURE_NAME).intValue());
    }

    @Test
    public void durationMoreThenMinLegth() {
        TimeCode MIN_SUBCLIP_LENGTH_FOR_SKLEROTIC = new TimeCode(MIN_SUBCLIP_LENGTH_FOR_SKLEROTIC_STR);

        List<Event> program1 = new ArrayList<>();
        List<Event> program2 = new ArrayList<>();

        Event eventForProgram1 = new Event(1, "", "00:00:00:00", MIN_SUBCLIP_LENGTH_FOR_SKLEROTIC.changeToNFramesConsideringMidnight(1).getDelimitedStr(), null, null, "0", "", SKLEROTIC, "");
        Event eventForProgram2 = new Event(2, "", "00:10:00:00", "00:10:00:00", null, null, "0", PROGRAM, "", "");

        program1.add(eventForProgram1);
        program2.add(eventForProgram2);

        allProgramsList.add(program1);
        allProgramsList.add(program2);

        Map<String, Integer> result = sklerotic.checkFeature(plKeeperMock);
        assertEquals(0, result.get(FEATURE_NAME).intValue());
    }

    @Test
    public void durationLessThenMinLegth() {
        TimeCode MIN_SUBCLIP_LENGTH_FOR_SKLEROTIC = new TimeCode(MIN_SUBCLIP_LENGTH_FOR_SKLEROTIC_STR);

        List<Event> program1 = new ArrayList<>();
        List<Event> program2 = new ArrayList<>();

        Event eventForProgram1 = new Event(1, "", "00:00:00:00", MIN_SUBCLIP_LENGTH_FOR_SKLEROTIC.changeToNFramesConsideringMidnight(-1).getDelimitedStr(), null, null, "0", "", SKLEROTIC, "");
        Event eventForProgram2 = new Event(2, "", "00:10:00:00", "00:10:00:00", null, null, "0", PROGRAM, "", "");

        program1.add(eventForProgram1);
        program2.add(eventForProgram2);

        allProgramsList.add(program1);
        allProgramsList.add(program2);

        Map<String, Integer> result = sklerotic.checkFeature(plKeeperMock);
        assertEquals(1, result.get(FEATURE_NAME).intValue());
    }

    @Test
    public void incorrectSklerotic() {
        TimeCode MIN_SUBCLIP_LENGTH_FOR_SKLEROTIC = new TimeCode(MIN_SUBCLIP_LENGTH_FOR_SKLEROTIC_STR);

        List<Event> program1 = new ArrayList<>();
        List<Event> program2 = new ArrayList<>();

        Event eventForProgram1 = new Event(1, "", "00:00:00:00", MIN_SUBCLIP_LENGTH_FOR_SKLEROTIC.getDelimitedStr(), null, null, "0", "", SKLEROTIC_INCORRECT, "");
        Event eventForProgram2 = new Event(2, "", "00:10:00:00", "00:10:00:00", null, null, "0", PROGRAM, "", "");

        program1.add(eventForProgram1);
        program2.add(eventForProgram2);

        allProgramsList.add(program1);
        allProgramsList.add(program2);

        Map<String, Integer> result = sklerotic.checkFeature(plKeeperMock);
        assertEquals(1, result.get(FEATURE_NAME).intValue());
    }

    @Test
    public void withoutSklerotic() {
        TimeCode MIN_SUBCLIP_LENGTH_FOR_SKLEROTIC = new TimeCode(MIN_SUBCLIP_LENGTH_FOR_SKLEROTIC_STR);

        List<Event> program1 = new ArrayList<>();
        List<Event> program2 = new ArrayList<>();

        Event eventForProgram1 = new Event(1, "", "00:00:00:00", MIN_SUBCLIP_LENGTH_FOR_SKLEROTIC.getDelimitedStr(), null, null, "0", "", "", "");
        Event eventForProgram2 = new Event(2, "", "00:10:00:00", "00:10:00:00", null, null, "0", PROGRAM, "", "");

        program1.add(eventForProgram1);
        program2.add(eventForProgram2);

        allProgramsList.add(program1);
        allProgramsList.add(program2);

        Map<String, Integer> result = sklerotic.checkFeature(plKeeperMock);
        assertEquals(1, result.get(FEATURE_NAME).intValue());
    }

    @Test
    public void tooManyCorrectSklerotics() {
        TimeCode MIN_SUBCLIP_LENGTH_FOR_SKLEROTIC = new TimeCode(MIN_SUBCLIP_LENGTH_FOR_SKLEROTIC_STR);

        List<Event> program1 = new ArrayList<>();
        List<Event> program2 = new ArrayList<>();

        Event eventForProgram1 = new Event(1, "", "00:00:00:00", MIN_SUBCLIP_LENGTH_FOR_SKLEROTIC.getDelimitedStr(), null, null, "0", "", SKLEROTIC + ", " + SKLEROTIC, "");
        Event eventForProgram2 = new Event(2, "", "00:10:00:00", "00:10:00:00", null, null, "0", PROGRAM, "", "");

        program1.add(eventForProgram1);
        program2.add(eventForProgram2);

        allProgramsList.add(program1);
        allProgramsList.add(program2);

        Map<String, Integer> result = sklerotic.checkFeature(plKeeperMock);
        assertEquals(1, result.get(FEATURE_NAME).intValue());
    }

    @Test
    public void tooManyDifferentSklerotics() {
        TimeCode MIN_SUBCLIP_LENGTH_FOR_SKLEROTIC = new TimeCode(MIN_SUBCLIP_LENGTH_FOR_SKLEROTIC_STR);

        List<Event> program1 = new ArrayList<>();
        List<Event> program2 = new ArrayList<>();

        Event eventForProgram1 = new Event(1, "", "00:00:00:00", MIN_SUBCLIP_LENGTH_FOR_SKLEROTIC.getDelimitedStr(), null, null, "0", "", SKLEROTIC + ", " + SKLEROTIC_INCORRECT, "");
        Event eventForProgram2 = new Event(2, "", "00:10:00:00", "00:10:00:00", null, null, "0", PROGRAM, "", "");

        program1.add(eventForProgram1);
        program2.add(eventForProgram2);

        allProgramsList.add(program1);
        allProgramsList.add(program2);

        Map<String, Integer> result = sklerotic.checkFeature(plKeeperMock);
        assertEquals(1, result.get(FEATURE_NAME).intValue());
    }

    @Test
    public void programWithoutSklerotics() {
        TimeCode MIN_SUBCLIP_LENGTH_FOR_SKLEROTIC = new TimeCode(MIN_SUBCLIP_LENGTH_FOR_SKLEROTIC_STR);

        List<Event> program1 = new ArrayList<>();
        List<Event> program2 = new ArrayList<>();

        Event eventForProgram1 = new Event(1, "", "00:00:00:00", MIN_SUBCLIP_LENGTH_FOR_SKLEROTIC.getDelimitedStr(), null, null, "0", "", "", "");
        Event eventForProgram2 = new Event(2, "", "00:10:00:00", "00:10:00:00", null, null, "0", PROGRAM_WITHOUT_SKLEROTIC, "", "");

        program1.add(eventForProgram1);
        program2.add(eventForProgram2);

        allProgramsList.add(program1);
        allProgramsList.add(program2);

        Map<String, Integer> result = sklerotic.checkFeature(plKeeperMock);
        assertEquals(0, result.get(FEATURE_NAME).intValue());
    }

    @Test
    public void programWithoutSkleroticsAndSklerotic() {
        TimeCode MIN_SUBCLIP_LENGTH_FOR_SKLEROTIC = new TimeCode(MIN_SUBCLIP_LENGTH_FOR_SKLEROTIC_STR);

        List<Event> program1 = new ArrayList<>();
        List<Event> program2 = new ArrayList<>();

        Event eventForProgram1 = new Event(1, "", "00:00:00:00", MIN_SUBCLIP_LENGTH_FOR_SKLEROTIC.getDelimitedStr(), null, null, "0", "", SKLEROTIC, "");
        Event eventForProgram2 = new Event(2, "", "00:10:00:00", "00:10:00:00", null, null, "0", PROGRAM_WITHOUT_SKLEROTIC, "", "");

        program1.add(eventForProgram1);
        program2.add(eventForProgram2);

        allProgramsList.add(program1);
        allProgramsList.add(program2);

        Map<String, Integer> result = sklerotic.checkFeature(plKeeperMock);
        assertEquals(1, result.get(FEATURE_NAME).intValue());
    }
}