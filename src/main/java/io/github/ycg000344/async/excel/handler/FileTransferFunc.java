package io.github.ycg000344.async.excel.handler;

/**
 * @author lusheng
 * @since 2021-07-09
 */
@FunctionalInterface
public interface FileTransferFunc {

    /**
     * 将源Excel文件转移至指定路径
     *
     * @param absolutePath 目标文件的绝对路径
     * @throws Exception 异常
     */
    void transferTo(String absolutePath) throws Exception;
}
