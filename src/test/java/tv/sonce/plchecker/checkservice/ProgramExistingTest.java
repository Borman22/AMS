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

public class ProgramExistingTest {

    private static final String PATH_TO_PROPERTY = "./properties/PLChecker/checkerservice/ProgramExisting.properties";

    private static String FEATURE_NAME;
    private static ProgramExisting programExisting;

    private List<List<Event>> allProgramList;
    private PLKeeper plKeeperMock;

    @BeforeClass
    public static void initClass() throws IOException {
        Properties properties = new Properties();
        properties.load(new FileReader(PATH_TO_PROPERTY));
        FEATURE_NAME = properties.getProperty("FEATURE_NAME");
        programExisting = new ProgramExisting();
    }

    @Before
    public void init() {
        allProgramList = new ArrayList<>();
        plKeeperMock = mock(PLKeeper.class);
        when(plKeeperMock.getAllProgramsList()).thenReturn(allProgramList);
    }

    @Test
    public void withoutErrors() {
        Event programFirstSubclip = new Event(1, "", "00:00:00:00", "00:01:00:00", null, null, "0", "HS-CriminalMinds", "", "");
        Event programLastSubclip = new Event(2, "", "00:00:00:00", "00:01:00:00", null, null, "0", "HS-CriminalMinds", "", "");

        List<Event> program = new ArrayList<>();
        program.add(programFirstSubclip);
        program.add(programLastSubclip);
        allProgramList.add(program);

        Map<String, Integer> result = programExisting.checkFeature(plKeeperMock);
        assertEquals(0, result.get(FEATURE_NAME).intValue());
    }

    @Test
    public void errorProgramDoesntExist() {
        Event programFirstSubclip = new Event(1, "", "00:00:00:00", "00:01:00:00", null, null, "0", "aaa", "", "");
        Event programLastSubclip = new Event(2, "", "00:00:00:00", "00:01:00:00", null, null, "0", "aaa", "", "");

        List<Event> program = new ArrayList<>();
        program.add(programFirstSubclip);
        program.add(programLastSubclip);
        allProgramList.add(program);

        Map<String, Integer> result = programExisting.checkFeature(plKeeperMock);
        assertEquals(1, result.get(FEATURE_NAME).intValue());
    }

}