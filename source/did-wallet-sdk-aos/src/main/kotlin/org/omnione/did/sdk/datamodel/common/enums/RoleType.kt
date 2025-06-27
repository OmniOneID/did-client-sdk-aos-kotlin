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

package org.omnione.did.sdk.datamodel.common.enums;

import org.omnione.did.sdk.datamodel.util.StringEnum;

class RoleType {

    enum class ROLE_TYPE(val value: String) : StringEnum {
        TAS("Tas"),
        WALLET("Wallet"),
        ISSUER("Issuer"),
        VERIFIER("Verifier"),
        WALLET_PROVIDER("WalletProvider"),
        APP_PROVIDER("AppProvider"),
        LIST_PROVIDER("ListProvider"),
        OP_PROVIDER("OpProvider"),
        KYC_PROVIDER("KycProvider"),
        NOTIFICATION_PROVIDER("NotificationProvider"),
        LOG_PROVIDER("LogProvider"),
        PORTAL_PROVIDER("PortalProvider"),
        DELEGATION_PROVIDER("DelegationProvider"),
        STORAGE_PROVIDER("StorageProvider"),
        BACKUP_PROVIDER("BackupProvider"),
        ETC("Etc");

        override fun getStringValue(): String {
            return value
        }

        override fun toString(): String = when (this) {
            TAS -> "Tas"
            WALLET -> "Wallet"
            ISSUER -> "Issuer"
            VERIFIER -> "Verifier"
            WALLET_PROVIDER -> "WalletProvider"
            APP_PROVIDER -> "AppProvider"
            LIST_PROVIDER -> "ListProvider"
            OP_PROVIDER -> "OpProvider"
            KYC_PROVIDER -> "KycProvider"
            NOTIFICATION_PROVIDER -> "NotificationProvider"
            LOG_PROVIDER -> "LogProvider"
            PORTAL_PROVIDER -> "PortalProvider"
            DELEGATION_PROVIDER -> "DelegationProvider"
            STORAGE_PROVIDER -> "StorageProvider"
            BACKUP_PROVIDER -> "BackupProvider"
            ETC -> "Etc"
        }

        companion object {
            @JvmStatic
            fun fromValue(value: String): ROLE_TYPE? =
                values().find { it.value == value }
        }

    }
}
