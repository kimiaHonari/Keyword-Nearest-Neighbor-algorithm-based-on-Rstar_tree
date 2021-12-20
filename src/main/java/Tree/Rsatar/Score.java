package Tree.Rsatar;

import Tree.Rsatar.dto.PointDTO;
import Tree.Rsatar.nodes.RStarNode;

import java.util.ArrayList;

/**
 * Created by kimia on 3/19/2017.
 */
public class Score {
    private double factor[];
    private double max[];
    private int dimension;
    private static Score scoreObject;

    public double[] getFactor() {
        return factor;
    }

    public static Score getScoreObject(){
        return scoreObject;
    }
    private Score(){


    }
    public static Score Initiate(double[] f,double [] m,int dimension){
        if(scoreObject!=null){
            scoreObject.dimension=dimension;
            scoreObject.factor=f;
            scoreObject.max=m;
            return scoreObject;
        }
        scoreObject=new Score();
        scoreObject.dimension=dimension;
        scoreObject.factor=f;
        scoreObject.max=m;
        return scoreObject;


    }
    public static Score getInstance(){

          return scoreObject;
    }
    public double getScore(PointDTO p1, PointDTO p2){

        double score=0;
        double dis=getDistance(p1,p2);

        for(int i=1;i<(dimension-1);i++){
            double min;
            min=p2.coords[i+1];
            if(min > p1.coords[i+1]){
                min=p1.coords[i+1];
            }
            score+=factor[i]*(min/max[i]);

        }
        score+=(factor[0])*(1-(dis/max[0]));


     return score;
    }
    public double getScore(RStarNode n, PointDTO p) {
     //   System.out.print("in score node with point : ");
        double dis = 0;
        double score = 0;
        double point[][];
        point = n.getMBR().getPoints();
        dis=getDistance(n,p,point);
     //   System.out.print("distance ; "+dis+" ");
        for (int i = 1; i < (dimension - 1); i++) {
            double min;
            min = p.coords[i + 1];
            if (min > point[i + 1][1]) {
                min = point[i + 1][1];
            }
         //   System.out.print("min of : "+min+" , ");
         //   System.out.print("max of : "+max[i]+" , ");
        //    System.out.print("factor of : "+factor[i]+" , ");
            score += factor[i] * (min / max[i]);

        }

        score += (factor[0]) * (1-(dis / max[0]));
      //  System.out.println(" score : "+score);
        return score;
    }
    public double getScore(RStarNode n1,RStarNode n2) {


        double dis = 0;
        double score = 0;
        double point1[][];
        point1 = n1.getMBR().getPoints();
      //  System.out.print("mbr node : "+n1.getNodeId()+" : ");
      //  for(int i=0;i<dimension;i++)
      //   //   System.out.print(" max-min : "+point1[i][0]+"-"+point1[i][1]+" , ");
        double point2[][];
        point2 = n2.getMBR().getPoints();
       // System.out.print("mbr node : "+n2.getNodeId()+" : ");
      //  for(int i=0;i<dimension;i++)
           // System.out.print(" max-min : "+point2[i][0]+"-"+point2[i][1]+" , ");
        dis=getDistance(n1,n2,point1,point2);
        for (int i = 1; i < (dimension - 1); i++) {
            double min;
            min = point1[i + 1][1];
            if (min > point2[i + 1][1]) {
                min = point2[i + 1][1];
            }
            score += factor[i] * (min / max[i]);

        }
        score += (factor[0]) * (1-(dis / max[0]));
        return score;
    }

    public double getDistance(PointDTO p1, PointDTO p2){

        double distance = 0;
        for (int i = 0; i < 2; i++) {
            double tmp = (p1.coords[i] * p1.coords[i]) - (p2.coords[i] * p2.coords[i]);
            if(tmp < 0)
                tmp = -1 * tmp;

            distance += tmp;
        }
        return  Math.pow(distance, 0.5);
    }
    public double getDistance(RStarNode n, PointDTO p, double point[][]){
        double distance = 0;

        for (int i = 0; i < 2; i++) {

            double temp = ComputeY(point[i][0], p.coords[i], p.coords[i], point[i][1]);
           // System.out.print(" cord low : "+point[i][0]+" cord max : "+ point[i][1]+" , ");
            distance += (temp * temp);
        }
        return  Math.pow(distance, 0.5);
    }
    public double getDistance(RStarNode n1,RStarNode n2,double point1[][],double point2[][]){
        double distance=0;
        for (int i = 0; i < 1; i++) {

            double temp = ComputeY(point2[i][0], point1[i][1],  point1[i][0], point2[i][1]);
            distance += (temp * temp);
        }
        return Math.pow(distance, 0.5);

    }

    public double getObjScore(ArrayList<PointDTO> pointDTOArrayList){
        double maxdis=0;
        double temp=0;
        double t=0;
        double score=0;
        double[] min=new double[dimension-2];
        boolean flag=false;
        for(int i=0;i<pointDTOArrayList.size();i++){
            for(int j=i+1;j<pointDTOArrayList.size();j++){
                temp=getDistance(pointDTOArrayList.get(i),pointDTOArrayList.get(j));
                if(maxdis < temp)
                    maxdis=temp;
            }
            for(int m=0;m<dimension-2;m++){
                t=pointDTOArrayList.get(i).coords[m+2];
                if(!flag)
                {
                    min[m]=t;
                }
                else{
                    if(min[m]>t)
                        min[m]=t;
                }
            }
            flag=true;
        }
        score+=factor[0]*(1-(maxdis/max[0]));
        for(int y=1;y<dimension-1;y++){
            score+=factor[y]*(min[y-1]/max[y]);
        }

        return score;
    }

    public double getNodeScore(ArrayList<RStarNode> RstarNodeArrayList){
        double maxdis=0;
        double temp=0;
        double t=0;
        double score=0;
        double[] min=new double[dimension-2];
        boolean flag=false;
        for(int i=0;i<RstarNodeArrayList.size();i++){
            for(int j=i+1;j<RstarNodeArrayList.size();j++){
                temp=getDistance(RstarNodeArrayList.get(i),RstarNodeArrayList.get(j),
                        RstarNodeArrayList.get(i).getMBR().getPoints(),RstarNodeArrayList.get(j).getMBR().getPoints());
            //    System.out.print("temp distance in score object  node id "+RstarNodeArrayList.get(i).getNodeId() +" with node "+RstarNodeArrayList.get(j).getNodeId()
            //    +" is  : "+temp);
                if(maxdis < temp)
                    maxdis=temp;
            }
            for(int m=0;m<dimension-2;m++){
                t=RstarNodeArrayList.get(i).getMBR().getPoints()[m+2][1];
                if(!flag)
                {
                    min[m]=t;
                }
                else{
                    if(min[m]>t)
                        min[m]=t;
                }
            }
            flag=true;
        }
        score+=factor[0]*(1-(maxdis/max[0]));
        for(int y=1;y<dimension-1;y++){
            score+=factor[y]*(min[y-1]/max[y]);
        }
     //   System.out.println("  score : "+score);
        return score;
    }

    public double ComputeY(double p,double t,double s,double q){
        if(p>t)
            return (p-t);
        if(s>q)
            return (s-q);
        if(q<t && p<t && q>s && p>s) return 0;
       // if(q>s && p<s) return (s-p);
       //if(p<t && q>t) return (q-t);

        return 0;

    }

}
