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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EroticaPlashkaTest {

    private static final String PATH_TO_PROPERTY = "./properties/PLChecker/checkerservice/EroticaPlashka.properties";

    private static String FEATURE_NAME;
    private static EroticaPlashka eroticaPlashka;

    private List<Event> allEventsList;
    private List<List<Event>> allProgramList;
    private PLKeeper plKeeperMock;

    @BeforeClass
    public static void initClass() throws IOException {
        Properties properties = new Properties();
        properties.load(new FileReader(PATH_TO_PROPERTY));
        FEATURE_NAME = properties.getProperty("FEATURE_NAME");
        eroticaPlashka = new EroticaPlashka();
    }

    @Before
    public void init() {
        allEventsList = new ArrayList<>();
        allProgramList = new ArrayList<>();

        List<Event> program = new ArrayList<>();
        allProgramList.add(program);

        plKeeperMock = mock(PLKeeper.class);
        when(plKeeperMock.getAllEventsList()).thenReturn(allEventsList);
        when(plKeeperMock.getAllProgramsList()).thenReturn(allProgramList);
    }

    @Test
    public void plashkaOK() {
        Event event1 = new Event(1, "", "00:00:00:00", "00:00:00:00", null, null, "0", "EROTIKA-PLASHKA-169", "", "");
        Event event2 = new Event(2, "", "00:00:00:00", "00:00:00:00", null, null, "0", "HF-Corporate", "", "");
        allEventsList.add(event1);
        allEventsList.add(event2);

        allProgramList.get(0).add(event2);

        Map<String, Integer> result = eroticaPlashka.checkFeature(plKeeperMock);
        assertEquals(0, result.get(FEATURE_NAME).intValue());
    }

    @Test
    public void thereIsNoPlashkaBeforeErotica() {
        Event event1 = new Event(1, "", "00:00:00:00", "00:00:00:00", null, null, "0", "aaa", "", "");
        Event event2 = new Event(2, "", "00:00:00:00", "00:00:00:00", null, null, "0", "HF-Corporate", "", "");
        allEventsList.add(event1);
        allEventsList.add(event2);

        allProgramList.get(0).add(event2);

        Map<String, Integer> result = eroticaPlashka.checkFeature(plKeeperMock);
        assertEquals(1, result.get(FEATURE_NAME).intValue());
    }

    @Test
    public void thereIsPlashkaBeforeSomeProgram() {
        Event event1 = new Event(1, "", "00:00:00:00", "00:00:00:00", null, null, "0", "EROTIKA-PLASHKA-169", "", "");
        Event event2 = new Event(2, "", "00:00:00:00", "00:00:00:00", null, null, "0", "aaa", "", "");
        Event event3 = new Event(3, "", "00:00:00:00", "00:00:00:00", null, null, "0", "HF-Corporate", "", "");
        allEventsList.add(event1);
        allEventsList.add(event2);
        allEventsList.add(event3);

        allProgramList.get(0).add(event3);

        Map<String, Integer> result = eroticaPlashka.checkFeature(plKeeperMock);
        assertEquals(1, result.get(FEATURE_NAME).intValue());
    }
}