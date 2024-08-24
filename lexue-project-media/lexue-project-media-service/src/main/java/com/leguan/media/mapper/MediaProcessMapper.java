package com.leguan.media.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.leguan.media.model.po.MediaProcess;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author leguan
 */
public interface MediaProcessMapper extends BaseMapper<MediaProcess> {

    @Select("SELECT * FROM media_process t WHERE t.id % #{shardTotal} = #{shardIndex} AND (t.status = 1 OR t.status = 3) AND t.fail_count < 3 LIMIT #{count}")
    List<MediaProcess> selectListByShardIndex(@Param("shardTotal") int shardTotal, @Param("shardIndex") int shardIndex, @Param("count") int count);

    /**
     * 开始一个任务
     * @param id 任务id
     * @return 更新记录数
     */
    @Update("update media_process m set m.status = '4' where (m.status = '1' or m.status = '3') and m.fail_count < 3 and m.id = #{id}")
    int startTask(@Param("id") Long id);
}
