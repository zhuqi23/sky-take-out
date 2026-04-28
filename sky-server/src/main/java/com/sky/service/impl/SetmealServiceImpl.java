package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

@Service
public class SetmealServiceImpl implements SetmealService {

	@Autowired
	private SetmealMapper setmealMapper;
	@Autowired
	private SetmealDishMapper setmealDishMapper;

	@Override
	@Transactional
	public void save(SetmealDTO setmealDTO) {
		// 插入套餐数据
		Setmeal setmeal = new Setmeal();
		BeanUtils.copyProperties(setmealDTO, setmeal);
		setmealMapper.insert(setmeal);

		// 插入套餐和菜品的关联关系
		Long setmealId = setmeal.getId();
		List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
		for (SetmealDish setmealDish : setmealDishes) {
			setmealDish.setSetmealId(setmealId);
		}

		setmealDishMapper.insertBatch(setmealDishes);
	}

	@Override
	public PageResult page(SetmealPageQueryDTO setmealPageQueryDTO) {
		PageHelper.startPage(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());
		Page<SetmealVO> page = setmealMapper.page(setmealPageQueryDTO);
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Override
	@Transactional
	public void deleteBatch(List<Long> ids) {
		// 判断是否可删: 起售
		boolean existSell = setmealMapper.checkSell(ids);
		if (existSell) {
			throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
		}
		// 批量删除关联关系
		setmealDishMapper.deleteBatch(ids);
		// 批量删除套餐
		setmealMapper.deleteBatch(ids);
	}

	@Override
	public SetmealVO getByIdWithDish(Long id) {
		Setmeal setmeal = setmealMapper.getById(id);
		List<SetmealDish> setmealDishes = setmealDishMapper.getBySetmealId(id);

		SetmealVO setmealVO = new SetmealVO();
		BeanUtils.copyProperties(setmeal, setmealVO);
		setmealVO.setSetmealDishes(setmealDishes);
		return setmealVO;
	}

	@Override
	@Transactional
	public void update(SetmealDTO setmealDTO) {
		// 更新套餐数据
		Setmeal setmeal = new Setmeal();
		BeanUtils.copyProperties(setmealDTO, setmeal);
		setmealMapper.update(setmeal);

		// 删除套餐和菜品的关联关系
		Long setmealId = setmeal.getId();
		setmealDishMapper.deleteBatch(Arrays.asList(setmealId));

		// 插入新的关联关系
		List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
		for (SetmealDish setmealDish : setmealDishes) {
			setmealDish.setSetmealId(setmealId);
		}
		setmealDishMapper.insertBatch(setmealDishes);
	}

	@Override
	@Transactional
	public void startOrStop(Integer status, Long id) {
		// 起售时不能有停售菜品
		if (status == StatusConstant.ENABLE) {
			// 是否在套餐中存在停售菜品
			boolean existNotSale = setmealMapper.checkExistNotSale(id);
			if (existNotSale) {
				throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ENABLE_FAILED);
			}
		}

		Setmeal setmeal = Setmeal.builder()
				.id(id)
				.status(status)
				.build();

		setmealMapper.update(setmeal);
	}


}
