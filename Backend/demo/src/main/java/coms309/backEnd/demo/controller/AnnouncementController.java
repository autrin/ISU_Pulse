package coms309.backEnd.demo.controller;

import coms309.backEnd.demo.entity.Announcement;
import coms309.backEnd.demo.repository.AnnouncementRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/announcements")
public class AnnouncementController {
    @Autowired
    private AnnouncementRepository announcementRepository;

    public AnnouncementController(AnnouncementRepository announcementRepository) {
        this.announcementRepository = announcementRepository;
    }

    /**
     * Retrieve announcements for a specific schedule.
     *
     * @param scheduleId The ID of the schedule to fetch announcements for.
     * @return A list of announcements ordered by timestamp in descending order.
     */
    @Operation(summary = "Fetch announcements for a schedule",
            description = "Retrieve all announcements associated with a specific schedule, ordered by their timestamp in descending order.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Announcements retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Announcement.class))),
            @ApiResponse(responseCode = "404",
                    description = "Schedule not found",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/schedule/{scheduleId}")
    public ResponseEntity<List<Announcement>> getAnnouncements(@Parameter(description = "ID of the schedule to retrieve announcements for", required = true) @PathVariable long scheduleId) {
        List<Announcement> announcements = announcementRepository.findByScheduleIdOrderByTimestampDesc(scheduleId);
        return ResponseEntity.ok(announcements);
    }
}
