package Tree.Rsatar.spatial;

import Tree.Rsatar.dto.PointDTO;
import Tree.Rsatar.interfaces.IDtoConvertible;
import org.json.simple.JSONObject;

;

public class SpatialPoint implements IDtoConvertible {
    private int _dimension;
    private double[] _cords;
    private float  _oid;
    private JSONObject info;

    public SpatialPoint() {
    }

    public SpatialPoint(int dimension) {
        this._dimension = dimension;
        this._oid = -1;
    }

    public SpatialPoint(double[] cords) {
        this._cords = cords;
        this._dimension = cords.length;
        this._oid = -1;
    }

    public SpatialPoint(double[] cords, float oid,JSONObject info) {
        this._cords = cords;
        this._dimension = cords.length;
        this._oid = oid;
        this.info=info;
    }

    public SpatialPoint(PointDTO dto) {
        this._cords = dto.coords;
        this._dimension = dto.coords.length;
        this._oid = dto.oid;
    }

    public int getDimension(){
        return _dimension;
    }

    public void setCords(double[] data){
        this._cords = data;
    }

    public double[] getCords() {
        return _cords;
    }

    public float getOid() {
        return _oid;
    }

    public void setOid(float oid) {
        this._oid = oid;
    }

    /**
     * calculate distance of this point with <pre>otherPoint</pre>
     * @param otherPoint the point from which this point's
     *                   distance is to be calculated
     * @return distance from <pre>otherPoint</pre>
     */
    public float distance(SpatialPoint otherPoint) {
        double[] otherPoints = otherPoint.getCords();
        float distance = 0;
        for (int i = 0; i < _cords.length; i++) {
            double tmp = (_cords[i] * _cords[i]) - (otherPoints[i] * otherPoints[i]);
            if(tmp < 0)
                tmp = -1 * tmp;

            distance += tmp;
        }
        return (float)Math.pow(distance, 0.5);
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("[");
        for (double cord : _cords) {
            str.append(cord).append(",");
        }
        str.append("]");
        return str.toString();
    }


    public PointDTO toDTO() {
        return new PointDTO(_oid, _cords,info);
    }
}
