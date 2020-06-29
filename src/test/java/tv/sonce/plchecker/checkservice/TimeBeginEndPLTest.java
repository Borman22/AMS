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

public class TimeBeginEndPLTest {

    private static final String PATH_TO_PROPERTY = "./properties/PLChecker/checkerservice/TimeBeginEndPL.properties";

    private static String FEATURE_NAME;
    private static String TIME_BEGIN_PL;
    private static String TIME_END_PL;
    private static TimeBeginEndPL timeBeginEndPL;

    private List<Event> allEventsList;
    private PLKeeper plKeeperMock;

    @BeforeClass
    public static void initClass() throws IOException {
        Properties properties = new Properties();
        properties.load(new FileReader(PATH_TO_PROPERTY));
        FEATURE_NAME = properties.getProperty("FEATURE_NAME");
        TIME_BEGIN_PL = properties.getProperty("TIME_BEGIN_PL");
        TIME_END_PL = properties.getProperty("TIME_END_PL");
        timeBeginEndPL = new TimeBeginEndPL();
    }

    @Before
    public void init() {
        allEventsList = new ArrayList<>();
        plKeeperMock = mock(PLKeeper.class);
        when(plKeeperMock.getAllEventsList()).thenReturn(allEventsList);
    }

    @Test
    public void withoutErrors() {
        Event event1 = new Event(1, "", TIME_BEGIN_PL, "01:00:00:00", null, null, "0", "Event1", "", "");
        Event event2 = new Event(2, "", TIME_END_PL, "00:00:00:00", null, null, "0", "Event2", "", "");

        allEventsList.add(event1);
        allEventsList.add(event2);

        Map<String, Integer> result = timeBeginEndPL.checkFeature(plKeeperMock);
        assertEquals(0, result.get(FEATURE_NAME).intValue());
    }

    @Test
    public void beginLessThenRequired() {
        Event event1 = new Event(1, "", new TimeCode(TIME_BEGIN_PL).changeToNFramesConsideringMidnight(-1).getDelimitedStr(), "01:00:00:00", null, null, "0", "Event1", "", "");
        Event event2 = new Event(2, "", TIME_END_PL, "00:00:00:00", null, null, "0", "Event2", "", "");

        allEventsList.add(event1);
        allEventsList.add(event2);

        Map<String, Integer> result = timeBeginEndPL.checkFeature(plKeeperMock);
        assertEquals(1, result.get(FEATURE_NAME).intValue());
    }

    @Test
    public void beginMoreThenRequired() {
        Event event1 = new Event(1, "", new TimeCode(TIME_BEGIN_PL).changeToNFramesConsideringMidnight(1).getDelimitedStr(), "01:00:00:00", null, null, "0", "Event1", "", "");
        Event event2 = new Event(2, "", TIME_END_PL, "00:00:00:00", null, null, "0", "Event2", "", "");

        allEventsList.add(event1);
        allEventsList.add(event2);

        Map<String, Integer> result = timeBeginEndPL.checkFeature(plKeeperMock);
        assertEquals(1, result.get(FEATURE_NAME).intValue());
    }

    @Test
    public void endLessThenRequired() {
        Event event1 = new Event(1, "", TIME_BEGIN_PL, "01:00:00:00", null, null, "0", "Event1", "", "");
        Event event2 = new Event(2, "", new TimeCode(TIME_END_PL).changeToNFramesConsideringMidnight(-1).getDelimitedStr(), "00:00:00:00", null, null, "0", "Event2", "", "");

        allEventsList.add(event1);
        allEventsList.add(event2);

        Map<String, Integer> result = timeBeginEndPL.checkFeature(plKeeperMock);
        assertEquals(1, result.get(FEATURE_NAME).intValue());
    }

    @Test
    public void endMoreThenRequired() {
        Event event1 = new Event(1, "", TIME_BEGIN_PL, "01:00:00:00", null, null, "0", "Event1", "", "");
        Event event2 = new Event(2, "", new TimeCode(TIME_END_PL).changeToNFramesConsideringMidnight(1).getDelimitedStr(), "00:00:00:00", null, null, "0", "Event2", "", "");

        allEventsList.add(event1);
        allEventsList.add(event2);

        Map<String, Integer> result = timeBeginEndPL.checkFeature(plKeeperMock);
        assertEquals(1, result.get(FEATURE_NAME).intValue());
    }

}