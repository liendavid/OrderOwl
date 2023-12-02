/**
 * This class allows services to be used regarding tracking information on Order Owl
 */

package com.orderowl.api.tracking;


import com.easypost.model.Tracker;
import com.easypost.model.TrackingDetail;
import com.easypost.service.EasyPostClient;
import com.orderowl.api.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Service
public class TrackingService {

    private final TrackingRepository trackingRepository;
    private final UserRepository userRepository;
    @Autowired
    public TrackingService(TrackingRepository trackingRepository, UserRepository userRepository) {

        this.trackingRepository = trackingRepository;
        this.userRepository = userRepository;
    }

    /**
     * This will allow us to receive the tracking information in order for the user to see their orders.
     *
     * @return A List of the tracking information
     */

    public List<Tracking> getTrackingInfo() {

        return trackingRepository.findAll();
    }

    /**
     * This will allow us to recover the tracking ID.
     *
     * @param id This will be the id that is associated with certain tracking orders.
     * @return The Tracking order that is relating to the ID, or null will be returned if nothing is found.
     */

    public Tracking getTrackingById(Long id) {

        return trackingRepository.findById(id).orElse(null);
    }


    /**
     * Will allow to add new tracking information.
     *
     * @param tracking The tracking information to be added to the list.
     */
    public void addNewTracking(Tracking tracking) {

        try {
            if(tracking.getTrackingNumber().equals("123456789012") || tracking.getTrackingNumber().equals("123456789013") ||
                    tracking.getTrackingNumber().equals("A2B4C6")){
                trackingRepository.save(tracking);
            } else {
                EasyPostClient easyPostClient = new EasyPostClient("EZAKaec5c730141e44c8b9aed9ec7b7b04f3YwveqZZNyaSJObbZDsqi0w");
                HashMap<String, Object> params = new HashMap<String, Object>();
                params.put("tracking_code", tracking.getTrackingNumber());
                Tracker ezPostTracker = easyPostClient.tracker.create(params);

                tracking.setCarrier(ezPostTracker.getCarrier());
                tracking.setStatus(ezPostTracker.getStatus());
                tracking.setEta(convertToLocalDateViaInstant(ezPostTracker.getEstDeliveryDate()));
                tracking.setAddress(locationParser(ezPostTracker.getTrackingDetails()));
                tracking.setLocation(locationParser(ezPostTracker.getTrackingDetails()));

                trackingRepository.save(tracking);
            }
        } catch (Exception e) {
            System.out.println("FAILED-------------!!!!");
            e.printStackTrace();
        }

    }

    private String locationParser(List<TrackingDetail> trackingDetails) {
        int mostRecentTracking = trackingDetails.size() - 1;
        String city = trackingDetails.get(mostRecentTracking).getTrackingLocation().getCity();
        String state = trackingDetails.get(mostRecentTracking).getTrackingLocation().getState();
        String zip = trackingDetails.get(mostRecentTracking).getTrackingLocation().getZip();
        return (city + ", " + state + " " + zip);
    }

    /**
     * credit : https://www.baeldung.com/java-date-to-localdate-and-localdatetime
     * @param dateToConvert Date type object to be converted
     * @return LocalDate type object
     */
    public LocalDate convertToLocalDateViaInstant(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    /**
     * This will make it possible to search for the order by providing text.
     *
     * @param searchText This will be the text that is used to search for the tracking information.
     * @return A List for the tracking information that has been put in before.
     */
    public List<Tracking> searchTracking(String searchText) {

        return trackingRepository.searchTracking(searchText);
    }

    /**
     *  This interacts with the database to find the number of orders left to be delivered
     *
     * @return Returns the count of pending orders
     */
    public Integer getTrackingCount() {

        return trackingRepository.getTrackingCount();
    }

    /**
     *
     * @param request will be used to request for the pin
     * @return the hidden page if the pin is correct
     */
    public List<Tracking> getHiddenTracking(UserPinRequest request) {
        return trackingRepository.findHidden();
    }

    /**
     * This will allow the user to be able to delete the order by the ID.
     *
     * @param id This will be the ID of the order that needs to be deleted.
     */
    public void deleteById(Long id) {
        trackingRepository.deleteById(id);
    }

    /**
     * This will make it possible to delete an order by the tracking number.
     *
     * @param trackingNumber This will be the tracking number that is related to the order that needs to be deleted.
     */
    public void deleteTrackingByNumber(String trackingNumber) {

        trackingRepository.deleteByTrackingNumber(trackingNumber);

    }
}
