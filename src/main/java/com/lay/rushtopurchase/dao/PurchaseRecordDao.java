package com.lay.rushtopurchase.dao;

import com.lay.rushtopurchase.entity.PurchaseRecord;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @Description:
 * @Author: lay
 * @Date: Created in 15:21 2018/11/23
 * @Modified By:IntelliJ IDEA
 */
@Mapper
public interface PurchaseRecordDao {
    public int insertPurchaseRecord(PurchaseRecord pr);
}
