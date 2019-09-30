![company](/image/company.png) 
# 基于地理格网的时空知识图谱
## 简介
ATLab-KnowledgeGraph 是北京超图软件股份有限公司未来GIS实验室发布的开源项目,在iobjects产品的基础上，将地理信息实体按照时间和位置划分到多个网格，使用网格、时间及各实体之间的位置关系来构建地理知识图谱。
使用本项目API，用户可以使用若干数据集来构建自己的地理格网知识图谱，从而快速查询出指定地点缓冲区内的兴趣点。
本项目在知识图谱的表示上使用了RDF，存储使用RDF4J数据库
图谱示意图：
![shiyitu](/image/shiyi.png)
最终效果展示：
![xiaoguo](/image/result.png)

--- 
## 如何运行及使用
- 运行
  - 用eclipse直接clone本项目，GettingStarted目录下的GettingStarted类可以直接运行，查看结果
  - 同时jar目录下有生成的jar包，下载后也可以直接调用
- 使用
  - 初次使用流程：新建知识图谱-->加载知识图谱-->添加数据-->添加或查询
  - 非初次：加载知识图谱-->添加或查询

---
## Geokg包中主要类与方法介绍
- KnowledgeGraph类
  - 创建知识图谱方法 
    - 调用创建图谱方法，则会在指定目录创建数据库，一个目录下只能创建一个知识图谱，否则程序报错并强制退出
    - 创建知识图谱的方法有两个，都为静态方法，可以通过类名KnowledgeGraph直接调用，分别为： 
      ```java
      //@param iGridLevel 要构建的知识图谱网格的等级，取值范围为0-20,小于0取自动取0，大于20自动取20
      //@param strDataStore 自定义的存储知识图谱的本地目录
      public static boolean createKnowledgeGraph(int iGridLevel,String strDataStore){}
  
      //@param iGridLength 构建知识图谱的网格宽度（单位：米），根据传入的参数自动映射到网格等级，取值范围为9.8-9220000，分别对应等级20和0，小于9.8默认取9.8，大于9220000默认取9220000
      //@param 自定义的存储知识图谱的本地目录
      public static boolean createKnowledgeGraph(double iGridLength,String strDataStore){}
      ``` 
  - 加载知识图谱方法 
    - 以固定的存储路径为参数来加载一个已经存在的知识图谱，方法将返回一个知识图谱对象。
    - 加载知识图谱的方法也为静态方法
      ```java
      //@param strDataStore 自定义的存储知识图谱的本地目录
      public static KnowledgeGraph loadKnowledgeGraph(String strDataStore){}
      ``` 
  - 增量更新方法
    - 通过加载知识图谱方法返回的对象来增量添加数据
      ```java
      //@param dataSource udb文件的路径
      //@param arType 要增加的类型，类型为udb中数据集的名称
      public boolean addKnowledgeGraph(String dataSource, String[] arType){}
      ```  
  - 查询图谱方法 
    - 通过加载知识图谱方法返回的对象来查询图谱，查询经纬度必须为WGS84，半径单位为米
      ```java
      //@param dLatitude 搜索点的纬度
      //@param dLongitude 搜索点的经度
      //@param iRadius 搜索半径，单位：米
      //@param arType 感兴趣的类型，具体名称也为udb数据源显示的数据集名称
      public HashMap<String, ArrayList<RecordSetEntity>> queryKnowledgeGraph(double dLatitude, double dLongitude, double iRadius,String[] arType){}
      //@param time 地理实体的时间
      public HashMap<String, ArrayList<RecordSetEntity>> queryKnowledgeGraph(double dLatitude, double dLongitude, double iRadius,String[] arType，String time){}
      ```   
    - 查询返回对象 HashMap<String, ArrayList<RecordSetEntity>> 介绍
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


---
## 用前须知
- 运行本项目需要有iobjects的运行权限，首先需要确保可以正常使用iobjects。
- 目前只支持udb文件
 
---
## 总结
项目从无到有，从知识图谱的基础知识、构建方式、数据库选型，到目前Demo阶段性的完成，耗费了不少心神，由于本项目定位为Demo，难免有很多问题，欢迎各位对知识图谱和地理信息有兴趣的同学加入，共同维护。