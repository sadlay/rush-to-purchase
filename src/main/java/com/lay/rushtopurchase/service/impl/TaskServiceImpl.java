package com.lay.rushtopurchase.service.impl;

import com.lay.rushtopurchase.constant.RedisPurchaseConstant;
import com.lay.rushtopurchase.entity.PurchaseRecord;
import com.lay.rushtopurchase.service.PurchaseService;
import com.lay.rushtopurchase.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @Description:
 * @Author: lay
 * @Date: Created in 20:53 2018/11/25
 * @Modified By:IntelliJ IDEA
 */
@Service
public class TaskServiceImpl implements TaskService {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private PurchaseService purchaseService;

    @Override
    //每天凌晨1点开始执行任务
    //@Scheduled(cron = "0 0 1 * * ?")
    //测试，每分钟执行
    @Scheduled(fixedRate = 1000 * 600)
    public void purchaseTask() {
        System.out.println("定时任务开始......");
        Set<String> productIdList = stringRedisTemplate.opsForSet().members(RedisPurchaseConstant.PRODUCT_SCHEDULE_SET);
        List<PurchaseRecord> prpList = new ArrayList<>();
        for (String productIdStr : productIdList) {
            Long productId = Long.parseLong(productIdStr);
            String purchaseKey = RedisPurchaseConstant.PURCHASE_PRODUCT_LIST + productId;
            BoundListOperations<String, String> ops = stringRedisTemplate.boundListOps(purchaseKey);
            //计算记录数
            Long size = stringRedisTemplate.opsForList().size(purchaseKey);
            Long times = size % RedisPurchaseConstant.ONE_TIME_SIZE == 0 ? size / RedisPurchaseConstant.ONE_TIME_SIZE : size / RedisPurchaseConstant.ONE_TIME_SIZE + 1;
            for (int i = 0; i < times; i++) {
                //获取至多Times_size个信息
                List<String> prList = null;
                prList = ops.range(i * RedisPurchaseConstant.ONE_TIME_SIZE,   (i + 1) *RedisPurchaseConstant.ONE_TIME_SIZE-1);
                for (String prStr : prList) {
                    PurchaseRecord prp = this.createPurchaseRecord(productId, prStr);
                    prpList.add(prp);
                }
                try {
                    purchaseService.dealRedisPurchase(prpList);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                //清除列表为空，等待重新写入数据
                prpList.clear();
            }
            //删除购买列表
            stringRedisTemplate.delete(purchaseKey);
            //从商品集合中删除商品
            stringRedisTemplate.opsForSet().remove(RedisPurchaseConstant.PRODUCT_SCHEDULE_SET, productIdStr);
        }
        System.out.println("定时任务结束......");
    }

    private PurchaseRecord createPurchaseRecord(Long productId, String prStr) {
        String[] arr = prStr.split(",");
        Long userId = Long.valueOf(arr[0]);
        int quantity = Integer.valueOf(arr[1]);
        double sum = Double.valueOf(arr[2]);
        double price = Double.valueOf(arr[3]);
        Long time = Long.parseLong(arr[4]);
        Timestamp purchaseTime = new Timestamp(time);
        PurchaseRecord pr = new PurchaseRecord();
        pr.setProductId(productId);
        pr.setUserId(userId);
        pr.setSum(sum);
        pr.setPrice(price);
        pr.setPurchaseTime(purchaseTime);
        pr.setQuantity(quantity);
        pr.setNote("购买日志，时间：" + purchaseTime.getTime());
        return pr;
    }
}
