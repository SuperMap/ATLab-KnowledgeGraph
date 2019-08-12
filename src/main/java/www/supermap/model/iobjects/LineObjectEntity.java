package www.supermap.model.iobjects;

import com.supermap.data.GeoLine;

public class LineObjectEntity extends GeoObjectEntity{

	private GeoLine line;
	public LineObjectEntity(GeoLine line, String entityType,String entityId) {
		// TODO Auto-generated constructor stub
		this.entityId = entityId;
		this.line =line;
		this.entityType = entityType;
	}
	public GeoLine getLine() {
		return line;
	}
	public void setLine(GeoLine line) {
		this.line = line;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((line == null) ? 0 : line.hashCode());
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
		LineObjectEntity other = (LineObjectEntity) obj;
		if (line == null) {
			if (other.line != null)
				return false;
		} else if (!line.equals(other.line))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "MultiLine [multiLine=" + line + "]";
	}

	
}
