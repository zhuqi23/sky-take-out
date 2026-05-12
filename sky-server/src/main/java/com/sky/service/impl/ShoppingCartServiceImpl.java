package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class ShoppingCartServiceImpl implements ShoppingCartService {

	@Autowired
	private ShoppingCartMapper shoppingCartMapper;
	@Autowired
	private DishMapper dishMapper;
	@Autowired
	private SetmealMapper setmealMapper;

	@Override
	@Transactional
	public void addShoppingCart(ShoppingCartDTO shoppingCartDTO) {
		// 判断当前加入的菜品是否在购物车中
		ShoppingCart shoppingCart = new ShoppingCart();
		BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
		shoppingCart.setUserId(BaseContext.getCurrentId());

		List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);

		// 存在则数量加一
		if (list != null && list.size() > 0) {
			ShoppingCart cart = list.get(0);
			cart.setNumber(cart.getNumber() + 1);
			shoppingCartMapper.updateNumberById(cart);
		} else {
		// 不存在则添加一条数据
			// 判断本次添加到购物车的是菜品还是套餐
			Long dishId = shoppingCartDTO.getDishId();
			if (dishId != null) {  // 添加的是菜品, 补充信息
				Dish dish = dishMapper.getById(dishId);

				shoppingCart.setName(dish.getName());
				shoppingCart.setImage(dish.getImage());
				shoppingCart.setAmount(dish.getPrice());
			} else {
				Long setmealId = shoppingCartDTO.getSetmealId();
				Setmeal setmeal = setmealMapper.getById(setmealId);

				shoppingCart.setName(setmeal.getName());
				shoppingCart.setImage(setmeal.getImage());
				shoppingCart.setAmount(setmeal.getPrice());
			}
			shoppingCart.setNumber(1);
			shoppingCart.setCreateTime(LocalDateTime.now());

			shoppingCartMapper.insert(shoppingCart);
		}


	}

	@Override
	public List<ShoppingCart> list() {
		Long userId = BaseContext.getCurrentId();
		ShoppingCart shoppingCart = ShoppingCart.builder()
				.userId(userId)
				.build();
		return shoppingCartMapper.list(shoppingCart);
	}

	@Override
	public void cleanShoppingCart() {
		Long userId = BaseContext.getCurrentId();
		shoppingCartMapper.deleteByUserId(userId);
	}

	@Override
	public void subShoppingCart(ShoppingCartDTO shoppingCartDTO) {
		ShoppingCart shoppingCart = new ShoppingCart();
		BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
		shoppingCart.setUserId(BaseContext.getCurrentId());

		List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);

		if (list != null && list.size() > 0) {
			shoppingCart = list.get(0);

			if (shoppingCart.getNumber() == 1) {
				shoppingCartMapper.deleteById(shoppingCart.getId());
			} else {
				shoppingCart.setNumber(shoppingCart.getNumber() - 1);
				shoppingCartMapper.updateNumberById(shoppingCart);
			}
		}
	}
}
