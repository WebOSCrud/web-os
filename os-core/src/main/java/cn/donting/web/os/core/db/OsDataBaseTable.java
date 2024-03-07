package cn.donting.web.os.core.db;

import java.util.List;
import java.util.Optional;

/**
 * os 数据存储与查询操作
 */
public interface OsDataBaseTable<T> {
    /**
     * 保存一个实体
     * @param entity
     */
    T save(T entity);

    /**
     * 删除一个实体
     * @param id
     * @return
     */
    T deleteById(String id);

    /**
     * 查找所有
     * @return
     */
    List<T> findAll();

    /**
     * 根据id 批量删除
     * @param ids
     * @return
     */
    int deleteAll(List<String> ids);

    /**
     * 根据id 查找
     * @param id
     * @return
     */
    Optional<T> findById(String id);

    int saveAll(List<T> t);

    String getId(T t);


}
