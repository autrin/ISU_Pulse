package com.coms309.isu_pulse_frontend.api;

import com.coms309.isu_pulse_frontend.model.Announcement;

import java.util.List;

public interface AnnouncementResponseListener {
    void onResponse(List<Announcement> announcements);

    void onError(String message);
}
