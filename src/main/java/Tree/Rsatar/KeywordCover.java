package Tree.Rsatar;

import Tree.Rsatar.dto.PointDTO;
import Tree.Rsatar.nodes.RStarNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kimia on 3/22/2017.
 */
public class KeywordCover<T> {

    public List<List<T>> group ;
    public ArrayList<T> result;
    public double score;
    private PointDTO pointDTO;
    private RStarNode rStarNode;
    Score ScoreObj;

    public PointDTO getPointDTO() {
        return pointDTO;
    }

    public void setPointDTO(PointDTO pointDTO) {
        this.pointDTO = pointDTO;
    }

    public RStarNode getrStarNode() {
        return rStarNode;
    }

    public void setrStarNode(RStarNode rStarNode) {
        this.rStarNode = rStarNode;
    }

    public KeywordCover(int t){
        group= new ArrayList<List<T>>(t);
        for(int i=0;i<t;i++)
        {
            List<T> lbkcList = new ArrayList<T>();

            group.add(lbkcList);
        }
        result=new ArrayList<T>();
        ScoreObj=Score.getInstance();
    }

    public void  addFirstObj(int key,T p){
        group.get(key).add(p);
        result.add(p);
        score=ScoreObj.getObjScore((ArrayList<PointDTO>) result);
    }
    public void addObjToResult(PointDTO p){
        pointDTO=p;
        result.add((T) p);
        score=ScoreObj.getObjScore((ArrayList<PointDTO>) result);
    }
    public void addNodeToResult(RStarNode p){
        rStarNode=p;
        result.add((T) p);
        score=ScoreObj.getNodeScore((ArrayList<RStarNode>) result);
    }
    public void  addObj(int key,T p) {
        group.get(key).add(p);
        ArrayList<PointDTO> t=new ArrayList<PointDTO>();
        t.add((PointDTO)p);
        t.add(pointDTO);
      System.out.println("group size:"+group.size());
        System.out.println("group size:"+group.get(0).size());
        //System.out.println("group size:"+group.get(1).size());
     //   System.out.println("temp size:"+t.size());
        ComputeHighestObjScore(t,0,key);
       /* for(int i=0;i<result.size();i++){
            System.out.println("point id : "+((PointDTO)result.get(i)).oid +"  cord 0 : "+((PointDTO)result.get(i)).coords[0]+
                    "  cord 1 : "+((PointDTO)result.get(i)).coords[1]+"  cord 2 : "+((PointDTO)result.get(i)).coords[2]);
        }*/
    }
    public void addFirstNode(int key,T p){
        group.get(key).add(p);
        result.add(p);
        score=ScoreObj.getNodeScore((ArrayList<RStarNode>) result);
    }
    public void  addNode(int key,T p) {
        group.get(key).add(p);
        ArrayList<RStarNode> t=new ArrayList<RStarNode>();
        t.add((RStarNode) p);

        ComputeHighestNodeScore(t,0,key);
    }

    public void ComputeHighestObjScore(final ArrayList<PointDTO> temp,int depth,int key){

        if(depth==group.size()){
           // temp.add(pointDTO);

            double y=ScoreObj.getObjScore(temp);
            System.out.println("temp size: "+temp.size() +"value : "+y);
            if(y>score ){
                score=y;
                result= (ArrayList<T>) temp.clone();

            }
            return;
        }

        for(int i=0;i<group.get(depth).size();i++){
            System.out.println(group.get(depth).size());
            ArrayList<PointDTO> t=(ArrayList<PointDTO>) temp.clone();
            if(depth==key){
                ComputeHighestObjScore(t, depth + 1, key);
            }
            else {
                t.add((PointDTO) group.get(depth).get(i));
                double y=ScoreObj.getObjScore(t);
                if(y<score){System.out.println("return");return;}
                ComputeHighestObjScore(t, depth + 1, key);

            }
        }
        return;
    }
    public void ComputeHighestNodeScore(final ArrayList<RStarNode> temp,int depth,int key){

        if(depth==group.size()){
            temp.add(rStarNode);
           // System.out.println("temp size: "+temp.size());
            double y=ScoreObj.getNodeScore(temp);
            if(y>score ){
                score=y;
                result= (ArrayList<T>) temp.clone();
            }
            return;
        }

        for(int i=0;i<group.get(depth).size();i++){
           ArrayList<RStarNode> t=(ArrayList<RStarNode>) temp.clone();
            if(depth==key){
                ComputeHighestNodeScore(t, depth + 1, key);
            }
            else {
                t.add((RStarNode) group.get(depth).get(i));
               double y=ScoreObj.getNodeScore(t);
                if(y<score){System.out.println("return");return;}
                ComputeHighestNodeScore(t, depth + 1, key);
            }
        }
        return;
    }

}
