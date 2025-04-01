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

package org.omnione.did.sdk.wallet.walletservice.exception

enum class WalletErrorCode(val feature: String, val code: String, val msg: String) {

    /* COMMON ERROR */
    // ModuleCode(1) + StackCode(3) + ComponentCode(3) + ErrorCode(5)
    // ModuleCode - Mobile (M)
    // StackCode - SDK (SDK)
    // ComponentCode - WalletService (WLT)

    ERR_CODE_WALLET_UNKNOWN("MSDKWLT", "05000", "Unknown error"),
    ERR_CODE_WALLET_FAIL("MSDKWLT", "05001", "General failure"),
    ERR_CODE_WALLET_VERIFY_PARAMETER_FAIL("MSDKWLT", "05002", "Failed to verify parameter"),
    ERR_CODE_WALLET_SERIALIZE_FAIL("MSDKWLT", "05003", "Failed to serialize"),
    ERR_CODE_WALLET_DESERIALIZE_FAIL("MSDKWLT", "05004", "Failed to deserialize"),
    ERR_CODE_WALLET_CREATE_PROOF_FAIL("MSDKWLT", "05005", "Failed to create proof"),
    ERR_CODE_WALLET_ROLE_MATCH_FAIL("MSDKWLT", "05006", "Failed to match role"),
    ERR_CODE_WALLET_VERIFY_CERT_VC_FAIL("MSDKWLT", "05007", "Failed to verify certVC"),

    ERR_CODE_WALLET_VERIFY_TOKEN_FAIL("MSDKWLT", "05010", "Failed to verify token"),
    ERR_CODE_WALLET_CREATE_WALLET_TOKEN_FAIL("MSDKWLT", "05011", "Failed to create wallet token"),

    ERR_CODE_WALLET_LOCKED_WALLET("MSDKWLT", "05020", "Wallet is locked"),
    ERR_CODE_WALLET_NEW_PIN_EQUALS_OLD_PIN("MSDKWLT", "05021", "Given new pin is equal to old pin"),
    ERR_CODE_WALLET_NOT_LOCK_TYPE("MSDKWLT", "05022", "Wallet is not a lock-type"),

    ERR_CODE_WALLET_INSERT_QUERY_FAIL("MSDKWLT", "05030", "Failed to execute insert query"),
    ERR_CODE_WALLET_SELECT_QUERY_FAIL("MSDKWLT", "05031", "Failed to execute select query"),
    ERR_CODE_WALLET_DELETE_QUERY_FAIL("MSDKWLT", "05032", "Failed to execute delete query"),

    ERR_CODE_WALLET_CREATE_WALLET_FAIL("MSDKWLT", "05040", "Failed to create wallet"),
    ERR_CODE_WALLET_PERSONALIZED_FAIL("MSDKWLT", "05041", "Failed to personalize"),
    ERR_CODE_WALLET_DEPERSONALIZED_FAIL("MSDKWLT", "05042", "Failed to depersonalize"),
    ERR_CODE_WALLET_SAVE_KEYSTORE_FAIL("MSDKWLT", "05043", "Failed to save keystore"),
    ERR_CODE_WALLET_INCORRECT_PASSCODE("MSDKWLT", "05044", "Incorrect passcode"),
    ERR_CODE_WALLET_NOT_FOUND_WALLET_ID("MSDKWLT", "05045", "Wallet ID not found"),

    ERR_CODE_WALLET_REGISTER_USER_FAIL("MSDKWLT", "05050", "Failed to register user"),
    ERR_CODE_WALLET_RESTORE_USER_FAIL("MSDKWLT", "05051", "Failed to restore user"),
    ERR_CODE_WALLET_DID_MATCH_FAIL("MSDKWLT", "05052", "Failed to match DID"),
    ERR_CODE_WALLET_UPDATE_USER_FAIL("MSDKWLT", "05053", "Failed to update user"),

    ERR_CODE_WALLET_ISSUE_CREDENTIAL_FAIL("MSDKWLT", "05060", "Failed to issue credential"),
    ERR_CODE_WALLET_REVOKE_CREDENTIAL_FAIL("MSDKWLT", "05061", "Failed to revoke credential"),

    ERR_CODE_WALLET_SUBMIT_CREDENTIAL_FAIL("MSDKWLT", "05070", "Failed to submit credential");

    companion object {
        @JvmStatic
        fun getEnumByCode(code: String): WalletErrorCode {
            return entries.find { it.code == code }
                ?: throw IllegalArgumentException("Unknown Enum Code: $code")
        }
    }
}