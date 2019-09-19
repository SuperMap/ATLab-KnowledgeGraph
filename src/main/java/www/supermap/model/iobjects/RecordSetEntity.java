package www.supermap.model.iobjects;

import java.io.File;
import java.util.ArrayList;

import com.supermap.data.CursorType;
import com.supermap.data.Dataset;
import com.supermap.data.DatasetVector;
import com.supermap.data.Datasource;
import com.supermap.data.DatasourceConnectionInfo;
import com.supermap.data.Datasources;
import com.supermap.data.EngineType;
import com.supermap.data.FieldInfo;
import com.supermap.data.FieldInfos;
import com.supermap.data.Geometry;
import com.supermap.data.Point2D;
import com.supermap.data.Recordset;
import com.supermap.data.Workspace;

public class RecordSetEntity {

	private String recordId;
	// private Recordset recordSet;
	private String dataDtoreDir;
	private String entityType;
	// 字段属性
	private Point2D point;
	private String mingCheng;

	public RecordSetEntity(String recordId, String dataDtoreDir, String entityType) {
		// TODO Auto-generated constructor stub
		this.recordId = recordId;
		this.dataDtoreDir = dataDtoreDir;
		this.entityType = entityType;
		// this.recordSet = getRecordSetById(recordId,dataDtoreDir,entityType);
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
		// System.out.println(dataSourceId+"-"+dataSetId+"-"+recordIndex);
		
		Datasource dataSource = null;
		try {
			dataSource = workSpace.getDatasources().open(dataSourceConnectionInfo);
		} catch (Exception e) {
//			System.out.println(dataSourceId + "-" + dataSetId + "-" + recordIndex);
//			System.out.println(recordId);
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
		// if(dataSource.isOpened()){
		// System.out.println(dataSourceId+"-"+dataSetId+"-"+recordIndex+"数据集已打开----------------------------------------------");
		// }

	}

	/**
	 * 通过recordset来获取字段值
	 * 
	 * @param recordSet
	 */
	private void getRequiredInfo(Recordset recordSet) {
		// TODO Auto-generated method stub
		// 取出实体位于的经纬度
		Geometry geometry = recordSet.getGeometry();
		this.point = geometry.getInnerPoint();
		// 获取位置信息
		// this.weiZhi = recordSet.getFieldValue("wz"));
		// this.quXian = recordSet.getFieldValue("qx"));
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

}
