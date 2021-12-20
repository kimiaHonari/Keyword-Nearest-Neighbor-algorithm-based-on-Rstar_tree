import MongodbDao.MongoDBbusinessDAO;
import com.mongodb.MongoClient;
import org.json.simple.JSONArray;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServlet;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by kimia on 5/11/2017.
 */
@javax.servlet.annotation.WebServlet("/main")
public class SetOptionServlet extends HttpServlet{
    @Override
    protected void doGet(javax.servlet.http.HttpServletRequest request,
                         javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {


    }
    @Override
    protected void doPost(javax.servlet.http.HttpServletRequest request,
                         javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {

        System.out.println("in servlet");
        MongoClient mongo = (MongoClient) request.getServletContext()
                .getAttribute("MONGO_CLIENT");
        System.out.println("before dao");
       int alpha= Integer.parseInt(request.getParameter("alpha"));


       MongoDBbusinessDAO bbusinessDAO=MongoDBbusinessDAO.getInstance(mongo);
        JSONArray jsonArray=new JSONArray();
        long startTime1 = System.currentTimeMillis();
       jsonArray= bbusinessDAO.getNearest(alpha);
        long endTime2   = System.currentTimeMillis();
        long totalTime1 = endTime2 - startTime1;
        System.out.println("after dao");

        System.out.println(" total time algo : "+totalTime1);
        System.out.println("alpha"+alpha);
        System.out.println(jsonArray);
        PrintWriter o = response.getWriter();
        o.println(jsonArray);


    }

}
