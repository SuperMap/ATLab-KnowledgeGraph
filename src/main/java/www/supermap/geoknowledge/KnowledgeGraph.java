package www.supermap.geoknowledge;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.common.geometry.S2CellId;
import com.google.common.geometry.S2LatLng;
import com.supermap.data.CoordSysTransMethod;
import com.supermap.data.CoordSysTransParameter;
import com.supermap.data.CoordSysTranslator;
import com.supermap.data.Dataset;
import com.supermap.data.Datasource;
import com.supermap.data.DatasourceConnectionInfo;
import com.supermap.data.EngineType;
import com.supermap.data.PrjCoordSys;
import com.supermap.data.Workspace;

import www.supermap.model.iobjects.ObjectGrid;
import www.supermap.model.iobjects.RecordSetEntity;
import www.supermap.utils.Common;
import www.supermap.utils.Iobjects;
import www.supermap.utils.Rdf4j;
import www.supermap.utils.S2;

public class KnowledgeGraph{
	//知识图谱的配置文件
	private static final String CONFIGFILE = "KnowledgeGraph.conf";
	//图谱存储目录
	private static final String KNOWLEDGE_STORE_DIR = "KnowledgeStore";
	//原始数据存储目录
	private static final String ORIGIN_DATA_DIR = "OriginData";
	//数据存储的根目录
	private String storeDir;
	//图谱构建使用的网格级别
	private int gridLevel = 13;
	/** 
	 * 加载默认配置下的图谱，如果不存在则构建一个空的知识图谱。默认参数：网格级别为13，图谱存储目录为当前项目根目录的GeoKnowledgeStore\\
	 */
	public KnowledgeGraph() {
		// TODO Auto-generated constructor stub
	}
	
	private KnowledgeGraph(int gridLevel, String storeDir) {
		// TODO Auto-generated constructor stub
		this.gridLevel = gridLevel;
		this.storeDir = storeDir;
	}

	/**
	 * 根据网格等级和图谱存储目录的路径来构建一个知识图谱
	 * @param iGridLevel 网格等级
	 * @param strDataStore
	 * @return
	 */
	public static boolean createKnowledgeGraph(int iGridLevel,String strDataStore){
//		KnowledgeGraph know = new KnowledgeGraph(iGridLevel,strDataStore);
		graphInit(strDataStore,iGridLevel);
		return true;
	}
	
	/**
	 * 根据网格长度和图谱存储目录的路径来构建一个新的知识图谱
	 * @param iGridLength
	 * @param strDataStore
	 * @return
	 */
	public static boolean createKnowledgeGraph(double iGridLength,String strDataStore){
		int gridLength = S2.getCellLevelFromLength(iGridLength);
		return createKnowledgeGraph(gridLength, strDataStore);
	}

	public String getStoreDir() {
		File file = new File(storeDir);	
		return file.getAbsolutePath();
	}
	
	public int getGridLevel(){
		return gridLevel;
	}
	
	private String getKnowledgeGraphStorePath(){
		return this.storeDir+File.separator+KNOWLEDGE_STORE_DIR;
	}
	
	private String getOriginDataStorePath(){
		return this.storeDir+File.separator+ORIGIN_DATA_DIR;
	}
	
	private String getConfigFilePath(){
		return this.storeDir+File.separator+CONFIGFILE;
	}
	/**
	 * 图谱初始化，加载目录下的配置文件，没有的话直接构建
	 * @param storeDir 存储路径
	 * @param gridLevel 网格级别
	 */
	private static void graphInit(String storeDir,int gridLevel) {
		//检查目录合法性
		boolean checkedDir = Common.checkDir(storeDir);
		//将对象路径改为完整路径
		String fullStoreDir = new File(storeDir).getAbsolutePath();
		HashMap<String, String> confInfo = getConfInfo(fullStoreDir);
		//目录下没有配置文件，按照输入网格级别直接创建并加载
		if(checkedDir&&confInfo.isEmpty()) {
			if(initDataStore(storeDir,gridLevel)){
				System.out.println("初始化成功，成功构建空图谱");			
			}
		}
		//一.目录下有配置文件，
		else {
			System.out.println("指定路径下已有知识图谱，请重新指定存储路径或删除当前图谱");
			System.exit(1);
			/*
			//一.1 和输入的网格级别参数一致，直接加载
			if(isConfInfoMatch(confInfo)) {
				//可以加上图谱的信息
				System.out.println("初始化成功，已成功加载图谱");
			}
			//一.2 和输入的网格级别参数不一致，报错，让开发者处理
			else {
				try {
					System.out.println("图谱初始化失败，实例化参数与存在的知识图谱参数不符");
					System.out.println("解决办法：1.删掉已存在的知识图谱存储的文件夹。2.改变gridLevel参数，使其与存在的知识图谱保持一致。");
					throw new Exception();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.exit(1);
				}				
			}*/
			
		}
		
	}

	/**
	 * 判断存储路径下的谱图谱参数与实例化时设定的参数是否一致（没加内容-2019年7月20日11:14:50）
	 * @param confInfo
	 * @return
	 */
	private boolean isConfInfoMatch(HashMap<String, String> confInfo) {
		// TODO Auto-generated method stub
		boolean isLevel = confInfo.get("gridLevel").equals(""+this.gridLevel);
		boolean isDir = confInfo.get("storeDir").equals(this.storeDir);
		if(isDir&&isLevel) {
			return true;
		}
		else {	
			return false;
		}
	}

	/**
	 * 获取图谱存储路径下的配置文件，没有则返回空HashMap
	 * @param storeDir
	 * @return
	 */
	private static HashMap<String, String> getConfInfo(String storeDir) {
		// TODO Auto-generated method stub
		String filePath = storeDir+File.separator+CONFIGFILE;
//		System.out.println(filePath);
		File file = new File(filePath);
		//一行一行读取内容放到集合中
		ArrayList<String> allInfos = new ArrayList<String>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String s = null;
			try {
				while((s = br.readLine())!= null) {
					allInfos.add(s);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
//			System.out.println("没有找到配置文件："+file.getAbsolutePath());
			return new HashMap<String, String>();
		}
		//将一行一行内容处理成map形式
		HashMap<String, String> confInfos = new HashMap<String, String>();
		for (String str : allInfos) {
			String[] info = str.split("=");	
			try {
				confInfos.put(info[0], info[1]);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("配置文件错误,请删除图谱存储文件夹，重新生成图谱");
				System.exit(1);
			}		
		}
		return confInfos;
	}

	/**
	 * 初始化数据仓库，包括生成配置文件，新建知识图谱存储目录和原始数据存储目录。
	 * 将配置信息写入图谱文件夹的KnowledgeGraph.conf
	 * @param storeDir
	 * @param gridLevel 
	 */
	private static boolean initDataStore(String storeDir, int gridLevel){
		// TODO Auto-generated method stub
		//固定gridLevel的范围，小于0则为0，大于20则为20
		if(gridLevel<0){
			gridLevel = 0;
		}
		else if(gridLevel>20){
			gridLevel = 20;
		}
		//将配置信息写入配置文件
		File confFile = new File(storeDir+File.separator+CONFIGFILE);
		String confContent = "";
		String confGridLevel = "gridLevel="+gridLevel+"\n";
		String absolutePath = "storeDir="+confFile.getAbsolutePath().substring(0, confFile.getAbsolutePath().length()-CONFIGFILE.length()-1);
		confContent=confGridLevel+absolutePath;
		try{
			FileOutputStream fos = new FileOutputStream(confFile);
			fos.write(confContent.getBytes());		
		}catch(IOException e){
			System.out.println(confFile.getAbsolutePath()+"打开失败");
			System.exit(1);
		}
		//新建知识图存储目录以及源数据（udb文件）存储目录
		File knowledgeDir = new File(storeDir+File.separator+KNOWLEDGE_STORE_DIR);
		knowledgeDir.mkdirs();
		File originDataDir = new File(storeDir+File.separator+ORIGIN_DATA_DIR);
		originDataDir.mkdirs();
		return true;
	}
	
	
	public static KnowledgeGraph loadKnowledgeGraph(String strDataStore){
		//检查目录合法性
		KnowledgeGraph know = null;
		boolean checkedDir = Common.checkDir(strDataStore);
		if(!checkedDir){
			System.out.println("载入知识图谱路径错误，请指定正确的路径名");
			System.exit(1);
		}
		//将对象路径改为完整路径
		String fullStrDataStore = new File(strDataStore).getAbsolutePath();
		HashMap<String, String> confInfo = getConfInfo(fullStrDataStore);
//		System.out.println(fullStrDataStore);
		if(confInfo.isEmpty()) {
			System.out.println("图谱配置信息载入失败，请重新指定图谱路径或删除当前图谱");			
			System.exit(1);
		}
		//一.目录下有配置文件，
		else {
			int gridLevel = Integer.valueOf(confInfo.get("gridLevel"));
			String storeDir = confInfo.get("storeDir");
			know = new KnowledgeGraph(gridLevel,storeDir);
		}
		System.out.println("成功加载知识图谱");
		return know;
	}
	
	/**
	 * 从指定数据源读取指定地理实体，存入构建好的知识图谱中
	 * @param dataSource 数据源路径，目前支持UDB所在目录
	 * @param arType 数据源中想要添加到知识图谱中的地理实体类型
	 * @return 添加成功返回True，否则返回False
	 */
	public boolean addKnowledgeGraph(String dataSource, String[] arType){
		//将指定数据集存入知识图谱数据源
		ArrayList<String> storeDataSetsIds = this.storeDataSource(dataSource,arType);
		//得到数据源中符合指定类型的所有数据集
//		ArrayList<GeoEntity> gisData = ProcessData.getGisDataFromDataSource(dataSource,geoTypes);
//		ArrayList<Grid> gridModels = ProcessData.getKnowledgeGraphModel(gisData,this.gridLevel);
//		Boolean bo = Rdf4j.writeToKnowledgeGraph(gridModels, this.storeDir);
		for (String dataSetId : storeDataSetsIds) {
			ArrayList<www.supermap.model.iobjects.GeoObjectEntity> gisData = Iobjects.getGisDataFromDataSet(this.getOriginDataStorePath(),dataSetId);
			//生成可以存入知识图谱的数据模型-Grid
			ArrayList<ObjectGrid> gridModels = Iobjects.getKnowledgeGraphModelFromObjects(gisData,this.gridLevel);
			//将数据增量存入知识图谱
			Boolean bo = Rdf4j.writeToKnowledgeGraphFromObject(gridModels, this.getKnowledgeGraphStorePath());		
			System.out.println(dataSetId.split("_")[2]+" 已存储到知识图谱");
		}
		System.out.println("增量更新完毕");
		return true;
	}
	
	/**
	 * 将要添加到知识图谱中的数据集添加到知识图谱数据源,并返回添加进的数据集id。首先支持udb与shp文件
	 * @param dataSource
	 * @param geoTypes
	 * @return ArrayList<String>  
	 */
	private ArrayList<String> storeDataSource(String dataSource, String[] geoTypes) {
		// TODO Auto-generated method stub
		ArrayList<String> storeDataSetsIds = new ArrayList<String>();
		//获得符合条件的数据集
		ArrayList<Dataset> allDataSets = Iobjects.getAllDataSets(dataSource);
		ArrayList<Dataset> dataSets = Iobjects.filterDataSetByAssignGeoTypes(allDataSets,geoTypes);
		//1.将数据集存储到图谱源文件
		//1.1 获得当前数据集要存储的数据源与数据集文件名
		String dataSetStartId = Iobjects.getEndDataSetId(this.getOriginDataStorePath());
		for (Dataset dataSet : dataSets) {
			String currentWholeIndexId = Iobjects.getNextDataSetId(dataSetStartId,this.getOriginDataStorePath());
			dataSetStartId = currentWholeIndexId;
			String[] idSplits = currentWholeIndexId.split("_");
 			String currentDataSourceIndexId = idSplits[0];
			String currentDataSetIndexId = idSplits[1];
			//数据集名称
			String currentDataSetName = dataSet.getName();
			String targetDataSetName = currentDataSetIndexId+"_"+currentDataSetName;
			String targetDataSetWholeId = currentDataSourceIndexId+"_"+targetDataSetName;
			String dataSourceServer = this.getOriginDataStorePath()+File.separator+currentDataSourceIndexId+".udb"; 
			//1.2 转换坐标系，将数据集存储到指定数据源
			DatasourceConnectionInfo dscio = new DatasourceConnectionInfo();
			dscio.setEngineType(EngineType.UDB);
			dscio.setServer(dataSourceServer);
			Datasource targetDataSource = new Workspace().getDatasources().open(dscio);
			PrjCoordSys targetPrjCoordSys = PrjCoordSys.fromEPSG(4326);
			CoordSysTransParameter coordSysTransParameter = new CoordSysTransParameter();
			CoordSysTranslator.convert(dataSet,targetPrjCoordSys,targetDataSource,targetDataSetName,coordSysTransParameter,CoordSysTransMethod.MTH_GEOCENTRIC_TRANSLATION);
			storeDataSetsIds.add(targetDataSetWholeId);
			targetDataSource.close();
		}
		//输出存储的信息
		int geoTypesNmuber = 0;
		if(geoTypes==null||geoTypes.length==0){
			geoTypesNmuber = allDataSets.size();
		}
		else if(geoTypes!=null||geoTypes.length!=0){
			geoTypesNmuber = geoTypes.length;
		}
		System.out.println("选择的数据集个数："+geoTypesNmuber+",已加载数据集个数："+storeDataSetsIds.size());
		return storeDataSetsIds;
	}
	
	/**
	 * 添加数据源中的所有信息到知识图谱
	 * @param dataSource
	 * @return
	 */
	private boolean add(String dataSource) {
		String[] geoTypes = null;
		Boolean bo = addKnowledgeGraph(dataSource, geoTypes);
		return bo;
	}
	
	/**
	 * 通过指定经纬度和半径构建缓冲区，从当前图谱中查询出符合候选类型的信息
	 * @param dLatitude 纬度
	 * @param dLongitude 经度
	 * @param iRadius 缓冲区半径
	 * @param arType 地理实体类型
	 * @return RecordSet
	 */
	public HashMap<String, ArrayList<RecordSetEntity>> queryKnowledgeGraph(double dLatitude, double dLongitude, double iRadius,String[] arType){
		//判断经纬度位于哪个网格
		S2LatLng laln = S2LatLng.fromDegrees(dLatitude, dLongitude);
		S2CellId cell = S2CellId.fromLatLng(laln).parent(this.gridLevel);
		//使用S2缓冲分析，得到缓冲区内的所有网格
		ArrayList<S2CellId> coverCells = S2.getCoveringCellIdsFromCell(cell, iRadius, this.gridLevel);
		//从知识图谱中获得指定类型的id
		HashMap<String, ArrayList<String>>idResults = Rdf4j.queryGeoFromMultiCellsAndGeoTypes(this.getKnowledgeGraphStorePath(), coverCells, arType);
		//通过id从源文件中取RecordSet
		HashMap<String, ArrayList<RecordSetEntity>> recordSetResults = Iobjects.getRecordSetFromIds(idResults,this.getOriginDataStorePath());
//		HashMap<String, ArrayList<RecordSetEntity>> searchResults = Rdf4j.queryGeoInfoFromMultiCellsAndGeoTypes(this.getKnowledgeGraphStorePath(),this.getOriginDataStorePath(),coverCells,geoTypes);
		return recordSetResults;
	}
	
	

	/**
	 * 通过指定经纬度和半径构建缓冲区，从当前图谱中查询出所有类型的信息
	 * @param dLatitude
	 * @param dLongitude
	 * @param iRadius
	 * @return
	 */
	private HashMap<String, ArrayList<RecordSetEntity>> search(double dLatitude, double dLongitude, double iRadius){
		return queryKnowledgeGraph(dLatitude,dLongitude,iRadius,null);
	}
}
