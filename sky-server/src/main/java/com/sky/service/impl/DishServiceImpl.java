package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class DishServiceImpl implements DishService {

	@Autowired
	private DishMapper dishMapper;
	@Autowired
	private DishFlavorMapper dishFlavorMapper;
	@Autowired
	private SetmealDishMapper setmealDishMapper;

	/**
	 * 新增菜品，同时保存对应的口味数据
	 * @param dishDTO
	 */
	@Override
	@Transactional
	public void saveWithFlavor(DishDTO dishDTO) {
		Dish Dish = new Dish();
		BeanUtils.copyProperties(dishDTO, Dish);

		// 向菜品表插入一条数据
		dishMapper.insert(Dish);

		Long dishId = Dish.getId();
		// 向口味表插入n条数据
		List<DishFlavor> flavors = dishDTO.getFlavors();
		if (flavors != null && !flavors.isEmpty()) {
			// 遍历设置菜品id
			for (DishFlavor flavor : flavors) {
				flavor.setDishId(dishId);
			}
			dishFlavorMapper.insertBatch(flavors);
		}
	}

	@Override
	public PageResult page(DishPageQueryDTO dishPageQueryDTO) {
		PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
		Page<DishVO> page = dishMapper.page(dishPageQueryDTO);
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Override
	@Transactional
	public void deleteBatch(List<Long> ids) {
		// 判断是否可删: 起售, 与套餐关联
		boolean existSell = dishMapper.checkDishStatus(ids);
		if (existSell) {
			throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
		}
		boolean existRelation = setmealDishMapper.checkDishRelation(ids);
		if (existRelation) {
			throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
		}
		// 先删除关联口味, 避免外键约束 (实际上是逻辑外键, 无所谓先后删除)
		dishFlavorMapper.deleteBatch(ids);
		// 批量删除
		dishMapper.deleteBatch(ids);
	}

	@Override
	public DishVO getById(Long id) {
		// 根据id查询菜品数据
		Dish dish = dishMapper.getById(id);

		// 根据id查询口味数据
		List<DishFlavor> dishFlavors = dishFlavorMapper.getByDishId(id);

		// 组装VO
		DishVO dishVO = new DishVO();
		BeanUtils.copyProperties(dish, dishVO);
		dishVO.setFlavors(dishFlavors);

		return dishVO;
	}

	@Override
	public void updateWithFlavor(DishDTO dishDTO) {
		// 先把原口味删掉
		dishFlavorMapper.deleteByDishId(dishDTO.getId());
		// 添加新的口味
		List<DishFlavor> flavors = dishDTO.getFlavors();
		if (flavors != null && !flavors.isEmpty()) {
			// 遍历设置菜品id
			for (DishFlavor flavor : flavors) {
				flavor.setDishId(dishDTO.getId());
			}
			dishFlavorMapper.insertBatch(flavors);
		}
		// 修改菜品
		Dish dish = new Dish();
		BeanUtils.copyProperties(dishDTO, dish);
		dishMapper.update(dish);
	}

	@Override
	public void startOrStop(Integer status, Long id) {
		Dish dish = Dish.builder()
				.id(id)
				.status(status)
				.build();
		dishMapper.update(dish);
	}

	@Override
	public List<Dish> list(Long categoryId) {
		List<Dish> list = dishMapper.list(categoryId);
		return list;
	}

}
