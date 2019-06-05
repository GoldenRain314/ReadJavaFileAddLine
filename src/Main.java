import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        AnalysisJava a = new AnalysisJava();
        List<String> filePahtList = new ArrayList<>();
        a.traverseFolder("C:\\Users\\Administrator\\Desktop\\evosuite", filePahtList);
        for(String fileName : filePahtList){
            a.setLog(fileName);
        }
    }

}
