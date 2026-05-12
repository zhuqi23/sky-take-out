package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {

	/**
	 * 根据用户id查询购物车数据
	 * @param
	 * @return
	 */
	List<ShoppingCart> list(ShoppingCart shoppingCart);

	/**
	 * 更新购物车数据，number
	 * @param cart
	 */
	@Update("update shopping_cart set number = #{number} where id = #{id}")
	void updateNumberById(ShoppingCart cart);

	/**
	 * 插入数据
	 * @param shoppingCart
	 */
	@Insert("insert into shopping_cart (name, image, dish_id, setmeal_id, dish_flavor, number, amount, create_time, user_id) " +
			"values (#{name}, #{image}, #{dishId}, #{setmealId}, #{dishFlavor}, #{number}, #{amount}, #{createTime}, #{userId})")
	void insert(ShoppingCart shoppingCart);

	/**
	 * 根据用户id删除
	 * @param userId
	 */
	@Delete("delete from shopping_cart where user_id = #{userId}")
	void deleteByUserId(Long userId);

	/**
	 * 根据id删除购物车数据
	 * @param id
	 */
	@Delete("delete from shopping_cart where id = #{id}")
	void deleteById(Long id);
}
