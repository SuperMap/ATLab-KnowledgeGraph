package run;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import www.supermap.geoknowledge.KnowledgeGraph;
import www.supermap.model.iobjects.RecordSetEntity;

/**
 * 
 * @author Supermap.AT_LAB
 * 这个类将演示如何使用知识图谱，本程序使用的测试dub文件为fenlei.udb
 * 首先将udb与udd文件复制到该项目的SampleData目录下，
 * 其次将本页代码24行的文件名sample改为您自己的文件名，26行的arType与35行的queryType改为您udb文件中有的数据集名称，便可以运行本程序
 */
public class SampleRun {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//1.以网格等级和图谱存储路径为参数，创建知识图谱
		String knowledgeGraphStoreDir = "SampleStore";
		KnowledgeGraph.createKnowledgeGraph(13,knowledgeGraphStoreDir);
//		KnowledgeGraph.createKnowledgeGraph(1000.0,knowledgeGraphStoreDir);    以网格长度为参数构建知识图谱
		
		
		//2.第一次创建之后，再次使用只需直接加载一个存在的知识图谱
		KnowledgeGraph knowledgeGraph = KnowledgeGraph.loadKnowledgeGraph(knowledgeGraphStoreDir);
		
		
		//3.增量更新知识图谱
		String dataSource = "SampleData\\sample.udb";
//		String[] arType = {};   //将udb中的所有数据集增加到知识图谱中
		String[] arType = {"住宅","轨道站点","集体宿舍"};
		knowledgeGraph.addKnowledgeGraph(dataSource, arType);
		
		
		//4.查询知识图谱
		double dLatitude = 29.70126388888889;	//要查询的点的纬度（WGS84）
		double dLongitude = 106.618;			//要查询的点的经度（WGS84）
		double iRadius =  977.45;				//查询半径，单位：米
//		String[] queryType = {};   查询指定经纬度范围内图谱中的所有数据集类型
		String[] queryType = {"集体宿舍"};
		HashMap<String,ArrayList<RecordSetEntity>> result = knowledgeGraph.queryKnowledgeGraph(dLatitude, dLongitude, iRadius, queryType);
		
		
		//5.打印搜索结果
		//RecordSetEntity类目前有两个属性，分别为point和mingCheng,分别为实体的经纬度与名称，可以通过get()获得
		for (Entry<String, ArrayList<RecordSetEntity>> entry : result.entrySet()) {
			System.out.println(entry.getKey()+":个数"+entry.getValue().size());
			for (RecordSetEntity recordSet : entry.getValue()) {
				System.out.println("\t"+recordSet.getMingCheng()+recordSet.getPoint());
			}
		}
	}
}
