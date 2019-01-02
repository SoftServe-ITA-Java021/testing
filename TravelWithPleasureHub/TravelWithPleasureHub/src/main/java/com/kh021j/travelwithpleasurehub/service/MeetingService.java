package com.kh021j.travelwithpleasurehub.service;

import com.kh021j.travelwithpleasurehub.model.Meeting;
import com.kh021j.travelwithpleasurehub.model.User;
import com.kh021j.travelwithpleasurehub.model.enumiration.MeetingType;
import com.kh021j.travelwithpleasurehub.repository.MeetingRepository;
import com.kh021j.travelwithpleasurehub.repository.UserRepository;
import com.kh021j.travelwithpleasurehub.service.dto.MeetingDTO;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MeetingService {

    private final MeetingRepository meetingRepository;

    private final UserRepository userRepository;

    private final Logger log = LoggerFactory.getLogger(MeetingService.class);

    private Meeting fromDTO(MeetingDTO meetingDTO) {
        if (meetingDTO == null) {
            return null;
        }
        return Meeting.builder()
                .id(meetingDTO.getId())
                .content(meetingDTO.getContent())
                .header(meetingDTO.getContent())
                .location(meetingDTO.getLocation())
                .links(meetingDTO.getLinks())
                .meetingType(meetingDTO.getMeetingType())
                .timeOfAction(meetingDTO.getTimeOfAction())
                .owner(userRepository.findById(meetingDTO.getOwnerId()).get())
                .confirmedUsers(Objects.nonNull(meetingDTO.getConfirmedUserIds()) ?
                        getListOfUsersById(meetingDTO.getConfirmedUserIds())
                        : null)
                .wishingUsers(Objects.nonNull(meetingDTO.getWishingUserIds()) ?
                        getListOfUsersById(meetingDTO.getWishingUserIds())
                        : null)
                .build();
    }

    private MeetingDTO toDTO(Meeting meeting) {
        if (meeting == null) {
            return null;
        }
        return MeetingDTO.builder()
                .id(meeting.getId())
                .content(meeting.getContent())
                .header(meeting.getContent())
                .links(meeting.getLinks())
                .location(meeting.getLocation())
                .meetingType(meeting.getMeetingType())
                .timeOfAction(meeting.getTimeOfAction())
                .ownerId(Objects.nonNull(meeting.getOwner()) ? meeting.getId() : null)
                .confirmedUserIds(Objects.nonNull(meeting.getConfirmedUsers()) ?
                        meeting.getConfirmedUsers().stream()
                                .filter(Objects::nonNull)
                                .map(User::getId)
                                .collect(Collectors.toList())
                        : null)
                .wishingUserIds(Objects.nonNull(meeting.getWishingUsers()) ?
                        meeting.getWishingUsers().stream()
                                .filter(Objects::nonNull)
                                .map(User::getId)
                                .collect(Collectors.toList())
                        : null)
                .build();
    }

    @Transactional
    public MeetingDTO save(MeetingDTO meetingDTO) throws IOException {
        meetingDTO = meetingDTO.toBuilder()
                .links(findLinksForMeeting(meetingDTO))
                .build();
        log.debug("Request to save Meeting : {}", meetingDTO);
        if (!meetingRepository.existsById(meetingDTO.getId())) {
            Meeting meeting = fromDTO(meetingDTO);
            return toDTO(meetingRepository.saveAndFlush(meeting));
        }
        log.debug("Request to save Meeting was failed : {}", meetingDTO);
        return null;
    }

    @Transactional
    public MeetingDTO update(MeetingDTO meetingDTO) {
        log.debug("Request to update Meeting : {}", meetingDTO);
        if (meetingRepository.existsById(meetingDTO.getId())) {
            Meeting meeting = fromDTO(meetingDTO);
            return toDTO(meetingRepository.saveAndFlush(meeting));
        }
        log.debug("Request to update Meeting was failed : {}", meetingDTO);
        return null;
    }

    @Transactional
    public MeetingDTO sendRequestForMeeting(Integer meetingId, Integer userId) {
        log.debug("Request to send request for Meeting with id : {} , and user id : {}", meetingId, userId);
        if (!meetingRepository.existsById(meetingId) || !userRepository.existsById(userId)) {
            log.error("Request to send request for Meeting with id : {} , and user id : {} was failed", meetingId, userId);
            return null;
        }
        User user = userRepository.findById(userId).get();
        Meeting meeting = meetingRepository.findById(meetingId).get();
        meeting = meeting.toBuilder()
                .wishingUsers(addUserInList(user, meeting))
                .build();
        return toDTO(meetingRepository.saveAndFlush(meeting));
    }

    @Transactional
    public MeetingDTO confirmUserForMeeting(Integer ownerId, Integer meetingId, Integer wishingUserId) {
        log.debug("Request to confirm for Meeting with id : {} ,owner id : {} , and wishing user id : {}", meetingId, ownerId, wishingUserId);
        if (!meetingRepository.existsById(meetingId) || !userRepository.existsById(wishingUserId)
                || !userRepository.existsById(ownerId)) {
            log.error("Request to confirm for Meeting with id : {} ,owner id : {} , and wishing user id : {} was failed", meetingId, ownerId, wishingUserId);
            return null;
        }
        User owner = userRepository.findById(ownerId).get();
        Meeting meeting = meetingRepository.findById(meetingId).get();
        if (owner.getId().equals(meeting.getId())) {
            User confirmedUser = userRepository.findById(wishingUserId).get();
            meeting = meeting.toBuilder()
                    .confirmedUsers(addUserInList(confirmedUser, meeting))
                    .wishingUsers(removeUserFromList(confirmedUser, meeting))
                    .build();
            return toDTO(meetingRepository.saveAndFlush(meeting));
        }
        log.error("Request to confirm for Meeting with id : {} ,owner id : {} , and wishing user id : {} was failed", meetingId, ownerId, wishingUserId);
        return null;
    }

    public List<MeetingDTO> findHistoryOfMeetingsByUserId(Integer id) {
        log.debug("Request to get history Meetings by user id : {} ", id);
        User user = userRepository.findById(id).get();
        return meetingRepository.findAllByConfirmedUsersContainsAndTimeOfActionIsBefore(user, LocalDateTime.now())
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public boolean deleteById(Integer id) {
        log.debug("Request to remove meeting with id : {} ", id);
        if (meetingRepository.existsById(id)) {
            meetingRepository.deleteById(id);
            log.debug("Meeting was removed");
            return true;
        }
        log.debug("Meeting wasn't removed");
        return false;
    }

    public Optional<MeetingDTO> findById(Integer id) {
        log.debug("Request to get Meeting by id : {}", id);
        return Optional.ofNullable(toDTO(meetingRepository.findById(id).get()));
    }

    public List<MeetingDTO> findAll() {
        log.debug("Request to get all Meetings ");
        return meetingRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<MeetingDTO> findAllByHeaderFilter(String header) {
        log.debug("Request to get all Meetings by header filter : {} ", header);
        return meetingRepository.findAllByHeaderContaining(header).stream()
                .map(this::toDTO).collect(Collectors.toList());
    }

    public List<MeetingDTO> findAllByDateAfter(LocalDateTime time) {
        log.debug("Request to get all Meetings by time filter after : {} ", time);
        return meetingRepository.findAllByTimeOfActionAfter(time).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private List<User> getListOfUsersById(List<Integer> ids) {
        List<User> users = new ArrayList<>();
        for (Integer id : ids) {
            users.add(userRepository.findById(id).get());
        }
        return users;
    }

    private List<User> addUserInList(User user, Meeting meeting) {
        List<User> users = new ArrayList<>(meeting.getConfirmedUsers());
        users.add(user);
        return users;
    }

    private List<User> removeUserFromList(User user, Meeting meeting) {
        List<User> users = new ArrayList<>(meeting.getConfirmedUsers());
        users.remove(user);
        return users;
    }

    private List<String> findLinksForMeeting(MeetingDTO meetingDTO) throws IOException {
        if (meetingDTO.getMeetingType().equals(MeetingType.WALKING)) {
            return null;
        }
        List<String> links = new ArrayList<>();
        String[] countryAndCity = meetingDTO.getLocation().split(",");
        String reference = "https://search.yahoo.com/search?p=" + countryAndCity[0].trim() + "+" + countryAndCity[1].trim()
                + "+buy+" + meetingDTO.getMeetingType() + "+tickets&fr=yfp-t&fp=1&toggle=1&cop=mss&ei=UTF-8";
        Document document = Jsoup.connect(reference).timeout(10000).get();
        Elements elements = document.select("h3").select(".title").select("a");
        for (int i = 0; i < 4; i++) {
            links.add(elements.get(i).attr("href"));
        }
        return links;
    }

}