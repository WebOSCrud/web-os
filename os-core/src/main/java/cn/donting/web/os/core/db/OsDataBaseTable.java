package cn.donting.web.os.core.db;

import java.util.List;
import java.util.Optional;

/**
 * os 数据存储与查询操作
 * jpa 的语法，方便后面兼容 JPA ，将数据保存在 其他类型的数据库
 */
public interface OsDataBaseTable<T extends  DataId, ID> {
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
    T deleteById(ID id);

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
    int deleteAll(List<ID> ids);

    /**
     * 根据id 查找
     * @param id
     * @return
     */
    Optional<T> findById(ID id);

}
