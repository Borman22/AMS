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

public class TelemagazinBumpersTest {

    private static final String PATH_TO_PROPERTY = "./properties/PLChecker/checkerservice/TelemagazinBumpers.properties";

    private static String FEATURE_NAME;
    private static TelemagazinBumpers telemagazinBumpers;

    private List<List<Event>> telemagazinBloksList;
    private PLKeeper plKeeperMock;

    private final String TELEMAGAZIN = "Telemagazin_DaVinci_16x9_v03";

    @BeforeClass
    public static void initClass() throws IOException {
        Properties properties = new Properties();
        properties.load(new FileReader(PATH_TO_PROPERTY));
        FEATURE_NAME = properties.getProperty("FEATURE_NAME");
        telemagazinBumpers = new TelemagazinBumpers();
    }

    @Before
    public void init() {
        telemagazinBloksList = new ArrayList<>();
        plKeeperMock = mock(PLKeeper.class);
        when(plKeeperMock.getAllTelemagazinBloksList()).thenReturn(telemagazinBloksList);
    }

    @Test
    public void withoutErrors() {
        Event bumperFirst = new Event(1, "", "00:00:00:00", "00:01:00:00", null, null, "0", TELEMAGAZIN, "", "");
        Event telemagazin1 = new Event(2, "", "00:00:00:00", "00:01:00:00", null, null, "0", "Telemagazin1", "", "");
        Event telemagazin2 = new Event(3, "", "00:00:00:00", "00:01:00:00", null, null, "0", "Telemagazin2", "", "");
        Event bumperLast = new Event(4, "", "00:00:00:00", "00:01:00:00", null, null, "0", TELEMAGAZIN, "", "");

        List<Event> telemagazinBlock = new ArrayList<>();
        telemagazinBlock.add(bumperFirst);
        telemagazinBlock.add(telemagazin1);
        telemagazinBlock.add(telemagazin2);
        telemagazinBlock.add(bumperLast);
        telemagazinBloksList.add(telemagazinBlock);

        Map<String, Integer> result = telemagazinBumpers.checkFeature(plKeeperMock);
        assertEquals(0, result.get(FEATURE_NAME).intValue());
    }

    @Test
    public void absentLastBumper() {
        Event bumperFirst = new Event(1, "", "00:00:00:00", "00:01:00:00", null, null, "0", TELEMAGAZIN, "", "");
        Event telemagazin1 = new Event(2, "", "00:00:00:00", "00:01:00:00", null, null, "0", "Telemagazin1", "", "");
        Event telemagazin2 = new Event(3, "", "00:00:00:00", "00:01:00:00", null, null, "0", "Telemagazin2", "", "");
        Event telemagazin3 = new Event(4, "", "00:00:00:00", "00:01:00:00", null, null, "0", "Telemagazin3", "", "");

        List<Event> telemagazinBlock = new ArrayList<>();
        telemagazinBlock.add(bumperFirst);
        telemagazinBlock.add(telemagazin1);
        telemagazinBlock.add(telemagazin2);
        telemagazinBlock.add(telemagazin3);
        telemagazinBloksList.add(telemagazinBlock);

        Map<String, Integer> result = telemagazinBumpers.checkFeature(plKeeperMock);
        assertEquals(1, result.get(FEATURE_NAME).intValue());
    }

    @Test
    public void absentFirstBumper() {
        Event telemagazin1 = new Event(1, "", "00:00:00:00", "00:01:00:00", null, null, "0", "Telemagazin1", "", "");
        Event telemagazin2 = new Event(2, "", "00:00:00:00", "00:01:00:00", null, null, "0", "Telemagazin2", "", "");
        Event telemagazin3 = new Event(3, "", "00:00:00:00", "00:01:00:00", null, null, "0", "Telemagazin3", "", "");
        Event bumperLast = new Event(4, "", "00:00:00:00", "00:01:00:00", null, null, "0", TELEMAGAZIN, "", "");

        List<Event> telemagazinBlock = new ArrayList<>();
        telemagazinBlock.add(telemagazin1);
        telemagazinBlock.add(telemagazin2);
        telemagazinBlock.add(telemagazin3);
        telemagazinBlock.add(bumperLast);
        telemagazinBloksList.add(telemagazinBlock);

        Map<String, Integer> result = telemagazinBumpers.checkFeature(plKeeperMock);
        assertEquals(1, result.get(FEATURE_NAME).intValue());
    }

    @Test
    public void absentAllBumpers() {
        Event telemagazin1 = new Event(1, "", "00:00:00:00", "00:01:00:00", null, null, "0", "Telemagazin1", "", "");
        Event telemagazin2 = new Event(2, "", "00:00:00:00", "00:01:00:00", null, null, "0", "Telemagazin2", "", "");
        Event telemagazin3 = new Event(3, "", "00:00:00:00", "00:01:00:00", null, null, "0", "Telemagazin3", "", "");
        Event telemagazin4 = new Event(4, "", "00:00:00:00", "00:01:00:00", null, null, "0", "Telemagazin4", "", "");

        List<Event> telemagazinBlock = new ArrayList<>();
        telemagazinBlock.add(telemagazin1);
        telemagazinBlock.add(telemagazin2);
        telemagazinBlock.add(telemagazin3);
        telemagazinBlock.add(telemagazin4);
        telemagazinBloksList.add(telemagazinBlock);

        Map<String, Integer> result = telemagazinBumpers.checkFeature(plKeeperMock);
        assertEquals(2, result.get(FEATURE_NAME).intValue());
    }

}