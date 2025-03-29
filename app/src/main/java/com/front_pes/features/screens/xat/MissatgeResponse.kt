import com.google.gson.annotations.SerializedName
import java.util.Date

data class MissatgeResponse(
    val id: Int,
    val text: String,
    @SerializedName("data") val fecha: Date, // "data" en JSON se mapea a "fecha"
    val xat: Int,
    val autor: String
)