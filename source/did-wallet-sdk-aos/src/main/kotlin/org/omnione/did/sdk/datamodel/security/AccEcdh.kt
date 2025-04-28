package org.omnione.did.sdk.datamodel.security

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.omnione.did.sdk.datamodel.common.Proof
import org.omnione.did.sdk.datamodel.common.enums.SymmetricCipherType
import org.omnione.did.sdk.datamodel.common.enums.SymmetricPaddingType
import org.omnione.did.sdk.datamodel.util.IntEnumAdapterFactory
import org.omnione.did.sdk.datamodel.util.JsonSortUtil

data class AccEcdh(
    @SerializedName("server")
    @Expose
    var server: String? = null,

    @SerializedName("serverNonce")
    @Expose
    var serverNonce: String? = null,

    @SerializedName("publicKey")
    @Expose
    var publicKey: String? = null,

    @SerializedName("cipher")
    @Expose
    var cipher: SymmetricCipherType.SYMMETRIC_CIPHER_TYPE? = null,

    @SerializedName("padding")
    @Expose
    var padding: SymmetricPaddingType.SYMMETRIC_PADDING_TYPE? = null,

    @SerializedName("proof")
    @Expose
    var proof: Proof? = null // Key agreement proof
) {
    fun toJson(): String {
        val gson = GsonBuilder()
            .registerTypeAdapterFactory(IntEnumAdapterFactory())
            .disableHtmlEscaping()
            .create()
        return JsonSortUtil.sortJsonString(gson, gson.toJson(this))
    }
}
