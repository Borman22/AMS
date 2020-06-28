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

public class ReklamaDurationInHourTest {

    private static final String PATH_TO_PROPERTY = "./properties/PLChecker/checkerservice/ReklamaDurationInHour.properties";

    private static String FEATURE_NAME;
    private static TimeCode REKLAMA_DURATION_IN_HOUR;
    private static ReklamaDurationInHour reklamaDurationInHour;

    private List<List<Event>> allReklamBloksList;
    private PLKeeper plKeeperMock;

    @BeforeClass
    public static void initClass() throws IOException {
        Properties properties = new Properties();
        properties.load(new FileReader(PATH_TO_PROPERTY));
        FEATURE_NAME = properties.getProperty("FEATURE_NAME");
        REKLAMA_DURATION_IN_HOUR = new TimeCode(properties.getProperty("REKLAMA_DURATION_IN_HOUR"));
        reklamaDurationInHour = new ReklamaDurationInHour();
    }

    @Before
    public void init() {
        allReklamBloksList = new ArrayList<>();
        plKeeperMock = mock(PLKeeper.class);
        when(plKeeperMock.getAllReklamBloksList()).thenReturn(allReklamBloksList);
    }

    @Test
    public void lessThenMaxDuration() {
        Event reklamaFromBlock1 = new Event(1, "", "00:00:00:00", REKLAMA_DURATION_IN_HOUR.changeToNFramesConsideringMidnight(-1).getDelimitedStr(), null, null, "0", "Reklama1", "", "");
        Event reklamaFromBlock2 = new Event(2, "", REKLAMA_DURATION_IN_HOUR.getDelimitedStr(), "00:00:00:00", null, null, "0", "Reklama2", "", "");

        List<Event> reklamaBlock1 = new ArrayList<>();
        List<Event> reklamaBlock2 = new ArrayList<>();
        reklamaBlock1.add(reklamaFromBlock1);
        reklamaBlock2.add(reklamaFromBlock2);
        allReklamBloksList.add(reklamaBlock1);
        allReklamBloksList.add(reklamaBlock2);

        Map<String, Integer> result = reklamaDurationInHour.checkFeature(plKeeperMock);
        assertEquals(0, result.get(FEATURE_NAME).intValue());
    }

    @Test
    public void eqMaxDuration() {
        Event reklamaFromBlock1 = new Event(1, "", "00:00:00:00", REKLAMA_DURATION_IN_HOUR.changeToNFramesConsideringMidnight(-1).getDelimitedStr(), null, null, "0", "Reklama1", "", "");
        Event reklamaFromBlock2 = new Event(2, "", REKLAMA_DURATION_IN_HOUR.getDelimitedStr(), "00:00:00:01", null, null, "0", "Reklama2", "", "");

        List<Event> reklamaBlock1 = new ArrayList<>();
        List<Event> reklamaBlock2 = new ArrayList<>();
        reklamaBlock1.add(reklamaFromBlock1);
        reklamaBlock2.add(reklamaFromBlock2);
        allReklamBloksList.add(reklamaBlock1);
        allReklamBloksList.add(reklamaBlock2);

        Map<String, Integer> result = reklamaDurationInHour.checkFeature(plKeeperMock);
        assertEquals(0, result.get(FEATURE_NAME).intValue());
    }

    @Test
    public void moreThenMaxDuration() {
        Event reklamaFromBlock1 = new Event(1, "", "00:00:00:00", REKLAMA_DURATION_IN_HOUR.changeToNFramesConsideringMidnight(-1).getDelimitedStr(), null, null, "0", "Reklama1", "", "");
        Event reklamaFromBlock2 = new Event(2, "", REKLAMA_DURATION_IN_HOUR.getDelimitedStr(), "00:00:00:02", null, null, "0", "Reklama2", "", "");

        List<Event> reklamaBlock1 = new ArrayList<>();
        List<Event> reklamaBlock2 = new ArrayList<>();

        reklamaBlock1.add(reklamaFromBlock1);
        reklamaBlock2.add(reklamaFromBlock2);

        allReklamBloksList.add(reklamaBlock1);
        allReklamBloksList.add(reklamaBlock2);

        Map<String, Integer> result = reklamaDurationInHour.checkFeature(plKeeperMock);
        assertEquals(1, result.get(FEATURE_NAME).intValue());
    }
}