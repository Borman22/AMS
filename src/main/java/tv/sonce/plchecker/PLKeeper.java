package tv.sonce.plchecker;

import javax.xml.XMLConstants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import tv.sonce.plchecker.entity.Event;
import tv.sonce.plchecker.entity.ProgramDescription;
import tv.sonce.properties.PlKeeperProperties;
import tv.sonce.utils.JsonReader;
import tv.sonce.utils.TimeCode;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.*;

public class PLKeeper {

    private static final String PATH_TO_PROPERTY = "./properties/PLChecker/PLKeeper.properties";

    private final PlKeeperProperties properties = PlKeeperProperties.getInstance(PATH_TO_PROPERTY);
    private List<Event> allEventsList;
    private List<List<Event>> allProgramsList;
    private List<List<Event>> reklamBloksList;
    private List<List<Event>> telemagazinBloksList;

    public PLKeeper() {
        fillAllEventsList(Objects.requireNonNull(parsePl()));
        fillAllProgramsList(allEventsList);
        fillReklamBloksListAndTelemagazinBlockList(allEventsList, allProgramsList);
    }

    public List<Event> getAllEventsList() {
        return allEventsList;
    }

    public List<List<Event>> getAllProgramsList() {
        return allProgramsList;
    }

    public List<List<Event>> getAllReklamBloksList() {
        return reklamBloksList;
    }

    public List<List<Event>> getAllTelemagazinBloksList() {
        return telemagazinBloksList;
    }



    private NodeList parsePl() {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            dbFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            dbFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(new File(properties.PATH_TO_PL));
            doc.getDocumentElement().normalize();
            return doc.getElementsByTagName(properties.EVENT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void fillAllEventsList(NodeList nodeList) {
        allEventsList = new ArrayList<>(nodeList.getLength());
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node nNode = nodeList.item(i);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                addNode(i, (Element) nNode, allEventsList);
            }
        }
    }

    private void fillAllProgramsList(List<Event> allEventsList) {

        allProgramsList = new ArrayList<>(properties.MAX_NUM_OF_PROGRAMS);

        // программа - событие дольше MAX_REKLAMA_DURATION и не в списке событий, которые не считаются программой
        // или меньше MAX_REKLAMA_DURATION, но начинается не с 00:00:00 и имя совпадает с предыдущими субклипами
        // субклип начинается с 00:00:00 - первый субклип
        // если начинается не с 00:00:00, но имя или таймкод совпадает с предыдущим - следующий субклип
        // ни имя ни таймкод не совпадает с предыдущими субклипами - новое событие, которое начинается не с 00:00:00

        List<String> allSynonymsNotAPrograms = new ArrayList<>();

        JsonReader<ProgramDescription[]> jsonReader = new JsonReader<>(properties.PATH_TO_NOT_CONSIDERED_A_PROGRAM, new ProgramDescription[]{});
        ProgramDescription[] notConsideredAProgram = jsonReader.getContent();

        for (ProgramDescription notAProgramDescription : notConsideredAProgram) {
            allSynonymsNotAPrograms.addAll(Arrays.asList(notAProgramDescription.getProgramSynonyms()));
        }

        List<Event> program = new ArrayList<>();
        Event oldEvent = allEventsList.get(0);
        for (Event event : allEventsList) {
            if ((event.getDuration() > properties.MAX_REKLAMA_DURATION || event.getTcIn() != 0) && !isMatchesStrWithRegexList(event.getCanonicalName(), allSynonymsNotAPrograms)) {
                if (event.getTcIn() == 0) { // начинается с 00:00:00
                    program = new ArrayList<>();
                    allProgramsList.add(program);
                    program.add(event);
                    oldEvent = event;
                } else {    // начинается не с 00:00:00
                    if(!TimeCode.isTheSameTC(oldEvent.getTcOut(), event.getTcIn(), properties.TC_DEVIATION) && !oldEvent.isTheSameName(event.getCanonicalName())) {
                        program = new ArrayList<>();
                        allProgramsList.add(program);
                        for(int i = oldEvent.getNumberOfEvent(); i < event.getNumberOfEvent() - 1; i++) {
                            if(TimeCode.isTheSameTC(allEventsList.get(i).getTcOut(), event.getTcIn(), properties.TC_DEVIATION) || allEventsList.get(i).isTheSameName(event.getCanonicalName())) {
                                program.add(allEventsList.get(i));
                                break;
                            }
                        }
                    }
                    program.add(event);
                    oldEvent = event;
                }
            }
        }
    }

    private void fillReklamBloksListAndTelemagazinBlockList(List<Event> allEventsList, List<List<Event>> allProgramsList) {

        reklamBloksList = new ArrayList<>(3 * properties.MAX_NUM_OF_PROGRAMS);
        telemagazinBloksList = new ArrayList<>();

        // Рекламный блок состоит из N реклам и отбивок. Располагается между субклипами или программами. Не содержит отбивку на тлемагазин или T- (телемагазин) или A- (анонс)
        // Телемагазинный блок состоит из N телемагазинов и отбивок. Содержит отбивку на телемагазин и T-. Не содержит А- (анонс)
        Event previousEvent = allEventsList.get(0);

        for(List<Event> currentProgram : allProgramsList) {
            for(Event subclip : currentProgram) {
                List<Event> tempBlock = new ArrayList<>();
                for(int i = previousEvent.getNumberOfEvent(); i < subclip.getNumberOfEvent() - 1; i++) {
                    tempBlock.add(allEventsList.get(i));
                }
                previousEvent = subclip;

                if(tempBlock.size() <= properties.MIN_EVENTS_IN_REKLAM_BLOCK) {
                    continue;
                }

                // исключим все избыточные события - анонсы, отбивки, новости...
                excludeRedundantEvents(tempBlock, properties.REDUNDANT_EVENT_TEMPLATE);

                int numberOfTelemagazin = 0;
                for(Event event : tempBlock) {
                    if(event.getName().matches(properties.TELEMAGAZIN_BUMPER_TEMPLATE) || event.getName().matches(properties.TELEMAGAZIN_TEMPLATE)) {
                        numberOfTelemagazin++;
                    }
                }
                if(tempBlock.size() / 2 < numberOfTelemagazin) {
                    telemagazinBloksList.add(tempBlock);
                } else {
                    reklamBloksList.add(tempBlock);
                }
            }
        }
    }

    private void excludeRedundantEvents(List<Event> tempBlock, String[] redundantEvents) {
        Iterator<Event> tempBlockIterator = tempBlock.iterator();
        while(tempBlockIterator.hasNext()) {
            Event tempEvent = tempBlockIterator.next();
            for(String redundantEventTemplate : redundantEvents)
                if(tempEvent.getName().matches(redundantEventTemplate))
                    tempBlockIterator.remove();
        }
    }

    private boolean isMatchesStrWithRegexList(String str, List<String> regexList) {
        for (String regex : regexList) {
            if (str.matches(regex))
                return true;
        }
        return false;
    }

    private void addNode(int numberOfEvent, Element element, List<Event> listOfEvents) {
        Event tempEvent = new Event(
                numberOfEvent + 1,  // События считаются с 1, но хранятся в массиве с 0
                element.getElementsByTagName(properties.DATE).item(0).getTextContent(),
                element.getElementsByTagName(properties.TIME).item(0).getTextContent(),
                element.getElementsByTagName(properties.DURATION).item(0).getTextContent(),
                element.getElementsByTagName(properties.TC_IN).item(0) == null ? null : element.getElementsByTagName(properties.TC_IN).item(0).getTextContent(),
                element.getElementsByTagName(properties.TC_OUT).item(0) == null ? null : element.getElementsByTagName(properties.TC_OUT).item(0).getTextContent(),
                element.getElementsByTagName(properties.ASSET_ID).item(0).getTextContent(),
                element.getElementsByTagName(properties.NAME).item(0).getTextContent(),
                element.getElementsByTagName(properties.FORMAT).item(0).getTextContent(),
                element.getElementsByTagName(properties.STATUS).item(0).getTextContent()
        );
        listOfEvents.add(tempEvent);
    }
}
