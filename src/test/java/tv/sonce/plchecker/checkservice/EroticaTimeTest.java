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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EroticaTimeTest {

    private static final String PATH_TO_PROPERTY = "./properties/PLChecker/checkerservice/EroticaTime.properties";

    private static String FEATURE_NAME;
    private static String EROTICA_TIME_BEGIN;
    private static String EROTICA_TIME_END;
    private static EroticaTime eroticaTime;

    private List<Event> allEventsList;
    private List<List<Event>> allProgramList;
    private PLKeeper plKeeperMock;

    @BeforeClass
    public static void initClass() throws IOException {
        Properties properties = new Properties();
        properties.load(new FileReader(PATH_TO_PROPERTY));
        FEATURE_NAME = properties.getProperty("FEATURE_NAME");
        EROTICA_TIME_BEGIN = properties.getProperty("EROTICA_TIME_BEGIN");
        EROTICA_TIME_END = properties.getProperty("EROTICA_TIME_END");
        eroticaTime = new EroticaTime();
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
    public void timeOK() {
        Event event1 = new Event(1, "", "01:00:00:00", "01:00:00:00", null, null, "0", "HF-Corporate", "", "");
        allEventsList.add(event1);

        allProgramList.get(0).add(event1);

        Map<String, Integer> result = eroticaTime.checkFeature(plKeeperMock);
        assertEquals(0, result.get(FEATURE_NAME).intValue());
    }

    @Test
    public void timeLessThenEroticaTimeBegin() {
        String time = new TimeCode(EROTICA_TIME_BEGIN).changeToNFramesConsideringMidnight(-1).toString();
        Event event1 = new Event(1, "", time, "00:00:01:00", null, null, "0", "HF-Corporate", "", "");
        allEventsList.add(event1);

        allProgramList.get(0).add(event1);

        Map<String, Integer> result = eroticaTime.checkFeature(plKeeperMock);
        assertEquals(1, result.get(FEATURE_NAME).intValue());
    }

    @Test
    public void notEroticaWithTimeLessThenEroticaTimeBegin() {
        String time = new TimeCode(EROTICA_TIME_BEGIN).changeToNFramesConsideringMidnight(-1).toString();
        Event event1 = new Event(1, "", time, "00:00:01:00", null, null, "0", "PR-AAAAAA", "", "");
        allEventsList.add(event1);

        allProgramList.get(0).add(event1);

        Map<String, Integer> result = eroticaTime.checkFeature(plKeeperMock);
        assertEquals(0, result.get(FEATURE_NAME).intValue());
    }

    @Test
    public void timeEqEroticaTimeBegin() {
        String time = new TimeCode(EROTICA_TIME_BEGIN).toString();
        Event event1 = new Event(1, "", time, "00:00:01:00", null, null, "0", "HF-Corporate", "", "");
        allEventsList.add(event1);

        allProgramList.get(0).add(event1);

        Map<String, Integer> result = eroticaTime.checkFeature(plKeeperMock);
        assertEquals(0, result.get(FEATURE_NAME).intValue());
    }

    @Test
    public void timeMoreThenEroticaTimeBegin() {
        String time = new TimeCode(EROTICA_TIME_BEGIN).changeToNFramesConsideringMidnight(1).toString();
        Event event1 = new Event(1, "", time, "00:00:01:00", null, null, "0", "HF-Corporate", "", "");
        allEventsList.add(event1);

        allProgramList.get(0).add(event1);

        Map<String, Integer> result = eroticaTime.checkFeature(plKeeperMock);
        assertEquals(0, result.get(FEATURE_NAME).intValue());
    }

    @Test
    public void timePlusDurationLessThenEroticaTimeEnd() {
        String time = new TimeCode(EROTICA_TIME_END).changeToNFramesConsideringMidnight(-2).toString();
        Event event1 = new Event(1, "", time, "00:00:00:01", null, null, "0", "HF-Corporate", "", "");
        allEventsList.add(event1);

        allProgramList.get(0).add(event1);

        Map<String, Integer> result = eroticaTime.checkFeature(plKeeperMock);
        assertEquals(0, result.get(FEATURE_NAME).intValue());
    }

    @Test
    public void timePlusDurationEqEroticaTimeEnd() {
        String time = new TimeCode(EROTICA_TIME_END).changeToNFramesConsideringMidnight(-1).toString();
        Event event1 = new Event(1, "", time, "00:00:00:01", null, null, "0", "HF-Corporate", "", "");
        allEventsList.add(event1);

        allProgramList.get(0).add(event1);

        Map<String, Integer> result = eroticaTime.checkFeature(plKeeperMock);
        assertEquals(0, result.get(FEATURE_NAME).intValue());
    }

    @Test
    public void timePlusDurationMoreThenEroticaTimeEnd() {
        String time = new TimeCode(EROTICA_TIME_END).changeToNFramesConsideringMidnight(-1).toString();
        Event event1 = new Event(1, "", time, "00:00:00:02", null, null, "0", "HF-Corporate", "", "");
        allEventsList.add(event1);

        allProgramList.get(0).add(event1);

        Map<String, Integer> result = eroticaTime.checkFeature(plKeeperMock);
        assertEquals(1, result.get(FEATURE_NAME).intValue());
    }

}