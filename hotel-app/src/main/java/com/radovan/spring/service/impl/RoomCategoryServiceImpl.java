package com.radovan.spring.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.radovan.spring.converter.TempConverter;
import com.radovan.spring.dto.RoomCategoryDto;
import com.radovan.spring.entity.RoomCategoryEntity;
import com.radovan.spring.repository.RoomCategoryRepository;
import com.radovan.spring.service.RoomCategoryService;

@Service
@Transactional
public class RoomCategoryServiceImpl implements RoomCategoryService {

	@Autowired
	private RoomCategoryRepository categoryRepository;

	@Autowired
	private TempConverter tempConverter;

	@Override
	public RoomCategoryDto addCategory(RoomCategoryDto category) {
		// TODO Auto-generated method stub
		RoomCategoryEntity categoryEntity = tempConverter.roomCategoryDtoToEntity(category);
		RoomCategoryEntity storedCategory = categoryRepository.save(categoryEntity);
		RoomCategoryDto returnValue = tempConverter.roomCategoryEntityToDto(storedCategory);
		return returnValue;
	}

	@Override
	public RoomCategoryDto getCategoryById(Integer categoryId) {
		// TODO Auto-generated method stub
		RoomCategoryDto returnValue = null;
		Optional<RoomCategoryEntity> categoryOpt = categoryRepository.findById(categoryId);
		if (categoryOpt.isPresent()) {
			returnValue = tempConverter.roomCategoryEntityToDto(categoryOpt.get());
		}
		return returnValue;
	}

	@Override
	public void deleteCategory(Integer categoryId) {
		// TODO Auto-generated method stub
		categoryRepository.deleteById(categoryId);
		categoryRepository.flush();
	}

	@Override
	public List<RoomCategoryDto> listAll() {
		// TODO Auto-generated method stub
		List<RoomCategoryDto> returnValue = new ArrayList<RoomCategoryDto>();
		Optional<List<RoomCategoryEntity>> allCategoriesOpt = Optional.ofNullable(categoryRepository.findAll());
		if (!allCategoriesOpt.isEmpty()) {
			allCategoriesOpt.get().forEach((category) -> {
				RoomCategoryDto categoryDto = tempConverter.roomCategoryEntityToDto(category);
				returnValue.add(categoryDto);
			});
		}
		return returnValue;
	}

}
