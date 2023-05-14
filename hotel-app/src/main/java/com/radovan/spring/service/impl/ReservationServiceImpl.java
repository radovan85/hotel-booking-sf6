package com.radovan.spring.service.impl;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.radovan.spring.converter.TempConverter;
import com.radovan.spring.dto.ReservationDto;
import com.radovan.spring.entity.NoteEntity;
import com.radovan.spring.entity.ReservationEntity;
import com.radovan.spring.entity.RoleEntity;
import com.radovan.spring.entity.RoomEntity;
import com.radovan.spring.entity.UserEntity;
import com.radovan.spring.repository.NoteRepository;
import com.radovan.spring.repository.ReservationRepository;
import com.radovan.spring.repository.RoomRepository;
import com.radovan.spring.repository.UserRepository;
import com.radovan.spring.service.ReservationService;

@Service
@Transactional
public class ReservationServiceImpl implements ReservationService {

	@Autowired
	private ReservationRepository reservationRepository;

	@Autowired
	private TempConverter tempConverter;

	@Autowired
	private RoomRepository roomRepository;

	@Autowired
	private NoteRepository noteRepository;

	@Autowired
	private UserRepository userRepository;

	@Override
	public ReservationDto addReservation(ReservationDto reservation, Integer roomId) {
		// TODO Auto-generated method stub
		reservation.setRoomId(roomId);

		Optional<RoomEntity> roomOpt = roomRepository.findById(roomId);
		if (roomOpt.isPresent()) {
			Float reservationPrice = reservation.getNumberOfNights() * roomOpt.get().getPrice();
			reservation.setPrice(reservationPrice);
		}

		ReservationEntity reservationEntity = tempConverter.reservationDtoToEntity(reservation);
		ReservationEntity storedReservation = reservationRepository.save(reservationEntity);
		ReservationDto returnValue = tempConverter.reservationEntityToDto(storedReservation);

		NoteEntity noteEntity = new NoteEntity();
		String text = "";
		noteEntity.setSubject("Reservation Created");

		UserEntity authUser = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		UserEntity userEntity = userRepository.findById(authUser.getId()).get();

		text = "User " + userEntity.getFirstName() + " " + userEntity.getLastName() + " reserved the room "
				+ storedReservation.getRoom().getRoomNumber() + ".Check-in is scheduled for "
				+ returnValue.getCheckInDateStr();

		noteEntity.setText(text);
		noteRepository.save(noteEntity);
		return returnValue;
	}

	@Override
	public ReservationDto getReservationById(Integer reservationId) {
		// TODO Auto-generated method stub
		ReservationDto returnValue = null;
		Optional<ReservationEntity> reservationOpt = reservationRepository.findById(reservationId);
		if (reservationOpt.isPresent()) {
			returnValue = tempConverter.reservationEntityToDto(reservationOpt.get());
		}
		return returnValue;
	}

	@Override
	public void deleteReservation(Integer reservationId) {
		// TODO Auto-generated method stub
		ReservationEntity reservation = reservationRepository.findById(reservationId).get();
		ReservationDto reservationDto = tempConverter.reservationEntityToDto(reservation);
		RoomEntity roomEntity = roomRepository.findById(reservation.getRoom().getRoomId()).get();
		NoteEntity noteEntity = new NoteEntity();
		noteEntity.setSubject("Reservation Canceled");

		UserEntity authUser = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		UserEntity userEntity = userRepository.findById(authUser.getId()).get();

		/*
		 * List<RoleEntity> userRoles = userEntity.getRoles(); for(RoleEntity
		 * role:userRoles) { if(role.getRole().contains("ROLE_USER")) { String text =
		 * "";
		 * 
		 * text = "Reservation for user " + userEntity.getFirstName() + " " +
		 * userEntity.getLastName() + " for room No " + roomEntity.getRoomNumber() +
		 * " scheduled for " + reservationDto.getCheckInDateStr() +
		 * " has been cancelled by user";
		 * 
		 * noteEntity.setText(text); noteRepository.saveAndFlush(noteEntity); } }
		 */

		List<RoleEntity> userRoles = userEntity.getRoles();
		userRoles.forEach((role) -> {
			if (role.getRole().contains("ROLE_USER")) {
				String text = "";

				text = "Reservation for user " + userEntity.getFirstName() + " " + userEntity.getLastName()
						+ " for room No " + roomEntity.getRoomNumber() + " scheduled for "
						+ reservationDto.getCheckInDateStr() + " has been cancelled by user";

				noteEntity.setText(text);
				noteRepository.saveAndFlush(noteEntity);
			}
		});

		reservationRepository.deleteById(reservationId);
		reservationRepository.flush();
	}

	@Override
	public List<ReservationDto> listAll() {
		// TODO Auto-generated method stub
		List<ReservationDto> returnValue = new ArrayList<ReservationDto>();
		Optional<List<ReservationEntity>> allReservationsOpt = Optional.ofNullable(reservationRepository.findAll());
		if (!allReservationsOpt.isEmpty()) {
			allReservationsOpt.get().forEach((reservation) -> {
				ReservationDto reservationDto = tempConverter.reservationEntityToDto(reservation);
				returnValue.add(reservationDto);
			});
		}
		return returnValue;
	}

	@Override
	public List<ReservationDto> listAllByGuestId(Integer guestId) {
		List<ReservationDto> returnValue = new ArrayList<ReservationDto>();
		Optional<List<ReservationEntity>> allReservationsOpt = Optional
				.ofNullable(reservationRepository.findAllByGuestId(guestId));
		if (!allReservationsOpt.isEmpty()) {
			allReservationsOpt.get().forEach((reservation) -> {
				ReservationDto reservationDto = tempConverter.reservationEntityToDto(reservation);
				returnValue.add(reservationDto);
			});
		}
		return returnValue;
	}

	@Override
	public List<ReservationDto> listAllByRoomId(Integer roomId) {
		// TODO Auto-generated method stub
		List<ReservationDto> returnValue = new ArrayList<ReservationDto>();
		Optional<List<ReservationEntity>> allReservationsOpt = Optional
				.ofNullable(reservationRepository.findAllByRoomId(roomId));
		if (!allReservationsOpt.isEmpty()) {
			allReservationsOpt.get().forEach((reservation) -> {
				ReservationDto reservationDto = tempConverter.reservationEntityToDto(reservation);
				returnValue.add(reservationDto);
			});
		}
		return returnValue;
	}

	@Override
	public Boolean isAvailable(Integer roomId, Timestamp checkInDate, Timestamp checkOutDate) {
		// TODO Auto-generated method stub
		Boolean returnValue = false;
		List<ReservationEntity> allReservationsOpt = reservationRepository.checkForReservations(roomId, checkInDate,
				checkOutDate);
		if (!allReservationsOpt.isEmpty()) {
			returnValue = false;
		} else {
			returnValue = true;
		}

		return returnValue;
	}

	@Override
	public ReservationDto updateReservation(ReservationDto reservation, Integer reservationId) {
		// TODO Auto-generated method stub
		ReservationEntity currentReservation = reservationRepository.findById(reservationId).get();
		ReservationEntity reservationEntity = tempConverter.reservationDtoToEntity(reservation);
		reservationEntity.setReservationId(reservationId);
		reservationEntity.setCreatedAt(currentReservation.getCreatedAt());
		ReservationEntity storedReservation = reservationRepository.saveAndFlush(reservationEntity);
		ReservationDto returnValue = tempConverter.reservationEntityToDto(storedReservation);

		NoteEntity noteEntity = new NoteEntity();
		noteEntity.setSubject("Reservation Updated");
		String text = "Reservation " + returnValue.getReservationId() + " scheduled for "
				+ returnValue.getCheckInDateStr() + " has been switched to room "
				+ storedReservation.getRoom().getRoomNumber();
		noteEntity.setText(text);
		noteRepository.saveAndFlush(noteEntity);
		return returnValue;
	}

	@Override
	public List<ReservationDto> listAllActive() {
		// TODO Auto-generated method stub
		List<ReservationDto> returnValue = new ArrayList<ReservationDto>();
		Optional<List<ReservationEntity>> allReservationsOpt = Optional.ofNullable(reservationRepository.findAll());
		LocalDateTime currentDate = LocalDateTime.now();
		if (!allReservationsOpt.isEmpty()) {
			allReservationsOpt.get().forEach((reservation) -> {
				if (reservation.getCheckOutDate().toLocalDateTime().isAfter(currentDate)) {
					ReservationDto reservationDto = tempConverter.reservationEntityToDto(reservation);
					returnValue.add(reservationDto);
				}
			});
		}
		return returnValue;
	}

	@Override
	public List<ReservationDto> listAllExpired() {
		// TODO Auto-generated method stub
		List<ReservationDto> returnValue = new ArrayList<ReservationDto>();
		Optional<List<ReservationEntity>> allReservationsOpt = Optional.ofNullable(reservationRepository.findAll());
		LocalDateTime currentDate = LocalDateTime.now();
		if (!allReservationsOpt.isEmpty()) {
			allReservationsOpt.get().forEach((reservation) -> {
				if (reservation.getCheckOutDate().toLocalDateTime().isBefore(currentDate)) {
					ReservationDto reservationDto = tempConverter.reservationEntityToDto(reservation);
					returnValue.add(reservationDto);
				}
			});
		}
		return returnValue;
	}

	@Override
	public void deleteAllByRoomId(Integer roomId) {
		// TODO Auto-generated method stub
		reservationRepository.deleteAllByRoomId(roomId);
		reservationRepository.flush();
	}

}
