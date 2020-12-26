package io.seata.samples.order;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.sql.SQLException;
import java.util.Map;

/**
 * 余额账户 DAO
 */
@Mapper
public interface MdDAO {

    int insertMd(@Param("xid") String xid, @Param("branch_id")long branch_id, @Param("log_status")long log_status) throws SQLException;

    Map selectMd(@Param("xid") String xid, @Param("branch_id")long branch_id, @Param("log_status")long log_status) throws SQLException;

    int updateMd(@Param("xid") String xid, @Param("branch_id")long branch_id, @Param("log_status")long log_status) throws SQLException;
}
