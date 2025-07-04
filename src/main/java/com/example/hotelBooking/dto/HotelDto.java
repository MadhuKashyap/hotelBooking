package com.example.hotelBooking.dto;

import com.example.hotelBooking.dao.HotelDao;
import com.example.hotelBooking.dao.RoomDao;
import com.example.hotelBooking.form.HotelFilterForm;
import com.example.hotelBooking.model.data.HotelData;
import com.example.hotelBooking.model.data.RoomData;
import com.example.hotelBooking.pojo.HotelPojo;
import com.example.hotelBooking.pojo.RoomPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HotelDto {
    @Autowired
    private HotelDao hotelDao;
    @Autowired
    private DtoHelper dtoHelper;
    @Autowired
    private RoomDao roomDao;
    public List<HotelData> fetchHotels(HotelFilterForm filterForm) {
        List<HotelPojo> hotels = hotelDao.findAll();
        List<HotelPojo> hotelByDateRange = new ArrayList<>();
        List<HotelPojo> hotelByPrice = new ArrayList<>();
        List<HotelPojo> hotelByRating = new ArrayList<>();
        List<HotelData> hotelDataList = new ArrayList<>();

        if(!ObjectUtils.isEmpty(filterForm.getStartDate()) && !ObjectUtils.isEmpty(filterForm.getEndDate())) {
            hotelByDateRange = DtoHelper.filteHotelByDateRange(hotels, filterForm);
        } if(!CollectionUtils.isEmpty(hotelByDateRange)) {
            hotelByPrice = dtoHelper.filterHotelByPrice(hotelByDateRange, filterForm);
        } if(!CollectionUtils.isEmpty(hotelByPrice)) {
            hotelByRating = DtoHelper.filterHotelByRating(hotelByPrice, filterForm);
        }
        for(HotelPojo hotelPojo : hotelByRating) {
            hotelDataList.add(dtoHelper.convertHotelPojoToData(hotelPojo));
        }
        return hotelDataList;
    }

    public List<RoomData> fetchRoomsByHotelId(Long hotelId) {
        List<RoomData> roomDataList = new ArrayList<>();
        List<RoomPojo> roomPojoList =  roomDao.findByHotelId(hotelId)
                .stream().filter( x -> x.getBookedDates().size() != 365)
                .collect(Collectors.toList()));
        for(RoomPojo roomPojo : roomPojoList) {
            roomDataList.add(dtoHelper.convertRoomPojoToData(roomPojo));
        }
        return roomDataList;
    }
}
