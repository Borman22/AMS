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

public class ZnakKrugTest {
    private static final String PATH_TO_PROPERTY = "./properties/PLChecker/checkerservice/ZnakKrug.properties";

    private static String FEATURE_NAME;
    private static String ZNAK_KRUG_12;
    private static String ZNAK_KRUG_16;
    private static String ZNAK_KRUG_18;
    private static ZnakKrug znakKrug;

    private final String PROGRAM_WITHOUT_ZNAK_KRUG = "PR-Kruti Rozvodi";
    private final String PROGRAM_WITH_ZNAK_KRUG_12 = "HS-CHAU-S01-01";
    private final String PROGRAM_WITH_ZNAK_KRUG_16 = "HS-CriminalMinds-S01-01";
//    private final String PROGRAM_WITH_ZNAK_KRUG_18 = "HF-TheGuesthouse";  // currently ZNAK_KRUG_18 doesn't use

//    private final String TEMPLATE_PROGRAM_WITH_ZNAK_KRUG_12 = "";  // currently TEMPLATE for ZNAK_KRUG_12 doesn't use
    private final String TEMPLATE_PROGRAM_WITH_ZNAK_KRUG_16 = "HS-AAAAAAAAA";
//    private final String TEMPLATE_PROGRAM_WITH_ZNAK_KRUG_18 = "";  // currently TEMPLATE for ZNAK_KRUG_16 doesn't use

    private List<List<Event>> allProgramsList;
    private PLKeeper plKeeperMock;

    @BeforeClass
    public static void initClass() throws IOException {
        Properties properties = new Properties();
        properties.load(new FileReader(PATH_TO_PROPERTY));
        FEATURE_NAME = properties.getProperty("FEATURE_NAME");
        ZNAK_KRUG_12 = properties.getProperty("ZNAK_KRUG_12_FORMAT");
        ZNAK_KRUG_16 = properties.getProperty("ZNAK_KRUG_16_FORMAT");
        ZNAK_KRUG_18 = properties.getProperty("ZNAK_KRUG_18_FORMAT");
        znakKrug = new ZnakKrug();
    }

    @Before
    public void init() {
        allProgramsList = new ArrayList<>();
        plKeeperMock = mock(PLKeeper.class);
        when(plKeeperMock.getAllProgramsList()).thenReturn(allProgramsList);
    }

    @Test
    public void znakKrug12WithoutError() {
        Event event = new Event(1, "", "00:00:00:00", "00:01:00:00", null, null, "0", PROGRAM_WITH_ZNAK_KRUG_12, ZNAK_KRUG_12, "");

        List<Event> program = new ArrayList<>();
        program.add(event);
        allProgramsList.add(program);

        Map<String, Integer> result = znakKrug.checkFeature(plKeeperMock);
        assertEquals(0, result.get(FEATURE_NAME).intValue());
    }

    @Test
    public void toManyOfZnakKrug12() {
        Event event = new Event(1, "", "00:00:00:00", "00:01:00:00", null, null, "0", PROGRAM_WITH_ZNAK_KRUG_12, ZNAK_KRUG_12 + ", " + ZNAK_KRUG_12, "");

        List<Event> program = new ArrayList<>();
        program.add(event);
        allProgramsList.add(program);

        Map<String, Integer> result = znakKrug.checkFeature(plKeeperMock);
        assertEquals(1, result.get(FEATURE_NAME).intValue());
    }

    @Test
    public void znakKrug12WithAnothorZnakKrug() {
        Event event = new Event(1, "", "00:00:00:00", "00:01:00:00", null, null, "0", PROGRAM_WITH_ZNAK_KRUG_12, ZNAK_KRUG_12 + ", " + ZNAK_KRUG_16, "");

        List<Event> program = new ArrayList<>();
        program.add(event);
        allProgramsList.add(program);

        Map<String, Integer> result = znakKrug.checkFeature(plKeeperMock);
        assertEquals(1, result.get(FEATURE_NAME).intValue());
    }

    @Test
    public void toFewOfZnakKrug12() {
        Event event = new Event(1, "", "00:00:00:00", "00:01:00:00", null, null, "0", PROGRAM_WITH_ZNAK_KRUG_12, "", "");

        List<Event> program = new ArrayList<>();
        program.add(event);
        allProgramsList.add(program);

        Map<String, Integer> result = znakKrug.checkFeature(plKeeperMock);
        assertEquals(1, result.get(FEATURE_NAME).intValue());
    }

    @Test
    public void znakKrug12IncorrectProgram() {
        Event event = new Event(1, "", "00:00:00:00", "00:01:00:00", null, null, "0", PROGRAM_WITH_ZNAK_KRUG_16, ZNAK_KRUG_12, "");

        List<Event> program = new ArrayList<>();
        program.add(event);
        allProgramsList.add(program);

        Map<String, Integer> result = znakKrug.checkFeature(plKeeperMock);
        assertEquals(1, result.get(FEATURE_NAME).intValue());
    }









    @Test
    public void znakKrug16WithoutError() {
        Event event = new Event(1, "", "00:00:00:00", "00:01:00:00", null, null, "0", PROGRAM_WITH_ZNAK_KRUG_16, ZNAK_KRUG_16, "");

        List<Event> program = new ArrayList<>();
        program.add(event);
        allProgramsList.add(program);

        Map<String, Integer> result = znakKrug.checkFeature(plKeeperMock);
        assertEquals(0, result.get(FEATURE_NAME).intValue());
    }

    @Test
    public void toManyOfZnakKrug16() {
        Event event = new Event(1, "", "00:00:00:00", "00:01:00:00", null, null, "0", PROGRAM_WITH_ZNAK_KRUG_16, ZNAK_KRUG_16 + ", " + ZNAK_KRUG_16, "");

        List<Event> program = new ArrayList<>();
        program.add(event);
        allProgramsList.add(program);

        Map<String, Integer> result = znakKrug.checkFeature(plKeeperMock);
        assertEquals(1, result.get(FEATURE_NAME).intValue());
    }

    @Test
    public void znakKrug16WithAnotherZnakKrug() {
        Event event = new Event(1, "", "00:00:00:00", "00:01:00:00", null, null, "0", PROGRAM_WITH_ZNAK_KRUG_16, ZNAK_KRUG_16 + ", " + ZNAK_KRUG_18, "");

        List<Event> program = new ArrayList<>();
        program.add(event);
        allProgramsList.add(program);

        Map<String, Integer> result = znakKrug.checkFeature(plKeeperMock);
        assertEquals(1, result.get(FEATURE_NAME).intValue());
    }

    @Test
    public void toFewOfZnakKrug16() {
        Event event = new Event(1, "", "00:00:00:00", "00:01:00:00", null, null, "0", PROGRAM_WITH_ZNAK_KRUG_16, "", "");

        List<Event> program = new ArrayList<>();
        program.add(event);
        allProgramsList.add(program);

        Map<String, Integer> result = znakKrug.checkFeature(plKeeperMock);
        assertEquals(1, result.get(FEATURE_NAME).intValue());
    }

    @Test
    public void znakKrug16IncorrectProgram() {
        Event event = new Event(1, "", "00:00:00:00", "00:01:00:00", null, null, "0", PROGRAM_WITH_ZNAK_KRUG_12, ZNAK_KRUG_16, "");

        List<Event> program = new ArrayList<>();
        program.add(event);
        allProgramsList.add(program);

        Map<String, Integer> result = znakKrug.checkFeature(plKeeperMock);
        assertEquals(1, result.get(FEATURE_NAME).intValue());
    }









    @Test
    public void znakKrug16TemplateWithoutError() {
        Event event = new Event(1, "", "00:00:00:00", "00:01:00:00", null, null, "0", TEMPLATE_PROGRAM_WITH_ZNAK_KRUG_16, ZNAK_KRUG_16, "");

        List<Event> program = new ArrayList<>();
        program.add(event);
        allProgramsList.add(program);

        Map<String, Integer> result = znakKrug.checkFeature(plKeeperMock);
        assertEquals(0, result.get(FEATURE_NAME).intValue());
    }

    @Test
    public void toManyOfZnakKrug16Template() {
        Event event = new Event(1, "", "00:00:00:00", "00:01:00:00", null, null, "0", TEMPLATE_PROGRAM_WITH_ZNAK_KRUG_16, ZNAK_KRUG_16 + ", " + ZNAK_KRUG_16, "");

        List<Event> program = new ArrayList<>();
        program.add(event);
        allProgramsList.add(program);

        Map<String, Integer> result = znakKrug.checkFeature(plKeeperMock);
        assertEquals(1, result.get(FEATURE_NAME).intValue());
    }

    @Test
    public void znakKrug16TemplateWithAnotherZnakKrug() {
        Event event = new Event(1, "", "00:00:00:00", "00:01:00:00", null, null, "0", TEMPLATE_PROGRAM_WITH_ZNAK_KRUG_16, ZNAK_KRUG_16 + ", " + ZNAK_KRUG_12, "");

        List<Event> program = new ArrayList<>();
        program.add(event);
        allProgramsList.add(program);

        Map<String, Integer> result = znakKrug.checkFeature(plKeeperMock);
        assertEquals(1, result.get(FEATURE_NAME).intValue());
    }

    @Test
    public void toFewOfZnakKrug16Template() {
        Event event = new Event(1, "", "00:00:00:00", "00:01:00:00", null, null, "0", TEMPLATE_PROGRAM_WITH_ZNAK_KRUG_16, "", "");

        List<Event> program = new ArrayList<>();
        program.add(event);
        allProgramsList.add(program);

        Map<String, Integer> result = znakKrug.checkFeature(plKeeperMock);
        assertEquals(1, result.get(FEATURE_NAME).intValue());
    }

    @Test
    public void znakKrug16TemplateIncorrectProgram() {
        Event event = new Event(1, "", "00:00:00:00", "00:01:00:00", null, null, "0", PROGRAM_WITH_ZNAK_KRUG_12, ZNAK_KRUG_16, "");

        List<Event> program = new ArrayList<>();
        program.add(event);
        allProgramsList.add(program);

        Map<String, Integer> result = znakKrug.checkFeature(plKeeperMock);
        assertEquals(1, result.get(FEATURE_NAME).intValue());
    }








    @Test
    public void withoutZnakKrugWithoutError() {
        Event event = new Event(1, "", "00:00:00:00", "00:01:00:00", null, null, "0", PROGRAM_WITHOUT_ZNAK_KRUG, "", "");

        List<Event> program = new ArrayList<>();
        program.add(event);
        allProgramsList.add(program);

        Map<String, Integer> result = znakKrug.checkFeature(plKeeperMock);
        assertEquals(0, result.get(FEATURE_NAME).intValue());
    }

    @Test
    public void withoutZnakKrugZnakKrug12() {
        Event event = new Event(1, "", "00:00:00:00", "00:01:00:00", null, null, "0", PROGRAM_WITHOUT_ZNAK_KRUG, ZNAK_KRUG_12, "");

        List<Event> program = new ArrayList<>();
        program.add(event);
        allProgramsList.add(program);

        Map<String, Integer> result = znakKrug.checkFeature(plKeeperMock);
        assertEquals(1, result.get(FEATURE_NAME).intValue());
    }

    @Test
    public void withoutZnakKrugZnakKrug16() {
        Event event = new Event(1, "", "00:00:00:00", "00:01:00:00", null, null, "0", PROGRAM_WITHOUT_ZNAK_KRUG, ZNAK_KRUG_16, "");

        List<Event> program = new ArrayList<>();
        program.add(event);
        allProgramsList.add(program);

        Map<String, Integer> result = znakKrug.checkFeature(plKeeperMock);
        assertEquals(1, result.get(FEATURE_NAME).intValue());
    }

    @Test
    public void withoutZnakKrugZnakKrug18() {
        Event event = new Event(1, "", "00:00:00:00", "00:01:00:00", null, null, "0", PROGRAM_WITHOUT_ZNAK_KRUG, ZNAK_KRUG_18, "");

        List<Event> program = new ArrayList<>();
        program.add(event);
        allProgramsList.add(program);

        Map<String, Integer> result = znakKrug.checkFeature(plKeeperMock);
        assertEquals(1, result.get(FEATURE_NAME).intValue());
    }

    @Test
    public void withoutZnakKrugDifferentZnakKrug() {
        Event event = new Event(1, "", "00:00:00:00", "00:01:00:00", null, null, "0", PROGRAM_WITHOUT_ZNAK_KRUG, ZNAK_KRUG_16 + ", " + ZNAK_KRUG_12 + ", " + ZNAK_KRUG_18, "");

        List<Event> program = new ArrayList<>();
        program.add(event);
        allProgramsList.add(program);

        Map<String, Integer> result = znakKrug.checkFeature(plKeeperMock);
        assertEquals(1, result.get(FEATURE_NAME).intValue());
    }





    @Test
    public void toManyOfDifferentZnakKrug() {
        Event event = new Event(1, "", "00:00:00:00", "00:01:00:00", null, null, "0", PROGRAM_WITH_ZNAK_KRUG_12, ZNAK_KRUG_16 + ", " + ZNAK_KRUG_12 + ", " + ZNAK_KRUG_18, "");

        List<Event> program = new ArrayList<>();
        program.add(event);
        allProgramsList.add(program);

        Map<String, Integer> result = znakKrug.checkFeature(plKeeperMock);
        assertEquals(1, result.get(FEATURE_NAME).intValue());
    }
}