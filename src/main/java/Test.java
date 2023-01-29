import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.IOException;
import java.util.ArrayList;

public class Test {
    public static void main(String[] args) throws IOException, InvalidFormatException {
        ArrayList <Integer> listOfVocations = new ArrayList<>();
        for (int i = 27; i < 31; i++){
            listOfVocations.add(i);
        }
        Report.fillExls(0,12, 2022, listOfVocations);
        Report.convert();

    }
}




