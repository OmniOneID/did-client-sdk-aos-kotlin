/*
 * Copyright 2025 OmniOne.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.omnione.did.sdk.core.keymanager.datamodel

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.omnione.did.sdk.datamodel.common.enums.AlgorithmType
import org.omnione.did.sdk.datamodel.common.enums.AuthType
import org.omnione.did.sdk.core.storagemanager.datamodel.Meta

open class KeyInfo : Meta {
    @SerializedName("authType")
    @Expose
    var authType: AuthType.AUTH_TYPE = AuthType.AUTH_TYPE.FREE

    @SerializedName("algorithm")
    @Expose
    var algorithm: AlgorithmType.ALGORITHM_TYPE = AlgorithmType.ALGORITHM_TYPE.SECP256R1

    @SerializedName("publicKey")
    @Expose
    var publicKey: String = ""

    @SerializedName("accessMethod")
    @Expose
    var accessMethod: KeyAccessMethod.KEY_ACCESS_METHOD = KeyAccessMethod.KEY_ACCESS_METHOD.WALLET_NONE

    constructor()

    constructor(
        id: String,
        authType: AuthType.AUTH_TYPE,
        algorithm: AlgorithmType.ALGORITHM_TYPE,
        publicKey: String,
        accessMethod: KeyAccessMethod.KEY_ACCESS_METHOD
    ) : super() {
        this.id = id
        this.authType = authType
        this.algorithm = algorithm
        this.publicKey = publicKey
        this.accessMethod = accessMethod
    }
}
