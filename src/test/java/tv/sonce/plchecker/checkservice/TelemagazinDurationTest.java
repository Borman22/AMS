package tv.sonce.plchecker.checkservice;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import tv.sonce.plchecker.PLKeeper;
import tv.sonce.plchecker.entity.Event;
import tv.sonce.utils.TimeCode;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TelemagazinDurationTest {

    private static final String PATH_TO_PROPERTY = "./properties/PLChecker/checkerservice/TelemagazinDuration.properties";

    private static String FEATURE_NAME;
    private static String TELEMAGAZIN_DURATION_STR;
    private static TelemagazinDuration telemagazinDuration;

    private List<List<Event>> allTelemagazinBloksList;
    private PLKeeper plKeeperMock;

    @BeforeClass
    public static void initClass() throws IOException {
        Properties properties = new Properties();
        properties.load(new FileReader(PATH_TO_PROPERTY));
        FEATURE_NAME = properties.getProperty("FEATURE_NAME");
        TELEMAGAZIN_DURATION_STR = properties.getProperty("TELEMAGAZIN_DURATION");
        telemagazinDuration = new TelemagazinDuration();
    }

    @Before
    public void init() {
        allTelemagazinBloksList = new ArrayList<>();
        plKeeperMock = mock(PLKeeper.class);
        when(plKeeperMock.getAllTelemagazinBloksList()).thenReturn(allTelemagazinBloksList);
    }

    @Test
    public void lessThenRequiredDuration() {
        TimeCode TELEMAGAZIN_DURATION = new TimeCode(TELEMAGAZIN_DURATION_STR);
        Event telemagazin1 = new Event(1, "", "00:00:00:00", TELEMAGAZIN_DURATION.changeToNFramesConsideringMidnight(-1).getDelimitedStr(), null, null, "0", "Telemagazin1", "", "");
        Event telemagazin2 = new Event(2, "", TELEMAGAZIN_DURATION.getDelimitedStr(), "00:00:00:00", null, null, "0", "Telemagazin2", "", "");

        List<Event> telemagazinBlock = new ArrayList<>();
        telemagazinBlock.add(telemagazin1);
        telemagazinBlock.add(telemagazin2);
        allTelemagazinBloksList.add(telemagazinBlock);

        Map<String, Integer> result = telemagazinDuration.checkFeature(plKeeperMock);
        assertEquals(1, result.get(FEATURE_NAME).intValue());
    }

    @Test
    public void lessThenRequiredDuration2() {
        TimeCode TELEMAGAZIN_DURATION = new TimeCode(TELEMAGAZIN_DURATION_STR);
        Event telemagazin1 = new Event(1, "", "00:00:00:00", TELEMAGAZIN_DURATION.changeToNFramesConsideringMidnight(-10).getDelimitedStr(), null, null, "0", "Telemagazin1", "", "");
        Event telemagazin2 = new Event(2, "", TELEMAGAZIN_DURATION.getDelimitedStr(), "00:00:00:09", null, null, "0", "Telemagazin2", "", "");

        List<Event> telemagazinBlock = new ArrayList<>();
        telemagazinBlock.add(telemagazin1);
        telemagazinBlock.add(telemagazin2);
        allTelemagazinBloksList.add(telemagazinBlock);

        Map<String, Integer> result = telemagazinDuration.checkFeature(plKeeperMock);
        assertEquals(1, result.get(FEATURE_NAME).intValue());
    }

    @Test
    public void eqRequiredDuration() {
        TimeCode TELEMAGAZIN_DURATION = new TimeCode(TELEMAGAZIN_DURATION_STR);
        Event telemagazin1 = new Event(1, "", "00:00:00:00", TELEMAGAZIN_DURATION.getDelimitedStr(), null, null, "0", "Telemagazin1", "", "");
        Event telemagazin2 = new Event(2, "", TELEMAGAZIN_DURATION.getDelimitedStr(), "00:00:00:00", null, null, "0", "Telemagazin2", "", "");

        List<Event> telemagazinBlock = new ArrayList<>();
        telemagazinBlock.add(telemagazin1);
        telemagazinBlock.add(telemagazin2);
        allTelemagazinBloksList.add(telemagazinBlock);

        Map<String, Integer> result = telemagazinDuration.checkFeature(plKeeperMock);
        assertEquals(0, result.get(FEATURE_NAME).intValue());
    }

    @Test
    public void eqRequiredDuration2() {
        TimeCode TELEMAGAZIN_DURATION = new TimeCode(TELEMAGAZIN_DURATION_STR);
        Event telemagazin1 = new Event(1, "", "00:00:00:00", TELEMAGAZIN_DURATION.changeToNFramesConsideringMidnight(-10).getDelimitedStr(), null, null, "0", "Telemagazin1", "", "");
        Event telemagazin2 = new Event(2, "", TELEMAGAZIN_DURATION.getDelimitedStr(), "00:00:00:10", null, null, "0", "Telemagazin2", "", "");

        List<Event> telemagazinBlock = new ArrayList<>();
        telemagazinBlock.add(telemagazin1);
        telemagazinBlock.add(telemagazin2);
        allTelemagazinBloksList.add(telemagazinBlock);

        Map<String, Integer> result = telemagazinDuration.checkFeature(plKeeperMock);
        assertEquals(0, result.get(FEATURE_NAME).intValue());
    }

    @Test
    public void moreThenRequiredDuration() {
        TimeCode TELEMAGAZIN_DURATION = new TimeCode(TELEMAGAZIN_DURATION_STR);
        Event telemagazin1 = new Event(1, "", "00:00:00:00", TELEMAGAZIN_DURATION.changeToNFramesConsideringMidnight(1).getDelimitedStr(), null, null, "0", "Telemagazin1", "", "");
        Event telemagazin2 = new Event(2, "", TELEMAGAZIN_DURATION.getDelimitedStr(), "00:00:00:00", null, null, "0", "Telemagazin2", "", "");

        List<Event> telemagazinBlock = new ArrayList<>();
        telemagazinBlock.add(telemagazin1);
        telemagazinBlock.add(telemagazin2);
        allTelemagazinBloksList.add(telemagazinBlock);

        Map<String, Integer> result = telemagazinDuration.checkFeature(plKeeperMock);
        assertEquals(0, result.get(FEATURE_NAME).intValue());
    }

    @Test
    public void moreThenRequiredDuration2() {
        TimeCode TELEMAGAZIN_DURATION = new TimeCode(TELEMAGAZIN_DURATION_STR);
        Event telemagazin1 = new Event(1, "", "00:00:00:00", TELEMAGAZIN_DURATION.changeToNFramesConsideringMidnight(-10).getDelimitedStr(), null, null, "0", "Telemagazin1", "", "");
        Event telemagazin2 = new Event(2, "", TELEMAGAZIN_DURATION.getDelimitedStr(), "00:00:00:11", null, null, "0", "Telemagazin2", "", "");

        List<Event> telemagazinBlock = new ArrayList<>();
        telemagazinBlock.add(telemagazin1);
        telemagazinBlock.add(telemagazin2);
        allTelemagazinBloksList.add(telemagazinBlock);

        Map<String, Integer> result = telemagazinDuration.checkFeature(plKeeperMock);
        assertEquals(0, result.get(FEATURE_NAME).intValue());
    }

}