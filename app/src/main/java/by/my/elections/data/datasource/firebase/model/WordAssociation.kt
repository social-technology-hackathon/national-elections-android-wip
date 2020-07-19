package by.my.elections.data.datasource.firebase.model

import android.os.Parcelable
import com.google.firebase.firestore.PropertyName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class WordAssociation(
    @PropertyName("key")
    val key: String = "",
    @PropertyName("association")
    val association: List<String> = emptyList()
) : Parcelable