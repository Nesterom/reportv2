package report;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

//added by Igor
import org.springframework.web.bind.annotation.CrossOrigin;
import com.google.gson.*;

@RestController
public class TestController {
//    private ArrayList<HashMap> listAll = new ArrayList<>(EmployeesList.getList());
   
    @CrossOrigin(origins="*")
    @RequestMapping(value = "/EList", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String testMethod (){
        String jsArray = new Gson().toJson(EmployeesList.getList());
        return jsArray;
    }
}
