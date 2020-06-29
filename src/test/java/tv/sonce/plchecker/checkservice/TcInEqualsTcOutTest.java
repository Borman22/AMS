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

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TcInEqualsTcOutTest {
    private static final String PATH_TO_PROPERTY = "./properties/PLChecker/checkerservice/TcInEqualsTcOut.properties";

    private static String FEATURE_NAME;
    private static int TC_DEVIATION;
    private static TcInEqualsTcOut tcInEqualsTcOut;

    private List<List<Event>> allProgramsList;
    private PLKeeper plKeeperMock;

    @BeforeClass
    public static void initClass() throws IOException {
        Properties properties = new Properties();
        properties.load(new FileReader(PATH_TO_PROPERTY));
        FEATURE_NAME = properties.getProperty("FEATURE_NAME");
        TC_DEVIATION = Integer.parseInt(properties.getProperty("TC_DEVIATION"));
        tcInEqualsTcOut = new TcInEqualsTcOut();
    }

    @Before
    public void init() {
        allProgramsList = new ArrayList<>();
        plKeeperMock = mock(PLKeeper.class);
        when(plKeeperMock.getAllProgramsList()).thenReturn(allProgramsList);
    }

    @Test
    public void tcInEqTcOut() {
        TimeCode anyTime = new TimeCode("00:00:00:00");
        TimeCode duration = new TimeCode("00:01:00:00");

        TimeCode in1 = new TimeCode("00:00:00:00");
        TimeCode out1 = new TimeCode(TimeCode.framesToDelimitedStr(in1.getFrames() + duration.getFrames()));

        TimeCode in2 = out1;
        TimeCode out2 = new TimeCode(TimeCode.framesToDelimitedStr(in2.getFrames() + duration.getFrames()));

        Event subClip1 = new Event(1, "", anyTime.getDelimitedStr(), duration.getDelimitedStr(), in1.getDelimitedStr(), out1.getDelimitedStr(), "0", "currentProgram", "", "");
        Event sugClip2 = new Event(2, "", anyTime.getDelimitedStr(), duration.getDelimitedStr(), in2.getDelimitedStr(), out2.getDelimitedStr(), "0", "currentProgram", "", "");

        List<Event> program = new ArrayList<>();
        program.add(subClip1);
        program.add(sugClip2);
        allProgramsList.add(program);

        Map<String, Integer> result = tcInEqualsTcOut.checkFeature(plKeeperMock);
        assertEquals(0, result.get(FEATURE_NAME).intValue());
    }

    @Test
    public void tcInLessThenTcOutPlusDeviation() {
        TimeCode anyTime = new TimeCode("00:00:00:00");
        TimeCode duration = new TimeCode("00:01:00:00");

        TimeCode in1 = new TimeCode("00:00:00:00");
        TimeCode out1 = new TimeCode(TimeCode.framesToDelimitedStr(in1.getFrames() + duration.getFrames()));

        TimeCode in2 = new TimeCode(TimeCode.framesToDelimitedStr(out1.getFrames() + TC_DEVIATION - 1));
        TimeCode out2 = new TimeCode(TimeCode.framesToDelimitedStr(in2.getFrames() + duration.getFrames()));

        Event subClip1 = new Event(1, "", anyTime.getDelimitedStr(), duration.getDelimitedStr(), in1.getDelimitedStr(), out1.getDelimitedStr(), "0", "currentProgram", "", "");
        Event sugClip2 = new Event(2, "", anyTime.getDelimitedStr(), duration.getDelimitedStr(), in2.getDelimitedStr(), out2.getDelimitedStr(), "0", "currentProgram", "", "");

        List<Event> program = new ArrayList<>();
        program.add(subClip1);
        program.add(sugClip2);
        allProgramsList.add(program);

        Map<String, Integer> result = tcInEqualsTcOut.checkFeature(plKeeperMock);
        assertEquals(0, result.get(FEATURE_NAME).intValue());
    }

    @Test
    public void tcInEqTcOutPlusDeviation() {
        TimeCode anyTime = new TimeCode("00:00:00:00");
        TimeCode duration = new TimeCode("00:01:00:00");

        TimeCode in1 = new TimeCode("00:00:00:00");
        TimeCode out1 = new TimeCode(TimeCode.framesToDelimitedStr(in1.getFrames() + duration.getFrames()));

        TimeCode in2 = new TimeCode(TimeCode.framesToDelimitedStr(out1.getFrames() + TC_DEVIATION));
        TimeCode out2 = new TimeCode(TimeCode.framesToDelimitedStr(in2.getFrames() + duration.getFrames()));

        Event subClip1 = new Event(1, "", anyTime.getDelimitedStr(), duration.getDelimitedStr(), in1.getDelimitedStr(), out1.getDelimitedStr(), "0", "currentProgram", "", "");
        Event sugClip2 = new Event(2, "", anyTime.getDelimitedStr(), duration.getDelimitedStr(), in2.getDelimitedStr(), out2.getDelimitedStr(), "0", "currentProgram", "", "");

        List<Event> program = new ArrayList<>();
        program.add(subClip1);
        program.add(sugClip2);
        allProgramsList.add(program);

        Map<String, Integer> result = tcInEqualsTcOut.checkFeature(plKeeperMock);
        assertEquals(0, result.get(FEATURE_NAME).intValue());
    }

    @Test
    public void tcInMoreThenTcOutPlusDeviation() {
        TimeCode anyTime = new TimeCode("00:00:00:00");
        TimeCode duration = new TimeCode("00:01:00:00");

        TimeCode in1 = new TimeCode("00:00:00:00");
        TimeCode out1 = new TimeCode(TimeCode.framesToDelimitedStr(in1.getFrames() + duration.getFrames()));

        TimeCode in2 = new TimeCode(TimeCode.framesToDelimitedStr(out1.getFrames() + TC_DEVIATION + 1));
        TimeCode out2 = new TimeCode(TimeCode.framesToDelimitedStr(in2.getFrames() + duration.getFrames()));

        Event subClip1 = new Event(1, "", anyTime.getDelimitedStr(), duration.getDelimitedStr(), in1.getDelimitedStr(), out1.getDelimitedStr(), "0", "currentProgram", "", "");
        Event sugClip2 = new Event(2, "", anyTime.getDelimitedStr(), duration.getDelimitedStr(), in2.getDelimitedStr(), out2.getDelimitedStr(), "0", "currentProgram", "", "");

        List<Event> program = new ArrayList<>();
        program.add(subClip1);
        program.add(sugClip2);
        allProgramsList.add(program);

        Map<String, Integer> result = tcInEqualsTcOut.checkFeature(plKeeperMock);
        assertEquals(1, result.get(FEATURE_NAME).intValue());
    }

    @Test
    public void tcInLessThenTcOutMinusDeviation() {
        TimeCode anyTime = new TimeCode("00:00:00:00");
        TimeCode duration = new TimeCode("00:01:00:00");

        TimeCode in1 = new TimeCode("00:00:00:00");
        TimeCode out1 = new TimeCode(TimeCode.framesToDelimitedStr(in1.getFrames() + duration.getFrames()));

        TimeCode in2 = new TimeCode(TimeCode.framesToDelimitedStr(out1.getFrames() - TC_DEVIATION - 1));
        TimeCode out2 = new TimeCode(TimeCode.framesToDelimitedStr(in2.getFrames() + duration.getFrames()));

        Event subClip1 = new Event(1, "", anyTime.getDelimitedStr(), duration.getDelimitedStr(), in1.getDelimitedStr(), out1.getDelimitedStr(), "0", "currentProgram", "", "");
        Event sugClip2 = new Event(2, "", anyTime.getDelimitedStr(), duration.getDelimitedStr(), in2.getDelimitedStr(), out2.getDelimitedStr(), "0", "currentProgram", "", "");

        List<Event> program = new ArrayList<>();
        program.add(subClip1);
        program.add(sugClip2);
        allProgramsList.add(program);

        Map<String, Integer> result = tcInEqualsTcOut.checkFeature(plKeeperMock);
        assertEquals(1, result.get(FEATURE_NAME).intValue());
    }

    @Test
    public void tcInEqTcOutMinusDeviation() {
        TimeCode anyTime = new TimeCode("00:00:00:00");
        TimeCode duration = new TimeCode("00:01:00:00");

        TimeCode in1 = new TimeCode("00:00:00:00");
        TimeCode out1 = new TimeCode(TimeCode.framesToDelimitedStr(in1.getFrames() + duration.getFrames()));

        TimeCode in2 = new TimeCode(TimeCode.framesToDelimitedStr(out1.getFrames() - TC_DEVIATION));
        TimeCode out2 = new TimeCode(TimeCode.framesToDelimitedStr(in2.getFrames() + duration.getFrames()));

        Event subClip1 = new Event(1, "", anyTime.getDelimitedStr(), duration.getDelimitedStr(), in1.getDelimitedStr(), out1.getDelimitedStr(), "0", "currentProgram", "", "");
        Event sugClip2 = new Event(2, "", anyTime.getDelimitedStr(), duration.getDelimitedStr(), in2.getDelimitedStr(), out2.getDelimitedStr(), "0", "currentProgram", "", "");

        List<Event> program = new ArrayList<>();
        program.add(subClip1);
        program.add(sugClip2);
        allProgramsList.add(program);

        Map<String, Integer> result = tcInEqualsTcOut.checkFeature(plKeeperMock);
        assertEquals(0, result.get(FEATURE_NAME).intValue());
    }

    @Test
    public void tcInMoreThenTcOutMinusDeviation() {
        TimeCode anyTime = new TimeCode("00:00:00:00");
        TimeCode duration = new TimeCode("00:01:00:00");

        TimeCode in1 = new TimeCode("00:00:00:00");
        TimeCode out1 = new TimeCode(TimeCode.framesToDelimitedStr(in1.getFrames() + duration.getFrames()));

        TimeCode in2 = new TimeCode(TimeCode.framesToDelimitedStr(out1.getFrames() - TC_DEVIATION + 1));
        TimeCode out2 = new TimeCode(TimeCode.framesToDelimitedStr(in2.getFrames() + duration.getFrames()));

        Event subClip1 = new Event(1, "", anyTime.getDelimitedStr(), duration.getDelimitedStr(), in1.getDelimitedStr(), out1.getDelimitedStr(), "0", "currentProgram", "", "");
        Event sugClip2 = new Event(2, "", anyTime.getDelimitedStr(), duration.getDelimitedStr(), in2.getDelimitedStr(), out2.getDelimitedStr(), "0", "currentProgram", "", "");

        List<Event> program = new ArrayList<>();
        program.add(subClip1);
        program.add(sugClip2);
        allProgramsList.add(program);

        Map<String, Integer> result = tcInEqualsTcOut.checkFeature(plKeeperMock);
        assertEquals(0, result.get(FEATURE_NAME).intValue());
    }
}