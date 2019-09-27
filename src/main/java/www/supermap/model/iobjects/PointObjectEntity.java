package www.supermap.model.iobjects;

import com.supermap.data.GeoPoint;
/**
 * 点实体
 * @author SunYasong
 *
 */
public class PointObjectEntity extends GeoObjectEntity{
	private GeoPoint point;
	public PointObjectEntity(GeoPoint point,String entityType,String entityId) {
		super();
		this.entityType = entityType;
		this.point = point;
		this.entityId = entityId;
	}
	public GeoPoint getPoint() {
		return point;
	}
	public void setPoint(GeoPoint point) {
		this.point = point;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((point == null) ? 0 : point.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		PointObjectEntity other = (PointObjectEntity) obj;
		if (point == null) {
			if (other.point != null)
				return false;
		} else if (!point.equals(other.point))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "PointEntity [point=" + point + "]";
	}
	
}
