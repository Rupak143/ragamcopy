import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Field

interface ApiService {
    @FormUrlEncoded
    @POST("signup.php") // Change to your actual PHP file path
    fun signup(
        @Field("full_name") fullName: String,
        @Field("email") email: String,
        @Field("phone_number") phone: String,
        @Field("gender") gender: String,
        @Field("dob") dob: String,
        @Field("password") password: String,
        @Field("confirm_password") confirmPassword: String
    ): Call<SignupResponse>
}
interface InstructorApiService {
    @FormUrlEncoded
    @POST("signup.php")
    fun signup(
        @Field("full_name") fullName: String,
        @Field("email") email: String,
        @Field("phone_number") phone: String,
        @Field("gender") gender: String,
        @Field("dob") dob: String,
        @Field("password") password: String
    ): Call<ResponseBody>
}
data class SignupResponse(
    val success: Boolean,
    val message: String
)
