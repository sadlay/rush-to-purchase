package com.lay.rushtopurchase.constant;

/**
 * @Description:
 * @Author: lay
 * @Date: Created in 20:59 2018/11/25
 * @Modified By:IntelliJ IDEA
 */
public interface RedisPurchaseConstant {
    //redis购买记录集合前缀
    String PURCHASE_PRODUCT_LIST = "purchase_list_";

    //抢购商品集合
    String PRODUCT_SCHEDULE_SET = "product_schedule_set";

    //每次取出1000条，避免一次取出消耗太多内存
    int ONE_TIME_SIZE = 1000;

    //lua脚本
    String PURCHASESCRIPT =
            //先将产品编号保存到集合中
            "redis.call('sadd', KEYS[1], ARGV[2]) \n"
                    //购买列表
                    + "local productPurchaseList=KEYS[2]..ARGV[2] \n"
                    //用户编号
                    + "local userId = ARGV[1] \n"
                    //产品键
                    + "local product = 'product_'..ARGV[2] \n"
                    //购买数量
                    + "local quantity = tonumber(ARGV[3]) \n"
                    //当前库存
                    + "local stock = tonumber(redis.call('hget', product, 'stock')) \n"
                    //价格
                    + "local price = tonumber(redis.call('hget', product, 'price')) \n"
                    //购买时间
                    + "local purchase_date = ARGV[4] \n"
                    //库存不足返回0
                    + "if stock < quantity then return 0 end \n"
                    //减库存
                    + "stock = stock-quantity \n"
                    + "redis.call('hset', product, 'stock', tostring(stock)) \n"
                    //计算价格
                    + "local sum = price * quantity \n"
                    //合并购买记录数据
                    + "local purchaseRecord = userId..','..quantity..',' \n"
                    + "..sum..','..price..','..purchase_date \n"
                    //将购买记录保存到list里
                    + "redis.call('rpush', productPurchaseList, purchaseRecord) \n"
                    //返回成功
                    + "return 1 \n";
}
