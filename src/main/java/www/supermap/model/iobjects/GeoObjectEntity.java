package www.supermap.model.iobjects;

import java.util.ArrayList;
/**
 * 抽象地理实体类，包含了一些点线面的共同点
 * @author SunYasong
 *
 */
public abstract class GeoObjectEntity {
	protected int cellLevel;
	protected String entityType;
	protected String entityId;
	protected ArrayList<Long> cellIds;
	protected String time;
	protected GeoObjectEntity() {
		super();
		// TODO Auto-generated constructor stub
	}
	public int getCellLevel() {
		return cellLevel;
	}
	public void setCellLevel(int cellLevel) {
		this.cellLevel = cellLevel;
	}
	public String getEntityType() {
		return entityType;
	}
	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}
	public String getEntityId() {
		return entityId;
	}
	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}
	public ArrayList<Long> getCellIds() {
		return cellIds;
	}
	public void setCellIds(ArrayList<Long> cellIds) {
		this.cellIds = cellIds;
	}
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cellIds == null) ? 0 : cellIds.hashCode());
		result = prime * result + cellLevel;
		result = prime * result + ((entityId == null) ? 0 : entityId.hashCode());
		result = prime * result + ((entityType == null) ? 0 : entityType.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GeoObjectEntity other = (GeoObjectEntity) obj;
		if (cellIds == null) {
			if (other.cellIds != null)
				return false;
		} else if (!cellIds.equals(other.cellIds))
			return false;
		if (cellLevel != other.cellLevel)
			return false;
		if (entityId == null) {
			if (other.entityId != null)
				return false;
		} else if (!entityId.equals(other.entityId))
			return false;
		if (entityType == null) {
			if (other.entityType != null)
				return false;
		} else if (!entityType.equals(other.entityType))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "GeoEntity [cellLevel=" + cellLevel + ", entityType=" + entityType + ", entityId=" + entityId
				+ ", cellIds=" + cellIds + "]";
	}
		
}
