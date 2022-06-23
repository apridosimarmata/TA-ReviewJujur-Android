package id.sireto.reviewjujur.services.api

import id.sireto.reviewjujur.models.BaseResponse
import id.sireto.reviewjujur.models.ReviewPost
import id.sireto.reviewjujur.models.UserEmailAuthenticationPost
import id.sireto.reviewjujur.models.UserPost
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @POST("/users")
    suspend fun registerUser(@Body user : UserPost) : Response<BaseResponse>

    @POST("/auth/authentication/email")
    suspend fun authenticateUserByEmail(@Body userEmailAuthenticationPost: UserEmailAuthenticationPost) : Response<BaseResponse>

    @GET("/auth/authentication/phone/{whatsappNo}")
    suspend fun requestUserVerificationCode(@Path("whatsappNo") whatsappNo : String) : Response<BaseResponse>

    @GET("/auth/authorization")
    suspend fun authorizeUser(@Header("Access-Token") accessToken : String, @Header("Access-Refresh-Token") refreshToken : String) : Response<BaseResponse>

    @GET("/auth/authorization/refresh")
    suspend fun refreshUserToken(@Header("Access-Refresh-Token") refreshToken : String) : Response<BaseResponse>

    @GET("/businesses/provinces")
    suspend fun getAllProvinces() : Response<BaseResponse>

    @GET("/businesses/locations/{provinceUid}")
    suspend fun getLocationsByProvinceUid(@Path("provinceUid") provinceUid : String) : Response<BaseResponse>

    @GET("/businesses/search?")
    suspend fun searchBusiness(@Query("limit") limit : Int?, @Query("page") page : Int?, @Query("locationUid") locationUid : String, @Query("search") businessName : String?, @Query("sort") sort : String?) : Response<BaseResponse>

    @GET("/businesses/{businessUid}")
    suspend fun getBusinessByUid(@Path("businessUid") businessUid : String) : Response<BaseResponse>

    @POST("/reviews")
    suspend fun createReview(@Header("Access-Token") accessToken : String, @Header("Access-Refresh-Token") refreshToken : String, @Body review : ReviewPost) : Response<BaseResponse>

    @GET("/reviews/business")
    suspend fun getBusinessReviews(@Query("businessUid") businessUid : String, @Query("createdAt") createdAt : Int?) : Response<BaseResponse>
}