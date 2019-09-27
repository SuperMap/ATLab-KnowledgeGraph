import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import www.supermap.geoknowledge.KnowledgeGraph;
import www.supermap.model.iobjects.RecordSetEntity;

public class GettingStarted {

	public static void main(String[] args) {
		// 1.以网格等级和图谱存储路径为参数，创建知识图谱
		String knowledgeGraphStoreDir = "SampleStore";
		KnowledgeGraph.createKnowledgeGraph(13, knowledgeGraphStoreDir);
		// 以网格长度为参数构建知识图谱
		//KnowledgeGraph.createKnowledgeGraph(1000.0,knowledgeGraphStoreDir);

		// 2.第一次创建之后，再次使用只需直接加载一个存在的知识图谱
		KnowledgeGraph knowledgeGraph = KnowledgeGraph.loadKnowledgeGraph(knowledgeGraphStoreDir);

		// 3.增量更新知识图谱
		String dataSource = "SampleData\\sample.udb";
		// 将udb中的所有数据集增加到知识图谱中的快捷方式
		// String[] arType = {};
		String[] arType = { "集体宿舍", "停车场", "行政办公用地" };
		knowledgeGraph.addKnowledgeGraph(dataSource, arType);

		// 4.查询知识图谱
		// 要查询的点的纬度及纬度举例（WGS84）
		double dLatitude = 29.6965845497988;
		double dLongitude = 106.625768872153;
		// 查询半径，单位：米
		double iRadius = 977.45;
		// 查询指定经纬度范围内图谱中的所有数据集类型
		// String[] queryType = {};
		String[] queryType = { "集体宿舍", "停车场", "行政办公用地" };
		HashMap<String, ArrayList<RecordSetEntity>> result = knowledgeGraph.queryKnowledgeGraph(dLatitude, dLongitude,iRadius, queryType);

		// 5.打印搜索结果
		// RecordSetEntity类目前有两个属性，分别为point和mingCheng,分别为实体的经纬度与名称，可以通过get()获得
		for (Entry<String, ArrayList<RecordSetEntity>> entry : result.entrySet()) {
			System.out.println(entry.getKey() + "个数:" + entry.getValue().size());
			for (RecordSetEntity recordSet : entry.getValue()) {
				System.out.println("\t" + recordSet.getMingCheng() + recordSet.getPoint());
			}
		}
	}
}
