package io.github.ycg000344.async.excel.handler;

import io.github.ycg000344.async.excel.bean.ImportHandlerResult;
import org.apache.ibatis.session.SqlSession;
import org.apache.poi.ss.usermodel.Row;

public interface AsyncImportHandler {

    /**
     * 利用 sqlSession.getMapper() 获取指定的 Mapper来实现全局统一事务
     *
     * @param sqlSession
     */
    void getMapper(SqlSession sqlSession);

    /**
     * 处理Excel的表头
     *
     * @param row
     */
    default void header(Row row) {
    }

    /**
     * 处理Excel内的每一行数据
     *
     * @param row
     * @return
     */
    ImportHandlerResult handle(Row row);

    /**
     * 将错误的记录写入新的Excel文件内
     *
     * @return
     */
    default boolean needFailure() {
        return true;
    }

}
