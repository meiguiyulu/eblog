# 记录

## 问题一：ftl与ftlh

> SpringBoot2.0以后前端页面的后缀名要使用 .ftlh 而不是 .ftl

## 问题二：数据库

> Failed to configure a DataSource: 'url' attribute is not specified and no embedded datasource could be configured.
>
> 无法配置DataSource：未指定'url'属性，也无法配置嵌入数据源。
>
> - 方案一：@SpringBootApplication(exclude= {DataSourceAutoConfiguration.class})
>
> - 方案二：在application.properties/或者application.yml文件中添加数据库配置信息
>
>   - ```yaml
>     spring:
>       datasource:
>         url: jdbc:mysql://localhost:3306/read_data?useUnicode=true&characterEncoding=UTF-8&useSSL=false
>         username: root
>         password: 123456
>         driver-class-name: com.mysql.jdbc.Driver
>     ```
>
> - 方案三：
>
>   - 在spring xml配置文件中引用了数据库地址 所以需要对:等进行转义处理.但是在application.properties/或者application.yml文件并不需要转义,错误和正确方法写在下面了
>
>   - ```java
>     //正确示例
>     spring.datasource.url = jdbc:mysql://192.168.0.20:1504/f_me?setUnicode=true&characterEncoding=utf8
>     ```
>
> - 方案四：
>
>   - yml或者properties文件没有被扫描到,需要在pom文件中<build></build>添加如下.来保证文件都能正常被扫描到并且加载成功.
>
>   - ```xml
>     <!-- 如果不添加此节点mybatis的mapper.xml文件都会被漏掉。 -->
>     <resources>
>         <resource>
>             <directory>src/main/java</directory>
>             <includes>
>                 <include>**/*.yml</include>
>                 <include>**/*.properties</include>
>                 <include>**/*.xml</include>
>             </includes>
>             <filtering>false</filtering>
>         </resource>
>         <resource>
>             <directory>src/main/resources</directory>
>             <includes>
>                 <include>**/*.yml</include>
>                 <include>**/*.properties</include>
>                 <include>**/*.xml</include>
>             </includes>
>             <filtering>false</filtering>
>         </resource>
>     </resources>
>     ```

## 问题三：MyBatisPlus

> 添加完 MyBatisPlus 以后，报错。
>
> 解决方法：
>
> 新建 MyBatisPlusConfig 类或者在启动类上添加注解
>
> ```java
> @MapperScan("com.lyj.eblog.mapper")
> ```

