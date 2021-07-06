# Async Excel Component

异步处理 Excel Starter

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
    manager.createImportTask(AsyncImportHandler handler, ExecutorService service, SqlSessionFactory sqlSessionFactory, RedisTemplate redisTemplate);
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
    manager.createExportTask(AsyncExportHandler handler, Executor service, RedisTemplate redisTemplate);
```