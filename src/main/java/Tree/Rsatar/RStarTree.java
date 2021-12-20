package Tree.Rsatar;


import Tree.Rsatar.dto.PointDTO;
import Tree.Rsatar.dto.TreeDTO;
import Tree.Rsatar.interfaces.IDtoConvertible;
import Tree.Rsatar.interfaces.ISpatialQuery;
import Tree.Rsatar.nodes.RStarInternal;
import Tree.Rsatar.nodes.RStarLeaf;
import Tree.Rsatar.nodes.RStarNode;
import Tree.Rsatar.nodes.RStarSplit;
import Tree.Rsatar.spatial.HyperRectangle;
import Tree.Rsatar.spatial.SpatialPoint;
import Tree.Rsatar.util.Constants;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class RStarTree implements ISpatialQuery, IDtoConvertible {

    private int dimension;
    private File saveFile;
    private StorageManager storage;
    private RStarNode root;
    private long rootPointer = -1;
    private RStarSplit splitManager;

    public RStarNode getRoot() {
        return root;
    }

    public void setRoot(RStarNode root) {
        this.root = root;
    }

    private float _pointSearchResult = -1;
    private ArrayList<SpatialPoint> _rangeSearchResult;
    private List<SpatialPoint> _knnSearchResult;
    private int bestSortOrder = -1;

    public RStarTree(int dimension) {
        this.dimension = dimension;
        this.saveFile = new File("MyRStarTree.rstar");
        this.storage = new StorageManager();
        this.splitManager = new RStarSplit(dimension, storage);

        storage.createDataDir(saveFile);
        setCapacities();
    }

    private void setCapacities(){
        Constants.DIMENSION = dimension;
//        Constants.MAX_CHILDREN = Constants.PAGESIZE/8;          // M = (pagesize - mbr_size)/ (size of Long = 8)
//        Constants.MIN_CHILDREN = Constants.MAX_CHILDREN/3;      // m = M/3
        Constants.MAX_CHILDREN = 20;
        Constants.MIN_CHILDREN = 5;
    }

    /* QUERY FUNCTIONS */

    /**
     * inserts a point in the tree and saves it on disk
     * @param point the point to be inserted
     * @return 1 if successful, else -1
     */
    @Override
    public int insert(SpatialPoint point) {
        System.out.println("inserting point with oid=" + point.getOid());
        RStarLeaf target = chooseLeaf(point);

        if (target.isNotFull()) {
            target.insert(point);
            storage.saveNode(target);
            //adjust root reference
            if (target.getNodeId() == rootPointer) {
                root = target;
            }
            adjustParentOf(target);
            return 1;
        } else {
            return treatLeafOverflow(target, point);
        }

    }

    /**
     * inserts a RStar node in the node pointed by nodePointer
     * @param nodePointer pointer to node in which the given node
     *                    is to be inserted
     * @param nodeToInsert the node to be inserted
     * @return 1 of successful, else -1
     */
    private int insertAt(Long nodePointer, RStarNode nodeToInsert) {
        storage.saveNode(nodeToInsert);
        RStarInternal target = (RStarInternal) loadNode(nodePointer);

        if (target.isNotFull()) {
            target.insert(nodeToInsert);

            if (target.getNodeId() == rootPointer) {
                root = target;
            }

            storage.saveNode(target);
            adjustParentOf(target);
            return 1;
        } else {
            return treatInternalOverflow(target, nodeToInsert);
        }
    }

    /**
     * searches for a spatial point in the tree and
     * returns its oid if its found.
     * @param point the point to be searched
     * @return oid of the point if found, else -1.
     */
    @Override
    public float pointSearch(SpatialPoint point) {
        _pointSearchResult = -1;
        loadRoot();
        _pointSearch(root, point);
        return _pointSearchResult;
    }

    private void _pointSearch(RStarNode start, SpatialPoint point) {
        HyperRectangle searchRegion = new HyperRectangle(point.getCords());
        HyperRectangle intersection = start.getMBR().getIntersection(searchRegion);

        if(intersection != null) {
            if (start.isLeaf()) {
                double[] searchPoints = point.getCords();

                //lazy loading of child points
                for (Long pointer : start.childPointers) {
                    PointDTO dto = storage.loadPoint(pointer);

                    double[] candidates = dto.coords;
                    boolean found = true;
                    for (int i = 0; i < candidates.length; i++) {
                        if (candidates[i] != searchPoints[i]){
                            found = false;
                            break;
                        }
                    }
                    if (found) {
                        _pointSearchResult = dto.oid;
                        break;
                    }
                }
            } else {
                for (Long pointer : start.childPointers) {
                    if(_pointSearchResult != -1)         // point found
                        break;

                    try {
                        RStarNode childNode = storage.loadNode(pointer);    //recurse down
                        _pointSearch(childNode, point);

                    } catch (FileNotFoundException e) {
                        System.err.println("Exception while loading node from disk. message = "+e.getMessage());
                    }
                }
            }
        }
    }


    public void printTree(RStarNode start){
        if (start.isLeaf()) {


            //lazy loading of child points
            for (Long pointer : start.childPointers) {
                PointDTO dto = storage.loadPoint(pointer);
                System.out.print("is leaf :");
                double[] candidates = dto.coords;
                boolean found = true;
                for (int i = 0; i < candidates.length; i++) {
                    System.out.print("cord "+i+" "+candidates[i]+" , ");
                }
                System.out.println();
            }
        } else {
            System.out.println("is node id : "+start);
            for (Long pointer : start.childPointers) {
                System.out.println("is node id : "+pointer);
                try {
                    RStarNode childNode = storage.loadNode(pointer);    //recurse down
                    printTree(childNode);
                } catch (FileNotFoundException e) {
                    System.err.println("Exception while loading node from disk. message = "+e.getMessage());
                }
            }
        }
    }

  /*  public PriorityQueue<KeywordCover<PointDTO>> getMax(PriorityQueue<KeywordCover<PointDTO>> queue){
        PriorityQueue<KeywordCover<PointDTO>> Newqueue = new PriorityQueue<KeywordCover<PointDTO>>(5, new Comparator< KeywordCover<PointDTO>>() {
            public int compare(KeywordCover<PointDTO> n1, KeywordCover<PointDTO> n2) {
                return Double.compare(n2.score, n1.score);
            }
        });
        while()
    }*/
    public KeywordCover<PointDTO> KNNE_search(Vector<RStarTree> T){
        int level=1;
        KeywordCover<PointDTO> bkc=null;
        PriorityQueue<LBKC> queue = new PriorityQueue<LBKC>(1000, new Comparator<LBKC>() {
            public int compare(LBKC n1, LBKC n2) {
                return Double.compare(n2.getScore(), n1.getScore());
            }
        });
        PriorityQueue<KeywordCover<PointDTO>> Keywordqueue = new PriorityQueue<KeywordCover<PointDTO>>(5, new Comparator< KeywordCover<PointDTO>>() {
            public int compare(KeywordCover<PointDTO> n1, KeywordCover<PointDTO> n2) {
                return Double.compare(n2.score, n1.score);
            }
        });
        level++;
      //  System.out.println("level : "+level);
        if(root.isLeaf()){
            for (Long pointer : root.childPointers) {
                LBKC temp = new LBKC();
                temp.setId(pointer);
                temp.setObj(true);
               temp.setCover(Local_Best_Keyword_Cover(T,storage.loadPoint(pointer)));
                temp.setScore(temp.getCover().score);
                queue.add(temp);
            }
        }else {
            for (Long pointer : root.childPointers) {
                LBKC temp = new LBKC();
                temp.setId(pointer);
                try {
                    temp.setNode(storage.loadNode(pointer));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                temp.setScore(Local_Best_Keyword_Cover(T, temp.getNode(), level));
                System.out.println(temp.getScore());
                queue.add(temp);
            }
        }

        while (!queue.isEmpty()){
            LBKC out=queue.remove();
            level++;
          //  System.out.println("level : "+level);
            if(!out.isObj()) {

                if (out.getNode().isLeaf()) {
                    for (Long pointer : out.getNode().childPointers) {
                        LBKC temp = new LBKC();
                        temp.setId(pointer);
                        temp.setObj(true);
                        temp.setCover(Local_Best_Keyword_Cover(T,storage.loadPoint(pointer)));
                        temp.setScore(temp.getCover().score);
                        queue.add(temp);
                    }
                } else {

                    for (Long pointer : out.getNode().childPointers) {
                        LBKC temp = new LBKC();
                        temp.setId(pointer);
                        try {
                            temp.setNode(storage.loadNode(pointer));
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                            temp.setScore(Local_Best_Keyword_Cover(T, temp.getNode(), level));

                        queue.add(temp);
                    }
                }
            }
            else{
                KeywordCover<PointDTO> t=out.getCover();
                Keywordqueue.add(t);
                System.out.println(Keywordqueue.size());
                if(bkc==null)
                    bkc=out.getCover();
                else if(out.getCover().score>bkc.score){
                    bkc=out.getCover();
                }
                Iterator<LBKC> i = queue.iterator();
                while (i.hasNext()) {
                    LBKC s = i.next(); // must be called before you can call i.remove()
                    if(s.getScore()<=bkc.score && !s.isObj())
                    {
                    i.remove();}
                }
               /* for(LBKC i:queue){
                    if(i.getScore()<=bkc.score)
                        queue.remove(i);
                }*/
            }
        }
      /*  System.out.println("another result start :");
        for(KeywordCover<PointDTO> TEMP: Keywordqueue)
        {
            System.out.println("result  : "+TEMP.score);
            for(int i=0;i<TEMP.result.size();i++){
                System.out.println("point id : "+TEMP.result.get(i).oid +"  cord 0 : "+TEMP.result.get(i).coords[0]+
                        "  cord 1 : "+TEMP.result.get(i).coords[1]+"  cord 2 : "+TEMP.result.get(i).coords[2]);
            }
            System.out.println("end result  :");
        }
        System.out.println("another result end");*/
        return bkc;
    }

    public double Local_Best_Keyword_Cover(Vector<RStarTree> T,RStarNode Nk,int level){

        PriorityQueue<KNN> knns = new PriorityQueue<KNN>(10000, new Comparator<KNN>() {
            public int compare(KNN n1, KNN n2) {
                return Double.compare(n2.result.getScore(), n1.result.getScore());
            }
        });
      //  System.out.println("local best node : T.size : "+T.size());
        KeywordCover<RStarNode> Kc=new KeywordCover<RStarNode>(T.size());
        for(int i=0;i<T.size();i++){
            KNN temp=new KNN(level,Nk,T.get(i));
            temp.getFirstNode();
            temp.setKeywordId(i);
            knns.add(temp);
            Kc.addFirstNode(i,temp.result.getNode());
        }
        Kc.addNodeToResult(Nk);
        while (!knns.isEmpty()){
            KNN m=knns.remove();
            double old=Kc.score;
          //  System.out.print("old kc : "+old+ " -- ");
            try {
                Kc.addNode(m.getKeywordId(), m.getNextNode().getNode());
                knns.add(m);
            //    System.out.println("new kc : "+old);
            }catch (NullPointerException e){

            }
            if(Kc.score >= old){
                Iterator<KNN> i = knns.iterator();
                while (i.hasNext()) {
                    KNN s = i.next(); // must be called before you can call i.remove()
                    if(s.result.getScore()<Kc.score)
                    {
                        i.remove();}
                }
                /*for(KNN i :knns){
                    if(i.result.getScore()<Kc.score)
                        knns.remove(i);
                }*/
            }

        }

        return Kc.score;
    }

    public KeywordCover<PointDTO> Local_Best_Keyword_Cover(Vector<RStarTree> T,PointDTO Pk){

        PriorityQueue<KNN> knns = new PriorityQueue<KNN>(10000, new Comparator<KNN>() {
            public int compare(KNN n1, KNN n2) {
                return Double.compare(n2.result.getScore(), n1.result.getScore());
            }
        });
       // System.out.println("local best node : T.size : "+T.size());
       KeywordCover<PointDTO> Kc=new KeywordCover<PointDTO>(T.size());
        for(int i=0;i<T.size();i++){
            KNN temp=new KNN(Pk,T.get(i));
            temp.getFirstObj();
            temp.setKeywordId(i);
            knns.add(temp);
            Kc.addFirstObj(i,temp.result.getPointDTO());
        }
        Kc.addObjToResult(Pk);
        while (!knns.isEmpty()){
            KNN m=knns.remove();
            double old=Kc.score;
           System.out.print("old kc : "+old+ " -- ");
            try {
                Kc.addObj(m.getKeywordId(), m.getNextObj().getPointDTO());
                knns.add(m);
            }catch (NullPointerException e){

            }
            System.out.println("new kc : "+Kc.score);
            if(Kc.score >= old){
                System.out.println();
                Iterator<KNN> i = knns.iterator();
                while (i.hasNext()) {
                    KNN s = i.next(); // must be called before you can call i.remove()
                    if(s.result.getScore()<Kc.score)
                    { // System.out.println("remove : "+s.result.getScore());
                        i.remove();
                    }
                }
               /* for(KNN i :knns){
                    if(i.result.getScore()<Kc.score)
                        knns.remove(i);
                }*/
            }

        }

        return Kc;
    }


    public RStarNode getNodeById(Long id){
        try {
            return storage.loadNode(id);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
    public PointDTO getPointById(Long id){
        return storage.loadPoint(id);
    }

    /**
     * searches for points in the given range of the center point
     * @param center center point of the search region.
     * @param range radius of the search region.
     * @return List of all the points found in the range
     */
    @Override
    public List<SpatialPoint> rangeSearch(SpatialPoint center, double range) {

        double[] points = center.getCords();
       double[][] mbrPoints = new double[dimension][2];
        for (int i = 0; i < dimension; i++) {
            mbrPoints[i][0] = points[i] + (float) range;
            mbrPoints[i][1] = points[i] - (float) range;
        }
        HyperRectangle searchRegion = new HyperRectangle(dimension);
        searchRegion.setPoints(mbrPoints);

        _rangeSearchResult = new ArrayList<SpatialPoint>();
        loadRoot();
        _rangeSearch(root, searchRegion);
        return _rangeSearchResult;
    }

    private void _rangeSearch(RStarNode start, HyperRectangle searchRegion) {
        HyperRectangle intersection = start.getMBR().getIntersection(searchRegion);
        if (intersection != null) {
            if (start.isLeaf()) {
                for (Long pointer : start.childPointers) {
                    PointDTO dto = storage.loadPoint(pointer);
                    SpatialPoint spoint = new SpatialPoint(dto);
                    HyperRectangle pointMbr = new HyperRectangle(dto.coords);

                    if(pointMbr.getIntersection(searchRegion) != null)
                        _rangeSearchResult.add(spoint);
                }
            }
            else {
                for (Long pointer : start.childPointers) {
                    try {
                        RStarNode childNode = storage.loadNode(pointer);    //recurse down
                        _rangeSearch(childNode, searchRegion);

                    } catch (FileNotFoundException e) {
                        System.err.println("Exception while loading node from disk");
                    }
                }
            }
        }
    }

    /**
     * searches for the k nearest neighbours of a center point
     * @param center SpatialPoint
     * @param k number of nearest neighbours required
     * @return List of the k nearest neighbours of center.
     */
    @Override
    public List<SpatialPoint> knnSearch(SpatialPoint center, int k) {
        loadRoot();
        _knnSearch(root, center, k, 1);
        _rangeSearchResult = new ArrayList<SpatialPoint>();
        return _knnSearchResult;
    }

    private void _knnSearch(RStarNode start, SpatialPoint center, int k, float range) {
        _rangeSearchResult = new ArrayList<SpatialPoint>();

        double[] points = center.getCords();
        double[][] mbrPoints = new double[dimension][2];
        for (int i = 0; i < dimension; i++) {
            mbrPoints[i][0] = points[i] + range;
            mbrPoints[i][1] = points[i] - range;
        }
        HyperRectangle searchRegion = new HyperRectangle(dimension);
        searchRegion.setPoints(mbrPoints);

        _rangeSearch(start, searchRegion);

        if (_rangeSearchResult.size() < k) {
            _knnSearch(start, center, k, 2 * range);
        } else {
            final SpatialPoint fcenter = center;
            Comparator<? super SpatialPoint> paramComparator = new Comparator<SpatialPoint>() {
                @Override
                public int compare(SpatialPoint point1, SpatialPoint point2) {
                    float deltaDist = fcenter.distance(point1) - fcenter.distance(point2);
                    if(deltaDist == 0)
                        return 0;
                    else
                        return (int)(deltaDist /(Math.abs(deltaDist)));
                }
            };
            Collections.sort(_rangeSearchResult, paramComparator);
            _knnSearchResult = _rangeSearchResult.subList(0, k);
        }
    }

    private int treatLeafOverflow(RStarLeaf target, SpatialPoint point) {
        try {
            splitLeaf(target, point);
            return 1;
        } catch (AssertionError e) {
            return -1;
        }
    }

    private int treatInternalOverflow(RStarInternal fullNode, RStarNode newChild) {
        try {
            splitInternalNode(fullNode, newChild);
            return 1;
        } catch (AssertionError e) {
            return -1;
        }
    }

    /**
     * inserts point into and splits the target leafnode
     * @param splittingLeaf the leaf to split
     * @param newPoint the point to be inserted
     * @throws AssertionError when the target node does
     * not have any children
     */
    private void splitLeaf(RStarLeaf splittingLeaf, SpatialPoint newPoint) throws AssertionError {
        RStarLeaf newChild = splitManager.splitLeaf(splittingLeaf, newPoint);
        if (splittingLeaf.getNodeId() == rootPointer) {
            //we just split root
            root = splittingLeaf;
            createRoot(newChild);
        } else {
            newChild.setParentId(splittingLeaf.getParentId());
            insertAt(splittingLeaf.getParentId(), newChild);
        }
    }

    /**
     * splits an internal node and inserts a new node
     * @param splittingNode the node to be split
     * @param node the node to be inserted
     */
    private void splitInternalNode(RStarInternal splittingNode, RStarNode node) {
        RStarNode createdNode;
        try {
            createdNode = splitManager.splitInternalNode(splittingNode, node);
            if (splittingNode.getNodeId() == rootPointer) {
                //we just split root
                root = splittingNode;
                createRoot(createdNode);
            } else {
                createdNode.setParentId(splittingNode.getParentId());
                insertAt(splittingNode.getParentId(), createdNode);
            }
        } catch (FileNotFoundException e) {
            System.err.println("Exception while loading node from disk. message: "+e.getMessage());
        }
    }

    /**
     * creates a new root and sets the old root (referred by this.root)
     * and siblingOfRoot its children
     * @param siblingOfRoot node created by splitting current root
     */
    private void createRoot(RStarNode siblingOfRoot) {
        RStarInternal newRoot = new RStarInternal(dimension);
        newRoot.setParentId(newRoot.getNodeId());
        newRoot.insert(root);
        newRoot.insert(siblingOfRoot);
        storage.saveNode(root);
        storage.saveNode(siblingOfRoot);
        storage.saveNode(newRoot);
        root = newRoot;
        rootPointer = newRoot.getNodeId();
    }

    /**
     * finds the most appropriate leaf node to
     * insert the newPoint into
     * @param newPoint the point to be inserted
     * @return RStarLeaf the most appropriate leaf to insert
     * newPoint
     */
    private RStarLeaf chooseLeaf(SpatialPoint newPoint) {
        loadRoot();
        SpatialPoint[] temp = new SpatialPoint[1];
        temp[0] = newPoint;


        return splitManager.chooseLeaf(root, new HyperRectangle(dimension, temp));
    }

    /**
     * updates mbr of all ancestor of a node
     * @param target updation starts from the parent of target
     */
    private void adjustParentOf(RStarNode target) {
        if (target.getNodeId() != rootPointer) {
            RStarNode parent = loadNode(target.getParentId());
            HyperRectangle mbr = parent.getMBR();
            mbr.update(target.getMBR());
            parent.setMbr(mbr);
            storage.saveNode(parent);
            if (parent.getNodeId() == rootPointer) {
                root = parent;
            }
            adjustParentOf(parent);
        }
    }

    /*
     ***** DISK RELATED FUNCTIONS ****
     */

    /**
     * loads root from disk if exists
     * otherwise creates a new LeafNode and
     * assigns it root.
     */
    private void loadRoot() {
        if (root == null) {
            //empty tree
            root = loadNode(rootPointer);
            if (root == null)            // still null -> empty tree
            {
                root = new RStarLeaf(dimension);
                root.setParentId(root.getNodeId());
            }
            rootPointer = root.getNodeId();
        }
    }

    /**
     * loads Nodes from disk using their nodeId
     * @param nodeId the nodeId attribute of the Node
     *               to be loaded
     * @return the Node required, null uf it doesn't exist
     */
    private RStarNode loadNode(long nodeId) {
        //check for valid nodeId
        if (nodeId != -1) {
            try {
                if (nodeId == rootPointer) {
                    loadRoot();
                    return root;
                } else {
                    return storage.loadNode(nodeId);
                }
            } catch (FileNotFoundException e) {
                System.err.println("Error while loading R* Tree node from file " + storage.constructFilename(nodeId));
            }
        }
        return null;
    }

    /**
     * saves the tree details to disk
     * @return 1 if successful, -1 otherwise
     */
    public int save() {
        return storage.saveTree(this.toDTO(), saveFile);
    }

    /**
     * converts this tree to its DTO representation
     * which in turn can be saved to disk.
     * @return TreeDTO object which is the DTO form of
     * this tree
     */
    @Override
    public TreeDTO toDTO() {
        return new TreeDTO(dimension, Constants.PAGESIZE, rootPointer);
    }

    /*private void loadTree() {
        if (saveFile.exists() && saveFile.length() != 0) {
            try {
                TreeDTO treeData = storage.loadTree(saveFile);
                if (treeData != null) {             //update tree fields from saveFile
                    this.dimension = treeData.dimension;
                    this.pagesize = treeData.pagesize;
                    this.rootPointer = treeData.rootPointer;
                    System.out.printf("Tree loaded successfully from %s. dimension = %d and pagesize = %d bytes%n",
                            saveFile.getName(), dimension, pagesize);
                }
            } catch (FileNotFoundException e) {
                System.err.println("Failed to load R* Tree from "+saveFile.getName());
            }

        }
    }*/
}
