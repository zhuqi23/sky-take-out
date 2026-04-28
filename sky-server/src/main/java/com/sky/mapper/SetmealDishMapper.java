package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {

	/**
	 * 判断当前菜品是否关联了套餐
	 * @param ids
	 * @return
	 */
	boolean checkDishRelation(List<Long> ids);

	/**
	 * 批量插入套餐和菜品的关联关系
	 * @param setmealDishes
	 */
	void insertBatch(List<SetmealDish> setmealDishes);

	/**
	 * 批量删除套餐和菜品的关联关系
	 * @param ids
	 */
	void deleteBatch(List<Long> ids);

	/**
	 * 根据套餐id查询套餐和菜品的关联关系
	 * @param id
	 * @return
	 */
	@Select("select * from setmeal_dish where setmeal_id = #{id}")
	List<SetmealDish> getBySetmealId(Long id);

}
