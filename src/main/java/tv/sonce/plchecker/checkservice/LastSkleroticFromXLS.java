package tv.sonce.plchecker.checkservice;

import tv.sonce.plchecker.PLKeeper;
import tv.sonce.plchecker.entity.Event;
import tv.sonce.plchecker.entity.ProgramDescription;
import tv.sonce.utils.ExcelParser;
import tv.sonce.utils.JsonReader;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class LastSkleroticFromXLS extends AbstractParticularFeatureChecker {

    private final String PATH_TO_PROPERTY = "./properties/PLChecker/checkerservice/LastSkleroticFromXLS.properties";

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
        final int MIN_SUBCLIP_LENGTH_IN_FRAME_FOR_SKLEROTIC = Integer.parseInt(properties.getProperty("MIN_SUBCLIP_LENGTH_IN_FRAME_FOR_SKLEROTIC"));

        JsonReader<ProgramDescription[]> jsonReader = new JsonReader<>(PATH_TO_ALL_PROGRAMS_DESCRIPTION, new ProgramDescription[]{});
        ProgramDescription[] allProgramsDescriptions = jsonReader.getContent();

        int numOfErrors = 0;

        List<List<Event>> allProgramsList = plKeeper.getAllProgramsList();
        List<Event> theLastestProgram = allProgramsList.get(allProgramsList.size() - 1);
        Event theLastestSubclip = theLastestProgram.get(theLastestProgram.size() - 1);

        // file "Doski" not found
        String absoluteExcelFilePath = getAbsoluteExcelFilePath(plKeeper, PATH_TO_EXCEL_FOLDER);
        if (absoluteExcelFilePath.equals("")) {
            theLastestSubclip.errors.add(ERROR_MESSAGE_EXCEL_FILE_NOT_FOUND);
            numOfErrors++;
            Map<String, Integer> nameAndNumberOfErrors = new HashMap<>(1);
            nameAndNumberOfErrors.put(FEATURE_NAME, numOfErrors);
            return nameAndNumberOfErrors;
        }

        ExcelParser excelParser = new ExcelParser(absoluteExcelFilePath, 0);

        // file "Doski" not match with playlist
        int rowInXls = theLastestSubclip.getNumberOfEvent() + SHIFT_BTW_XLS_AND_XML_NUMS;
        if (rowInXls >= excelParser.getSheet().size()) {       //  instead of ArrayIndexOutOfBoundsException
            theLastestSubclip.errors.add(ERROR_MESSAGE_EXCEL_FILE_NOT_MATCH_WITH_PLAYLIST);
            numOfErrors++;
            Map<String, Integer> nameAndNumberOfErrors = new HashMap<>(1);
            nameAndNumberOfErrors.put(FEATURE_NAME, numOfErrors);
            return nameAndNumberOfErrors;
        }

        String skleroticInExcel = excelParser.getSheet().get(rowInXls).get(CELL_NUM_FOR_SKLEROTIC);
        String skleroticName = findSklerotic(skleroticInExcel, allProgramsDescriptions);

        // Если длинна субклипа маленькая или склеротика не существует, то убедимся, что никакой склеротик не стоит
        if (theLastestSubclip.getDuration() < MIN_SUBCLIP_LENGTH_IN_FRAME_FOR_SKLEROTIC || skleroticName.equals(EMPTY_SKLEROTIC)) {
            for (String currentFormat : theLastestSubclip.getFormat()) {
                if (currentFormat.matches(SKLEROTIC_TEMPLATE)) {
                    theLastestSubclip.errors.add(ERROR_MESSAGE_SUPERFLUOUS_SKLEROTIC);
                    numOfErrors++;
                }
            }
        } else {  // Если длинна субклипа большая и склеротик существует, то убедимся, что он стоит
            if (!isThereCorrectSklerotic(skleroticName, theLastestSubclip.getFormat())) {
                theLastestSubclip.errors.add(ERROR_MESSAGE + skleroticName);
                numOfErrors++;
            }
        }

        Map<String, Integer> nameAndNumberOfErrors = new HashMap<>(1);
        nameAndNumberOfErrors.put(FEATURE_NAME, numOfErrors);
        return nameAndNumberOfErrors;
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

    private boolean isThereCorrectSklerotic(String skleroticName, String[] formats) {
        for (String currentFormat : formats) {
            if (skleroticName.matches(currentFormat))
                return true;
        }
        return false;
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
}
