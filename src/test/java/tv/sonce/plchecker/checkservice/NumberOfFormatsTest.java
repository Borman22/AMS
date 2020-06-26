package tv.sonce.plchecker.checkservice;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import tv.sonce.plchecker.PLKeeper;
import tv.sonce.plchecker.entity.Event;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NumberOfFormatsTest {
    private static final String PATH_TO_PROPERTY = "./properties/PLChecker/checkerservice/NumberOfFormats.properties";

    private static String FEATURE_NAME;
    private static String LOGO_V2 = "logo v2";
    private static String LOGO_TRAUR = "logo Traur";
    private static String SVECHA = "Svecha";
    private static String ZNAK_KRUG_12 = "znak krug 12";
    private static String ZNAK_KRUG_16 = "znak krug 16";
    private static String ZNAK_KRUG_18 = "znak krug 18";
    private static String SKLEROTIC = "Dali HS-CriminalMinds";
    private static String TICKER = "Ticker 1";
    private static NumberOfFormats numberOfFormats;

    private List<List<Event>> allProgramList;
    private List<Event> allEventsList;
    private PLKeeper plKeeperMock;

    private String allFormatsAtNotAProgram = LOGO_V2 + ", " + LOGO_TRAUR + ", " + SVECHA;
    private String allFormatsOnNotTheLastSubclip = allFormatsAtNotAProgram + ", " + ZNAK_KRUG_12 + ", " + ZNAK_KRUG_16 + ", " + ZNAK_KRUG_18 + ", " + TICKER;
    private String allFormatsAtTheLastSubclip = allFormatsOnNotTheLastSubclip + ", " + SKLEROTIC;

    @BeforeClass
    public static void initClass() throws IOException {
        Properties properties = new Properties();
        properties.load(new FileReader(PATH_TO_PROPERTY));
        FEATURE_NAME = properties.getProperty("FEATURE_NAME");
        numberOfFormats = new NumberOfFormats();
    }

    @Before
    public void init() {
        allEventsList = new ArrayList<>();
        allProgramList = new ArrayList<>();
        plKeeperMock = mock(PLKeeper.class);
        when(plKeeperMock.getAllEventsList()).thenReturn(allEventsList);
        when(plKeeperMock.getAllProgramsList()).thenReturn(allProgramList);
    }

    @Test
    public void withoutErrors() {
        Event programFirstSubclip = new Event(1, "", "00:00:00:00", "00:01:00:00", null, null, "0", "HS-CriminalMinds", "PGM, " + allFormatsOnNotTheLastSubclip, "");
        Event programLastSubclip = new Event(2, "", "00:00:00:00", "00:01:00:00", null, null, "0", "HS-CriminalMinds", "PGM, " + allFormatsAtTheLastSubclip, "");
        Event notAProgram = new Event(3, "", "00:00:00:00", "00:01:00:00", null, null, "0", "someEvent", "PGM, " + allFormatsAtNotAProgram, "");
        allEventsList.add(programFirstSubclip);
        allEventsList.add(programLastSubclip);
        allEventsList.add(notAProgram);

        List<Event> program = new ArrayList<>();
        program.add(programFirstSubclip);
        program.add(programLastSubclip);
        allProgramList.add(program);

        Map<String, Integer> result = numberOfFormats.checkFeature(plKeeperMock);
        assertEquals(0, result.get(FEATURE_NAME).intValue());
    }

    @Test
    public void withoutPgmOnNotTheLastSubclip() {
        Event programFirstSubclip = new Event(1, "", "00:00:00:00", "00:01:00:00", null, null, "0", "HS-CriminalMinds", allFormatsOnNotTheLastSubclip, "");
        Event programLastSubclip = new Event(2, "", "00:00:00:00", "00:01:00:00", null, null, "0", "HS-CriminalMinds", "PGM, " + allFormatsAtTheLastSubclip, "");
        Event notAProgram = new Event(3, "", "00:00:00:00", "00:01:00:00", null, null, "0", "someEvent", "PGM, " + allFormatsAtNotAProgram, "");
        allEventsList.add(programFirstSubclip);
        allEventsList.add(programLastSubclip);
        allEventsList.add(notAProgram);

        List<Event> program = new ArrayList<>();
        program.add(programFirstSubclip);
        program.add(programLastSubclip);
        allProgramList.add(program);

        Map<String, Integer> result = numberOfFormats.checkFeature(plKeeperMock);
        assertEquals(1, result.get(FEATURE_NAME).intValue());
    }

    @Test
    public void withTwoPgmOnNotTheLastSubclip() {
        Event programFirstSubclip = new Event(1, "", "00:00:00:00", "00:01:00:00", null, null, "0", "HS-CriminalMinds", "PGM, PGM, " + allFormatsOnNotTheLastSubclip, "");
        Event programLastSubclip = new Event(2, "", "00:00:00:00", "00:01:00:00", null, null, "0", "HS-CriminalMinds", "PGM, " + allFormatsAtTheLastSubclip, "");
        Event notAProgram = new Event(3, "", "00:00:00:00", "00:01:00:00", null, null, "0", "someEvent", "PGM, " + allFormatsAtNotAProgram, "");
        allEventsList.add(programFirstSubclip);
        allEventsList.add(programLastSubclip);
        allEventsList.add(notAProgram);

        List<Event> program = new ArrayList<>();
        program.add(programFirstSubclip);
        program.add(programLastSubclip);
        allProgramList.add(program);

        Map<String, Integer> result = numberOfFormats.checkFeature(plKeeperMock);
        assertEquals(1, result.get(FEATURE_NAME).intValue());
    }

    @Test
    public void withoutPgmAtTheLastSubclip() {
        Event programFirstSubclip = new Event(1, "", "00:00:00:00", "00:01:00:00", null, null, "0", "HS-CriminalMinds", "PGM, " + allFormatsOnNotTheLastSubclip, "");
        Event programLastSubclip = new Event(2, "", "00:00:00:00", "00:01:00:00", null, null, "0", "HS-CriminalMinds", allFormatsAtTheLastSubclip, "");
        Event notAProgram = new Event(3, "", "00:00:00:00", "00:01:00:00", null, null, "0", "someEvent", "PGM, " + allFormatsAtNotAProgram, "");
        allEventsList.add(programFirstSubclip);
        allEventsList.add(programLastSubclip);
        allEventsList.add(notAProgram);

        List<Event> program = new ArrayList<>();
        program.add(programFirstSubclip);
        program.add(programLastSubclip);
        allProgramList.add(program);

        Map<String, Integer> result = numberOfFormats.checkFeature(plKeeperMock);
        assertEquals(1, result.get(FEATURE_NAME).intValue());
    }

    @Test
    public void withTwoPgmAtTheLastSubclip() {
        Event programFirstSubclip = new Event(1, "", "00:00:00:00", "00:01:00:00", null, null, "0", "HS-CriminalMinds", "PGM, " + allFormatsOnNotTheLastSubclip, "");
        Event programLastSubclip = new Event(2, "", "00:00:00:00", "00:01:00:00", null, null, "0", "HS-CriminalMinds", "PGM, PGM, " + allFormatsAtTheLastSubclip, "");
        Event notAProgram = new Event(3, "", "00:00:00:00", "00:01:00:00", null, null, "0", "someEvent", "PGM, " + allFormatsAtNotAProgram, "");
        allEventsList.add(programFirstSubclip);
        allEventsList.add(programLastSubclip);
        allEventsList.add(notAProgram);

        List<Event> program = new ArrayList<>();
        program.add(programFirstSubclip);
        program.add(programLastSubclip);
        allProgramList.add(program);

        Map<String, Integer> result = numberOfFormats.checkFeature(plKeeperMock);
        assertEquals(1, result.get(FEATURE_NAME).intValue());
    }

    @Test
    public void withoutPgmAtNotAProgram() {
        Event programFirstSubclip = new Event(1, "", "00:00:00:00", "00:01:00:00", null, null, "0", "HS-CriminalMinds", "PGM, " + allFormatsOnNotTheLastSubclip, "");
        Event programLastSubclip = new Event(2, "", "00:00:00:00", "00:01:00:00", null, null, "0", "HS-CriminalMinds", "PGM, " + allFormatsAtTheLastSubclip, "");
        Event notAProgram = new Event(3, "", "00:00:00:00", "00:01:00:00", null, null, "0", "someEvent", allFormatsAtNotAProgram, "");
        allEventsList.add(programFirstSubclip);
        allEventsList.add(programLastSubclip);
        allEventsList.add(notAProgram);

        List<Event> program = new ArrayList<>();
        program.add(programFirstSubclip);
        program.add(programLastSubclip);
        allProgramList.add(program);

        Map<String, Integer> result = numberOfFormats.checkFeature(plKeeperMock);
        assertEquals(1, result.get(FEATURE_NAME).intValue());
    }

    @Test
    public void withTwoPgmAtNotAProgram() {
        Event programFirstSubclip = new Event(1, "", "00:00:00:00", "00:01:00:00", null, null, "0", "HS-CriminalMinds", "PGM, " + allFormatsOnNotTheLastSubclip, "");
        Event programLastSubclip = new Event(2, "", "00:00:00:00", "00:01:00:00", null, null, "0", "HS-CriminalMinds", "PGM, " + allFormatsAtTheLastSubclip, "");
        Event notAProgram = new Event(3, "", "00:00:00:00", "00:01:00:00", null, null, "0", "someEvent", "PGM, PGM, " + allFormatsAtNotAProgram, "");
        allEventsList.add(programFirstSubclip);
        allEventsList.add(programLastSubclip);
        allEventsList.add(notAProgram);

        List<Event> program = new ArrayList<>();
        program.add(programFirstSubclip);
        program.add(programLastSubclip);
        allProgramList.add(program);

        Map<String, Integer> result = numberOfFormats.checkFeature(plKeeperMock);
        assertEquals(1, result.get(FEATURE_NAME).intValue());
    }
}