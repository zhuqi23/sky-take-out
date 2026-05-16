package com.sky.mapper;

import com.sky.entity.Orders;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;

@Mapper
public interface OrderMapper {
	/**
	 * 插入订单数据
	 * @param orders
	 */
	void insert(Orders orders);

	/**
	 * 根据订单号查询订单
	 * @param orderNumber
	 */
	@Select("select * from orders where number = #{orderNumber}")
	Orders getByNumber(String orderNumber);

	/**
	 * 修改订单信息
	 * @param orders
	 */
	void update(Orders orders);

	/**
	 * 修改订单状态
	 * @param orderStatus
	 * @param orderPaidStatus
	 * @param checkOutTime
	 * @param orderNumber
	 */
	void updateStatus(@Param("orderStatus") Integer orderStatus,
                      @Param("orderPaidStatus") Integer orderPaidStatus, 
                      @Param("checkOutTime") LocalDateTime checkOutTime, 
                      @Param("orderNumber") String orderNumber);
}
