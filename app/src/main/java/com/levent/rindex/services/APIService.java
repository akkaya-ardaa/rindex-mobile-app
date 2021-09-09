package com.levent.rindex.services;

import com.levent.rindex.ccs.SavedSettings;
import com.levent.rindex.models.Answer;
import com.levent.rindex.models.Comment;
import com.levent.rindex.models.ListResponse;
import com.levent.rindex.models.NearbyPlace;
import com.levent.rindex.models.Place;
import com.levent.rindex.models.Post;
import com.levent.rindex.models.Question;
import com.levent.rindex.models.Response;
import com.levent.rindex.models.SingleResponse;
import com.levent.rindex.models.Token;
import com.levent.rindex.models.User;
import com.levent.rindex.models.ViewCount;
import com.levent.rindex.models.additionalInfo.RestaurantAdditionalInfo;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface APIService {
    @GET("api/account/login")
    Call<SingleResponse<Token>> login(@Query("mail") String mail, @Query("password") String password, @Query("notificationToken") String notificationToken);

    @GET("api/places/getpd")
    Call<ListResponse<Place>> getPlacesByProvinceAndDistrict(@Query("province") String province,@Query("district") String district);

    @GET("api/places/getp")
    Call<ListResponse<Place>> getPlacesByProvince(@Query("province") String province);

    @GET("api/places/getindex")
    Call<ListResponse<Place>> getPlacesByIndex(@Query("startIndex") int startIndex,@Query("endIndex") int endIndex);

    @GET("api/places/search")
    Call<ListResponse<Place>> searchPlaces(@Query("q") String q);

    @GET("api/places/getviewcount")
    Call<SingleResponse<ViewCount>> getPlaceViewCount(@Query("placeId") int placeId);

    @GET("api/places/addviewcount")
    Call<Response> increasePlaceViewCount(@Query("placeId") int placeId);

    @GET("api/users/getpublic")
    Call<SingleResponse<User>> getPublicUserInfo(@Query("userId") int userId);

    @GET("api/places/getcomments")
    Call<ListResponse<Comment>> getComments(@Query("placeId") int placeId,@Header("Authorization") String apiKey);

    @GET("api/places/getmycomment")
    Call<SingleResponse<Comment>> getMyComment(@Query("placeId") int placeId,@Header("Authorization") String apiKey);

    @POST("api/places/uploadcomment")
    Call<Response> uploadComment(@Query("placeId") int placeId, @Body Comment comment, @Header("Authorization") String apiKey);

    @GET("api/account/getallcomments")
    Call<ListResponse<Comment>> getAllComments(@Query("userId") int userId);

    @GET("api/places/getbyid")
    Call<SingleResponse<Place>> getPlaceById(@Query("placeId") int placeId);

    @GET("api/posts/getbyplaceid")
    Call<ListResponse<Post>> getPostsByPlaceId(@Query("placeId") int placeId,@Query("startIndex") int startIndex, @Query("endIndex") int endIndex);

    @POST("api/posts/add")
    Call<Response> addPost(@Body Post post,@Header("Authorization") String apiKey);

    @GET("api/posts/delete")
    Call<Response> deletePost(@Query("postId") int postId,@Header("Authorization") String apiKey);

    @GET("api/posts/getbyuserid")
    Call<ListResponse<Post>> getAllPosts(@Query("id") int userId);

    @GET("api/account/updatenamesurname")
    Call<Response> updateNameSurname(@Query("name") String name,@Query("surname") String surname, @Header("Authorization") String apiKey);

    @GET("api/account/updatephoto")
    Call<Response> updatePhoto(@Query("path") String path, @Header("Authorization") String apiKey);

    @GET("api/account/updatebiography")
    Call<Response> updateBiography(@Query("biography") String biography, @Header("Authorization") String apiKey);

    @GET("api/account/deletecomment")
    Call<Response> deleteComment(@Query("commentId") int commentId, @Header("Authorization") String apiKey);

    @GET("api/questions/getquestionsbyplaceid")
    Call<ListResponse<Question>> getQuestionsByPlaceId(@Query("id") int placeId);

    @GET("api/questions/getquestionsbyuserid")
    Call<ListResponse<Question>> getQuestionsByUserId(@Query("id") int userId);

    @POST("api/questions/add")
    Call<Response> addQuestion(@Body Question question, @Header("Authorization") String apiKey);

    @GET("api/questions/delete")
    Call<Response> deleteQuestion(@Query("id") int id, @Header("Authorization") String apiKey);

    @GET("api/answers/getanswersbyquestionid")
    Call<ListResponse<Answer>> getAnswersByQuestionId(@Query("id") int id);

    @GET("api/answers/togglestar")
    Call<Response> toggleStarAnswer(@Query("id") int id,@Header("Authorization") String apiKey);

    @POST("api/answers/add")
    Call<Response> addAnswer(@Body Answer answer,@Header("Authorization") String apiKey);

    @GET("api/places/getrating")
    Call<Float> getRating(@Query("placeId") int placeId);

    @GET("api/nearbyplaces/getbyplaceid")
    Call<ListResponse<NearbyPlace>> getNearbyPlacesByPlaceId(@Query("id") int id, @Query("type") int type);

    @GET("api/nearbyplaces/getinformation")
    Call<RestaurantAdditionalInfo> getNearbyPlaceInformationRestaurant(@Query("placeId") int id);

    @GET("api/nearbyplaces/getcomments")
    Call<ListResponse<Comment>> getNearbyComments(@Query("placeId") int placeId,@Header("Authorization") String apiKey);

    @GET("api/nearbyplaces/getmycomment")
    Call<SingleResponse<Comment>> getMyCommentNearby(@Query("placeId") int placeId,@Header("Authorization") String apiKey);
}
