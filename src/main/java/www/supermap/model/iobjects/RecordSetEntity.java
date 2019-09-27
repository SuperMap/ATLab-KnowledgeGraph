package www.supermap.model.iobjects;

import java.io.File;

import com.supermap.data.CursorType;
import com.supermap.data.Dataset;
import com.supermap.data.DatasetVector;
import com.supermap.data.Datasource;
import com.supermap.data.DatasourceConnectionInfo;
import com.supermap.data.EngineType;
import com.supermap.data.Geometry;
import com.supermap.data.Point2D;
import com.supermap.data.Recordset;
import com.supermap.data.Workspace;
/**
 * 记录集实体
 * @author SunYasong
 *
 */
public class RecordSetEntity {
	//记录集在图谱中的id，通过id可以去对应数据源、数据集查看记录集
	private String recordId;
	// private Recordset recordSet;
	//记录集所在数据集的路径
	private String dataDtoreDir;
	//记录的类型，即数据集的名称
	private String entityType;
	//记录集所代表的图形的内点
	private Point2D point;
	//记录集中的一个字段，名称
	private String mingCheng;

	public RecordSetEntity(String recordId, String dataDtoreDir, String entityType) {
		this.recordId = recordId;
		this.dataDtoreDir = dataDtoreDir;
		this.entityType = entityType;
		getInfoByRecordId(recordId, dataDtoreDir, entityType);
	}

	public RecordSetEntity(Recordset recordSet) {
		getRequiredInfo(recordSet);
	}

	public Point2D getPoint() {
		return point;
	}

	public String getMingCheng() {
		return mingCheng;
	}

	/**
	 * 获得recordset中所需要的字段
	 * 
	 * @param recordId
	 * @param dataDtoreDir
	 * @param entityType
	 */
	private void getInfoByRecordId(String recordId, String dataDtoreDir, String entityType) {
		// TODO Auto-generated method stub
		String[] idSplits = recordId.split("_");
		String dataSourceId = idSplits[0];
		String dataSetId = idSplits[1];
		int recordIndex = Integer.valueOf(idSplits[2]);
		// 获得recordset
		Workspace workSpace = new Workspace();
		DatasourceConnectionInfo dataSourceConnectionInfo = new DatasourceConnectionInfo();
		dataSourceConnectionInfo.setServer(dataDtoreDir + File.separator + dataSourceId + ".udb");
		dataSourceConnectionInfo.setEngineType(EngineType.UDB);
		
		Datasource dataSource = null;
		try {
			dataSource = workSpace.getDatasources().open(dataSourceConnectionInfo);
		} catch (Exception e) {
		}
		if (dataSource != null) {
			Dataset dataSet = dataSource.getDatasets().get(dataSetId + "_" + entityType);
			DatasetVector dataSetVector = (DatasetVector) dataSet;
			Recordset recordSet = dataSetVector.getRecordset(false, CursorType.STATIC);
			recordSet.moveTo(recordIndex);
			// 通过recordset取出所需要的字段值
			getRequiredInfo(recordSet);
			dataSource.close();
		}
	}

	/**
	 * 通过recordset来获取类似名称字段值，有的记录集没有名称字段，则返回类似的字段，如：位置、区县
	 * 
	 * @param recordSet
	 */
	private void getRequiredInfo(Recordset recordSet) {
		// TODO Auto-generated method stub
		// 取出实体位于的经纬度
		Geometry geometry = recordSet.getGeometry();
		this.point = geometry.getInnerPoint();
		// 获取位置信息
		// 不是所有的实体都有名称字段，因此首先检查名称字段，没有的话检查位置，再检查区县，再没有就直接用null代替
		try {
			this.mingCheng = (String) recordSet.getFieldValue("mc");
		} catch (Exception e) {
			// TODO: handle exception
			try {
				this.mingCheng = (String) recordSet.getFieldValue("wz");
			} catch (Exception e2) {
				// TODO: handle exception
				try {
					this.mingCheng = (String) recordSet.getFieldValue("qx");
				} catch (Exception e3) {
					// TODO: handle exception
					this.mingCheng = null;
				}
			}
		}
	}

	/**
	 * 如果添加或删除了字段要记得重新生成toString
	 */
	@Override
	public String toString() {
		return "[point=" + point + ", mingCheng=" + mingCheng + "]";
	}

	private static Recordset getRecordSetById(String recordId, String dataDtoreDir, String entityType) {
		String[] idSplits = recordId.split("_");
		String dataSourceId = idSplits[0];
		String dataSetId = idSplits[1];
		int recordIndex = Integer.valueOf(idSplits[2]);
		// 读取数据
		Workspace workSpace = new Workspace();
		DatasourceConnectionInfo dataSourceConnectionInfo = new DatasourceConnectionInfo();
		dataSourceConnectionInfo.setServer(dataDtoreDir + File.separator + dataSourceId + ".udb");
		dataSourceConnectionInfo.setEngineType(EngineType.UDB);
		Datasource dataSource = workSpace.getDatasources().open(dataSourceConnectionInfo);
		Dataset dataSet = dataSource.getDatasets().get(dataSetId + "_" + entityType);
		DatasetVector dataSetVector = (DatasetVector) dataSet;
		Recordset recordSet = dataSetVector.getRecordset(false, CursorType.STATIC);
		recordSet.moveTo(recordIndex);
		dataSource.close();
		return recordSet;
	}

}
