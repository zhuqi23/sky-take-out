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
}
