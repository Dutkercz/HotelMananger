package ItaipuHotelManager.manager.services;

import ItaipuHotelManager.manager.entities.Hosting;
import ItaipuHotelManager.manager.entities.HotelClient;
import ItaipuHotelManager.manager.entities.HotelRoom;
import ItaipuHotelManager.manager.entities.utils.RoomStatus;
import ItaipuHotelManager.manager.repositories.HostingRepository;
import ItaipuHotelManager.manager.repositories.HotelRoomRepository;
import jakarta.transaction.Transactional;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class HostingService {
    @Autowired
    private HostingRepository hostingRepository;
    @Autowired
    private HotelRoomRepository roomRepository;
    @Autowired
    private HotelClientService clientService;
    @Autowired
    HotelRoomService roomService;

    public Double hostingTotalPriceDebit(Hosting hosting){
        return hosting.getBasePrice()*1.03;
    }

    public Double hostingTotalPriceCredit(Hosting hosting){
        return hosting.getBasePrice()*1.05;
    }

    public Double hostingTotalPrice(Hosting hosting){
        return hosting.getBasePrice();
    }

    @Transactional
    public void checkOut(Hosting hosting) {
        HotelRoom room = hosting.getRoom();
        room.setStatus(RoomStatus.DISPONIVEL);
        room.setClient(null);
        roomService.saveRoom(room);

        hosting.setStatus(RoomStatus.FINALIZADO);
        hosting.setCheckOut(LocalDateTime.now());
        hostingRepository.save(hosting);
    }

    @Transactional
    public List<Hosting> findAllHostings (String cpf){
        HotelClient client = clientService.findByCpf(cpf);
        if (client == null){
            System.out.println("Cliente não encontrado.");
        }
        return hostingRepository.findByClient(client);
    }

    public List<Hosting> findAll() {
        return hostingRepository.findAll();
    }

    @Transactional
    public void checkIn(Hosting hosting, HotelClient selectedClient, HotelRoom selectedRoom) {
        hostingRepository.save(hosting);
        roomService.updateRoomStatusOccupied(selectedRoom.getRoomNumber(), selectedClient);
    }

    public List<Hosting> findActiveHosting() {
        return hostingRepository.findByRoomStatus(RoomStatus.OCUPADO);
    }

    public Hosting findByRoomNumber(String roomNumber){
        return hostingRepository.findByRoomRoomNumber(roomNumber);
    }
}
