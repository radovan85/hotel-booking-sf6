package com.radovan.spring.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.radovan.spring.converter.TempConverter;
import com.radovan.spring.dto.RoomDto;
import com.radovan.spring.entity.RoomCategoryEntity;
import com.radovan.spring.entity.RoomEntity;
import com.radovan.spring.exceptions.ExistingRoomNumberException;
import com.radovan.spring.repository.RoomCategoryRepository;
import com.radovan.spring.repository.RoomRepository;
import com.radovan.spring.service.RoomService;

@Service
@Transactional
public class RoomServiceImpl implements RoomService {

	@Autowired
	private RoomRepository roomRepository;

	@Autowired
	private RoomCategoryRepository categoryRepository;

	@Autowired
	private TempConverter tempConverter;

	@Override
	public RoomDto addRoom(RoomDto room) {
		// TODO Auto-generated method stub
		Optional<Integer> categoryIdOpt = Optional.ofNullable(room.getRoomCategoryId());
		if (categoryIdOpt.isPresent()) {
			Integer categoryId = categoryIdOpt.get();
			RoomCategoryEntity categoryEntity = categoryRepository.findById(categoryId).get();
			room.setPrice(categoryEntity.getPrice());
		}

		Optional<Integer> roomNumberOpt = Optional.ofNullable(room.getRoomNumber());
		if (roomNumberOpt.isPresent()) {
			Optional<RoomEntity> roomOpt = Optional.ofNullable(roomRepository.findByRoomNumber(roomNumberOpt.get()));
			if (roomOpt.isPresent()) {
				Error error = new Error("Existing room number");
				throw new ExistingRoomNumberException(error);
			}
		}
		RoomEntity roomEntity = tempConverter.roomDtoToEntity(room);
		RoomEntity storedRoom = roomRepository.save(roomEntity);
		RoomDto returnValue = tempConverter.roomEntityToDto(storedRoom);
		return returnValue;
	}

	@Override
	public RoomDto getRoomById(Integer roomId) {
		// TODO Auto-generated method stub
		RoomDto returnValue = null;
		Optional<RoomEntity> roomOpt = roomRepository.findById(roomId);
		if (roomOpt.isPresent()) {
			returnValue = tempConverter.roomEntityToDto(roomOpt.get());
		}
		return returnValue;
	}

	@Override
	public void deleteRoom(Integer roomId) {
		// TODO Auto-generated method stub
		roomRepository.deleteById(roomId);
		roomRepository.flush();
	}

	@Override
	public List<RoomDto> listAll() {
		// TODO Auto-generated method stub
		List<RoomDto> returnValue = new ArrayList<RoomDto>();
		Optional<List<RoomEntity>> allRoomsOpt = Optional.ofNullable(roomRepository.findAll());
		if (!allRoomsOpt.isEmpty()) {
			allRoomsOpt.get().forEach((room) -> {
				RoomDto roomDto = tempConverter.roomEntityToDto(room);
				returnValue.add(roomDto);
			});
		}
		return returnValue;
	}

	@Override
	public List<RoomDto> listAllByCategoryId(Integer categoryId) {
		// TODO Auto-generated method stub
		List<RoomDto> returnValue = new ArrayList<RoomDto>();
		Optional<List<RoomEntity>> allRoomsOpt = Optional.ofNullable(roomRepository.findAllByCategoryId(categoryId));
		if (!allRoomsOpt.isEmpty()) {
			allRoomsOpt.get().forEach((room) -> {
				RoomDto roomDto = tempConverter.roomEntityToDto(room);
				returnValue.add(roomDto);
			});
		}
		return returnValue;
	}

	@Override
	public void deleteAllByCategoryId(Integer categoryId) {
		// TODO Auto-generated method stub
		roomRepository.deleteAllByCategoryId(categoryId);
		roomRepository.flush();
	}

}
