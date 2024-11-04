package coms309.backEnd.demo.websocket;

public class AnnouncementMessage {
    private long scheduleId;
    private String content;

    // Getters and Setters
    public long getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(long scheduleId) {
        this.scheduleId = scheduleId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}