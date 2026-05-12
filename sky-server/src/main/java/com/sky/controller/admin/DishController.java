package com.sky.controller.admin;

import com.github.pagehelper.PageHelper;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/admin/dish")
@Api(tags = "菜品相关接口")
@Slf4j
public class DishController {

	@Autowired
	private DishService dishService;
	@Autowired
	private RedisTemplate redisTemplate;

	/**
	 * 新增菜品
	 * @param dishDTO
	 * @return
	 */
	@PostMapping
	@ApiOperation("新增菜品")
	public Result save(@RequestBody DishDTO dishDTO) {
		log.info("新增菜品：{}", dishDTO);
		dishService.saveWithFlavor(dishDTO);

		// 删除缓存数据
		String key = "dish_" + dishDTO.getCategoryId();
		clearCache(key);

		return Result.success();
	}

	/**
	 * 菜品管理分页查询
	 * @param dishPageQueryDTO
	 * @return
	 */
	@GetMapping("/page")
	@ApiOperation("菜品管理分页查询")
	public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO) {
		log.info("菜品管理分页查询：{}", dishPageQueryDTO);
		PageResult pageResult = dishService.page(dishPageQueryDTO);
		return Result.success(pageResult);
	}

	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@DeleteMapping
	@ApiOperation("批量删除")
	public Result delete(@RequestParam List<Long> ids) {  // 传入的是字符串1,2,3, 用 RequestParam 解析成List
		log.info("批量删除：{}", ids);
		dishService.deleteBatch(ids);

		// 删除缓存数据, 可能影响多个key, 全删了
		clearCache("dish_*");

		return Result.success();
	}

	/**
	 * 根据id查询菜品和对应的口味数据
	 * @param id
	 * @return
	 */
	@GetMapping("/{id}")
	@ApiOperation("根据id查询菜品和对应的口味数据")
	public Result<DishVO> getById(@PathVariable Long id) {
		log.info("根据id查询菜品和对应的口味数据：{}", id);
		DishVO dishVO = dishService.getById(id);
		return Result.success(dishVO);
	}

	/**
	 * 修改菜品
	 * @param dishDTO
	 * @return
	 */
	@PutMapping
	@ApiOperation("修改菜品")
	public Result update(@RequestBody DishDTO dishDTO) {
		log.info("修改菜品：{}", dishDTO);
		dishService.updateWithFlavor(dishDTO);

		// 删除缓存数据, 可能修改分类, 影响多个key, 全删了
		clearCache("dish_*");

		return Result.success();
	}

	/**
	 * 菜品起售停售
	 * @param status
	 * @param id
	 * @return
	 */
	@PostMapping("/status/{status}")
	@ApiOperation("菜品起售停售")
	public Result startOrStop(@PathVariable Integer status, Long id) {
		log.info("菜品起售停售：{}", status, id);
		dishService.startOrStop(status, id);

		// 删除缓存数据, 要精准获取分类id还要查数据库, 所以全删了
		clearCache("dish_*");

		return Result.success();
	}

	@GetMapping("/list")
	@ApiOperation("根据分类id查询菜品数据")
	public Result<List<Dish>> list(Long categoryId) {
		log.info("根据分类id查询菜品数据：{}", categoryId);
		List<Dish> list = dishService.list(categoryId);
		return Result.success(list);
	}

	private void clearCache(String key) {
		Set keys = redisTemplate.keys(key);
		redisTemplate.delete(keys);
	}
}
