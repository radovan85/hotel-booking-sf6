package com.radovan.spring.converter;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.radovan.spring.dto.GuestDto;
import com.radovan.spring.dto.NoteDto;
import com.radovan.spring.dto.ReservationDto;
import com.radovan.spring.dto.RoleDto;
import com.radovan.spring.dto.RoomCategoryDto;
import com.radovan.spring.dto.RoomDto;
import com.radovan.spring.dto.UserDto;
import com.radovan.spring.entity.GuestEntity;
import com.radovan.spring.entity.NoteEntity;
import com.radovan.spring.entity.ReservationEntity;
import com.radovan.spring.entity.RoleEntity;
import com.radovan.spring.entity.RoomCategoryEntity;
import com.radovan.spring.entity.RoomEntity;
import com.radovan.spring.entity.UserEntity;
import com.radovan.spring.repository.GuestRepository;
import com.radovan.spring.repository.RoleRepository;
import com.radovan.spring.repository.RoomCategoryRepository;
import com.radovan.spring.repository.RoomRepository;
import com.radovan.spring.repository.UserRepository;

@Component
public class TempConverter {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private RoomCategoryRepository roomCategoryRepository;

	@Autowired
	private RoomRepository roomRepository;

	@Autowired
	private GuestRepository guestRepository;

	@Autowired
	private ModelMapper mapper;

	private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
	
	private DecimalFormat decfor = new DecimalFormat("0.00");

	public GuestDto guestEntityToDto(GuestEntity guestEntity) {
		GuestDto returnValue = mapper.map(guestEntity, GuestDto.class);
		Optional<UserEntity> userOpt = Optional.ofNullable(guestEntity.getUser());
		if (userOpt.isPresent()) {
			returnValue.setUserId(userOpt.get().getId());
		}

		return returnValue;
	}

	public GuestEntity guestDtoToEntity(GuestDto guest) {
		GuestEntity returnValue = mapper.map(guest, GuestEntity.class);
		Optional<Integer> userIdOpt = Optional.ofNullable(guest.getUserId());
		if (userIdOpt.isPresent()) {
			Integer userId = userIdOpt.get();
			UserEntity userEntity = userRepository.findById(userId).get();
			returnValue.setUser(userEntity);
		}

		return returnValue;
	}

	public RoomDto roomEntityToDto(RoomEntity roomEntity) {
		RoomDto returnValue = mapper.map(roomEntity, RoomDto.class);
		Optional<RoomCategoryEntity> categoryOpt = Optional.ofNullable(roomEntity.getRoomCategory());
		if (categoryOpt.isPresent()) {
			returnValue.setRoomCategoryId(categoryOpt.get().getRoomCategoryId());
		}
		return returnValue;
	}

	public RoomEntity roomDtoToEntity(RoomDto room) {
		RoomEntity returnValue = mapper.map(room, RoomEntity.class);
		Optional<Integer> categoryIdOpt = Optional.ofNullable(room.getRoomCategoryId());
		if (categoryIdOpt.isPresent()) {
			Integer categoryId = categoryIdOpt.get();
			RoomCategoryEntity categoryEntity = roomCategoryRepository.findById(categoryId).get();
			returnValue.setRoomCategory(categoryEntity);
		}
		return returnValue;
	}

	public RoomCategoryDto roomCategoryEntityToDto(RoomCategoryEntity categoryEntity) {
		RoomCategoryDto returnValue = mapper.map(categoryEntity, RoomCategoryDto.class);
		Float price = Float.valueOf(decfor.format(returnValue.getPrice()));
		returnValue.setPrice(price);
		return returnValue;
	}

	public RoomCategoryEntity roomCategoryDtoToEntity(RoomCategoryDto category) {
		RoomCategoryEntity returnValue = mapper.map(category, RoomCategoryEntity.class);
		Float price = Float.valueOf(decfor.format(returnValue.getPrice()));
		returnValue.setPrice(price);
		return returnValue;
	}

	public ReservationDto reservationEntityToDto(ReservationEntity reservation) {
		ReservationDto returnValue = mapper.map(reservation, ReservationDto.class);

		Optional<RoomEntity> roomOpt = Optional.ofNullable(reservation.getRoom());
		if (roomOpt.isPresent()) {
			returnValue.setRoomId(roomOpt.get().getRoomId());
		}

		Optional<GuestEntity> guestOpt = Optional.ofNullable(reservation.getGuest());
		if (guestOpt.isPresent()) {
			returnValue.setGuestId(guestOpt.get().getGuestId());
		}

		Optional<Timestamp> checkInDateOpt = Optional.ofNullable(reservation.getCheckInDate());
		if (checkInDateOpt.isPresent()) {
			String checkInDateStr = checkInDateOpt.get().toLocalDateTime().format(formatter);
			returnValue.setCheckInDateStr(checkInDateStr);
		}

		Optional<Timestamp> checkOutDateOpt = Optional.ofNullable(reservation.getCheckOutDate());
		if (checkOutDateOpt.isPresent()) {
			String checkOutDateStr = checkOutDateOpt.get().toLocalDateTime().format(formatter);
			returnValue.setCheckOutDateStr(checkOutDateStr);
		}

		return returnValue;
	}

	public ReservationEntity reservationDtoToEntity(ReservationDto reservation) {
		ReservationEntity returnValue = mapper.map(reservation, ReservationEntity.class);

		Optional<Integer> roomIdOpt = Optional.ofNullable(reservation.getRoomId());
		if (roomIdOpt.isPresent()) {
			Integer roomId = roomIdOpt.get();
			RoomEntity roomEntity = roomRepository.findById(roomId).get();
			returnValue.setRoom(roomEntity);
		}

		Optional<Integer> guestIdOpt = Optional.ofNullable(reservation.getGuestId());
		if (guestIdOpt.isPresent()) {
			Integer guestId = guestIdOpt.get();
			GuestEntity guestEntity = guestRepository.findById(guestId).get();
			returnValue.setGuest(guestEntity);
		}

		Optional<String> checkInDateStrOpt = Optional.ofNullable(reservation.getCheckInDateStr());
		if (checkInDateStrOpt.isPresent()) {
			String checkInDateStr = checkInDateStrOpt.get();

			LocalDateTime checkInDate = LocalDateTime.parse(checkInDateStr, formatter);
			returnValue.setCheckInDate(Timestamp.valueOf(checkInDate));
		}

		Optional<String> checkOutDateStrOpt = Optional.ofNullable(reservation.getCheckOutDateStr());
		if (checkOutDateStrOpt.isPresent()) {
			String checkOutDateStr = checkOutDateStrOpt.get();
			LocalDateTime checkOutDate = LocalDateTime.parse(checkOutDateStr, formatter);
			returnValue.setCheckOutDate(Timestamp.valueOf(checkOutDate));
		}

		return returnValue;
	}

	public NoteDto noteEntityToDto(NoteEntity noteEntity) {
		NoteDto returnValue = mapper.map(noteEntity, NoteDto.class);
		Optional<Timestamp> createdAtOpt = Optional.ofNullable(noteEntity.getCreatedAt());
		if (createdAtOpt.isPresent()) {
			String createdAtStr = String.valueOf(createdAtOpt.get().toLocalDateTime().format(formatter));
			returnValue.setCreatedAtStr(createdAtStr);
		}

		return returnValue;
	}

	public NoteEntity noteDtoToEntity(NoteDto noteDto) {
		NoteEntity returnValue = mapper.map(noteDto, NoteEntity.class);
		return returnValue;
	}

	public UserDto userEntityToDto(UserEntity userEntity) {
		UserDto returnValue = mapper.map(userEntity, UserDto.class);
		returnValue.setEnabled(userEntity.getEnabled());
		Optional<List<RoleEntity>> rolesOpt = Optional.ofNullable(userEntity.getRoles());
		List<Integer> rolesIds = new ArrayList<Integer>();

		if (!rolesOpt.isEmpty()) {
			rolesOpt.get().forEach((roleEntity) -> {
				rolesIds.add(roleEntity.getId());
			});
		}

		returnValue.setRolesIds(rolesIds);

		return returnValue;
	}

	public UserEntity userDtoToEntity(UserDto userDto) {
		UserEntity returnValue = mapper.map(userDto, UserEntity.class);
		List<RoleEntity> roles = new ArrayList<>();
		Optional<List<Integer>> rolesIdsOpt = Optional.ofNullable(userDto.getRolesIds());

		if (!rolesIdsOpt.isEmpty()) {
			rolesIdsOpt.get().forEach((roleId) -> {
				RoleEntity role = roleRepository.findById(roleId).get();
				roles.add(role);
			});
		}

		returnValue.setRoles(roles);

		return returnValue;
	}

	public RoleDto roleEntityToDto(RoleEntity roleEntity) {
		RoleDto returnValue = mapper.map(roleEntity, RoleDto.class);
		List<UserEntity> users = roleEntity.getUsers();
		List<Integer> usersIds = new ArrayList<>();

		users.forEach((user) -> {
			usersIds.add(user.getId());
		});

		returnValue.setUsersIds(usersIds);
		return returnValue;
	}

	public RoleEntity roleDtoToEntity(RoleDto roleDto) {
		RoleEntity returnValue = mapper.map(roleDto, RoleEntity.class);
		List<Integer> usersIds = roleDto.getUsersIds();
		List<UserEntity> users = new ArrayList<>();

		usersIds.forEach((userId) -> {
			UserEntity userEntity = userRepository.findById(userId).get();
			users.add(userEntity);
		});

		returnValue.setUsers(users);
		return returnValue;
	}
}
