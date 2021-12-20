import MongodbDao.MongoDBbusinessDAO;
import com.mongodb.MongoClient;

import com.mongodb.MongoClient;
import org.json.simple.JSONObject;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServlet;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;


/**
 * Created by kimia on 7/9/2017.
 */
@javax.servlet.annotation.WebServlet("/CreateTree")
public class CreateTreeServlet extends HttpServlet{
    @Override
    protected void doGet(javax.servlet.http.HttpServletRequest request,
                          javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {
       response.sendRedirect("CreateTree.html");
    }
    @Override
    protected void doPost(javax.servlet.http.HttpServletRequest request,
                         javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        System.out.println("in servlet");
        MongoClient mongo = (MongoClient) request.getServletContext()
                .getAttribute("MONGO_CLIENT");

        JSONObject error=new JSONObject();
        Vector<Integer> vector= new Vector<Integer>();
        for(int i=0;i<9;i++) {
            System.out.println(request.getParameter(String.valueOf(i)));
            if(request.getParameter(String.valueOf(i))!=null){
                System.out.println(i);
                vector.add(i);
            }
        }
        System.out.println(vector.get(0)+vector.get(1));
       long startTime = System.currentTimeMillis();
        MongoDBbusinessDAO bbusinessDAO=MongoDBbusinessDAO.getInstance(mongo);
        bbusinessDAO.CreateTrees(vector);
        long endTime   = System.currentTimeMillis();
        long totalTime = endTime - startTime;


        System.out.println(" total time tree : "+totalTime);
        error.put("success","KRR*_trees are created!");
        PrintWriter o = response.getWriter();
        o.println(error);

    }

}
