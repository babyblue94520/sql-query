#

##

### 使用

* 表達式

    * 參數表達式

      **Repository**

      ```java
      public interface QueryRepository {
          @Sql(query = "select * from table where column1 = :value1 and column2 = :value2")
          List query(String value1, String value2);
      
          // where in
          @Sql(query = "select * from table where column1 in :values1 and column2 in :values2")
          List query2(String[] values1,Collection values2);
      }
      ```

      **Service**

      ```java
      import org.springframework.util.StringUtils;import java.util.Collection;
      
      public class QueryServer {
          public List query(String value1, String value2) {
              queryRepository.query(value1, value2);
          }
    
          // where in
          public  List query2(String[] values1,Collection values2) {
              // values can't empty or null
              queryRepository.query2(values1, values2);
          }
      }
      ```

    * 動態語法表達式

      **Repository**

        ```java
        public interface QueryRepository {
            @Sql(query = "select * from table where 1=1 {and1} {and2}")
            List query(String and1, String and2, String value1, String value2);
        }
        ```

      **Service**

        ```java
        import org.springframework.util.StringUtils;
        
        public class QueryServer {
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

* **SQL** 語法設定 
