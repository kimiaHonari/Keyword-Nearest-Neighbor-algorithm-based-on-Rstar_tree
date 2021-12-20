package MongodbDao;

import Tree.Rsatar.KeywordCover;
import Tree.Rsatar.RStarTree;
import Tree.Rsatar.Score;
import Tree.Rsatar.dto.PointDTO;
import Tree.Rsatar.spatial.SpatialPoint;
import com.mongodb.*;
import com.mongodb.client.MongoDatabase;
import com.mongodb.util.JSON;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Vector;
import java.util.Random;


//50 is the maximum and the 1 is our minimum
/**
 * Created by kimia on 5/11/2017.
 */
public class MongoDBbusinessDAO {
    static MongoDBbusinessDAO mongoDBbusinessDAO;
    static private DBCollection col;
    double maxlat=0;
    double maxlon=0;
    double maxstar=5;
    RStarTree minTree;
    int NumOfMinTree;
    int rankMinTree=0;
    String keywordNC[];
    int aveKeyword;
    Vector<RStarTree> vector;
    int n=0;
    private MongoDBbusinessDAO(){
        aveKeyword=200;
        keywordNC=new String[]{"Hotels","Arts & Entertainment","Fast Food","Bars","Shopping","Beauty & Spas","Health & Medical","Local Services","Active Life"};
        maxlat=57.5922852;
        maxlon=115.086769;
        vector=null;
    }
   static public MongoDBbusinessDAO getInstance(MongoClient mongo){
        if(mongoDBbusinessDAO!=null) {

            return mongoDBbusinessDAO;}
        else{
            mongoDBbusinessDAO=new MongoDBbusinessDAO();
            setMongo(mongo);
            return mongoDBbusinessDAO;
        }
    }
    static private void setMongo(MongoClient mongo) {
        System.out.println("in dao");
        DB db = mongo.getDB("projectdb");
        col=  db.getCollection("yel_business");

    }
    public Vector<RStarTree> CreateTrees(Vector<Integer> id) {
        minTree=null;
        NumOfMinTree=0;
        rankMinTree=0;
        vector=null;
        vector=new Vector<RStarTree>();
        n=0;
       /* DBObject myDoc = col.findOne();*/
       // getTree("NC", "Hotels & Travel", 1);
       // getTree("NC", "Arts & Entertainment", 2);
    for(int j=0;j<id.size();j++) {
            getTree("Toronto",keywordNC[id.get(j)] , j+1);
            System.out.println(keywordNC[id.get(j)]);
      }
        vector.remove(rankMinTree-1);
        return vector;
    }
    public JSONArray getNearest(int alpha){
        JSONArray jsonArray=new JSONArray();
        double[] f = new double[2];
        f[0] =alpha;
        f[1] =100-alpha;
        double[] m = new double[2];
        m[0] = 1.4142135624;//30.2489669245;
        m[1] = 1;
        Score score = Score.Initiate(f, m, 3);

      //  minTree.printTree(minTree.getRoot());
        KeywordCover<PointDTO> result=minTree.KNNE_search(vector);
        System.out.println(result.score);
        for(int i=0;i<result.result.size();i++){
            System.out.println(result.result.get(i).info);
           jsonArray.add(result.result.get(i).info);
        }
        return jsonArray;
    }

    RStarTree getTree(String city,String category,int m){
        BasicDBObject whereQuery = new BasicDBObject();
        whereQuery.append("categories", category);
        whereQuery.append("city", city);
        DBCursor cursor = col.find(whereQuery);
        int j=0;
        RStarTree tree=new RStarTree(3);
       // Random rand = new Random();

       // int  o = 20;//rand.nextInt(80) + 20;
        while(cursor.hasNext()) {
            n++;
            DBObject dbObject=cursor.next();
            //System.out.println(dbObject);
            JSONObject temp=new JSONObject();
            temp.put("1",String.valueOf(dbObject.get("_id")));
            temp.put("2",String.valueOf(dbObject.get("name")));
            temp.put("3",String.valueOf(dbObject.get("city")));
            temp.put("4",String.valueOf(dbObject.get("state")));
            temp.put("5",String.valueOf(dbObject.get("latitude")));
            temp.put("6",String.valueOf(dbObject.get("longitude")));
            temp.put("7",String.valueOf(dbObject.get("stars")));
            temp.put("8",category);

            double lat=Double.parseDouble(String.valueOf(temp.get("5")));
            double lon=Double.parseDouble(String.valueOf(temp.get("6")));
            double star=Double.parseDouble(String.valueOf(temp.get("7")));
            tree.insert(new SpatialPoint(new double[]{lat/maxlat, lon/maxlon, star/maxstar}, n,temp));
            j++;
        //    if(j>o){ break;}

        }

        tree.save();
       //tree.printTree(tree.getRoot());
        vector.add(tree);
        if(NumOfMinTree==0){NumOfMinTree=j; minTree=tree;rankMinTree=m;}
        else if(j< NumOfMinTree){
            NumOfMinTree=j; minTree=tree;rankMinTree=m;
        }
       return tree;
    }
}
