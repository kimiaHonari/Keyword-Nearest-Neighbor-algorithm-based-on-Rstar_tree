package Tree.Rsatar.dto;

import org.json.simple.JSONObject;

public class PointDTO extends AbstractDTO{
    public float oid;
    public double[] coords;
    public JSONObject info;


    public PointDTO(float oid, double[] coords,JSONObject info) {
        this.oid = oid;
        this.coords = coords;
        this.info=info;
    }
    public void print(){
        for(int i=0;i<coords.length;i++)
            System.out.print(coords[i]+" , ");
        System.out.println();
    }
}
