package com.midounoo.midounoo.Remote;

import com.midounoo.midounoo.Model.AppResponse;
import com.midounoo.midounoo.Model.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiService {
    @Headers(
            {
                    "Content-type:application/json",
                    "Authorization:key=AAAAthl_f2E:APA91bG76XbqOYSEQ--ZohtfaO-GhJUhYRFZrqtP_Z0qr9WFkGaqfRjwbsFaZGrVWmZmxlGtNzfXChpuyFNUjp3yLyi_cCm5nheWMV2rmWoKHoLvhHvd0AH9SYO5iUwQsS5A6EVnPgrqSDV_eIbgwmn9Z240D4F8og",
            }
    )
    @POST("fcm/send")
    Call<AppResponse> sendNotification(@Body Sender body);
}
