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

public class ReklamaInsideHourTest {
    private static final String PATH_TO_PROPERTY = "./properties/PLChecker/checkerservice/ReklamaInsideHour.properties";

    private static String FEATURE_NAME;
    private static ReklamaInsideHour reklamaInsideHour;

    private List<List<Event>> allReklamBloksList;
    private PLKeeper plKeeperMock;

    @BeforeClass
    public static void initClass() throws IOException {
        Properties properties = new Properties();
        properties.load(new FileReader(PATH_TO_PROPERTY));
        FEATURE_NAME = properties.getProperty("FEATURE_NAME");
        reklamaInsideHour = new ReklamaInsideHour();
    }

    @Before
    public void init() {
        allReklamBloksList = new ArrayList<>();
        plKeeperMock = mock(PLKeeper.class);
        when(plKeeperMock.getAllReklamBloksList()).thenReturn(allReklamBloksList);
    }

    @Test
    public void reklamaInsideHour() {
        Event reklama1 = new Event(1, "", "00:00:00:00", "00:02:00:00", null, null, "0", "Reklama1", "", "");
        Event reklama2 = new Event(2, "", "00:02:00:00", "00:02:00:00", null, null, "0", "Reklama2", "", "");

        List<Event> reklamaBlock = new ArrayList<>();
        reklamaBlock.add(reklama1);
        reklamaBlock.add(reklama2);
        allReklamBloksList.add(reklamaBlock);

        Map<String, Integer> result = reklamaInsideHour.checkFeature(plKeeperMock);
        assertEquals(0, result.get(FEATURE_NAME).intValue());
    }

    @Test
    public void reklamaClimbsOutAtAnotherHour() {
        Event reklama1 = new Event(1, "", "00:57:00:00", "00:02:00:00", null, null, "0", "Reklama1", "", "");
        Event reklama2 = new Event(2, "", "00:59:00:00", "00:02:00:00", null, null, "0", "Reklama2", "", "");

        List<Event> reklamaBlock = new ArrayList<>();
        reklamaBlock.add(reklama1);
        reklamaBlock.add(reklama2);
        allReklamBloksList.add(reklamaBlock);

        Map<String, Integer> result = reklamaInsideHour.checkFeature(plKeeperMock);
        assertEquals(1, result.get(FEATURE_NAME).intValue());
    }

}