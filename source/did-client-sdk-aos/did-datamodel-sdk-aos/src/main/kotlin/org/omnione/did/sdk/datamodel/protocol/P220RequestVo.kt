package org.omnione.did.sdk.datamodel.protocol

import com.google.gson.annotations.SerializedName
import org.omnione.did.sdk.datamodel.security.ReqEcdh
import org.omnione.did.sdk.datamodel.token.ServerTokenSeed
import org.omnione.did.sdk.datamodel.vc.issue.ReqRevokeVC

data class P220RequestVo @JvmOverloads constructor(
    override var id: String,
    override var txId: String? = null,
    var vcId: String? = null,
    var reqEcdh: ReqEcdh? = null,
    var seed: ServerTokenSeed? = null,
    var serverToken: String? = null,
    @SerializedName("request")
    var reqRevokeVc: ReqRevokeVC? = null
) : BaseRequestVo(id, txId)
