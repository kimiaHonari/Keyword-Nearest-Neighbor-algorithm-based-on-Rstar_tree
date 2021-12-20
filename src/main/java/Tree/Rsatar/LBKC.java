package Tree.Rsatar;

import Tree.Rsatar.dto.PointDTO;
import Tree.Rsatar.nodes.RStarNode;

/**
 * Created by kimia on 3/18/2017.
 */
public class LBKC {
    private double score;
    private Long id;
    int level;
    private boolean obj=false;
    private RStarNode node;
    private PointDTO pointDTO;
    private KeywordCover<PointDTO> cover;

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public KeywordCover<PointDTO> getCover() {
        return cover;
    }

    public void setCover(KeywordCover<PointDTO> cover) {
        this.cover = cover;
    }

    public PointDTO getPointDTO() {
        return pointDTO;
    }

    public void setPointDTO(PointDTO pointDTO) {
        this.pointDTO = pointDTO;
    }

    public RStarNode getNode() {
        return node;
    }

    public void setNode(RStarNode node) {
        this.node = node;
    }

    public boolean isObj() {
        return obj;
    }

    public void setObj(boolean obj) {
        this.obj = obj;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
}
