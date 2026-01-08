package com.okayjam.web.dao;

import com.okayjam.web.entity.Demo;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @description: ${description}
 * @author: Chen wei guang
 * @create: 2018/08/08 14:42
 **/
@Mapper
@Component
public interface DemoMapper {

    // 插入 并查询id 赋给传入的对象
    @Insert("INSERT INTO tb_test(key, value) VALUES(#{key}, #{value})")
    @SelectKey(statement = "SELECT seq id FROM sqlite_sequence WHERE (name = 'tb_test')", before = false, keyProperty = "id", resultType = int.class)
    int insert(Demo model);

    // 根据 ID 查询
    @Select("SELECT * FROM tb_test WHERE id=#{id}")
    Demo select(int id);

    // 查询全部
    @Select("SELECT * FROM tb_test")
    List<Demo> selectAll();

    // 更新 value
    @Update("UPDATE tb_test SET value=#{value} WHERE id=#{id}")
    int updateValue(Demo model);

    // 根据 ID 删除
    @Delete("DELETE FROM tb_test WHERE id=#{id}")
    int delete(Integer id);
}

