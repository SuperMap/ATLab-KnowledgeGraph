# ATLab-KnowledgeGraph

---
## 1.用前须知
- 默认使用该项目的用户拥有iobjects的运行权限，如果没有或不确定，需要先确保可以正常使用iobjects。
- 目前只支持udb文件
--- 
## 2.地理知识图谱介绍
- 2.1 功能：
  用户可以将UDB文件中指定或全部数据集写入到本地知识图谱仓库中，同时可以使用确定的经纬度（WGS84）与半径（米）从知识图谱中查询出符合条件的所有实体信息。例如：将北京市的小区、医院、学校等数据集添加到知识图谱后，想要知道某一个经纬度3公里范围内有哪些学校、医院。
- 2.2 流程：
  - 没有创建知识图谱：
    - 新建知识图谱-->添加数据-->添加或查询
  - 使用创建过的知识图谱
    - 加载知识图谱-->添加或查询
---
## 3.如何使用
- 用eclipse直接clone本项目

---
##4.Geokg包中主要类与方法介绍
- KnowledgeGraph类
  - 4.1 创建知识图谱方法 
    - 4.1.1 调用创建图谱方法，则会在指定目录创建数据库，一个目录下只能创建一个知识图谱，否则程序报错并强制退出
    - 4.1.2 创建知识图谱的方法有两个，都为静态方法，可以通过类名KnowledgeGraph直接调用，分别为： 
      ```java
      //@param iGridLevel 要构建的知识图谱网格的等级，取值范围为0-20,小于0取自动取0，大于20自动取20
      //@param strDataStore 自定义的存储知识图谱的本地目录
      public static boolean createKnowledgeGraph(int iGridLevel,String strDataStore){}
  
      //@param iGridLength 构建知识图谱的网格宽度（单位：米），根据传入的参数自动映射到网格等级，取值范围为9.8-9220000，分别对应等级20和0，小于9.8默认取9.8，大于9220000默认取9220000
      //@param 自定义的存储知识图谱的本地目录
      public static boolean createKnowledgeGraph(double iGridLength,String strDataStore){}
      ``` 
  - 4.2 加载知识图谱方法 
    - 4.2.1 以固定的存储路径为参数来加载一个已经存在的知识图谱，方法将返回一个知识图谱对象。
    - 4.2.2 加载知识图谱的方法也为静态方法
      ```java
      //@param strDataStore 自定义的存储知识图谱的本地目录
      public static KnowledgeGraph loadKnowledgeGraph(String strDataStore){}
      ``` 
  - 4.3 增量更新方法
    - 4.3.1 通过加载知识图谱方法返回的对象来增量添加数据
      ```java
      //@param dataSource udb文件的路径
      //@param arType 要增加的类型，类型为udb中数据集的名称
      public boolean addKnowledgeGraph(String dataSource, String[] arType){}
      ```  
  - 4.4 查询图谱方法 
    - 4.4.1 通过加载知识图谱方法返回的对象来查询图谱，查询经纬度必须为WGS84，半径单位为米
      ```java
      //@param dLatitude 搜索点的纬度
      //@param dLongitude 搜索点的经度
      //@param iRadius 搜索半径，单位：米
      //@param arType 感兴趣的类型，具体名称也为udb数据源显示的数据集名称
      public HashMap<String, ArrayList<RecordSetEntity>> queryKnowledgeGraph(double dLatitude, double dLongitude, double iRadius,String[] arType){}
      ```   
    - 4.4.2 查询返回对象 HashMap<String, ArrayList<RecordSetEntity>> 介绍
      - HashMap的key为您输入的类型，vaule为该类型的实体
      - RecordSetEntity类目前有两个属性，分别为point和mingCheng,分别为实体的经纬度与名称，可以通过get()获得
      - 注意：使用recordSet.getMingCheng()获取的可能为null，因为有些数据集可能没有名称字段，目前的处理方式为：没有名称字段便寻找位置字段，然后寻找区县字段，都没有则置为null
        ```java
        //查看搜索返回的各个类型的实体个数
        for (Entry<String, ArrayList<RecordSetEntity>> entry : result.entrySet()) {
            System.out.println(entry.getKey()+":个数"+entry.getValue().size());
        }

        //查询各个类型的具体实体信息
        for (Entry<String, ArrayList<RecordSetEntity>> entry : result.entrySet()) {
              System.out.println(entry.getKey()+":个数"+entry.getValue().size());
              for (RecordSetEntity recordSet : entry.getValue()) {
                System.out.println("\t"+recordSet.getMingCheng()+recordSet.getPoint());
              }
        }
        ``` 
