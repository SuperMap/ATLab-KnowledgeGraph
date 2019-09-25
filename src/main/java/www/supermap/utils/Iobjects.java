package www.supermap.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import com.supermap.data.CursorType;
import com.supermap.data.Dataset;
import com.supermap.data.DatasetType;
import com.supermap.data.DatasetVector;
import com.supermap.data.Datasets;
import com.supermap.data.Datasource;
import com.supermap.data.DatasourceConnectionInfo;
import com.supermap.data.EngineType;
import com.supermap.data.GeoLine;
import com.supermap.data.GeoPoint;
import com.supermap.data.GeoRegion;
import com.supermap.data.Recordset;
import com.supermap.data.Workspace;

import www.supermap.model.iobjects.GeoObjectEntity;
import www.supermap.model.iobjects.LineObjectEntity;
import www.supermap.model.iobjects.ObjectGrid;
import www.supermap.model.iobjects.PointObjectEntity;
import www.supermap.model.iobjects.RecordSetEntity;
import www.supermap.model.iobjects.RegionObjectEntity;

public class Iobjects {
	private static final int MAX_DATASET_NUM = 100;

	public Iobjects() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * 通过检查当前图谱源文件下存储的数据集，得到最后一个数据集的id
	 * 
	 * @return
	 */
	public static String getEndDataSetId(String storeDir) {
		// TODO Auto-generated method stub
		// 目前只使用一个数据源，id为0
		// 一个数据集存放100个数据集，多出的存到新数据集
		// 1.找到数据库文件下最后一个数据源
		File oriFile = new File(storeDir);
		File[] files = oriFile.listFiles();
		HashSet<Integer> idSet = new HashSet<Integer>();
		for (File file : files) {
			String fileWholeName = file.getName();
			if (!fileWholeName.endsWith("udb")) {
				continue;
			}
			int fileId = Integer.valueOf(fileWholeName.split("\\.")[0]);
			idSet.add(fileId);
		}
		// 2.找出最大的数据源id
		int maxId = 0;
		if (!idSet.isEmpty()) {
			maxId = Collections.max(idSet);
		}
		String currentStore = maxId + ".udb";
		Workspace workSpace = new Workspace();
		DatasourceConnectionInfo datasourceConnectionInfo = new DatasourceConnectionInfo();
		datasourceConnectionInfo.setServer(storeDir + File.separator + currentStore);
		datasourceConnectionInfo.setEngineType(EngineType.UDB);
		Datasource dataSource = null;
		try {
			// 打开数据源
			dataSource = workSpace.getDatasources().open(datasourceConnectionInfo);
		} catch (javax.management.RuntimeErrorException e) {
			// TODO: handle exception
			dataSource = workSpace.getDatasources().create(datasourceConnectionInfo);
		}
		int id = dataSource.getDatasets().getCount() - 1;
		dataSource.close();
		return maxId + "_D" + id;
	}

	/**
	 * 从所有的数据集中过滤出指定类型的数据集
	 * 
	 * @param allDataSets
	 * @param geoTypes
	 * @return
	 */
	public static ArrayList<Dataset> filterDataSetByAssignGeoTypes(ArrayList<Dataset> allDataSets, String[] geoTypes) {
		// TODO Auto-generated method stub
		ArrayList<Dataset> filterDataSets = new ArrayList<Dataset>();
		if (geoTypes == null || geoTypes.length == 0) {
			for (Dataset dataSet : allDataSets) {
				try {
					Dataset dataSetVector = (DatasetVector) dataSet;
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					System.out.println("目前只支持矢量数据集，请指定矢量数据集或删除数据源中的所有非矢量数据集，然后再次尝试");
				}
			}
			filterDataSets = allDataSets;
		} else {
			for (String type : geoTypes) {
				for (Dataset dataSet : allDataSets) {
					// 以数据集名字作为类型判别
					if (dataSet.getName().equals(type)) {
						try {
							Dataset dataSetVector = (DatasetVector) dataSet;
						} catch (Exception e) {
							// TODO: handle exception
							e.printStackTrace();
							System.out.println("目前只支持矢量数据集，检测到  " + type + " 为非矢量数据集，请删除该类型后重试 ");
							System.exit(1);
						}
						filterDataSets.add(dataSet);
						break;
					}
				}
			}
		}
		return filterDataSets;
	}

	/**
	 * 通过字符串来获得包含的所有数据源,目前只实现了udb
	 * 
	 * @param dataSource
	 * @return
	 */
	public static ArrayList<Dataset> getAllDataSets(String dataSource) {
		// TODO Auto-generated method stub
		ArrayList<Dataset> dataSets = new ArrayList<Dataset>();
		Workspace workSpace = new Workspace();
		DatasourceConnectionInfo datasourceConnectionInfo = new DatasourceConnectionInfo();
		// 要判断是否为smwu工作空间，udb数据源，shp文件，数据库连接字符串
		if (dataSource.endsWith("smwu")) {
			// smwu工作空间
		} else if (dataSource.endsWith("udb")) {
			// udb文件型数据源
			datasourceConnectionInfo.setServer(dataSource);
			datasourceConnectionInfo.setEngineType(EngineType.UDB);
			Datasource datasource = workSpace.getDatasources().open(datasourceConnectionInfo);
			Datasets allDataSets = datasource.getDatasets();
			for (int i = 0; i < allDataSets.getCount(); i++) {
				Dataset dataSet = allDataSets.get(i);
				dataSets.add(dataSet);
			}
		} else if (dataSource.endsWith("shp")) {
			// shp文件
		} else if (dataSource.endsWith("shujuku~!@#$%^&*()_+")) {
			// 预留给数据库
		}
		return dataSets;
	}

	/**
	 * 获得下一个数据集要存的id，如果需要则创建新的数据源
	 * 
	 * @param currentSetStartId
	 * @param originDataStorePath
	 * @return
	 */
	public static String getNextDataSetId(String currentSetStartId, String originDataStorePath) {
		// TODO Auto-generated method stub
		String[] idSplits = currentSetStartId.split("_");
		int preDataSourceId = Integer.valueOf(idSplits[0]);
		int preDataSetId = Integer.valueOf(idSplits[1].substring(1));
		int currentDataSourceId;
		int currentDataSetId;
		if (preDataSetId >= MAX_DATASET_NUM - 1) {
			currentDataSourceId = preDataSourceId + 1;
			currentDataSetId = 0;
			Workspace workSpace = new Workspace();
			DatasourceConnectionInfo datasourceConnectionInfo = new DatasourceConnectionInfo();
			datasourceConnectionInfo.setServer(originDataStorePath + File.separator + currentDataSourceId + ".udb");
			datasourceConnectionInfo.setEngineType(EngineType.UDB);
			Datasource dataSource = workSpace.getDatasources().create(datasourceConnectionInfo);
			dataSource.close();
		} else {
			currentDataSourceId = preDataSourceId;
			currentDataSetId = preDataSetId + 1;
		}
		return currentDataSourceId + "_D" + currentDataSetId;
	}

	/**
	 * 从存储到图谱数据源的数据集中读取数据
	 * 
	 * @param storeDataSetsIds
	 * @return
	 */
	public static ArrayList<GeoObjectEntity> getGisDataFromDataSet(String dataSourcePath, String wholeDataSetId) {
		// TODO Auto-generated method stub
		String[] idSplits = wholeDataSetId.split("_");
		String dataSourceId = idSplits[0];
		String dataSetId = idSplits[1];
		String entityType = idSplits[2];
		String recordPrefix = dataSourceId + "_" + dataSetId + "_";
		ArrayList<GeoObjectEntity> geoEntities = getGeoEntityFromDataSet(dataSourcePath, dataSourceId, dataSetId,
				entityType, recordPrefix);
		return geoEntities;
	}

	/**
	 * 从指定数据集中读取数据
	 * 
	 * @param dataSourceId
	 * @param dataSetId
	 * @param entityType
	 * @param recordPrefix
	 * @param dataSourcePath
	 * @return
	 */
	private static ArrayList<GeoObjectEntity> getGeoEntityFromDataSet(String dataSourcePath, String dataSourceId,
			String dataSetId, String entityType, String recordPrefix) {
		// TODO Auto-generated method stub
		ArrayList<GeoObjectEntity> geoEntities = new ArrayList<GeoObjectEntity>();
		// 打开数据源
		Workspace workSpace = new Workspace();
		DatasourceConnectionInfo dataSourceConnectionInfo = new DatasourceConnectionInfo();
		dataSourceConnectionInfo.setServer(dataSourcePath + File.separator + dataSourceId + ".udb");
		dataSourceConnectionInfo.setEngineType(EngineType.UDB);
		Datasource dataSource = workSpace.getDatasources().open(dataSourceConnectionInfo);
		Dataset dataSet = dataSource.getDatasets().get(dataSetId + "_" + entityType);
		DatasetVector dataSetVector = (DatasetVector) dataSet;
		Recordset recordSet = dataSetVector.getRecordset(false, CursorType.STATIC);
		DatasetType dataSetType = dataSetVector.getType();
		// 处理点线面
		if (dataSetType.equals(DatasetType.POINT)) {
			for (int i = 0; i < recordSet.getRecordCount(); i++) {
				GeoPoint point = (GeoPoint) recordSet.getGeometry();
				String entityId = recordPrefix + recordSet.getID();
				PointObjectEntity pointEntity = new PointObjectEntity(point, entityType, entityId);
				geoEntities.add(pointEntity);
				recordSet.moveNext();
			}
		} else if (dataSetType.equals(DatasetType.LINE)) {
			for (int i = 0; i < recordSet.getRecordCount(); i++) {
				GeoLine line = (GeoLine) recordSet.getGeometry();
				String entityId = recordPrefix + recordSet.getID();
				LineObjectEntity lineEntity = new LineObjectEntity(line, entityType, entityId);
				geoEntities.add(lineEntity);
				recordSet.moveNext();
			}
		} else if (dataSetType.equals(DatasetType.REGION)) {
			for (int i = 0; i < recordSet.getRecordCount(); i++) {
				GeoRegion region = (GeoRegion) recordSet.getGeometry();
				String entityId = recordPrefix + recordSet.getID();
				RegionObjectEntity regionEntity = new RegionObjectEntity(region, entityType, entityId);
				geoEntities.add(regionEntity);
				recordSet.moveNext();
			}
		}
		dataSource.close();
		return geoEntities;
	}

	/**
	 * 将传入的gis数填补，转换成适存储的grid
	 * 
	 * @param gisData
	 * @param gridLevel
	 * @return
	 */
	public static ArrayList<ObjectGrid> getKnowledgeGraphModelFromObjects(ArrayList<GeoObjectEntity> gisData,
			int gridLevel) {
		// TODO Auto-generated method stub
		for (GeoObjectEntity geoEntity : gisData) {
			// 处理点实体
			if (geoEntity instanceof PointObjectEntity) {
				PointObjectEntity pointEntity = (PointObjectEntity) geoEntity;
				pointEntity.setCellLevel(gridLevel);
				GeoPoint point = pointEntity.getPoint();
				ArrayList<Long> cellIds = S2.getGeoPointCoveringCell(point, gridLevel);
				pointEntity.setCellIds(cellIds);
			} else if (geoEntity instanceof LineObjectEntity) {
				LineObjectEntity lineEntity = (LineObjectEntity) geoEntity;
				lineEntity.setCellLevel(gridLevel);
				GeoLine line = lineEntity.getLine();
				ArrayList<Long> cellIds = S2.getGeoLineCoveringCells(line, gridLevel);
				lineEntity.setCellIds(cellIds);
			} else if (geoEntity instanceof RegionObjectEntity) {
				RegionObjectEntity regionEntity = (RegionObjectEntity) geoEntity;
				regionEntity.setCellLevel(gridLevel);
				GeoRegion region = regionEntity.getMultiPolygon();
				ArrayList<Long> cellIds = S2.getGeoRegionCoveringCells(region, gridLevel);
				regionEntity.setCellIds(cellIds);
			}
		}
		// 完善实体信息后生成用于存储的网格实体
		ArrayList<ObjectGrid> grids = getGridModelFromObjects(gisData);
		return grids;
	}

	/**
	 * 使用完整的地理实体生成网格模型
	 * 
	 * @param gisData
	 * @return
	 */
	private static ArrayList<ObjectGrid> getGridModelFromObjects(ArrayList<GeoObjectEntity> gisData) {
		// TODO Auto-generated method stub
		HashSet<Long> ids = new HashSet<Long>();
		for (GeoObjectEntity geoEntity : gisData) {
			for (Long id : geoEntity.getCellIds()) {
				ids.add(id);
			}
		}
		ArrayList<ObjectGrid> grids = new ArrayList<ObjectGrid>();
		for (Long id : ids) {
			ArrayList<GeoObjectEntity> entityies = new ArrayList<GeoObjectEntity>();
			for (GeoObjectEntity geoEntity : gisData) {
				for (Long cellId : geoEntity.getCellIds()) {
					if (cellId.longValue() == id.longValue()) {
						entityies.add(geoEntity);
						break;
					}
				}
			}
			ObjectGrid grid = new ObjectGrid(id, entityies);
			grids.add(grid);
		}
		return grids;
	}

	/**
	 * 通过查询出的id去文件里取RecordSet
	 * 
	 * @param idResults
	 * @param originDataStorePath
	 * @return
	 */
	public static HashMap<String, ArrayList<RecordSetEntity>> getRecordSetFromIds(
			HashMap<String, ArrayList<String>> idResults, String originDataStorePath) {
		// TODO Auto-generated method stub
		HashMap<String, ArrayList<RecordSetEntity>> idAndRecordSets = new HashMap<String, ArrayList<RecordSetEntity>>();
		/**
		 * V1.0版本的处理方式：每读一条记录就要打开和关闭一次数据源，已作废
		 */
		// for (Entry<String, ArrayList<String>> entry : idResults.entrySet()) {
		// String entityType = entry.getKey();
		// ArrayList<RecordSetEntity> recordSetEntities = new
		// ArrayList<RecordSetEntity>();
		// for (String recordId : entry.getValue()) {
		// RecordSetEntity recordSetEntity = new RecordSetEntity(recordId,
		// originDataStorePath, entityType);
		// if (recordSetEntity.getPoint() != null) {
		// recordSetEntities.add(recordSetEntity);
		// }
		// }
		// idAndRecordSets.put(entityType, recordSetEntities);
		// }

		for (String type : idResults.keySet()) {
			ArrayList<String> recordIds = idResults.get(type);
			// 获得当前类型存在哪个数据源、数据集
			String[] idSplits = recordIds.get(0).split("_");
			String dataSourceId = idSplits[0];
			String dataSetId = idSplits[1];
			// System.out.println(originDataStorePath + File.separator +
			// dataSourceId + ".udb");
			Workspace workSpace = new Workspace();
			DatasourceConnectionInfo dataSourceConnectionInfo = new DatasourceConnectionInfo();
			dataSourceConnectionInfo.setServer(originDataStorePath + File.separator + dataSourceId + ".udb");
			dataSourceConnectionInfo.setEngineType(EngineType.UDB);
			Datasource dataSource = workSpace.getDatasources().open(dataSourceConnectionInfo);
			Dataset dataSet = dataSource.getDatasets().get(dataSetId + "_" + type);
			DatasetVector dataSetVector = (DatasetVector) dataSet;
			Recordset recordSet = dataSetVector.getRecordset(false, CursorType.STATIC);
			ArrayList<RecordSetEntity> recordSetEntities = new ArrayList<RecordSetEntity>();
			for (String recordId : recordIds) {
				int recordIndex = Integer.valueOf(recordId.split("_")[2]);
				recordSet.moveTo(recordIndex);
				// 通过recordset取出所需要的字段值
				RecordSetEntity recordSetEntity = new RecordSetEntity(recordSet);
				recordSetEntities.add(recordSetEntity);
			}
			idAndRecordSets.put(type, recordSetEntities);
			dataSource.close();
		}
		return idAndRecordSets;
	}

	/**
	 * 判断数据集是否需要已经存过了，已经存过的不需要再存
	 * 
	 * @param dataSet
	 *            需要判断的数据集
	 * @param originDataStorePath
	 *            存储数据的文件夹
	 * @return 没有存储过返回true，存储过不需要存的返回false
	 */
	public static boolean dataSetNeedStoreOrNot(Dataset dataSet, String originDataStorePath) {
		// 找出所有的udb文件
		File file = new File(originDataStorePath);
		File[] fs = file.listFiles();
		if (fs.length == 0) {
			return true;
		}
		Datasource dataSource = null;
		for (File f : fs) {
			if (!f.getName().endsWith("udb")) {
				continue;
			}

			// 对所有udb文件中的dataset与当前dataset进行比较
			Workspace workSpace = new Workspace();
			DatasourceConnectionInfo datasourceConnectionInfo = new DatasourceConnectionInfo();
			datasourceConnectionInfo.setServer(f.getAbsolutePath());
			// System.out.println(f.getAbsolutePath());
			datasourceConnectionInfo.setEngineType(EngineType.UDB);
			dataSource = workSpace.getDatasources().open(datasourceConnectionInfo);
			Datasets dataSets = dataSource.getDatasets();
			for (int i = 0; i < dataSets.getCount(); i++) {
				Dataset originDataSet = dataSets.get(i);
				// 判断两个数据集是否一样的条件为：数据集名称、数据集类型（点线面体等）以及数据表名称
				if (dataSet.getName().equals(originDataSet.getName().split("_")[1])
						&& dataSet.getType().equals(originDataSet.getType())) {
					// 判断相同位置的某一个recordset的字段列数是否一致，一致则认为是同一数据集
					DatasetVector dataSetVector = (DatasetVector) dataSet;
					Recordset recordSet = dataSetVector.getRecordset(false, CursorType.STATIC);
					// recordSet.moveFirst();
					DatasetVector originDataSetVector = (DatasetVector) originDataSet;
					Recordset originRecordSet = originDataSetVector.getRecordset(false, CursorType.STATIC);
					// originRecordSet.moveFirst();
					if (recordSet.getFieldCount() == originRecordSet.getFieldCount()) {
						dataSource.close();
						// System.out.println("Done");
						return false;
					} else {
						continue;
					}
				}
			}
			dataSource.close();
		}
		return true;
	}
}
