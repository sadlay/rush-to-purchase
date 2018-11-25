package com.lay.rushtopurchase.service;

import com.lay.rushtopurchase.entity.PurchaseRecord;

import java.util.List;

/**
 * @Description:
 * @Author: lay
 * @Date: Created in 15:32 2018/11/23
 * @Modified By:IntelliJ IDEA
 */
public interface PurchaseService {
    //处理购买业务
    public boolean purchase(Long userId,Long productId,int quantity);

    //使用redis处理
    public boolean purchaseRedis(Long userId,Long productId,int quantity);

    //保存购买记录
    public boolean dealRedisPurchase(List<PurchaseRecord> prpList);
}


