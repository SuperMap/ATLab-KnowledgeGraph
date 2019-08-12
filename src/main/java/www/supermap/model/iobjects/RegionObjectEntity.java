package www.supermap.model.iobjects;

import com.supermap.data.GeoRegion;

public class RegionObjectEntity  extends GeoObjectEntity{

	private GeoRegion region;
	
	public RegionObjectEntity(GeoRegion region,String entityType,String entityId) {
		// TODO Auto-generated constructor stub
		this.entityId = entityId;
		this.region = region;
		this.entityType = entityType;
	}

	public GeoRegion getMultiPolygon() {
		return region;
	}

	public void setMultiPolygon(GeoRegion region) {
		this.region = region;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((region == null) ? 0 : region.hashCode());
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
		RegionObjectEntity other = (RegionObjectEntity) obj;
		if (region == null) {
			if (other.region != null)
				return false;
		} else if (!region.equals(other.region))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "RegionEntity [multiPolygon=" + region + "]";
	}
	

}
