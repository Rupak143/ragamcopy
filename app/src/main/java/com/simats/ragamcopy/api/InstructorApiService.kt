package com.simats.ragamcopy.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

data class Instructor(
    val id: Int,
    val name: String,
    val email: String,
    val education: String,
    val profile: String?
)

data class LoginResponse(
    val status: String,
    val message: String,
    val instructor: Instructor?
)

data class SignupResponse(
    val status: String,
    val message: String
)

interface InstructorApiService {

    @FormUrlEncoded
    @POST("instructorlogin.php")
    suspend fun loginInstructor(
        @Field("email") email: String,
        @Field("password") password: String
    ): Response<LoginResponse>

    @FormUrlEncoded
    @POST("instructor/signup")
    fun signupInstructor(
        @Field("first_name") firstName: String,
        @Field("last_name") lastName: String,
        @Field("email") email: String,
        @Field("phone_number") phoneNumber: String,
        @Field("gender") gender: String,
        @Field("dob") dob: String,
        @Field("password") password: String
    ): Call<ResponseBody>
}
