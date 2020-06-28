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

public class PgmAmountTest {
    private static final String PATH_TO_PROPERTY = "./properties/PLChecker/checkerservice/PgmAmount.properties";

    private static String FEATURE_NAME;
    private static String LOGO_V2 = "logo v2";
    private static String LOGO_TRAUR = "logo Traur";
    private static String SVECHA = "Svecha";
    private static String ZNAK_KRUG_12 = "znak krug 12";
    private static String ZNAK_KRUG_16 = "znak krug 16";
    private static String ZNAK_KRUG_18 = "znak krug 18";
    private static String SKLEROTIC = "Dali HS-CriminalMinds";
    private static String TICKER = "Ticker 1";
    private static String PGM = "PGM";
    private static String PGM43 = "PGM 4x3";
    private static PgmAmount pgmAmount;

    private List<Event> allEventsList;
    private PLKeeper plKeeperMock;
    private static String allFormats;

    @BeforeClass
    public static void initClass() throws IOException {
        Properties properties = new Properties();
        properties.load(new FileReader(PATH_TO_PROPERTY));
        FEATURE_NAME = properties.getProperty("FEATURE_NAME");
        allFormats = ", " + LOGO_V2 + ", " + LOGO_TRAUR + ", " + SVECHA + ", " + ZNAK_KRUG_12 + ", " + ZNAK_KRUG_16 + ", "
                + ZNAK_KRUG_18 + ", " + SKLEROTIC + ", " + TICKER;
        pgmAmount = new PgmAmount();
    }

    @Before
    public void init() {
        allEventsList = new ArrayList<>();
        plKeeperMock = mock(PLKeeper.class);
        when(plKeeperMock.getAllEventsList()).thenReturn(allEventsList);
    }

    @Test
    public void withoutErrorsPGM() {
        Event event = new Event(1, "", "00:00:00:00", "00:01:00:00", null, null, "0", "HS-CriminalMinds", PGM + allFormats, "");
        allEventsList.add(event);
        Map<String, Integer> result = pgmAmount.checkFeature(plKeeperMock);
        assertEquals(0, result.get(FEATURE_NAME).intValue());
    }

    @Test
    public void withoutErrorsPGM43() {
        Event event = new Event(1, "", "00:00:00:00", "00:01:00:00", null, null, "0", "HS-CriminalMinds", PGM43 + allFormats, "");
        allEventsList.add(event);
        Map<String, Integer> result = pgmAmount.checkFeature(plKeeperMock);
        assertEquals(0, result.get(FEATURE_NAME).intValue());
    }

    @Test
    public void errorsPgmPgm() {
        Event event = new Event(1, "", "00:00:00:00", "00:01:00:00", null, null, "0", "HS-CriminalMinds", PGM + ", " + PGM + allFormats, "");
        allEventsList.add(event);
        Map<String, Integer> result = pgmAmount.checkFeature(plKeeperMock);
        assertEquals(1, result.get(FEATURE_NAME).intValue());
    }

    @Test
    public void errorsPgmPgm43() {
        Event event = new Event(1, "", "00:00:00:00", "00:01:00:00", null, null, "0", "HS-CriminalMinds", PGM + ", " + PGM43 + allFormats, "");
        allEventsList.add(event);
        Map<String, Integer> result = pgmAmount.checkFeature(plKeeperMock);
        assertEquals(1, result.get(FEATURE_NAME).intValue());
    }

    @Test
    public void errorsPgm43Pgm43() {
        Event event = new Event(1, "", "00:00:00:00", "00:01:00:00", null, null, "0", "HS-CriminalMinds", PGM43 + ", " + PGM43 + allFormats, "");
        allEventsList.add(event);
        Map<String, Integer> result = pgmAmount.checkFeature(plKeeperMock);
        assertEquals(1, result.get(FEATURE_NAME).intValue());
    }

    @Test
    public void errorsWithoutPgm() {
        Event event = new Event(1, "", "00:00:00:00", "00:01:00:00", null, null, "0", "HS-CriminalMinds", allFormats, "");
        allEventsList.add(event);
        Map<String, Integer> result = pgmAmount.checkFeature(plKeeperMock);
        assertEquals(1, result.get(FEATURE_NAME).intValue());
    }
}