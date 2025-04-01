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

open class SecureKeyGenRequest : KeyGenRequest {
    @SerializedName("accessMethod")
    @Expose
    var accessMethod: KeyStoreAccessMethod.KEYSTORE_ACCESS_METHOD

    constructor() : super() {
        accessMethod = KeyStoreAccessMethod.KEYSTORE_ACCESS_METHOD.NONE
    }

    constructor(
        id: String,
        algoType: AlgorithmType.ALGORITHM_TYPE,
        storageOption: StorageOption.STORAGE_OPTION,
        accessMethod: KeyStoreAccessMethod.KEYSTORE_ACCESS_METHOD
    ) : super(id, algoType, storageOption) {
        this.accessMethod = accessMethod
    }
}
