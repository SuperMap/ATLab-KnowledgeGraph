package www.supermap.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import com.google.common.geometry.S2Cap;
import com.google.common.geometry.S2CellId;
import com.google.common.geometry.S2LatLng;
import com.google.common.geometry.S2Loop;
import com.google.common.geometry.S2Point;
import com.google.common.geometry.S2Polygon;
import com.google.common.geometry.S2Polyline;
import com.google.common.geometry.S2Region;
import com.google.common.geometry.S2RegionCoverer;
import com.supermap.data.GeoLine;
import com.supermap.data.GeoPoint;
import com.supermap.data.GeoRegion;
import com.supermap.data.Point2D;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
/**
 * google s2相关的方法
 * @author SunYasong
 *
 */
public class S2 {
	/**
	 * 指定网格与缓冲区半径及网格层级，找到缓冲区的所有符合要求的网格
	 * @param cell 指定的中心网格
	 * @param radius 缓冲区半径，单位：米
	 * @param cellLevel 层级
	 * @return
	 */
	public static ArrayList<S2CellId> getCoveringCellIdsFromCell(S2CellId cell,Double radius,int cellLevel){
		S2LatLng la = cell.toLatLng();
		S2Point point = la.toPoint();
		Double capHeight = 1-Math.cos(radius/12742000);
		S2Cap cap = S2Cap.fromAxisHeight(point, capHeight);
		S2RegionCoverer cover = new S2RegionCoverer();
	    cover.setMaxLevel(cellLevel);
	    cover.setMinLevel(cellLevel);
//	    cover.setMaxCells(30);
	    ArrayList<S2CellId> covering = new ArrayList<S2CellId>();
	    cover.getCovering(cap, covering);
	    return covering;
	}
	
	/**
	 * 指定经纬度缓冲区半径及网格层级来得到符合要求的网格
	 * @param laln 经纬度
	 * @param radius 半径：米
	 * @param cellLevel 网格层级
	 * @return
	 */
	public static ArrayList<S2CellId> getCoveringCellIdsFromLatlng(S2LatLng laln,Double radius,int cellLevel){
		S2Point point = laln.toPoint();
		Double capHeight = 1-Math.cos(radius/12742000);
		S2Cap cap = S2Cap.fromAxisHeight(point, capHeight);
		S2RegionCoverer cover = new S2RegionCoverer();
	    cover.setMaxLevel(cellLevel);
	    cover.setMinLevel(cellLevel);
//	    cover.setMaxCells(30);
	    ArrayList<S2CellId> covering = new ArrayList<S2CellId>();
	    cover.getCovering(cap, covering);
	    return covering;
	}
	
	/**
	 * 按照网格长度计算网格级别
	 * @param gridLength
	 * @return
	 */
	public static int getCellLevelFromLength(Double gridLength) {
		// TODO Auto-generated method stub
		double[] level = {9220000,4610000,2454000,1283000,643000,322000,161000,79000,40000,20000,10000,5000,2500,1260,632,315,157,78,39,19,9.8};
		for (int i = 0; i < level.length; i++) {
			if(gridLength>=level[i]) {
				return i;
			}
		}
		return level.length-1;
	}

	/**
	 * 通过cell的id计算网格等级
	 * @param cellId
	 * @return
	 */
	public static int getCellLevelFromId(Long cellId) {
		// TODO Auto-generated method stub
		S2CellId cell = new S2CellId(cellId);
		return cell.level();
	}

	/**
	 * 获得GeoPoint所在的cell
	 * @param point
	 * @param gridLevel
	 * @return
	 */
	public static ArrayList<Long> getGeoPointCoveringCell(GeoPoint point, int gridLevel) {
		// TODO Auto-generated method stub
		ArrayList<Long> cellIds = new ArrayList<Long>();
		S2LatLng laln = S2LatLng.fromDegrees(point.getY(), point.getX());
		S2CellId cell = S2CellId.fromLatLng(laln).parent(gridLevel);
		cellIds.add(cell.id());
		return cellIds;
	}
	
	/**
	 * 获得GeoLine所覆盖的cell集合
	 * @param line
	 * @param gridLevel
	 * @return
	 */
	public static ArrayList<Long> getGeoLineCoveringCells(GeoLine line, int gridLevel) {
		// TODO Auto-generated method stub
		HashSet<Long> cellIds = new HashSet<Long>();
		for (int i = 0; i < line.getPartCount(); i++) {
			ArrayList<S2Point> s2Points = new ArrayList<S2Point>();
			Point2D[] point2ds = line.getPart(i).toArray();
			S2Point beginPoint = S2LatLng.fromDegrees(point2ds[0].getY(), point2ds[0].getX()).toPoint();
			S2Point endPoint = S2LatLng.fromDegrees(point2ds[1].getY(), point2ds[1].getX()).toPoint();
			s2Points.add(beginPoint);
			s2Points.add(endPoint);
			S2Polyline polyLine = new S2Polyline(s2Points);
			S2RegionCoverer cover = new S2RegionCoverer();
			cover.setMaxLevel(gridLevel);
			cover.setMinLevel(gridLevel);
			ArrayList<S2CellId> covering = new ArrayList<S2CellId>();
			cover.getCovering(polyLine, covering);
			for (S2CellId s2CellId : covering) {
				cellIds.add(s2CellId.id());
			}
		}
		return new ArrayList<Long>(cellIds);
	}

	/**
	 * 获得GeoRegion所覆盖的面所占的cell
	 * @param region
	 * @param gridLevel
	 * @return
	 */
	public static ArrayList<Long> getGeoRegionCoveringCells(GeoRegion region, int gridLevel) {
		// TODO Auto-generated method stub
		//取出第一个面的所有转成S2Point的点（没有重复点）
			Point2D[] points = region.getPart(0).toArray();
			Point2D firstPoint = points[0];
			ArrayList<S2Point> s2Points = new ArrayList<S2Point>();		
			for (int i = 1; i < points.length; i++) {
				S2Point point = S2LatLng.fromDegrees(points[i].getY(), points[i].getX()).toPoint();
				s2Points.add(point);
				if((firstPoint.getX()==points[i].getX())&&(firstPoint.getY()==points[i].getY())){
					break;
				}
			}
			/**
			 * 找到S2point按逆时针存的集合
			 * 思路：先找到一个Z轴最大值的点，尽量保证是凸点，然后找到相邻的两个点，计算法向量，判断z轴的值。
			 * 举例：找到p1点，集合中p1的前一个点则为p2，后一个点为p3，用向量p1p2与p3p1叉乘计算法向量，然后指定一个向量为（0,0,1），计算两者之间的值，夹角小于90值为正，反之则为负。由于指定向量的关系，逆时针的为正，所以计算出为正，直接使用。反之则翻转。
			 */
			//找到z值最大的点
			int maxIndex = 0;
			for (int i=1; i<s2Points.size();i++) {
				if(s2Points.get(i).get(2)>s2Points.get(maxIndex).get(2)){
					maxIndex = i;
				}
			}
			//找到相邻的3个点,放进数组
			S2Point[] s2Arr = new S2Point[3];
			if(maxIndex!=0&&maxIndex!=s2Points.size()-1){
				s2Arr[0]=s2Points.get(maxIndex-1);
				s2Arr[1]=s2Points.get(maxIndex);
				s2Arr[2]=s2Points.get(maxIndex+1);
			}
			else if(maxIndex==0){
				s2Arr[0]=s2Points.get(s2Points.size()-1);
				s2Arr[1]=s2Points.get(0);
				s2Arr[2]=s2Points.get(1);
			}else{
				s2Arr[0]=s2Points.get(maxIndex-1);
				s2Arr[1]=s2Points.get(maxIndex);
				s2Arr[2]=s2Points.get(0);
			}
			//向量叉乘。第二个点与第一个点组成向量叉乘第三个点与第二个点组成的向量
			S2Point firstVer = S2Point.sub(s2Arr[0],s2Arr[1]);
			S2Point endVer = S2Point.sub(s2Arr[1],s2Arr[2]);
			S2Point crossVaule = S2Point.crossProd(firstVer, endVer);
			//以z值为判断条件，大于0则世界使用，小于0则翻转
			if(crossVaule.get(2)<0){
				Collections.reverse(s2Points);  
			}
//				System.out.println(s2Points.size());
			S2Loop s2Loop = new S2Loop(s2Points);
			S2Polygon polygon = new S2Polygon(s2Loop); // 创建多边形	
			S2RegionCoverer cover = new S2RegionCoverer();		
			cover.setMaxLevel(gridLevel);
			cover.setMinLevel(gridLevel);
			ArrayList<S2CellId> covering = new ArrayList<S2CellId>();
			cover.getCovering(polygon, covering);
			ArrayList<Long> cellIds = new ArrayList<Long>();
			for (S2CellId s2CellId : covering) {
				cellIds.add(s2CellId.id());
			}
			return cellIds;
	}
	


}
