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

public class DurationTest {

    private final static String PATH_TO_PROPERTY = "./properties/PLChecker/checkerservice/Duration.properties";
    private static String FEATURE_NAME;
    private static Duration duration;

    private List<Event> allEventsList;
    private PLKeeper plKeeperMock;

    @BeforeClass
    public static void initClass() throws IOException {
        Properties properties = new Properties();
        properties.load(new FileReader(PATH_TO_PROPERTY));
        FEATURE_NAME = properties.getProperty("FEATURE_NAME");
        duration = new Duration();
    }

    @Before
    public void init() {
        allEventsList = new ArrayList<>();
        plKeeperMock = mock(PLKeeper.class);
        when(plKeeperMock.getAllEventsList()).thenReturn(allEventsList);
    }

    /*
        <event>
          <date>2020-05-20</date>
          <time>06:00:06:23</time>
          <duration>00:40:50:24</duration>
          <asset_id>74499</asset_id>
          <name>PR-pravila-11</name>
          <format>PGM, Dali PR-Paralelniy Svit</format>
          <status>OK</status>
        </event>
        <event>
          <date>2020-05-20</date>
          <time>06:40:57:22</time>
          <duration>00:05:48:14</duration>
          <tc_in>00:00:00:00</tc_in>
          <tc_out>00:05:48:14</tc_out>
          <asset_id>59417</asset_id>
          <name>PR-ParalMir-1220 сег. 4-1</name>
          <format>PGM 4x3</format>
          <status>OK</status>
        </event>
    */

    @Test
    public void durationEqDiffInOutStartsFromZero() {
        Event event1 = new Event(1, "", "06:00:00:00", "01:00:00:00", "00:00:00:00", "01:00:00:00", "0", "", "", "");
        Event event2 = new Event(2, "", "07:00:00:00", "23:00:00:00", null, null, "0", "", "", "");
        allEventsList.add(event1);
        allEventsList.add(event2);

        Map<String, Integer> result = duration.checkFeature(plKeeperMock);
        assertEquals(result.get(FEATURE_NAME).intValue(), 0);
    }

    @Test
    public void durationDoesNotEqDiffInOutStartsFromZero() {
        Event event1 = new Event(1, "", "06:00:00:00", "01:00:00:00", "00:00:00:00", "01:00:00:01", "0", "", "", "");
        Event event2 = new Event(2, "", "07:00:00:00", "23:00:00:00", null, null, "0", "", "", "");
        allEventsList.add(event1);
        allEventsList.add(event2);

        Map<String, Integer> result = duration.checkFeature(plKeeperMock);
        assertEquals(result.get(FEATURE_NAME).intValue(), 1);
    }

    @Test
    public void durationEqDiffInOutStartsNotFromZero() {
        Event event1 = new Event(1, "", "06:00:00:00", "01:00:00:00", "00:04:00:00", "01:04:00:00", "0", "", "", "");
        Event event2 = new Event(2, "", "07:00:00:00", "23:00:00:00", null, null, "0", "", "", "");
        allEventsList.add(event1);
        allEventsList.add(event2);

        Map<String, Integer> result = duration.checkFeature(plKeeperMock);
        assertEquals(result.get(FEATURE_NAME).intValue(), 0);
    }

    @Test
    public void durationDoesNotEqDiffInOutStartsNotFromZero() {
        Event event1 = new Event(1, "", "06:00:00:00", "01:00:00:00", "00:04:00:00", "01:04:00:01", "0", "", "", "");
        Event event2 = new Event(2, "", "07:00:00:00", "23:00:00:00", null, null, "0", "", "", "");
        allEventsList.add(event1);
        allEventsList.add(event2);

        Map<String, Integer> result = duration.checkFeature(plKeeperMock);
        assertEquals(result.get(FEATURE_NAME).intValue(), 1);
    }

    @Test
    public void durationEqTimeFirstTimeSecond() {
        Event event1 = new Event(1, "", "06:00:00:00", "01:00:00:01", null, null, "0", "", "", "");
        Event event2 = new Event(2, "", "07:00:00:01", "22:59:59:24", null, null, "0", "", "", "");
        allEventsList.add(event1);
        allEventsList.add(event2);

        Map<String, Integer> result = duration.checkFeature(plKeeperMock);
        assertEquals(result.get(FEATURE_NAME).intValue(), 0);
    }

    @Test
    public void durationDoesNotEqTimeFirstTimeSecond() {
        Event event1 = new Event(1, "", "06:00:00:00", "01:00:00:00", null, null, "0", "", "", "");
        Event event2 = new Event(2, "", "07:00:00:01", "22:59:59:24", null, null, "0", "", "", "");
        allEventsList.add(event1);
        allEventsList.add(event2);

        Map<String, Integer> result = duration.checkFeature(plKeeperMock);
        assertEquals(result.get(FEATURE_NAME).intValue(), 1);
    }

    @Test
    public void plEndsAtSixOclock() {
        Event event1 = new Event(1, "", "06:00:00:00", "01:00:00:00", null, null, "0", "", "", "");
        Event event2 = new Event(2, "", "07:00:00:00", "23:00:00:00", null, null, "0", "", "", "");
        allEventsList.add(event1);
        allEventsList.add(event2);

        Map<String, Integer> result = duration.checkFeature(plKeeperMock);
        assertEquals(result.get(FEATURE_NAME).intValue(), 0);
    }

    @Test
    public void plDoesNotEndAtSixOclock() {
        Event event1 = new Event(1, "", "06:00:00:00", "01:00:00:00", null, null, "0", "", "", "");
        Event event2 = new Event(2, "", "07:00:00:00", "22:59:59:24", null, null, "0", "", "", "");
        allEventsList.add(event1);
        allEventsList.add(event2);

        Map<String, Integer> result = duration.checkFeature(plKeeperMock);
        assertEquals(result.get(FEATURE_NAME).intValue(), 1);
    }

}