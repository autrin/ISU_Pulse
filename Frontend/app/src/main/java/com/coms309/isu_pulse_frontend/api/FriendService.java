package com.coms309.isu_pulse_frontend.api;

import static com.coms309.isu_pulse_frontend.api.Constants.BASE_URL;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;

public class FriendService {
    private final RequestQueue requestQueue;


    public FriendService(Context context) {
        this.requestQueue = Volley.newRequestQueue(context);
    }

    public interface IsFriendCallback {
        void onResult(boolean isFriend);
        void onError(String errorMessage);
    }


    // Get received friend requests
    public void getReceivedRequests(String netId, Response.Listener<JSONArray> listener, Response.ErrorListener errorListener) {
        String url = BASE_URL + "friendRequest/receivedRequest/" + netId;
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, listener, errorListener);
        requestQueue.add(request);
    }

    public void fetchFriendChats(String netId, Long groupId, Response.Listener<JSONArray> listener, Response.ErrorListener errorListener) {
        String url = BASE_URL + "friendShip/fetchFriendNotInAGivenGroup?netId=" + netId + "&groupId=" + groupId;
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, listener, errorListener);
        requestQueue.add(request);
    }



    // Get sent friend requests
    public void getSentRequests(String netId, Response.Listener<JSONArray> listener, Response.ErrorListener errorListener) {
        String url = BASE_URL + "friendRequest/sentRequest/" + netId;
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, listener, errorListener);
        requestQueue.add(request);
    }

    public void getFriendSuggestions(String netId, Response.Listener<JSONArray> listener, Response.ErrorListener errorListener) {
        String url = BASE_URL + "friendShip/friendSuggestion/" + netId;
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, listener, errorListener);
        requestQueue.add(request);
    }

    // Send a friend request
    public void sendFriendRequest(String senderNetId, String receiverNetId, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        String url = BASE_URL + "friendRequest/request?senderNetId=" + senderNetId + "&receiverNetId=" + receiverNetId;
        StringRequest request = new StringRequest(Request.Method.POST, url, listener, errorListener);
        requestQueue.add(request);
    }

    // Unfriend a friend
    public void unfriendFriend(String userNetId1, String userNetId2, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        String url = BASE_URL + "friendShip/unfriend?userNetId1=" + userNetId1 + "&userNetId2=" + userNetId2;
        StringRequest request = new StringRequest(Request.Method.DELETE, url, listener, errorListener);
        requestQueue.add(request);
    }

    // Accept a friend request
    public void acceptFriendRequest(String receiverNetId, String senderNetId, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        String url = BASE_URL + "friendRequest/accept?receiverNetId=" + receiverNetId + "&senderNetId=" + senderNetId;
        StringRequest request = new StringRequest(Request.Method.DELETE, url, listener, errorListener);
        requestQueue.add(request);
    }

    // Reject a friend request
    public void rejectFriendRequest(String receiverNetId, String senderNetId, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        String url = BASE_URL + "friendRequest/reject?receiverNetId=" + receiverNetId + "&senderNetId=" + senderNetId;
        StringRequest request = new StringRequest(Request.Method.DELETE, url, listener, errorListener);
        requestQueue.add(request);
    }

    // Unsend a friend request
    public void unsendFriendRequest(String senderNetId, String receiverNetId, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        String url = BASE_URL + "friendRequest/unsent?senderNetId=" + senderNetId + "&receiverNetId=" + receiverNetId;
        StringRequest request = new StringRequest(Request.Method.DELETE, url, listener, errorListener);
        requestQueue.add(request);
    }

    // Get friend list
    public void getFriendList(String netId, Response.Listener<JSONArray> listener, Response.ErrorListener errorListener) {
        String url = BASE_URL + "friendShip/friends/" + netId;
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, listener, errorListener);
        requestQueue.add(request);
    }

    // Get friends in common
    public void getFriendsInCommon(String netIdUser1, String netIdUser2, Response.Listener<JSONArray> listener, Response.ErrorListener errorListener) {
        String url = BASE_URL + "friendShip/sameFriends?netIdUser1=" + netIdUser1 + "&netIdUser2=" + netIdUser2;
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, listener, errorListener);
        requestQueue.add(request);
    }

    public void isFriend(String netIdUser1, String netIdUser2, Response.Listener<Boolean> listener, Response.ErrorListener errorListener) {
        String url = BASE_URL + "friendShip/isFriend?netIdUser1=" + netIdUser1 + "&netIdUser2=" + netIdUser2;

        // Use JsonObjectRequest to make a GET request and parse the Boolean response
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        // Assuming the response contains a simple Boolean (true or false)
                        Boolean isFriend = response.getBoolean("isFriend");
                        listener.onResponse(isFriend);
                    } catch (Exception e) {
                        errorListener.onErrorResponse(new VolleyError(e));
                    }
                },
                errorListener
        );

        requestQueue.add(request);
    }
}
