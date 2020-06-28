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

public class ReklamaBumpersTest {

    private static final String PATH_TO_PROPERTY = "./properties/PLChecker/checkerservice/ReklamaBumpers.properties";

    private static String FEATURE_NAME;
    private static ReklamaBumpers reklamaBumpers;

    private List<List<Event>> allReklamBloksList;
    private PLKeeper plKeeperMock;

    private final String BALLS = "BUMPER_BALLS_DaVinci_16x9";
    private final String BUTTERFLIES = "BUMPER_BUTTERFLIES_DaVinci_16x9";
    private final String MACAROONS = "BUMPER_MACAROONS_DaVinci_16x9";
    private final String WATERMELONS = "BUMPER_WATERMELONS_DaVinci_16x9";
    private final String UMBRELLAS = "BUMPER_UMBRELLAS_DaVinci_16x9";


    @BeforeClass
    public static void initClass() throws IOException {
        Properties properties = new Properties();
        properties.load(new FileReader(PATH_TO_PROPERTY));
        FEATURE_NAME = properties.getProperty("FEATURE_NAME");
        reklamaBumpers = new ReklamaBumpers();
    }

    @Before
    public void init() {
        allReklamBloksList = new ArrayList<>();
        plKeeperMock = mock(PLKeeper.class);
        when(plKeeperMock.getAllReklamBloksList()).thenReturn(allReklamBloksList);
    }

    @Test
    public void withoutErrors() {
        Event bumperFirst = new Event(1, "", "00:00:00:00", "00:01:00:00", null, null, "0", BALLS, "", "");
        Event reklama1 = new Event(2, "", "00:00:00:00", "00:01:00:00", null, null, "0", "Reklama1", "", "");
        Event reklama2 = new Event(3, "", "00:00:00:00", "00:01:00:00", null, null, "0", "Reklama2", "", "");
        Event bumperLast = new Event(4, "", "00:00:00:00", "00:01:00:00", null, null, "0", BUTTERFLIES, "", "");

        List<Event> reklamBlock = new ArrayList<>();
        reklamBlock.add(bumperFirst);
        reklamBlock.add(reklama1);
        reklamBlock.add(reklama2);
        reklamBlock.add(bumperLast);
        allReklamBloksList.add(reklamBlock);

        Map<String, Integer> result = reklamaBumpers.checkFeature(plKeeperMock);
        assertEquals(0, result.get(FEATURE_NAME).intValue());
    }

    @Test
    public void absentLastBumper() {
        Event bumperFirst = new Event(1, "", "00:00:00:00", "00:01:00:00", null, null, "0", MACAROONS, "", "");
        Event reklama1 = new Event(2, "", "00:00:00:00", "00:01:00:00", null, null, "0", "Reklama1", "", "");
        Event reklama2 = new Event(3, "", "00:00:00:00", "00:01:00:00", null, null, "0", "Reklama2", "", "");
        Event reklama3 = new Event(4, "", "00:00:00:00", "00:01:00:00", null, null, "0", "Reklama3", "", "");

        List<Event> reklamBlock = new ArrayList<>();
        reklamBlock.add(bumperFirst);
        reklamBlock.add(reklama1);
        reklamBlock.add(reklama2);
        reklamBlock.add(reklama3);
        allReklamBloksList.add(reklamBlock);

        Map<String, Integer> result = reklamaBumpers.checkFeature(plKeeperMock);
        assertEquals(1, result.get(FEATURE_NAME).intValue());
    }

    @Test
    public void absentFirstBumper() {
        Event reklama1 = new Event(1, "", "00:00:00:00", "00:01:00:00", null, null, "0", "Reklama1", "", "");
        Event reklama2 = new Event(2, "", "00:00:00:00", "00:01:00:00", null, null, "0", "Reklama2", "", "");
        Event reklama3 = new Event(3, "", "00:00:00:00", "00:01:00:00", null, null, "0", "Reklama3", "", "");
        Event bumperLast = new Event(4, "", "00:00:00:00", "00:01:00:00", null, null, "0", BUTTERFLIES, "", "");

        List<Event> reklamBlock = new ArrayList<>();
        reklamBlock.add(reklama1);
        reklamBlock.add(reklama2);
        reklamBlock.add(reklama3);
        reklamBlock.add(bumperLast);
        allReklamBloksList.add(reklamBlock);

        Map<String, Integer> result = reklamaBumpers.checkFeature(plKeeperMock);
        assertEquals(1, result.get(FEATURE_NAME).intValue());
    }

    @Test
    public void absentAllBumpers() {
        Event reklama1 = new Event(1, "", "00:00:00:00", "00:01:00:00", null, null, "0", "Reklama1", "", "");
        Event reklama2 = new Event(2, "", "00:00:00:00", "00:01:00:00", null, null, "0", "Reklama2", "", "");
        Event reklama3 = new Event(3, "", "00:00:00:00", "00:01:00:00", null, null, "0", "Reklama3", "", "");
        Event reklama4 = new Event(4, "", "00:00:00:00", "00:01:00:00", null, null, "0", "Reklama4", "", "");

        List<Event> reklamBlock = new ArrayList<>();
        reklamBlock.add(reklama1);
        reklamBlock.add(reklama2);
        reklamBlock.add(reklama3);
        reklamBlock.add(reklama4);
        allReklamBloksList.add(reklamBlock);

        Map<String, Integer> result = reklamaBumpers.checkFeature(plKeeperMock);
        assertEquals(2, result.get(FEATURE_NAME).intValue());
    }
}