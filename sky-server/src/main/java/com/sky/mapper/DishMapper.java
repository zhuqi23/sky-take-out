package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishMapper {

	/**
	 * 根据分类id查询菜品数量
	 * @param id
	 * @return
	 */
	@Select("select count(id) from dish where category_id = #{id}")
	Integer countByCategoryId(Long id);

	/**
	 * 插入菜品数据
	 *
	 * @param dish
	 * @return
	 */
	@Insert("insert into dish (name, category_id, price, status, create_time, update_time, create_user, update_user, image, description) " +
			"values (#{name}, #{categoryId}, #{price}, #{status}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser}, #{image}, #{description})")
	@Options(useGeneratedKeys = true, keyProperty = "id")  // 将数据库生成的自增 ID 回填到传入对象的 id 属性中
	@AutoFill(value = OperationType.INSERT)
	void insert(Dish dish);  // Long insert() 返回值是受影响数量, 不是id

	/**
	 * 菜品分页查询
	 * @param dishPageQueryDTO
	 * @return
	 */
	Page<DishVO> page(DishPageQueryDTO dishPageQueryDTO);

	/**
	 * 查询ids中是否有在售的菜品
	 * @param ids
	 * @return
	 */
	Boolean checkDishStatus(List<Long> ids);

	/**
	 * 批量删除菜品
	 * @param ids
	 */
	void deleteBatch(List<Long> ids);


	/**
	 * 根据id查询菜品数据
	 * @param id
	 * @return
	 */
	@Select("select * from dish where id = #{id}")
	Dish getById(Long id);

	/**
	 * 根据id修改菜品数据
	 * @param dish
	 */
	@AutoFill(value = OperationType.UPDATE)
	void update(Dish dish);

	/**
	 * 根据分类id查询菜品
	 * @param categoryId
	 * @return
	 */
	@Select("select * from dish where category_id = #{categoryId}")
	List<Dish> list(Long categoryId);
}
