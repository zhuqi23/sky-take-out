package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealMapper {

	/**
	 * 根据分类id查询套餐的数量
	 * @param id
	 * @return
	 */
	@Select("select count(id) from setmeal where category_id = #{id}")
	Integer countByCategoryId(Long id);

	/**
	 * 插入套餐数据
	 * @param setmeal
	 */
	@Insert("insert into setmeal (name, category_id, price, status, create_time, update_time, create_user, update_user, description, image) " +
			"values (#{name}, #{categoryId}, #{price}, #{status}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser}, #{description}, #{image})")
	@AutoFill(value = OperationType.INSERT)
	@Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
	void insert(Setmeal setmeal);

	/**
	 * 套餐分页查询
	 * @param setmealPageQueryDTO
	 * @return
	 */
	Page<SetmealVO> page(SetmealPageQueryDTO setmealPageQueryDTO);

	/**
	 * 检查套餐是否在售
	 * @param ids
	 * @return
	 */
	boolean checkSell(List<Long> ids);

	/**
	 * 批量删除套餐
	 * @param ids
	 */
	void deleteBatch(List<Long> ids);

	/**
	 * 根据id查询套餐数据
	 * @param id
	 * @return
	 */
	@Select("select * from setmeal where id = #{id}")
	Setmeal getById(Long id);

	/**
	 * 修改套餐数据
	 * @param setmeal
	 */
	@AutoFill(value = OperationType.UPDATE)
	@Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
	void update(Setmeal setmeal);

	/**
	 * 是否在套餐中存在停售菜品
	 * @param id
	 * @return
	 */
	boolean checkExistNotSale(Long id);
}
