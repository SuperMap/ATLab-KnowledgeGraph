package www.supermap.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.geometry.S2Cap;
import com.google.common.geometry.S2CellId;
import com.google.common.geometry.S2LatLng;
import com.google.common.geometry.S2LatLngRect;
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
import com.supermap.data.Point2Ds;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
public class S2 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		S2LatLng laln = S2LatLng.fromDegrees(24.48831170375997, 109.7406709288192);
		S2CellId cell = S2CellId.fromLatLng(laln).parent(15);
		S2CellId celll = new S2CellId(cell.id());
		getCoveringCellIdsFromCell(celll, 15000.0, 15);
	}
	
	/**
	 * 通过S2CellId得到geotools支持的坐标系
	 * @param cell
	 * @return 
	 */
	public static Coordinate convertCoordinateFromS2ToGeotool(S2CellId cell){
		S2LatLng laln = cell.toLatLng();
		Double ln = laln.lngDegrees();
		Double la = laln.latDegrees();
		Coordinate coord = new Coordinate(ln, la);
		return coord;
	}
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
	
	public static Long getCellId(Point point,int cellLevel){
		Double lng = point.getX();
		Double lat = point.getY();
		S2LatLng laln = S2LatLng.fromDegrees(lat, lng);
		S2CellId cell = S2CellId.fromLatLng(laln).parent(cellLevel);
		return cell.id();
	}
	
	/**
	 * 获得点占据的cell
	 * @param coordinate
	 * @param gridLevel
	 * @return
	 */
	public static ArrayList<Long> getPointCoveringCell(Coordinate coordinate, int gridLevel){
		ArrayList<Long> cellIds = new ArrayList<Long>();
		S2LatLng laln = S2LatLng.fromDegrees(coordinate.y, coordinate.x);
		S2CellId cell = S2CellId.fromLatLng(laln).parent(gridLevel);
		cellIds.add(cell.id());
		return cellIds;
	}
	public static void rabbish(){
		S2LatLng start = S2LatLng.fromDegrees(24.48831170375997, 109.7406709288192);
		S2LatLng end = S2LatLng.fromDegrees(24.316190270496378, 109.38342575075413);
		Double dis = start.getEarthDistance(end);
		// Double placeDistance = math.
		System.out.println(dis);
		System.out.println(S2CellId.fromLatLng(start).parent(8));
		System.out.println(S2CellId.fromLatLng(end).parent(8));
		S2CellId cell = S2CellId.fromLatLng(start).parent(8);
		long a = cell.id();
		System.out.println(cell);
		// System.out.println(cell.pos());
		// Long i = 3937641410135588864;
		S2CellId id = new S2CellId(a);
		S2LatLng la = id.toLatLng();
		double laD = la.latDegrees();
		double loD = la.lngDegrees();
		System.out.println(laD + "---" + loD);

		System.out.println("-----------------------");
		// 创建圆形区域
		double capHeight = 15; // 半径
		S2LatLng s2LatLng = S2LatLng.fromDegrees(24.48831170375997, 109.7406709288192);
		S2Region cap = (S2Region) S2Cap.fromAxisHeight(s2LatLng.toPoint(), capHeight);
		// System.out.println(cap.height());
		S2RegionCoverer ner = new S2RegionCoverer();
		// ner.setMinLevel(30);
		// ner.setMaxLevel(30);
		ner.setMaxCells(20000);
		ArrayList<S2CellId> arr = ner.getCovering(cap).cellIds();
		for (S2CellId sd : arr) {
			System.out.println(sd);

		}
		
		
	}
	
	/**
	 * 计算传入的几何图形，来计算cell
	 * @param geometry
	 * @param gridLevel
	 */
	public static ArrayList<Long> getCovingCells(Geometry geometry, int gridLevel) {	
		/*
		 * 分情况讨论：
		 * 1.如果是点，就简单了，直接返回
		 * 2.如果是线或多线，暂时只管所有线的顶点
		 * 3.如果是面：
		 * 		3.1  单面：取所有的顶点构建闭合区域，s2有方法直接获取所有的cell
		 * 		3.2  多面：想法：取出第一个面的所有顶点构建闭合区域，后续与单面相同。   因为多个面的情况，第二个开始是在第一个上挖洞。
		 * 		3.3总结：可以抽象出一个方法：由于get不用管是什么类型，getCoordinates()都将按顺序返回所有坐标的一维数组，因此保留第一个坐标，然后循环到该坐标或数组遍历完毕，就取出了最大的单面，符合要求。
		 * 4.S2里面找线的cell与面是类似的
		 */
		
		ArrayList<S2Point> s2Points = new ArrayList<S2Point>();
		Coordinate[] coordinates = geometry.getCoordinates();
		if(geometry instanceof Point){
			//处理点
			Coordinate firstCoordinate = geometry.getCoordinate();
			return getPointCoveringCell(firstCoordinate,gridLevel);
		}else if(geometry instanceof LineString||geometry instanceof MultiLineString){
			//循环处理每一条线
			return getLineCoveringCells(coordinates,gridLevel);
		}else if (geometry instanceof Polygon||geometry instanceof MultiPolygon){
			//只处理第一个面
			return getPolygonCoveringCells(coordinates,gridLevel);
		}
		return null;

	}
	
	
	/**
	 * 返回一个面占据的S2网格
	 * @param coordinates
	 * @param gridLevel
	 * @return
	 */
	private static ArrayList<Long> getPolygonCoveringCells(Coordinate[] coordinates, int gridLevel) {
		// TODO Auto-generated method stub	
		//取出第一个面的所有转成S2Point的点（没有重复点）
		Coordinate firstCoordinate = coordinates[0];
		ArrayList<S2Point> s2Points = new ArrayList<S2Point>();		
		for(int i =1; i<coordinates.length;i++){
			S2Point point = S2LatLng.fromDegrees(coordinates[i].y, coordinates[i].x).toPoint();
			s2Points.add(point);
			if((firstCoordinate.x==coordinates[i].x)&&(firstCoordinate.y==coordinates[i].y)){
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
//		System.out.println(s2Points.size());
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

	/**
	 * 获得线或多线经过的s2的cell。循环处理每一条线，将所有线经过的cell混起来。
	 * @param coordinates
	 * @param gridLevel
	 * @return
	 */
	private static ArrayList<Long> getLineCoveringCells(Coordinate[] coordinates, int gridLevel) {
		// TODO Auto-generated method stub
		if(coordinates.length<2){
			return new ArrayList<Long>();
		}
		HashSet<Long> cellIds = new HashSet<Long>();
		for(int i =0;i<coordinates.length;i+=2){
			ArrayList<S2Point> s2Points = new ArrayList<S2Point>();
			S2Point beginPoint = S2LatLng.fromDegrees(coordinates[i].y, coordinates[i].x).toPoint();
			S2Point endPoint = S2LatLng.fromDegrees(coordinates[i+1].y, coordinates[i+1].x).toPoint();
			s2Points.add(beginPoint);
			s2Points.add(endPoint);
			S2Polyline line = new S2Polyline(s2Points);
			S2RegionCoverer cover = new S2RegionCoverer();
			cover.setMaxLevel(gridLevel);
			cover.setMinLevel(gridLevel);
			ArrayList<S2CellId> covering = new ArrayList<S2CellId>();
			cover.getCovering(line, covering);
			for (S2CellId s2CellId : covering) {
				cellIds.add(s2CellId.id());
			}
		}
		return new ArrayList<Long>(cellIds);
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
