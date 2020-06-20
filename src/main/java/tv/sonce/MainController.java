package tv.sonce;

import tv.sonce.plchecker.PLChecker;

public class MainController {
    public static void main(String[] args) {

        try {
            PLChecker plChecker = new PLChecker();
            plChecker.checkPL();
        } catch (Exception e) {
            System.out.println("Не удалось проверить плейлист");
            e.printStackTrace();
        }

    }
}
