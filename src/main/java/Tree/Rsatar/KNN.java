package Tree.Rsatar;

import Tree.Rsatar.dto.PointDTO;
import Tree.Rsatar.nodes.RStarNode;

import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;

/**
 * Created by kimia on 3/19/2017.
 */
public class KNN {
    private int keywordId=0;
    private int level;
    boolean flag=false;
    private RStarNode node;
    private PointDTO dto;
    private RStarTree tree;
    private PriorityQueue<LBKC> queue ;
    private int i=1;
    int ki=0;
    LBKC result;

    public int getKeywordId() {
        return keywordId;
    }

    public void setKeywordId(int keywordId) {
        this.keywordId = keywordId;
    }

    private Score score;

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public RStarNode getNode() {
        return node;
    }

    public void setNode(RStarNode node) {
        this.node = node;
    }

    public PointDTO getDto() {
        return dto;
    }

    public void setDto(PointDTO dto) {
        this.dto = dto;
    }

    public RStarTree getTree() {
        return tree;
    }

    public void setTree(RStarTree tree) {
        this.tree = tree;
    }

    public KNN(){
        queue = new PriorityQueue<LBKC>(100, new Comparator<LBKC>() {
            public int compare(LBKC n1, LBKC n2) {
                return Double.compare(n2.getScore(), n1.getScore());
            }
        });
        score=Score.getInstance();
    }
    public KNN(int level,RStarNode node,RStarTree tree){
        this.level=level;
        this.node=node;
        this.tree=tree;
        flag=true;
        queue = new PriorityQueue<LBKC>(100, new Comparator<LBKC>() {
            public int compare(LBKC n1, LBKC n2) {
                return Double.compare(n2.getScore(), n1.getScore());
            }
        });
        score=Score.getInstance();
    }
    public KNN(PointDTO dto,RStarTree tree){
        this.dto=dto;
        this.tree=tree;
        flag=true;
        queue = new PriorityQueue<LBKC>(100, new Comparator<LBKC>() {
            public int compare(LBKC n1, LBKC n2) {
                return Double.compare(n2.getScore(), n1.getScore());
            }
        });
        score=Score.getInstance();
        if(score.getFactor()[0]==0){
            flag=false;
        }
    }

    public LBKC getFirstObj(){

        LBKC root=new LBKC();
       // System.out.println("root id : "+tree.getRoot().getNodeId());
        root.setId(tree.getRoot().getNodeId());
        root.setScore(score.getScore(tree.getRoot(),dto));
        queue.add(root);
        System.out.println(queue.size());
        LBKC first=getNextObj();
        return first;
    }
    public LBKC getNextObj() {

        while (!queue.isEmpty()){
            LBKC l=queue.remove();
           // System.out.print("remove from queue id : "+l.getId()+" ");
            if (l.isObj()){
         //       System.out.print("l is obj "+l.getScore()+"  cord : ");
                l.getPointDTO().print();
                ki++;
                result=l;
                return l;
            }
            else {
               // System.out.print ("l is node , ");
                RStarNode n=tree.getNodeById(l.getId());
                l.setNode(n);
                if(n.isLeaf()){
                   // System.out.print("l is leaf  , ");
                  // System.out.println("l score : "+l.getScore());
                    for (Long pointer : n.childPointers) {
                        LBKC temp = new LBKC();
                        temp.setId(pointer);
                        temp.setObj(true);

                        temp.setPointDTO(tree.getPointById(pointer));
                        temp.setScore(score.getScore(dto,temp.getPointDTO()));



                            queue.add(temp);
                            Iterator<LBKC> i = queue.iterator();
                            while (i.hasNext()) {
                                LBKC s = i.next(); // must be called before you can call i.remove()
                                if (s.getScore() < temp.getScore() && !s.isObj()) {
                                    i.remove();
                                }
                            }

                      /*  else if(temp.getPointDTO().coords[2]==5){
                            queue.add(temp);
                            Iterator<LBKC> i = queue.iterator();
                            while (i.hasNext()) {
                                LBKC s = i.next(); // must be called before you can call i.remove()
                                if (s.getScore() < temp.getScore() && !s.isObj()) {
                                    i.remove();
                                }
                            }
                        }*/
                    }
                }
                else{
                   //System.out.println("l score : "+l.getScore());
                    for (Long pointer : n.childPointers) {
                        LBKC temp = new LBKC();
                        temp.setId(pointer);
                        temp.setObj(false);

                        temp.setNode(tree.getNodeById(pointer));
                        temp.setScore(score.getScore(temp.getNode(),dto));
                       // System.out.print("child node  score : "+temp.getScore()+" ");
                        queue.add(temp);
                    }
                    System.out.println();
                }

            }
        }

        return  null;
    }

    public LBKC getFirstNode(){
        i=1;
        LBKC root=new LBKC();
        root.setId(tree.getRoot().getNodeId());
        root.setScore(0);
        queue.add(root);
        return  getNextNode();
    }
    public LBKC getNextNode(){

        while (!queue.isEmpty()){
         //   System.out.println("in while next node ");
            LBKC l=queue.remove();
            if(i==level){
                ki++;
                result=l;
                return l;
            }
            else if (!l.isObj() && i<=level){
                RStarNode n=tree.getNodeById(l.getId());
                l.setNode(n);
                if(!n.isLeaf()){
                    for (Long pointer : n.childPointers) {
                   //     System.out.print("add node -- ");
                        LBKC temp = new LBKC();
                        temp.setId(pointer);
                        temp.setObj(false);
                        temp.setLevel(i);
                     ///   System.out.println("add node obj");
                        temp.setNode(tree.getNodeById(pointer));

                        temp.setScore(score.getScore(temp.getNode(),node));
                     //   System.out.println("temp distance node id "+temp.getNode().getNodeId() +" with node "+node.getNodeId()
                             //   +" is  : "+temp.getScore());
                        queue.add(temp);
                        if(i==level){
                            Iterator<LBKC> y = queue.iterator();
                            while (y.hasNext()) {
                                LBKC s = y.next(); // must be called before you can call i.remove()
                                if (s.getScore() < temp.getScore() && s.getLevel()<level) {
                                    y.remove();
                                }
                            }
                        }
                    }
                    i++;
                }
                else i=level;
            }
        }

        return  null;
    }

    public PointDTO getFirstPoint(){

        return null;

    }


}
