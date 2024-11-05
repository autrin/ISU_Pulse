package com.coms309.isu_pulse_frontend.adapters;

import java.util.List;

public class AnnouncementListAdapter {
    private List<Object> annoucementList;
    private WeeklyCalendarAdapter weekCalendarAdapter;

    public AnnouncementListAdapter(List<Object> annoucementList, WeeklyCalendarAdapter weekCalendarAdapter) {
        this.annoucementList = annoucementList;
        this.weekCalendarAdapter = weekCalendarAdapter;
    }
}
