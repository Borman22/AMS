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

public class LogoAmountTest {
    private static final String PATH_TO_PROPERTY = "./properties/PLChecker/checkerservice/LogoAmount.properties";

    private static String FEATURE_NAME;
    private static String LOGO_V2;
    private static String LOGO_TRAUR;
    private static String ERROR_MESSAGE;
    private static String WARNING_MESSAGE;
    private static LogoAmount logoAmount;

    //    private List<Event> allEventsList;
    private List<Event> allEventsList;
    private PLKeeper plKeeperMock;

    @BeforeClass
    public static void initClass() throws IOException {
        Properties properties = new Properties();
        properties.load(new FileReader(PATH_TO_PROPERTY));
        FEATURE_NAME = properties.getProperty("FEATURE_NAME");
        LOGO_V2 = properties.getProperty("LOGO_V2");
        LOGO_TRAUR = properties.getProperty("LOGO_TRAUR");
        ERROR_MESSAGE = properties.getProperty("ERROR_MESSAGE");
        WARNING_MESSAGE = properties.getProperty("WARNING_MESSAGE");
        logoAmount = new LogoAmount();
    }

    @Before
    public void init() {
        allEventsList = new ArrayList<>();
        plKeeperMock = mock(PLKeeper.class);
        when(plKeeperMock.getAllEventsList()).thenReturn(allEventsList);
    }

    @Test
    public void logoV2Alone() {
        Event event = new Event(1, "", "00:00:00:00", "00:01:00:00", null, null, "0", "", LOGO_V2, "");
        allEventsList.add(event);

        Map<String, Integer> result = logoAmount.checkFeature(plKeeperMock);
        assertEquals(1, result.get(FEATURE_NAME).intValue());
        assertEquals(WARNING_MESSAGE, allEventsList.get(0).errors.get(0));
    }

    @Test
    public void withoutLogo() {
        Event event = new Event(1, "", "00:00:00:00", "00:01:00:00", null, null, "0", "", "", "");
        allEventsList.add(event);

        Map<String, Integer> result = logoAmount.checkFeature(plKeeperMock);
        assertEquals(0, result.get(FEATURE_NAME).intValue());
    }

    @Test
    public void logoTraurAlone() {
        Event event = new Event(1, "", "00:00:00:00", "00:01:00:00", null, null, "0", "", LOGO_TRAUR, "");
        allEventsList.add(event);

        Map<String, Integer> result = logoAmount.checkFeature(plKeeperMock);
        assertEquals(1, result.get(FEATURE_NAME).intValue());
        assertEquals(WARNING_MESSAGE, allEventsList.get(0).errors.get(0));
    }

    @Test
    public void logoTraurAndLogoV2() {
        Event event = new Event(1, "", "00:00:00:00", "00:01:00:00", null, null, "0", "", LOGO_TRAUR + ", " + LOGO_V2, "");
        allEventsList.add(event);

        Map<String, Integer> result = logoAmount.checkFeature(plKeeperMock);
        assertEquals(1, result.get(FEATURE_NAME).intValue());
        assertEquals(ERROR_MESSAGE, allEventsList.get(0).errors.get(0));
    }

    @Test
    public void twoLogoV2() {
        Event event = new Event(1, "", "00:00:00:00", "00:01:00:00", null, null, "0", "", LOGO_V2 + ", " + LOGO_V2, "");
        allEventsList.add(event);

        Map<String, Integer> result = logoAmount.checkFeature(plKeeperMock);
        assertEquals(1, result.get(FEATURE_NAME).intValue());
        assertEquals(ERROR_MESSAGE, allEventsList.get(0).errors.get(0));
    }

    @Test
    public void twoLogoTraur() {
        Event event = new Event(1, "", "00:00:00:00", "00:01:00:00", null, null, "0", "", LOGO_TRAUR + ", " + LOGO_TRAUR, "");
        allEventsList.add(event);

        Map<String, Integer> result = logoAmount.checkFeature(plKeeperMock);
        assertEquals(1, result.get(FEATURE_NAME).intValue());
        assertEquals(ERROR_MESSAGE, allEventsList.get(0).errors.get(0));
    }
}