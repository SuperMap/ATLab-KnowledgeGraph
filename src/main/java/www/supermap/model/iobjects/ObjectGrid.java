package www.supermap.model.iobjects;

import java.util.ArrayList;

public class ObjectGrid {
	//网格的id
	private Long id;
	//空间实体对象
	private ArrayList<GeoObjectEntity> geoEntitys;
	
	public ObjectGrid(Long id, ArrayList<GeoObjectEntity> geoEntitys) {
		super();
		this.id = id;
		this.geoEntitys = geoEntitys;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public ArrayList<GeoObjectEntity> getGeoEntitys() {
		return geoEntitys;
	}
	public void setGeoEntitys(ArrayList<GeoObjectEntity> geoEntitys) {
		this.geoEntitys = geoEntitys;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((geoEntitys == null) ? 0 : geoEntitys.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		ObjectGrid other = (ObjectGrid) obj;
		if (geoEntitys == null) {
			if (other.geoEntitys != null)
				return false;
		} else if (!geoEntitys.equals(other.geoEntitys))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Grid [id=" + id + ", geoEntitys=" + geoEntitys + "]";
	}

	
}
