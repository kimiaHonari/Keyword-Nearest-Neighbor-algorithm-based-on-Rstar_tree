import com.mongodb.MongoClient;
import org.json.simple.JSONObject;

import javax.servlet.http.HttpServlet;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by kimia on 7/9/2017.
 */
@javax.servlet.annotation.WebServlet("/GetKeyword")
public class GetKeywordServlet extends HttpServlet{
    @Override
    protected void doGet(javax.servlet.http.HttpServletRequest request,
                         javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        String keywordNC[];
         keywordNC=new String[]{"Hotels","Arts & Entertainment","Fast Food","Bars","Shopping","Beauty & Spas","Health & Medical","Local Services","Active Life"};


        JSONObject json=new JSONObject();

        int v=0;
        for(v=0;v<9;v++){

            json.put(v,keywordNC[v]);
        }
        System.out.println(json);
        PrintWriter o = response.getWriter();
        o.println(json);


    }
}
