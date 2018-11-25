package com.lay.rushtopurchase.dao;

import com.lay.rushtopurchase.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @Description:
 * @Author: lay
 * @Date: Created in 15:19 2018/11/23
 * @Modified By:IntelliJ IDEA
 */
@Mapper
public interface ProductDao {
    //获取产品
    public Product getProduct(@Param("id") Long id);

    //减库存，而@Param标明Mybatis参数传递给后台
    public int decreaseProduct(@Param("id") Long id,@Param("quantity") int quantity);

    int getProductLastStock(@Param("id")Long productId,@Param("quantity")  int quantity);
}
