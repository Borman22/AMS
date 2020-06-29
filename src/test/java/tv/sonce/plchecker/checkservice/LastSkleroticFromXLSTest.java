package tv.sonce.plchecker.checkservice;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import tv.sonce.plchecker.PLKeeper;
import tv.sonce.plchecker.entity.Event;
import tv.sonce.utils.ExcelParser;
import tv.sonce.utils.TimeCode;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LastSkleroticFromXLSTest {

    private static final String PATH_TO_PROPERTY = "./properties/PLChecker/checkerservice/LastSkleroticFromXLS.properties";

    private static LastSkleroticFromXLS lastSkleroticFromXLS;

    private PLKeeper plKeeperMock;
    private ExcelParser excelParserMock;
    private List<List<Event>> allProgramsList;
    private List<List<String>> excelSheet;

    private static String FEATURE_NAME;
    private static int SHIFT_BTW_XLS_AND_XML_NUMS;
    private static int CELL_NUM_FOR_SKLEROTIC;
    private static String MIN_SUBCLIP_LENGTH_FOR_SKLEROTIC_STR;

    private final String SKLEROTIC_IN_EXCEL_FILE = "Мислити як вбивця";
    private final String SKLEROTIC_IN_PLAYLIST_FILE = "Dali HS-CriminalMinds";
    private final String SKLEROTIC_IN_PLAYLIST_FILE_INCORRECT = "Dali HS-CSI NewYork";



    @BeforeClass
    public static void initClass() throws IOException {
        Properties properties = new Properties();
        properties.load(new FileReader(PATH_TO_PROPERTY));
        FEATURE_NAME = properties.getProperty("FEATURE_NAME");
        SHIFT_BTW_XLS_AND_XML_NUMS = Integer.parseInt(properties.getProperty("SHIFT_BTW_XLS_AND_XML_NUMS"));
        CELL_NUM_FOR_SKLEROTIC = Integer.parseInt(properties.getProperty("CELL_NUM_FOR_SKLEROTIC"));
        MIN_SUBCLIP_LENGTH_FOR_SKLEROTIC_STR = properties.getProperty("MIN_SUBCLIP_LENGTH_FOR_SKLEROTIC");
    }

    @Before
    public void init() {
        allProgramsList = new ArrayList<>();
        excelSheet = new ArrayList<>();

        plKeeperMock = mock(PLKeeper.class);
        excelParserMock = mock(ExcelParser.class);

        when(plKeeperMock.getAllProgramsList()).thenReturn(allProgramsList);
        when(excelParserMock.getSheet()).thenReturn(excelSheet);

        lastSkleroticFromXLS = new LastSkleroticFromXLS(excelParserMock);
    }

    @Test
    public void withoutErrors() {
        TimeCode MIN_SUBCLIP_LENGTH_FOR_SKLEROTIC = new TimeCode(MIN_SUBCLIP_LENGTH_FOR_SKLEROTIC_STR);
        List<Event> program = new ArrayList<>();
        Event firstSubclip = new Event(1, "", "00:00:00:00", "00:01:00:00", null, null, "0", "", "", "");
        Event lastSubclip = new Event(2, "", "00:01:00:00", MIN_SUBCLIP_LENGTH_FOR_SKLEROTIC.getDelimitedStr(), null, null, "0", "", SKLEROTIC_IN_PLAYLIST_FILE, "");
        program.add(firstSubclip);
        program.add(lastSubclip);
        allProgramsList.add(program);

        int numberOfImportantExcelRow = program.size() + SHIFT_BTW_XLS_AND_XML_NUMS;    // строки и столбцы в excel начинаются считаться с 0
        for(int i = 0; i < numberOfImportantExcelRow; i++)
            excelSheet.add(new ArrayList());    // not important Excel rows

        List<String> importantExcelRow = new ArrayList();
        for(int i = 0; i < CELL_NUM_FOR_SKLEROTIC; i++)
            importantExcelRow.add("");
        importantExcelRow.add(SKLEROTIC_IN_EXCEL_FILE);
        excelSheet.add(importantExcelRow);

        Map<String, Integer> result = lastSkleroticFromXLS.checkFeature(plKeeperMock);
        assertEquals(0, result.get(FEATURE_NAME).intValue());
    }

    @Test
    public void moreThenMinSubclipLength() {
        TimeCode MIN_SUBCLIP_LENGTH_FOR_SKLEROTIC = new TimeCode(MIN_SUBCLIP_LENGTH_FOR_SKLEROTIC_STR);
        List<Event> program = new ArrayList<>();
        Event firstSubclip = new Event(1, "", "00:00:00:00", "00:01:00:00", null, null, "0", "", "", "");
        Event lastSubclip = new Event(2, "", "00:01:00:00", MIN_SUBCLIP_LENGTH_FOR_SKLEROTIC.changeToNFramesConsideringMidnight(1).getDelimitedStr(), null, null, "0", "", SKLEROTIC_IN_PLAYLIST_FILE, "");
        program.add(firstSubclip);
        program.add(lastSubclip);
        allProgramsList.add(program);

        int numberOfImportantExcelRow = program.size() + SHIFT_BTW_XLS_AND_XML_NUMS;    // строки и столбцы в excel начинаются считаться с 0
        for(int i = 0; i < numberOfImportantExcelRow; i++)
            excelSheet.add(new ArrayList());    // not important Excel rows

        List<String> importantExcelRow = new ArrayList();
        for(int i = 0; i < CELL_NUM_FOR_SKLEROTIC; i++)
            importantExcelRow.add("");
        importantExcelRow.add(SKLEROTIC_IN_EXCEL_FILE);
        excelSheet.add(importantExcelRow);

        Map<String, Integer> result = lastSkleroticFromXLS.checkFeature(plKeeperMock);
        assertEquals(0, result.get(FEATURE_NAME).intValue());
    }

    @Test
    public void moreThenMinSubclipLengthAndWithoutSklerotic() {
        TimeCode MIN_SUBCLIP_LENGTH_FOR_SKLEROTIC = new TimeCode(MIN_SUBCLIP_LENGTH_FOR_SKLEROTIC_STR);
        List<Event> program = new ArrayList<>();
        Event firstSubclip = new Event(1, "", "00:00:00:00", "00:01:00:00", null, null, "0", "", "", "");
        Event lastSubclip = new Event(2, "", "00:01:00:00", MIN_SUBCLIP_LENGTH_FOR_SKLEROTIC.changeToNFramesConsideringMidnight(1).getDelimitedStr(), null, null, "0", "", "", "");
        program.add(firstSubclip);
        program.add(lastSubclip);
        allProgramsList.add(program);

        int numberOfImportantExcelRow = program.size() + SHIFT_BTW_XLS_AND_XML_NUMS;    // строки и столбцы в excel начинаются считаться с 0
        for(int i = 0; i < numberOfImportantExcelRow; i++)
            excelSheet.add(new ArrayList());    // not important Excel rows

        List<String> importantExcelRow = new ArrayList();
        for(int i = 0; i < CELL_NUM_FOR_SKLEROTIC; i++)
            importantExcelRow.add("");
        importantExcelRow.add(SKLEROTIC_IN_EXCEL_FILE);
        excelSheet.add(importantExcelRow);

        Map<String, Integer> result = lastSkleroticFromXLS.checkFeature(plKeeperMock);
        assertEquals(1, result.get(FEATURE_NAME).intValue());
    }

    @Test
    public void lessThenMinSubclipLength() {
        TimeCode MIN_SUBCLIP_LENGTH_FOR_SKLEROTIC = new TimeCode(MIN_SUBCLIP_LENGTH_FOR_SKLEROTIC_STR);
        List<Event> program = new ArrayList<>();
        Event firstSubclip = new Event(1, "", "00:00:00:00", "00:01:00:00", null, null, "0", "", "", "");
        Event lastSubclip = new Event(2, "", "00:01:00:00", MIN_SUBCLIP_LENGTH_FOR_SKLEROTIC.changeToNFramesConsideringMidnight(-1).getDelimitedStr(), null, null, "0", "", SKLEROTIC_IN_PLAYLIST_FILE, "");
        program.add(firstSubclip);
        program.add(lastSubclip);
        allProgramsList.add(program);

        int numberOfImportantExcelRow = program.size() + SHIFT_BTW_XLS_AND_XML_NUMS;    // строки и столбцы в excel начинаются считаться с 0
        for(int i = 0; i < numberOfImportantExcelRow; i++)
            excelSheet.add(new ArrayList());    // not important Excel rows

        List<String> importantExcelRow = new ArrayList();
        for(int i = 0; i < CELL_NUM_FOR_SKLEROTIC; i++)
            importantExcelRow.add("");
        importantExcelRow.add(SKLEROTIC_IN_EXCEL_FILE);
        excelSheet.add(importantExcelRow);

        Map<String, Integer> result = lastSkleroticFromXLS.checkFeature(plKeeperMock);
        assertEquals(1, result.get(FEATURE_NAME).intValue());
    }

    @Test
    public void lessThenMinSubclipLengthAndWithoutSklerotic() {
        TimeCode MIN_SUBCLIP_LENGTH_FOR_SKLEROTIC = new TimeCode(MIN_SUBCLIP_LENGTH_FOR_SKLEROTIC_STR);
        List<Event> program = new ArrayList<>();
        Event firstSubclip = new Event(1, "", "00:00:00:00", "00:01:00:00", null, null, "0", "", "", "");
        Event lastSubclip = new Event(2, "", "00:01:00:00", MIN_SUBCLIP_LENGTH_FOR_SKLEROTIC.changeToNFramesConsideringMidnight(-1).getDelimitedStr(), null, null, "0", "", "", "");
        program.add(firstSubclip);
        program.add(lastSubclip);
        allProgramsList.add(program);

        int numberOfImportantExcelRow = program.size() + SHIFT_BTW_XLS_AND_XML_NUMS;    // строки и столбцы в excel начинаются считаться с 0
        for(int i = 0; i < numberOfImportantExcelRow; i++)
            excelSheet.add(new ArrayList());    // not important Excel rows

        List<String> importantExcelRow = new ArrayList();
        for(int i = 0; i < CELL_NUM_FOR_SKLEROTIC; i++)
            importantExcelRow.add("");
        importantExcelRow.add(SKLEROTIC_IN_EXCEL_FILE);
        excelSheet.add(importantExcelRow);

        Map<String, Integer> result = lastSkleroticFromXLS.checkFeature(plKeeperMock);
        assertEquals(0, result.get(FEATURE_NAME).intValue());
    }

    @Test
    public void incorrectSklerotic() {
        TimeCode MIN_SUBCLIP_LENGTH_FOR_SKLEROTIC = new TimeCode(MIN_SUBCLIP_LENGTH_FOR_SKLEROTIC_STR);
        List<Event> program = new ArrayList<>();
        Event firstSubclip = new Event(1, "", "00:00:00:00", "00:01:00:00", null, null, "0", "", "", "");
        Event lastSubclip = new Event(2, "", "00:01:00:00", MIN_SUBCLIP_LENGTH_FOR_SKLEROTIC.getDelimitedStr(), null, null, "0", "", SKLEROTIC_IN_PLAYLIST_FILE_INCORRECT, "");
        program.add(firstSubclip);
        program.add(lastSubclip);
        allProgramsList.add(program);

        int numberOfImportantExcelRow = program.size() + SHIFT_BTW_XLS_AND_XML_NUMS;    // строки и столбцы в excel начинаются считаться с 0
        for(int i = 0; i < numberOfImportantExcelRow; i++)
            excelSheet.add(new ArrayList());    // not important Excel rows

        List<String> importantExcelRow = new ArrayList();
        for(int i = 0; i < CELL_NUM_FOR_SKLEROTIC; i++)
            importantExcelRow.add("");
        importantExcelRow.add(SKLEROTIC_IN_EXCEL_FILE);
        excelSheet.add(importantExcelRow);

        Map<String, Integer> result = lastSkleroticFromXLS.checkFeature(plKeeperMock);
        assertEquals(1, result.get(FEATURE_NAME).intValue());
    }

    @Test
    public void tooManyCorrectSklerotics() {
        TimeCode MIN_SUBCLIP_LENGTH_FOR_SKLEROTIC = new TimeCode(MIN_SUBCLIP_LENGTH_FOR_SKLEROTIC_STR);
        List<Event> program = new ArrayList<>();
        Event firstSubclip = new Event(1, "", "00:00:00:00", "00:01:00:00", null, null, "0", "", "", "");
        Event lastSubclip = new Event(2, "", "00:01:00:00", MIN_SUBCLIP_LENGTH_FOR_SKLEROTIC.getDelimitedStr(), null, null, "0", "", SKLEROTIC_IN_PLAYLIST_FILE + ", " + SKLEROTIC_IN_PLAYLIST_FILE, "");
        program.add(firstSubclip);
        program.add(lastSubclip);
        allProgramsList.add(program);

        int numberOfImportantExcelRow = program.size() + SHIFT_BTW_XLS_AND_XML_NUMS;    // строки и столбцы в excel начинаются считаться с 0
        for(int i = 0; i < numberOfImportantExcelRow; i++)
            excelSheet.add(new ArrayList());    // not important Excel rows

        List<String> importantExcelRow = new ArrayList();
        for(int i = 0; i < CELL_NUM_FOR_SKLEROTIC; i++)
            importantExcelRow.add("");
        importantExcelRow.add(SKLEROTIC_IN_EXCEL_FILE);
        excelSheet.add(importantExcelRow);

        Map<String, Integer> result = lastSkleroticFromXLS.checkFeature(plKeeperMock);
        assertEquals(1, result.get(FEATURE_NAME).intValue());
    }

    @Test
    public void tooManyDifferentSklerotics() {
        TimeCode MIN_SUBCLIP_LENGTH_FOR_SKLEROTIC = new TimeCode(MIN_SUBCLIP_LENGTH_FOR_SKLEROTIC_STR);
        List<Event> program = new ArrayList<>();
        Event firstSubclip = new Event(1, "", "00:00:00:00", "00:01:00:00", null, null, "0", "", "", "");
        Event lastSubclip = new Event(2, "", "00:01:00:00", MIN_SUBCLIP_LENGTH_FOR_SKLEROTIC.getDelimitedStr(), null, null, "0", "", SKLEROTIC_IN_PLAYLIST_FILE + ", " + SKLEROTIC_IN_PLAYLIST_FILE_INCORRECT, "");
        program.add(firstSubclip);
        program.add(lastSubclip);
        allProgramsList.add(program);

        int numberOfImportantExcelRow = program.size() + SHIFT_BTW_XLS_AND_XML_NUMS;    // строки и столбцы в excel начинаются считаться с 0
        for(int i = 0; i < numberOfImportantExcelRow; i++)
            excelSheet.add(new ArrayList());    // not important Excel rows

        List<String> importantExcelRow = new ArrayList();
        for(int i = 0; i < CELL_NUM_FOR_SKLEROTIC; i++)
            importantExcelRow.add("");
        importantExcelRow.add(SKLEROTIC_IN_EXCEL_FILE);
        excelSheet.add(importantExcelRow);

        Map<String, Integer> result = lastSkleroticFromXLS.checkFeature(plKeeperMock);
        assertEquals(1, result.get(FEATURE_NAME).intValue());
    }

    @Test
    public void veryShortExcelFile() {
        TimeCode MIN_SUBCLIP_LENGTH_FOR_SKLEROTIC = new TimeCode(MIN_SUBCLIP_LENGTH_FOR_SKLEROTIC_STR);
        List<Event> program = new ArrayList<>();
        Event firstSubclip = new Event(1, "", "00:00:00:00", "00:01:00:00", null, null, "0", "", "", "");
        Event lastSubclip = new Event(2, "", "00:01:00:00", MIN_SUBCLIP_LENGTH_FOR_SKLEROTIC.getDelimitedStr(), null, null, "0", "", "", "");
        program.add(firstSubclip);
        program.add(lastSubclip);
        allProgramsList.add(program);

        int numberOfImportantExcelRow = program.size() + SHIFT_BTW_XLS_AND_XML_NUMS - 1;    // строки и столбцы в excel начинаются считаться с 0
        for(int i = 0; i < numberOfImportantExcelRow; i++)
            excelSheet.add(new ArrayList());    // not important Excel rows

        List<String> importantExcelRow = new ArrayList();
        for(int i = 0; i < CELL_NUM_FOR_SKLEROTIC; i++)
            importantExcelRow.add("");
        importantExcelRow.add("");
        excelSheet.add(importantExcelRow);

        Map<String, Integer> result = lastSkleroticFromXLS.checkFeature(plKeeperMock);
        assertEquals(1, result.get(FEATURE_NAME).intValue());
    }
}