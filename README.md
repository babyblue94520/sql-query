#

##

### 配置

```java
@Configuration
@SQLScan(
        sqlStoreServiceRef = SQLQueryConfig.SQLStoreServiceName
        , basePackages = {"pers.clare.demo.data.sql"}
)
public class SQLQueryConfig {
    public static final String Prefix = "demo";
    public static final String SQLStoreServiceName = Prefix + "SQLStoreService";

    @Primary
    @Bean(name = SQLStoreServiceName)
    public SQLStoreService sqlStoreService(
            DataSource dataSource
    ) {
        return new SQLStoreService(dataSource);
    }
}
```

### 使用

* **SQLCrudRepository**

    實作 **Entity** 查詢、新增、修改和刪除等等的簡易操作
    
    **預設方法**
    
    ```java
    @NoRepositoryBean
    public interface SQLCrudRepository<T> extends SQLRepository {
    
        long count();
    
        long count(T entity);
    
        long countById(Object... keys);
    
        List<T> findAll();
    
        Page<T> page(Pagination pagination);
    
        Next<T> next(Pagination pagination);
    
        T find(T entity);
    
        T findById(Object... keys);
    
        T insert(T entity);
    
        int update(T entity);
    
        int delete(T entity);
    
        int deleteById(Object... keys);
    
        int deleteAll();
    }
    ```
    
    **簡易使用**
  
    ```Java
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Entity
    public class User {
    
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
    
        private String account;
    
        private String name;
    
        private String email;
    
        private Boolean locked;
    
        private Boolean enabled;
    
        @Column(name = "update_time")
        private Long updateTime;
    
        @Column(name = "update_user")
        private Long updateUser;
    
        @Column(name = "create_time", updatable = false)
        private Long createTime;
    
        @Column(name = "create_user", updatable = false)
        private Long createUser;
    }

    public interface UserRepository extends SQLCrudRepository<User> {
    
    }
    ```

* **SQLRepository**
    
    * **SQL** 表達式
    
        * 參數
            
            * **:value** 表示傳入的值
    
          **Repository**
    
          ```java
          public interface QueryRepository extends SQLRepository {
              @Sql(query = "select * from table where column1 = :value1 and column2 = :value2")
              List query(String value1, String value2);
          
              // where in
              @Sql(query = "select * from table where column1 in :values1 and column2 in :values2")
              List query2(String[] values1,Collection values2);
          }
          ```
    
          **Service**
    
          ```java
          public class QueryService {
              public List query(String value1, String value2) {
                  queryRepository.query(value1, value2);
              }
        
              // where in
              public  List query2(String[] values1, Collection values2) {
                  // values can't empty or null
                  queryRepository.query2(values1, values2);
              }
          }
          ```
    
        * 動態語法
        
            * **{SQL}** 表示將被替換內容的 **SQL** 語法
    
          **Repository**
    
            ```java
            public interface QueryRepository extends SQLRepository {
                @Sql(query = "select * from table where 1=1 {and1} {and2}")
                List query(String and1, String and2, String value1, String value2);
            }
            ```
    
          **Service**
        
            * **value1、value2** 為空值時，則不加入查詢條件
    
            ```java
            public class QueryService {
                public List query(String value1, String value2) {
                    queryRepository.query(
                            StringUtils.isEmpty(value1) ? "" : "and column1 = :value1"
                            , StringUtils.isEmpty(value2) ? "" : "and column2 = :value2"
                            , value1
                            , value2
                    );
                }
            }
            ```
    
    * 回傳資料格式
    
        * **Basic Type**
        * **Java Bean**
        * **Map**
        * **Set**
        * **List**
        * **Page**
        * **Next**

        **Interface**
    
        ```java
        @Getter
        @AllArgsConstructor
        @NoArgsConstructor
        public class SimpleUser {
            private Long id;
            private String name;
        }

        public interface SimpleUserRepository extends SQLRepository {
            @Sql("select id from user")
            Long findId();
        
            @Sql("select * from user")
            User find();
        
            @Sql("select id,name from user limit 0,10")
            List findAllSimpleMap();
        
            @Sql("select id,name from user limit 0,10")
            List<SimpleUser> findAllSimple();
        
            @Sql("select id,name from user")
            List<Map<String, Object>> findAllMap(Pagination pagination);
        
            @Sql("select id,name from user")
            List<SimpleUser> findAll(Pagination pagination);
        
            @Sql("select id from user")
            List<Long> findAllId(Pagination pagination);
        
            @Sql("select id,name from user limit 0,10")
            Set findAllSimpleSetMap();
        
            @Sql("select id,name from user limit 0,10")
            Set<Map<String, String>> findAllSimpleSetMapString();
        
            @Sql("select create_time from user")
            Set<Long> findAllTime(Pagination pagination);
        
            @Sql("select * from user")
            Page<Map> mapPage(Pagination pagination);
        
            @Sql("select * from user where create_time between :startTime and :endTime {andId}{andName}")
            Page<User> page(
                    String andId
                    , String andName
                    , Pagination pagination
                    , Long startTime
                    , Long endTime
                    , Long id
                    , String name
            );
        
            @Sql("select id,name from user where name like ? limit ?,?")
            List<SimpleUser> findAllSimple(String name, int page, int size);
        
            /**
             * use method name to get sql from XML
             */
            List<Map<String, Object>> findAllMapXML(Pagination pagination);
        
            /**
             * use name to get sql from XML
             */
            @Sql(name = "pageMapXML")
            Page<User> pageMapXML(
                    String andId
                    , String andName
                    , Pagination pagination
                    , Long startTime
                    , Long endTime
                    , Long id
                    , String name
            );
        }
        ```

      **Example**

        ```java
        @RequestMapping("user/simple")
        @RestController
        public class SimpleUserController {
        
            @Autowired
            private SimpleUserRepository simpleUserRepository;
        
            @GetMapping("one/id")
            public Long findId(
            ) throws Exception {
                return simpleUserRepository.findId();
            }
        
            @GetMapping("one")
            public User find(
            ) throws Exception {
                return simpleUserRepository.find();
            }
        
            @GetMapping("map")
            public Collection findAllSimpleMap(
            ) throws Exception {
                return simpleUserRepository.findAllSimpleMap();
            }
        
            @GetMapping("map/2")
            public Collection findAllSimpleMap(
                    Pagination pagination
            ) throws Exception {
                return simpleUserRepository.findAllMap(pagination);
            }
        
            @GetMapping
            public Collection findAllSimple(
            ) throws Exception {
                return simpleUserRepository.findAllSimple();
            }
        
            @GetMapping("id")
            public Collection findAllId(
                    Pagination pagination
            ) throws Exception {
                return simpleUserRepository.findAllId(pagination);
            }
        
            @GetMapping("set")
            public Collection findAllSimpleSetMap(
            ) throws Exception {
                return simpleUserRepository.findAllSimpleSetMap();
            }
        
            @GetMapping("time")
            public Collection findAllSimpleSetMapString(
                    Pagination pagination
            ) throws Exception {
                return simpleUserRepository.findAllTime(pagination);
            }
        
            @GetMapping("page/map")
            public Page mapPage(
                    Pagination pagination
            ) throws Exception {
                return simpleUserRepository.mapPage(pagination);
            }
        
            @GetMapping("page")
            public Page<User> page(
                    Pagination pagination
                    , Long startTime
                    , Long endTime
                    , Long id
                    , String name
            ) throws Exception {
                return simpleUserRepository.page(
                        id == null ? "" : "and id = :id"
                        , StringUtils.isEmpty(name) ? "" : "and name like :name"
                        , pagination
                        , startTime
                        , endTime
                        , id
                        , name
                );
            }
        
            @GetMapping("2")
            public Collection findAllSimple(
                    String name
                    , int page
                    , int size
            ) throws Exception {
                return simpleUserRepository.findAllSimple(name, page, size);
            }
        
            @GetMapping("xml")
            public Collection findAllMapXML(
                    Pagination pagination
            ) throws Exception {
                return simpleUserRepository.findAllMapXML(pagination);
            }
        
            @GetMapping("page/xml")
            public Page<User> pageMapXML(
                    Pagination pagination
                    , Long startTime
                    , Long endTime
                    , Long id
                    , String name
            ) throws Exception {
                return simpleUserRepository.pageMapXML(
                        id == null ? "" : "and id = :id"
                        , StringUtils.isEmpty(name) ? "" : "and name like :name"
                        , pagination
                        , startTime
                        , endTime
                        , id
                        , name
                );
            }
        }
        ```


* 配置 **XML SQL**

    * 根目錄 **resources/sqlquery/**
    * **package/Repository.XML**
    * 依照 **Method Name** or **@Sql(name=...)** 載入對應的標籤名稱
    * 方便複雜的 **SQL** 排版
      
        ex: resources\sqlquery\pers\clare\demo\data\sql\SimpleUserRepository.xml

        ```xml
        <?xml version="1.0" encoding="UTF-8"?>
        <!DOCTYPE SQL>
        <SQL>
            <findAllMapXML><![CDATA[
                select id
                    ,name
                    ,create_time
                from user
            ]]></findAllMapXML>
            <findAllMapXML2><![CDATA[
                select *
                from user
                where create_time between :startTime and :endTime
                {andId}
                {andName}
            ]]></findAllMapXML2>
        </SQL>
        ```

* **Transaction**
