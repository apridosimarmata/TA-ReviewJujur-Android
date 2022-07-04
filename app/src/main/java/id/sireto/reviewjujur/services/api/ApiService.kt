package id.sireto.reviewjujur.services.api

import id.sireto.reviewjujur.models.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @POST("/users")
    suspend fun registerUser(@Body user : UserRequest) : Response<BaseResponse>

    @PATCH("/users/name")
    suspend fun updateUserName(@Header("Access-Token") accessToken : String, @Header("Access-Refresh-Token") refreshToken : String, @Body userUpdateNameRequest: UserNameRequest) : Response<BaseResponse>

    @PATCH("/users/password")
    suspend fun updateUserPassword(@Header("Access-Token") accessToken : String, @Header("Access-Refresh-Token") refreshToken : String, @Body userUpdatePasswordRequest: UserPasswordRequest) : Response<BaseResponse>

    @POST("/users/verification/whatsapp")
    suspend fun verifyWhatsApp(@Body codeVerificationRequest: CodeVerificationRequest) : Response<BaseResponse>

    @GET("/auth/authorization")
    suspend fun getUserDetails(@Header("Access-Token") accessToken : String, @Header("Access-Refresh-Token") refreshToken : String) : Response<BaseResponse>

    @POST("/auth/authentication/email")
    suspend fun authenticateUserByEmail(@Body userEmailAuthenticationRequest: UserEmailAuthenticationRequest) : Response<BaseResponse>

    @POST("/auth/authentication/whatsapp")
    suspend fun authenticateUserWhatsApp(@Body codeVerificationRequest: CodeVerificationRequest, @Query("whatsAppVerification") whatsappVerification : Int?) : Response<BaseResponse>

    @GET("/auth/authentication/whatsapp/{whatsappNo}")
    suspend fun requestUserVerificationCode(@Path("whatsappNo") whatsappNo : String) : Response<BaseResponse>

    @GET("/auth/authorization")
    suspend fun authorizeUser(@Header("Access-Token") accessToken : String, @Header("Access-Refresh-Token") refreshToken : String) : Response<BaseResponse>

    @GET("/auth/authorization/refresh")
    suspend fun refreshUserToken(@Header("Access-Refresh-Token") refreshToken : String) : Response<BaseResponse>

    @POST("/businesses")
    suspend fun createBusiness(@Header("Access-Token") accessToken : String, @Header("Access-Refresh-Token") refreshToken : String, @Body businessRequest: BusinessRequest) : Response<BaseResponse>

    @GET("/businesses/provinces")
    suspend fun getAllProvinces() : Response<BaseResponse>

    @GET("/businesses/user")
    suspend fun getUserBusiness(@Header("Access-Token") accessToken : String, @Header("Access-Refresh-Token") refreshToken : String) : Response<BaseResponse>

    @GET("/businesses/locations/{provinceUid}")
    suspend fun getLocationsByProvinceUid(@Path("provinceUid") provinceUid : String) : Response<BaseResponse>

    @GET("/businesses/search?")
    suspend fun searchBusiness(@Query("limit") limit : Int?, @Query("page") page : Int?, @Query("locationUid") locationUid : String, @Query("query") businessName : String?, @Query("sort") sort : String?) : Response<BaseResponse>

    @GET("/businesses/{businessUid}")
    suspend fun getBusinessByUid(@Path("businessUid") businessUid : String) : Response<BaseResponse>

    @POST("/reviews")
    suspend fun createReview(@Header("Access-Token") accessToken : String, @Header("Access-Refresh-Token") refreshToken : String, @Body review : ReviewRequest) : Response<BaseResponse>

    @GET("/reviews/business")
    suspend fun getBusinessReviews(@Query("businessUid") businessUid : String, @Query("createdAt") createdAt : String) : Response<BaseResponse>

    @GET("/reviews/user")
    suspend fun getUserReviews(@Header("Access-Token") accessToken : String, @Header("Access-Refresh-Token") refreshToken : String, @Query("createdAt") createdAt : String) : Response<BaseResponse>
}