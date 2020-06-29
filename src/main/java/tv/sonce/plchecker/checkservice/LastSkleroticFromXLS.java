package tv.sonce.plchecker.checkservice;

import tv.sonce.plchecker.PLKeeper;
import tv.sonce.plchecker.entity.Event;
import tv.sonce.plchecker.entity.ProgramDescription;
import tv.sonce.utils.ExcelParser;
import tv.sonce.utils.JsonReader;
import tv.sonce.utils.TimeCode;

import java.io.File;
import java.util.*;

public class LastSkleroticFromXLS extends AbstractParticularFeatureChecker {

    private final String PATH_TO_PROPERTY = "./properties/PLChecker/checkerservice/LastSkleroticFromXLS.properties";
    private int numOfErrors = 0;
    private ExcelParser excelParser = null;

    public LastSkleroticFromXLS() {}

    // constructor for unit tests
    public LastSkleroticFromXLS(ExcelParser excelParser) { this.excelParser = excelParser;}

    @Override
    public Map<String, Integer> checkFeature(PLKeeper plKeeper) {

        Properties properties = getProperties(PATH_TO_PROPERTY);

        final String PATH_TO_ALL_PROGRAMS_DESCRIPTION = properties.getProperty("PATH_TO_ALL_PROGRAMS_DESCRIPTION");
        final String PATH_TO_EXCEL_FOLDER = properties.getProperty("PATH_TO_EXCEL_FOLDER");
        final String ERROR_MESSAGE = properties.getProperty("ERROR_MESSAGE");
        final String ERROR_MESSAGE_SUPERFLUOUS_SKLEROTIC = properties.getProperty("ERROR_MESSAGE_SUPERFLUOUS_SKLEROTIC");
        final String ERROR_MESSAGE_EXCEL_FILE_NOT_FOUND = properties.getProperty("ERROR_MESSAGE_EXCEL_FILE_NOT_FOUND");
        final String ERROR_MESSAGE_EXCEL_FILE_NOT_MATCH_WITH_PLAYLIST = properties.getProperty("ERROR_MESSAGE_EXCEL_FILE_NOT_MATCH_WITH_PLAYLIST");
        final int SHIFT_BTW_XLS_AND_XML_NUMS = Integer.parseInt(properties.getProperty("SHIFT_BTW_XLS_AND_XML_NUMS"));
        final int CELL_NUM_FOR_SKLEROTIC = Integer.parseInt(properties.getProperty("CELL_NUM_FOR_SKLEROTIC")); // в какой по счету ячейке лежит склеротик в xls

        final String SKLEROTIC_TEMPLATE = properties.getProperty("SKLEROTIC_TEMPLATE");
        final String EMPTY_SKLEROTIC = properties.getProperty("EMPTY_SKLEROTIC");
        final TimeCode MIN_SUBCLIP_LENGTH_FOR_SKLEROTIC = new TimeCode(properties.getProperty("MIN_SUBCLIP_LENGTH_FOR_SKLEROTIC"));

        JsonReader<ProgramDescription[]> jsonReader = new JsonReader<>(PATH_TO_ALL_PROGRAMS_DESCRIPTION, new ProgramDescription[]{});
        ProgramDescription[] allProgramsDescriptions = jsonReader.getContent();



        List<List<Event>> allProgramsList = plKeeper.getAllProgramsList();
        List<Event> theLastestProgram = allProgramsList.get(allProgramsList.size() - 1);
        Event theLatestSubclip = theLastestProgram.get(theLastestProgram.size() - 1);

        if(excelParser == null) {   // constructor without parameters was used
            String absoluteExcelFilePath = getAbsoluteExcelFilePath(plKeeper, PATH_TO_EXCEL_FOLDER);
            if (absoluteExcelFilePath.equals("")) {     // file "Doski" not found
                theLatestSubclip.errors.add(ERROR_MESSAGE_EXCEL_FILE_NOT_FOUND);
                numOfErrors++;
                return returnResult();
            }
            excelParser = new ExcelParser(absoluteExcelFilePath, 0);
        }

        // file "Doski" not match with playlist
        int rowNumberInXls = theLatestSubclip.getNumberOfEvent() + SHIFT_BTW_XLS_AND_XML_NUMS;
        if (rowNumberInXls >= excelParser.getSheet().size()) {       //  instead of ArrayIndexOutOfBoundsException
            theLatestSubclip.errors.add(ERROR_MESSAGE_EXCEL_FILE_NOT_MATCH_WITH_PLAYLIST);
            numOfErrors++;
            return returnResult();
        }

        String skleroticInExcel = excelParser.getSheet().get(rowNumberInXls).get(CELL_NUM_FOR_SKLEROTIC);
        String skleroticName = findSklerotic(skleroticInExcel, allProgramsDescriptions);

        // Если длинна субклипа маленькая или склеротика не существует, то убедимся, что никакой склеротик не стоит
        if (theLatestSubclip.getDuration() < MIN_SUBCLIP_LENGTH_FOR_SKLEROTIC.getFrames() || skleroticName.equals(EMPTY_SKLEROTIC)) {
            if(howMuchIsThereSklerotic(SKLEROTIC_TEMPLATE, theLatestSubclip.getFormat()) != 0) {
                theLatestSubclip.errors.add(ERROR_MESSAGE_SUPERFLUOUS_SKLEROTIC);
                numOfErrors++;
            }
        } else {  // Если длинна субклипа большая и склеротик существует, то убедимся, что он стоит
            if (howMuchIsThereSklerotic(skleroticName, theLatestSubclip.getFormat()) != 1 || howMuchIsThereSklerotic(SKLEROTIC_TEMPLATE, theLatestSubclip.getFormat()) != 1) {
                theLatestSubclip.errors.add(ERROR_MESSAGE + skleroticName);
                numOfErrors++;
            }
        }
        return returnResult();
    }

    private String getAbsoluteExcelFilePath(PLKeeper plKeeper, String PATH_TO_EXCEL_FOLDER) {
        String currentDate = plKeeper.getAllEventsList().get(0).getDate();  // 2020-03-22
        String tempExcelFileName = currentDate.substring(8, 10) + '.' + currentDate.substring(5, 7) + '.' + currentDate.substring(0, 4);
        String absoluteExcelFilePath = "";

        File[] folderEntries = new File(PATH_TO_EXCEL_FOLDER).listFiles();
        if (folderEntries != null)
            for (File folderEntry : folderEntries) {
                if (folderEntry.getName().startsWith(tempExcelFileName)) {
                    absoluteExcelFilePath = folderEntry.getAbsolutePath();
                    break;
                }
            }
        return absoluteExcelFilePath;
    }

    private int howMuchIsThereSklerotic(String skleroticName, String[] formats) {
        return (int) Arrays.stream(formats).filter(currentFormat -> currentFormat.matches(skleroticName)).count();
    }

    private String findSklerotic(String skleroticInExcel, ProgramDescription[] allProgramsDescriptions) {
        for (ProgramDescription currentProgramDescription : allProgramsDescriptions) {
            for (String currentProgramSynonym : currentProgramDescription.getProgramSynonyms()) {
                if (skleroticInExcel.matches(currentProgramSynonym))
                    return currentProgramDescription.getSkleroticName();
            }
        }
        return "";
    }

    private Map<String, Integer> returnResult(){
        Map<String, Integer> nameAndNumberOfErrors = new HashMap<>(1);
        nameAndNumberOfErrors.put(FEATURE_NAME, numOfErrors);
        return nameAndNumberOfErrors;
    }
}
