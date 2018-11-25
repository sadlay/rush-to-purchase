package com.lay.rushtopurchase.service.impl;

import com.lay.rushtopurchase.constant.RedisPurchaseConstant;
import com.lay.rushtopurchase.dao.ProductDao;
import com.lay.rushtopurchase.dao.PurchaseRecordDao;
import com.lay.rushtopurchase.entity.Product;
import com.lay.rushtopurchase.entity.PurchaseRecord;
import com.lay.rushtopurchase.service.PurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;

import java.util.List;

/**
 * @Description:
 * @Author: lay
 * @Date: Created in 15:34 2018/11/23
 * @Modified By:IntelliJ IDEA
 */
@Service
public class PurchaseServiceImpl implements PurchaseService {

    @Autowired
    private ProductDao productDao;

    @Autowired
    private PurchaseRecordDao purchaseRecordDao;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    //lua脚本
    private String purchaseScript =RedisPurchaseConstant.PURCHASESCRIPT;

    //32位SHA1码，第一次执行的时候先让Redis进行缓存脚本返回
    private String sha1 = null;


    @Override
    @Transactional
    public boolean purchase(Long userId, Long productId, int quantity) {
        //获取产品
        Product product = productDao.getProduct(productId);
        //比较库存和购买数量
        if (product.getStock() < quantity) {
            //库存不足
            return false;
        }
        //扣减库存
        productDao.decreaseProduct(productId, quantity);
        //初始化购买记录
        PurchaseRecord pr = initPurChaseRecord(userId, product, quantity);
        //插入购买记录
        purchaseRecordDao.insertPurchaseRecord(pr);
        return true;

    }

    @Override
    public boolean purchaseRedis(Long userId, Long productId, int quantity) {
        //购买时间
        Long purchaseDate = System.currentTimeMillis();
        Jedis jedis = null;
        //获得原始连接
        try {
            jedis = (Jedis) stringRedisTemplate.getConnectionFactory().getConnection().getNativeConnection();
            //如果没有加载过，则先将脚本加载到Redis服务器，让其返回sha1
            if (sha1 == null) {
                sha1 = jedis.scriptLoad(purchaseScript);
            }
            //执行脚本，返回结果
            Object res = jedis.evalsha(sha1, 2, RedisPurchaseConstant.PRODUCT_SCHEDULE_SET, RedisPurchaseConstant.PURCHASE_PRODUCT_LIST, userId + "", productId + "", quantity + "", purchaseDate + "");
            Long result = (Long) res;
            return result == 1;
        } finally {
            //关闭jedis连接
            if (jedis != null && jedis.isConnected()) {
                jedis.close();
            }
        }
    }

    @Override
    //当运行方法启用新事务独立运行
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean dealRedisPurchase(List<PurchaseRecord> prpList) {
        for (PurchaseRecord prp : prpList) {
            purchaseRecordDao.insertPurchaseRecord(prp);
            productDao.decreaseProduct(prp.getProductId(),prp.getQuantity());
        }
        return true;
    }

    //初始化购买信息
    private PurchaseRecord initPurChaseRecord(Long userId, Product product, int quantity) {
        PurchaseRecord pr = new PurchaseRecord();
        pr.setNote("购买日志，时间：" + System.currentTimeMillis());
        pr.setPrice(product.getPrice());
        pr.setUserId(userId);
        pr.setProductId(product.getId());
        pr.setQuantity(quantity);
        double sum = product.getPrice() * quantity;
        pr.setSum(sum);
        return pr;
    }
}
