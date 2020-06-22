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

public class GapBetweenReklamBlocksTest {

    private static final String PATH_TO_PROPERTY = "./properties/PLChecker/checkerservice/GapBetweenReklamBlocks.properties";

    private static String FEATURE_NAME;
    private static GapBetweenReklamBlocks gapBetweenReklamBlocks;
    private static TimeCode GAP_BETWEEN_REKLAM_BLOCKS;

//    private List<Event> allEventsList;
    private List<List<Event>> allReklamBloksList;
    private PLKeeper plKeeperMock;

    @BeforeClass
    public static void initClass() throws IOException {
        Properties properties = new Properties();
        properties.load(new FileReader(PATH_TO_PROPERTY));
        FEATURE_NAME = properties.getProperty("FEATURE_NAME");
        GAP_BETWEEN_REKLAM_BLOCKS = new TimeCode(properties.getProperty("GAP_BETWEEN_REKLAM_BLOCKS"));
        gapBetweenReklamBlocks = new GapBetweenReklamBlocks();
    }

    @Before
    public void init() {
        allReklamBloksList = new ArrayList<>();

        List<Event> reklamBlock1 = new ArrayList<>();
        List<Event> reklamBlock2 = new ArrayList<>();
        allReklamBloksList.add(reklamBlock1);
        allReklamBloksList.add(reklamBlock2);

        plKeeperMock = mock(PLKeeper.class);
        when(plKeeperMock.getAllReklamBloksList()).thenReturn(allReklamBloksList);
    }

    @Test
    public void gabBetweenReklamBlocksMoreThenRequired() {
        String startTime = "00:00:00:00";
        String duration = "00:01:00:00";

        String startTimeSecondBlock = new TimeCode(startTime).changeToNFramesConsideringMidnight(new TimeCode(duration).getFrames() + GAP_BETWEEN_REKLAM_BLOCKS.getFrames() + 1).toString();

        Event reklama1 = new Event(1, "", startTime, duration, null, null, "0", "", "", "");
        Event reklama2 = new Event(1, "", startTimeSecondBlock, duration, null, null, "0", "", "", "");

        allReklamBloksList.get(0).add(reklama1);
        allReklamBloksList.get(1).add(reklama2);

        Map<String, Integer> result = gapBetweenReklamBlocks.checkFeature(plKeeperMock);
        assertEquals(0, result.get(FEATURE_NAME).intValue());
    }

    @Test
    public void gabBetweenReklamBlocksEqRequired() {
        String startTime = "00:00:00:00";
        String duration = "00:01:00:00";

        String startTimeSecondBlock = new TimeCode(startTime).changeToNFramesConsideringMidnight(new TimeCode(duration).getFrames() + GAP_BETWEEN_REKLAM_BLOCKS.getFrames() + 1).toString();

        Event reklama1 = new Event(1, "", startTime, duration, null, null, "0", "", "", "");
        Event reklama2 = new Event(1, "", startTimeSecondBlock, duration, null, null, "0", "", "", "");

        allReklamBloksList.get(0).add(reklama1);
        allReklamBloksList.get(1).add(reklama2);

        Map<String, Integer> result = gapBetweenReklamBlocks.checkFeature(plKeeperMock);
        assertEquals(0, result.get(FEATURE_NAME).intValue());
    }

    @Test
    public void gabBetweenReklamBlocksLessThenRequired() {
        String startTime = "00:00:00:00";
        String duration = "00:01:00:00";

        String startTimeSecondBlock = new TimeCode(startTime).changeToNFramesConsideringMidnight(new TimeCode(duration).getFrames() + GAP_BETWEEN_REKLAM_BLOCKS.getFrames() - 1).toString();

        Event reklama1 = new Event(1, "", startTime, duration, null, null, "0", "", "", "");
        Event reklama2 = new Event(1, "", startTimeSecondBlock, duration, null, null, "0", "", "", "");

        allReklamBloksList.get(0).add(reklama1);
        allReklamBloksList.get(1).add(reklama2);

        Map<String, Integer> result = gapBetweenReklamBlocks.checkFeature(plKeeperMock);
        assertEquals(1, result.get(FEATURE_NAME).intValue());
    }
}