package tv.sonce.plchecker;

import tv.sonce.plchecker.checkservice.IParticularFeatureChecker;
import tv.sonce.plchecker.entity.Event;
import tv.sonce.utils.ConsoleHandler;

import java.util.*;

public class PLChecker {

//    private final String PATH_PROPERTY = "./properties/PLChecker/PLChecker.properties";

    public void checkPL() {

        PLKeeper plKeeper = new PLKeeper();
        Map<String, Integer> nameAndNumberOfErrors = new HashMap<>();

        for (IParticularFeatureChecker particularFeatureChecker : ServiceLoader.load(IParticularFeatureChecker.class))
            nameAndNumberOfErrors.putAll(particularFeatureChecker.checkFeature(plKeeper));

        // view
        if(System.getProperty("os.name").startsWith("Windows 10")) {
            ConsoleHandler.initWinConsole();

            String[] color = new String[plKeeper.getAllEventsList().size()];
            Arrays.fill(color, ConsoleHandler.ANSI_RESET);

            // проходимся по всем программам, рекламным блокам и телемагазинам. Если хоть одно событие в блоке имеет ошибку - присваиваем новый цвет всем событиям в этом блоке
            fillColorsInBlockWithError(plKeeper.getAllProgramsList(), color);
            fillColorsInBlockWithError(plKeeper.getAllReklamBloksList(), color);
            fillColorsInBlockWithError(plKeeper.getAllTelemagazinBloksList(), color);

            int i = 0;
            for (Event event : plKeeper.getAllEventsList()) {
                System.out.print(color[i++]);
                System.out.printf("%-3d%-130s", event.getNumberOfEvent(), event);
                if (event.errors.size() != 0)
                    System.out.println(".  " + event.errors);
                else
                    System.out.println(".");
            }

            System.out.println(ConsoleHandler.ANSI_RED);
        } else {
            for (Event event : plKeeper.getAllEventsList()) {
                System.out.printf("%-3d%-130s", event.getNumberOfEvent(), event);
                if (event.errors.size() != 0)
                    System.out.println(".  " + event.errors);
                else
                    System.out.println(".");
            }
        }
            System.out.println();
            for (Map.Entry<String, Integer> entry : nameAndNumberOfErrors.entrySet()) {
                if (entry.getValue() != 0)
                    System.out.println("Ошибок в " + entry.getKey() + " = " + entry.getValue());
            }
    }

    private void fillColorsInBlockWithError(List<List<Event>> listOfEventBlocks, String[] color) {
        for(List<Event> eventsBlock : listOfEventBlocks){
            int amountOfErrors = 0;
            for(Event event : eventsBlock)
                amountOfErrors += event.errors.size();
            String currentColor = ConsoleHandler.getNextBrightTextColor();
            if(amountOfErrors != 0)
                for(Event event : eventsBlock)
                    color[event.getNumberOfEvent() - 1] = currentColor;
        }
    }
}
