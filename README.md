# Async Excel Component

异步处理 Excel Starter


> 背景：
> 
> 现在后台管理项目里或多或少会有 `Excel`的导入导出需求，进行导入、导出时采取同步的方式代码写起来比较无聊。
> 
> 组长给了要求：
> 
> 1. 采取异步的方式
> 2. 导入时的功能要求：
> 
>       2.1 以流的方式来操作 `Excel`，按行读取`Excel`进行操作
>    
>       2.2 有事务控制，提交、回滚
> 
>       2.3 不符合要求的数据要生成一份新的 `Excel`
> 
> 
> 3. 导入时的功能要求： 
>
>       3.1 导出时要分批次把数据写到  `Excel`
> 
>       3.2 支持多`sheet`导出
> 
> 4. 足够的抽象，便于不同的功能使用
> 
> 5. 要有进度



## 安装



### Maven

```xml
<async-excel-version.version>1.1.4</async-excel-versionversion>
```

在项目的pom.xml的dependencies中加入以下内容:

```xml
<dependency>
    <groupId>io.github.ycg000344</groupId>
    <artifactId>async-excel-spring-boot-starter</artifactId>
    <version>${async-excel-version.version}</version>
</dependency>
```

*注：若项目内引入了`mybatis`，需要将本项目中的`mybatis`移除*

```xml
<dependency>
    <groupId>io.github.ycg000344</groupId>
    <artifactId>async-excel-spring-boot-starter</artifactId>
    <version>${async-excel-version.version}</version>
    <exclusions>
      <exclusion>
	      <groupId>org.mybatis</groupId>
	      <artifactId>mybatis</artifactId>
      </exclusion>
    </exclusions>
</dependency>
```



## Import

特点：

+ 全局统一事务
+ 每次处理Excel内的一行数据，返回对该行数据处理的结果(`ImportHandlerResult.class`)
+ 将错误的记录行写到新的Excel文件内，并描述失败的原因
+ 计算进度

> AsyncImportHandler.class

使用示例：

```java

    // 1. 实现 handler
    class Handler implements AsyncImportHandler {}
    // 2. 创建对象
    Handler handler = new Handler();
    // 3. 注入 AsyncExcelTaskManager
    @Autowired
    AsyncExcelTaskManager manager;
    // 4. 创建任务
    manager.createImportTask(FileTransferFunc fileTransferFunc, AsyncImportHandler handler, ExecutorService service, SqlSessionFactory sqlSessionFactory, TaskProcessCacheFunc taskProcessCacheFunc);
```

## Export

特点：


+ 分页查询数据库，将查询的数据写入到 `Workbook` 对象内
+ 可以将查询数据库后的对象列表进行再次加工，转化为别的class对象列表
+ 支持多 sheet 导出
+ 支持静态表头、动态表头导出
+ 计算进度

> AsyncExportHandler.class

使用示例：

```java
    // 1. 继承 handler
    class Handler extends StaticExportHandler{}
    // 2. 创建对象
    Handler handler = new Handler();
    // 3. 注入 AsyncExcelTaskManager
    @Autowired
    AsyncExcelTaskManager manager;
    // 4. 创建任务
    manager.createExportTask(AsyncExportHandler handler, Executor service, TaskProcessCacheFunc taskProcessCacheFunc);
```


## Async Excel Demo

[demo](https://github.com/ycg000344/async_excel_demo)