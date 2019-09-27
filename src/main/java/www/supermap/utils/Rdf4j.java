package www.supermap.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;


import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryResult;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.rio.UnsupportedRDFormatException;
import org.eclipse.rdf4j.sail.memory.MemoryStore;
import org.eclipse.rdf4j.sail.nativerdf.NativeStore;

import com.google.common.geometry.S2CellId;

import www.supermap.model.iobjects.GeoObjectEntity;
import www.supermap.model.iobjects.LineObjectEntity;
import www.supermap.model.iobjects.ObjectGrid;
import www.supermap.model.iobjects.PointObjectEntity;
import www.supermap.model.iobjects.RecordSetEntity;
import www.supermap.model.iobjects.RegionObjectEntity;
/**
 * RDF4J相关
 * @author SunYasong
 *
 */
public class Rdf4j {
	/**
	 * 得到一个初始化后的可以放再本地的内存仓库
	 * 
	 * @param storeDir 仓库存储的目录 
	 * @return
	 */
	public static Repository getNavmoryStore(String storeDir) {
		File dataDir = new File(storeDir);
		MemoryStore memoryStore = new MemoryStore(dataDir);
		// memoryStore.setSyncDelay(1000L);
		Repository store = new SailRepository(memoryStore);
		store.initialize();
		return store;
	}

	/**
	 * 得到一个初始化后的本地数据库
	 * 
	 * @param storeDir
	 * @return
	 */
	public static Repository getNaviteStore(String storeDir) {
		File dataDir = new File(storeDir);
		NativeStore nativeStore = new NativeStore(dataDir);
		Repository store = new SailRepository(nativeStore);
		store.initialize();
		return store;
	}

	/**
	 * 通过指定三元组从指定仓库中查询符合条件的语句
	 * @param db
	 * @param subject
	 * @param predicate
	 * @param bing
	 * @return
	 */
	public static RepositoryResult<Statement> queryByStatement(Repository db, IRI subject, IRI predicate, IRI bing) {
		RepositoryResult<Statement> statements = null;
		try (RepositoryConnection conn = db.getConnection()) {
			// 有条件的查询
			statements = conn.getStatements(subject, predicate, bing, true);
			statements.close();
		} catch (Exception e) {
			System.out.println("---------------------" + e.getMessage());
		} finally {
			db.shutDown();
		}
		return statements;
	}

	/**
	 * 通过传入的多个cell和类型查询知识图谱，并返回结果
	 * @param coverCells 缓冲区内的所有cell
	 * @param geoTypes 要查询的实体类型
	 * @return
	 */
	public static HashMap<String, ArrayList<String>> queryGeoFromMultiCellsAndGeoTypes(String storeDir,ArrayList<S2CellId> coverCells,String[] geoTypes) {
		// TODO Auto-generated method stub
		//判断指定类型还是全部类型
		//全部类型
		Model model = new LinkedHashModel();
		if(geoTypes == null||geoTypes.length == 0) {
			Model preModel = getRDF4jModelFromAllCellAndGeoTypes(storeDir,coverCells);
			model.addAll(preModel);
		}
		//指定类型
		else {
			//从图谱中查询，得到所有符合类型的model
			for (S2CellId cell : coverCells) {
				Model preModel = getRDF4jModelFromSingleCellAndGeoTypes(storeDir,cell,geoTypes);	
				model.addAll(preModel);
			}
		}	
		
		//对model按类型进行分解，并存到hashmap
		HashMap<String, ArrayList<String>> result = getInfoFromRDF4jModel(model);
		return result;
	}
	
	/**
	 * 通过传入的多个cell和类型查询知识图谱，并返回结果
	 * @param coverCells 缓冲区内的所有cell
	 * @param geoTypes 要查询的实体类型
	 * @return
	 */
	public static HashMap<String, ArrayList<RecordSetEntity>> queryGeoInfoFromMultiCellsAndGeoTypes(String KnowledgeGraphStorePath,String originDataStorePath,ArrayList<S2CellId> coverCells,String[] geoTypes) {
		// TODO Auto-generated method stub
		//判断指定类型还是全部类型
		//全部类型
		Model model = new LinkedHashModel();
		if(geoTypes == null||geoTypes.length == 0) {
			Model preModel = getRDF4jModelFromAllCellAndGeoTypes(KnowledgeGraphStorePath,coverCells);
			model.addAll(preModel);
		}
		//指定类型
		else {
			//从图谱中查询，得到所有符合类型的model
			for (S2CellId cell : coverCells) {
				Model preModel = getRDF4jModelFromSingleCellAndGeoTypes(KnowledgeGraphStorePath,cell,geoTypes);	
				model.addAll(preModel);
			}
		}	
		
		//对model按类型进行分解，并存到hashmap
//		HashMap<String, ArrayList<String>> result = getInfoFromRDF4jModel(model);
		HashMap<String, ArrayList<RecordSetEntity>> result = getRecordSetFromRDF4jModel(model,originDataStorePath);
		return result;
	}
	
	/**
	 * 通过查询出的model里的id去数据源中读取
	 * @param model
	 * @param storeDir
	 * @return
	 */
	private static HashMap<String, ArrayList<RecordSetEntity>> getRecordSetFromRDF4jModel(Model model,String originDataStorePath) {
		// TODO Auto-generated method stub
		//找出Model里的所有类型
		HashMap<String, ArrayList<RecordSetEntity>> info = new HashMap<String, ArrayList<RecordSetEntity>>();
		Set<Value> allTypes = model.filter(null, RDF.TYPE,null).objects();
		for (Value value : allTypes) {
			//去除cell信息
			if(value.toString().substring(16).equals("Cell")) {
				continue;
			}
			String entityType = value.toString().substring(16);
			//找到指定类型的实体
			Set<Resource> allEntity = model.filter(null, RDF.TYPE,value).subjects();
			ArrayList<RecordSetEntity> recordSets = new ArrayList<RecordSetEntity>();
			for (Resource resource : allEntity) {
				String entity = resource.toString();
				String[] splitEntity = entity.split("#");
				String recordId = splitEntity[splitEntity.length-1];
				RecordSetEntity recordSetEntity = new RecordSetEntity(recordId, originDataStorePath, entityType);
				recordSets.add(recordSetEntity);
			}
			info.put(entityType, recordSets);
		}
		return info;
	}

	/**
	 * 获得多个cell里的所有类型的数据
	 * @param storeDir
	 * @param coverCells
	 * @return
	 */
	private static Model getRDF4jModelFromAllCellAndGeoTypes(String storeDir, ArrayList<S2CellId> coverCells) {
		// TODO Auto-generated method stub
		Repository store = new SailRepository(new MemoryStore());
		store.initialize();
		ValueFactory f = store.getValueFactory();
		Model model = new LinkedHashModel();
		for (S2CellId cell : coverCells) {
			String fileName = storeDir+"\\"+cell.id()+".ntriples";
			InputStream input;
			try {
				input = new FileInputStream(fileName);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				continue;
			}
			Model curModel;
			try {
				curModel = Rio.parse(input, "", RDFFormat.NTRIPLES).filter(null,RDF.TYPE,null);
				model.addAll(curModel);
			} catch (RDFParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedRDFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return model.filter(null,RDF.TYPE,null);
	}

	/**
	 * 对model中的信息进行分类
	 * @param model
	 * @return
	 */
	private static HashMap<String, ArrayList<String>> getInfoFromRDF4jModel(Model model) {
		// TODO Auto-generated method stub
		//找出Model里的所有类型
		HashMap<String, ArrayList<String>> info = new HashMap<String, ArrayList<String>>();
		Set<Value> allTypes = model.filter(null, RDF.TYPE,null).objects();
		for (Value value : allTypes) {
			//去除cell信息
			if(value.toString().substring(16).equals("Cell")) {
				continue;
			}
			//找到指定类型的实体
			Set<Resource> allEntity = model.filter(null, RDF.TYPE,value).subjects();
			ArrayList<String> entitys = new ArrayList<String>();
			for (Resource resource : allEntity) {
				String entity = resource.toString();
				String[] splitEntity = entity.split("#");
				entitys.add(splitEntity[splitEntity.length-1]);
			}
			info.put(value.toString().substring(16), entitys);
		}
		return info;
	}

	/**
	 * 从一个cell中获得符合类型的model
	 * @param cell
	 * @param geoTypes
	 * @return
	 */
	private static Model getRDF4jModelFromSingleCellAndGeoTypes(String storeDir,S2CellId cell, String[] geoTypes) {
		// TODO Auto-generated method stub
		String fileName = storeDir+"\\"+cell.id()+".ntriples";
		InputStream input;
		Model curModel = null;
		try {
			input = new FileInputStream(fileName);
			try {
				curModel = Rio.parse(input, "", RDFFormat.NTRIPLES);
			} catch (RDFParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedRDFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			return new LinkedHashModel();
//			e.printStackTrace();
		}
		Model filterModel = new LinkedHashModel();
		Repository store = new SailRepository(new MemoryStore());
		store.initialize();
		ValueFactory f = store.getValueFactory();
		for (String geoType : geoTypes) {
			IRI geoEntity = f.createIRI("http://ontology/"+geoType);
			filterModel.addAll(curModel.filter(null, RDF.TYPE, geoEntity));
		}
		return filterModel;
	}
	
	/**
	 * 将通过objects生成的Grid数据写入知识图谱中
	 * @param gridModels
	 * @param storeDir
	 * @return
	 */
	public static Boolean writeToKnowledgeGraphFromObject(ArrayList<ObjectGrid> gridModels,String storeDir) {
		// TODO Auto-generated method stub
		Repository store = new SailRepository(new MemoryStore());
		store.initialize();
		ValueFactory f = store.getValueFactory();
		String eneityPrefix = "http://";
		String ontologyPrefix = "http://ontology";
		String cellsPrefix = "http://cell/id#";
		IRI ontologyCell = f.createIRI(ontologyPrefix + "/Cell");
		IRI ontologyHave = f.createIRI(ontologyPrefix + "/have");
//		IRI ontologyInclude = f.createIRI(ontologyPrefix + "/include");
		for (ObjectGrid grid : gridModels) {
			Model model = new LinkedHashModel();
			// 实体写入model
			Long cellId = grid.getId();
			for (GeoObjectEntity geoEntity : grid.getGeoEntitys()) {
				String entityType = null;
				String entityId = null;
				//处理点实体对象
				if (geoEntity instanceof PointObjectEntity) {
					PointObjectEntity pointEntity = (PointObjectEntity) geoEntity;
					entityType = pointEntity.getEntityType();
					entityId = pointEntity.getEntityId();
				} else if (geoEntity instanceof LineObjectEntity) {
					LineObjectEntity lineEntity = (LineObjectEntity) geoEntity;
					entityType = lineEntity.getEntityType();
					entityId = lineEntity.getEntityId();
				} else if (geoEntity instanceof RegionObjectEntity) {
					RegionObjectEntity pointEntity = (RegionObjectEntity) geoEntity;
					entityType = pointEntity.getEntityType();
					entityId = pointEntity.getEntityId();
				}
				IRI cellIID = f.createIRI(cellsPrefix + cellId);
				IRI entityIdIRI = f.createIRI(eneityPrefix + entityType + "/id#" + entityId);
				IRI typeEntity = f.createIRI(ontologyPrefix + "/" + entityType);
				model.add(cellIID, RDF.TYPE, ontologyCell);	
				model.add(cellIID,ontologyHave,typeEntity);
				model.add(entityIdIRI, RDF.TYPE, typeEntity);
//				model.add(typeEntity,ontologyInclude,entityIdIRI);
//				entityType = entityType.substring(0, 1).toUpperCase() + entityType.substring(1);
//				model.add(cellIID, ontologyHave, entityIdIRI);
			}
			// 通过判断生成的文件是否存在来检查是更新图谱还是要新建
			File file = new File(storeDir + "\\" + cellId + ".ntriples");
			if (file.exists()) {
				InputStream input;
				try {
					input = new FileInputStream(file);
					Model preModel;
					try {
						preModel = Rio.parse(input, "", RDFFormat.NTRIPLES);
						model.addAll(preModel);
					} catch (RDFParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (UnsupportedRDFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			FileOutputStream out;
			try {
				out = new FileOutputStream(file);
				Rio.write(model, out, RDFFormat.NTRIPLES);
				try {
					out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}
}
