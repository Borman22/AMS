package tv.sonce.utils;

import tv.sonce.exceptions.TimeCodeFormatExceptionRT;

public class TimeCode {

    private static final int TWENTY_FOUR_HOURS = 24*60*60*25; //2_160_000 кадров - сутки

    // Internal representation of the TimeCode class
    private int frames; // в кадрах 23*90_000 + 59*1500 + 59*25 + 24
    private String delimitedStr; // с разделителями ':'  "23:59:59:24"




    // 23:59:59:24
    public TimeCode(String delimitedStr) {
        setDelimitedStrAndFrames(delimitedStr);
    }

    // 23595924
    public TimeCode(int intStr) {
        setDelimitedStrAndFrames(intStr);
    }




    public String getDelimitedStr() {
        return delimitedStr;
    }

    public int getFrames() {
        return frames;
    }

    @Override
    public String toString() {
        return delimitedStr;
    }

    public static String framesToDelimitedStr(int frames) {
        return new TimeCode(TimeCode.framesToIntStr(frames)).getDelimitedStr();
    }

    public static int delimitedStrToFrames(String delimitedStr) {
        int intStr = delimitedStrToIntStr(delimitedStr);
        return intStrToFrames(intStr);
    }

    // считаем таймкод одним и тем же, если разница не больше, чем deviation кадров
    public static boolean isTheSameTC(int tc1, int tc2, int deviation) {
        return Math.abs(tc1 - tc2) <= deviation;
    }

    public static int TCDifferenceConsideringMidnight(int nextTCInFrames, int previousTCInFrames) {
        if (nextTCInFrames < previousTCInFrames)
            nextTCInFrames += TWENTY_FOUR_HOURS;
        return nextTCInFrames - previousTCInFrames;
    }

    public static String absoluteDifferenceToDelimitedStr(int nextTCInFrames, int previousTCInFrames){
        int frames = nextTCInFrames - previousTCInFrames;
        frames %= TWENTY_FOUR_HOURS;
        return frames < 0 ? "-" + framesToDelimitedStr(Math.abs(frames)) : framesToDelimitedStr(Math.abs(frames));
    }

    public TimeCode changeToNFramesConsideringMidnight(int frames){
        frames %= TWENTY_FOUR_HOURS;
        if(frames < 0 && Math.abs(frames) > this.frames)
            this.frames += TWENTY_FOUR_HOURS;

        frames += this.frames;
        this.frames = frames % TWENTY_FOUR_HOURS;
        delimitedStr = framesToDelimitedStr(frames);
        return this;
    }





    // 23595924
    private void setDelimitedStrAndFrames(int intStr) {
        String[] arrayStr = {"00", "00", "00", "00"}; // {23, 59, 59, 24}
        int[] arrayInt = {0, 0, 0, 0}; // {23, 59, 59, 24}

        if (intStr < 0)    // validation part 1
            throw new TimeCodeFormatExceptionRT("Таймкод должен быть больше или равен 0. В данном случае на входе " + intStr);

        // без разделителей 23595924
        arrayInt[0] = intStr / 1_000_000;    // часы
        intStr = intStr - 1_000_000 * arrayInt[0];

        arrayInt[1] = intStr / 10_000;  // минуты
        intStr = intStr - 10_000 * arrayInt[1];

        arrayInt[2] = intStr / 100;    // секунды
        arrayInt[3] = intStr - 100 * arrayInt[2]; // кадры

        if (arrayInt[0] > 23 || arrayInt[1] > 59 || arrayInt[2] > 59 || arrayInt[3] > 24)     // validation part 2
            throw new TimeCodeFormatExceptionRT("Часов не может быть больше 23, минут и секунд - больше 59, а кадров - больше 24." +
                    " В данном случае: [" + arrayInt[0] + ":" + arrayInt[1] + ":" + arrayInt[2] + ":" + arrayInt[3] + "]");

        //Заполняем массив стрингов
        for (int j = 0; j < 4; j++) {
            if (arrayInt[j] < 10) arrayStr[j] = ('0' + Integer.toString(arrayInt[j]));
            else arrayStr[j] = Integer.toString(arrayInt[j]);
        }

        frames = 90_000 * arrayInt[0] + 1500 * arrayInt[1] + 25 * arrayInt[2] + arrayInt[3];

        // Преобразовываем в сторку формата "23:59:59:24"
        delimitedStr = arrayStr[0] + ':' + arrayStr[1] + ':' + arrayStr[2] + ':' + arrayStr[3];
    }

    private void setDelimitedStrAndFrames(String delimitedStr) {
        setDelimitedStrAndFrames(delimitedStrToIntStr(delimitedStr));
    }

    private static int framesToIntStr(int frames) {
        int hours, minutes, seconds, finalFrames;
        hours = frames / 90_000;
        frames = frames - 90_000 * hours;
        minutes = frames / 1500;
        frames = frames - 1500 * minutes;
        seconds = frames / 25;
        finalFrames = frames - 25 * seconds;
        return 1_000_000 * hours + 10_000 * minutes + 100 * seconds + finalFrames;
    }

    private static int intStrToFrames(int intStr) {
        int hours, minutes, seconds, frames;
        hours = intStr / 1_000_000;
        intStr = intStr - 1_000_000 * hours;
        minutes = intStr / 10_000;
        intStr = intStr - 10_000 * minutes;
        seconds = intStr / 100;
        frames = intStr - 100 * seconds;
        return 90_000 * hours + 1500 * minutes + 25 * seconds + frames;
    }

    private static int delimitedStrToIntStr(String delimitedStr) {
        if (delimitedStr == null)
            return 0;
        if (delimitedStr.length() > 11)
            delimitedStr = delimitedStr.substring(0, 11);
        if (!delimitedStr.matches("\\d\\d:\\d\\d:\\d\\d:\\d\\d")) // todo Loging
            throw new TimeCodeFormatExceptionRT("Таймкод имеет неправильный формат. Должен быть в таком виде: 23:59:59:24, а не в таком: " + delimitedStr);
        delimitedStr = delimitedStr.replace(":", "");
        return Integer.parseInt(delimitedStr);
    }

}
   


