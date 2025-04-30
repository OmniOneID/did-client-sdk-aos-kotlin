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

package org.omnione.did.sdk.core.exception

enum class WalletCoreErrorCode(val feature: String, val code: String, val msg: String) {

    /* COMMON ERROR */
    // ModuleCode(1) + StackCode(3) + ComponentCode(3) + ErrorCode(5)
    // ModuleCode - Mobile (M)
    // StackCode - SDK (SDK)
    // ComponentCode - WalletService (WLT)

//    WALLET_CORE_PUBLIC("0"),
//    WALLET_CORE_PRIVATE("1"),

    /* [00] KEY_MANAGER  */
    //PUBLIC
    ERR_CODE_KEY_MANAGER_INVALID_PARAMETER("MSDKWLT", "00000", "Invalid parameter : "),
    ERR_CODE_KEY_MANAGER_DUPLICATED_PARAMETER("MSDKWLT", "00001", "Duplicated parameter : "),
    ERR_CODE_KEY_MANAGER_FAILED_TO_DECODE("MSDKWLT", "00002", "Fail to decode : "),
    ERR_CODE_KEY_MANAGER_EXIST_KEY_ID("MSDKWLT", "00100", "Given key id already exists"),
    ERR_CODE_KEY_MANAGER_NOT_CONFORM_TO_KEY_GEN_REQUEST("MSDKWLT", "00101", "Given keyGenRequest does not conform to {Wallet/Secure}KeyGenRequest"),
    ERR_CODE_KEY_MANAGER_NEW_PIN_EQUALS_OLD_PIN("MSDKWLT", "00200", "Given new pin is equal to old pin"),
    ERR_CODE_KEY_MANAGER_NOT_PIN_AUTH_TYPE("MSDKWLT", "00201","Given key id is not pin auth type"),
    ERR_CODE_KEY_MANAGER_FOUND_NO_KEY_BY_KEY_TYPE("MSDKWLT", "00400","Found no key by given keyType"),
    ERR_CODE_KEY_MANAGER_INSUFFICIENT_RESULT_BY_KEY_TYPE("MSDKWLT", "00401","Insufficient result by given keyType"),
    ERR_CODE_KEY_MANAGER_UNSUPPORTED_ALGORITHM("MSDKWLT", "00900","Given algorithm is unsupported"),


    /*  [01] DIDManager  */
    ERR_CODE_DID_MANAGER_INVALID_PARAMETER("MSDKWLT", "01000", "Invalid parameter : "),
    ERR_CODE_DID_MANAGER_DUPLICATED_PARAMETER("MSDKWLT", "01001", "Duplicated parameter : "),
    ERR_CODE_DID_MANAGER_FAILED_TO_DECODE("MSDKWLT", "01002", "Fail to decode : "),
    ERR_CODE_DID_MANAGER_FAIL_TO_GENERATE_RANDOM("MSDKWLT", "01100", "Fail to generate random"),
    ERR_CODE_DID_MANAGER_DOCUMENT_IS_ALREADY_EXISTS("MSDKWLT", "01200", "Document is already exists"),
    ERR_CODE_DID_MANAGER_DUPLICATE_KEY_ID_EXISTS_IN_VERIFICATION_METHOD("MSDKWLT", "01300", "Duplicate key ID exists in verification method"),
    ERR_CODE_DID_MANAGER_NOT_FOUND_KEY_ID_IN_VERIFICATION_METHOD("MSDKWLT", "01301", "Not found key ID in verification method"),
    ERR_CODE_DID_MANAGER_DUPLICATE_SERVICE_ID_EXISTS_IN_SERVICE("MSDKWLT", "01302", "Duplicate service ID exists in service"),
    ERR_CODE_DID_MANAGER_NOT_FOUND_SERVICE_ID_IN_SERVICE("MSDKWLT", "01303", "Not found service ID in service"),
    ERR_CODE_DID_MANAGER_DONT_CALL_RESET_CHANGES_IF_NO_DOCUMENT_SAVED("MSDKWLT", "01304", "Don't call 'resetChanges' if no document saved"),
    ERR_CODE_DID_MANAGER_UNEXPECTED_CONDITION("MSDKWLT", "01900", "Unexpected condition occurred"),


    /*  [02] VCManager  */
    ERR_CODE_VC_MANAGER_INVALID_PARAMETER("MSDKWLT", "02000", "Invalid parameter : "),
    ERR_CODE_VC_MANAGER_DUPLICATED_PARAMETER("MSDKWLT", "02001", "Duplicated parameter : "),
    ERR_CODE_VC_MANAGER_FAIL_TO_DECODER("MSDKWLT", "02002", "Fail to decode : "),
    ERR_CODE_VC_MANAGER_NO_CLAIM_CODE_IN_CREDENTIAL_FOR_PRESENTATION("MSDKWLT", "02100", "No claim code in credential(VC) for presentation"),

    /*  [03] SecureEncryptor  */
    ERR_CODE_SECURE_ENCRYPTOR_INVALID_PARAMETER("MSDKWLT", "03000", "Invalid parameter : "),
    ERR_CODE_SECURE_ENCRYPTOR_DUPLICATED_PARAMETER("MSDKWLT", "03001", "Duplicated parameter : "),
    ERR_CODE_SECURE_ENCRYPTOR_FAILED_TO_DECODE("MSDKWLT", "03002", "Fail to decode : "),

    /*  [10] StorageManager  */
    ERR_CODE_STORAGE_MANAGER_INVALID_PARAMETER("MSDKWLT", "10000", "Invalid parameter : "),
    ERR_CODE_STORAGE_MANAGER_DUPLICATED_PARAMETER("MSDKWLT", "10001", "Duplicated parameter : "),
    ERR_CODE_STORAGE_MANAGER_FAILED_TO_DECODE("MSDKWLT", "10002", "Fail to decode : "),
    ERR_CODE_STORAGE_MANAGER_FAIL_TO_SAVE_WALLET("MSDKWLT", "10100", "Fail to save wallet : "),
    ERR_CODE_STORAGE_MANAGER_ITEM_DUPLICATED_WITH_IT_IN_WALLET("MSDKWLT", "10101", "Item duplicated with it in wallet"),
    ERR_CODE_STORAGE_MANAGER_NO_ITEM_TO_UPDATE("MSDKWLT", "10200", "No item to update in wallet"),
    ERR_CODE_STORAGE_MANAGER_NO_ITEMS_TO_REMOVE("MSDKWLT", "10300", "No items to remove in wallet"),
    ERR_CODE_STORAGE_MANAGER_FAIL_TO_REMOVE_ITEMS("MSDKWLT", "10301", "Fail to remove items from wallet"),
    ERR_CODE_STORAGE_MANAGER_NO_ITEMS_SAVED ("MSDKWLT", "10400", "No items saved in wallet"),
    ERR_CODE_STORAGE_MANAGER_NO_ITEMS_TO_FIND("MSDKWLT", "10401", "No items to find in wallet"),
    ERR_CODE_STORAGE_MANAGER_FAIL_TO_READ_WALLET_FILE("MSDKWLT", "10402", "Fail to read wallet file : "),
    ERR_CODE_STORAGE_MANAGER_MALFORMED_EXTERNAL_WALLET("MSDKWLT", "10500", "Malformed external wallet format : "),
    ERR_CODE_STORAGE_MANAGER_MALFORMED_WALLET_SIGNATURE("MSDKWLT", "10501", "Malformed wallet signature"),
    ERR_CODE_STORAGE_MANAGER_MALFORMED_INNER_WALLET("MSDKWLT", "10502", "Malformed inner wallet format : "),
    ERR_CODE_STORAGE_MANAGER_MALFORMED_ITEM_TYPE("MSDKWLT", "10503", "Malformed item object type about item of inner wallet : "),
    ERR_CODE_STORAGE_MANAGER_UNEXPECTED_ERROR("MSDKWLT", "10900", "Unexpected error occurred : "),

    /*  [11] Signable  */
    ERR_CODE_SIGNABLE_INVALID_PARAMETER("MSDKWLT", "11000", "Invalid parameter : "),
    ERR_CODE_SIGNABLE_DUPLICATED_PARAMETER("MSDKWLT", "11001", "Duplicated parameter : "),
    ERR_CODE_SIGNABLE_FAILED_TO_DECODE("MSDKWLT", "11002", "Fail to decode : "),
    ERR_CODE_SIGNABLE_INVALID_PUBLIC_KEY("MSDKWLT", "11100", "Not proper public key format"),
    ERR_CODE_SIGNABLE_INVALID_PRIVATE_KEY("MSDKWLT", "11101", "Not proper private key format"),
    ERR_CODE_SIGNABLE_NOT_DERIVED_KEY_FROM_PRIVATE_KEY("MSDKWLT", "11102","Private and public keys are not pair"),
    ERR_CODE_SIGNABLE_FAIL_TO_CONVERT_COMPACT_REPRESENTATION("MSDKWLT", "11200", "Converting failed to compact representation"),
    ERR_CODE_SIGNABLE_CREATE_SIGNATURE("MSDKWLT", "11201", "Signing failed : "),
    ERR_CODE_SIGNABLE_VERIFY_SIGNATURE_FAILED("MSDKWLT", "11202", "Failed to verify signature : "),


    /*  [12] KeystoreManager  */
    ERR_CODE_KEYSTORE_MANAGER_INVALID_PARAMETER("MSDKWLT", "12000", "Invalid parameter : "),
    ERR_CODE_KEYSTORE_MANAGER_DUPLICATED_PARAMETER("MSDKWLT", "12001", "Duplicated parameter : "),
    ERR_CODE_KEYSTORE_MANAGER_FAILED_TO_DECODE("MSDKWLT", "12002", "Fail to decode : "),
    ERR_CODE_KEYSTORE_MANAGER_FAILED_TO_CREATE_SECURE_KEY("MSDKWLT", "12100", "Failed to create secure key : "),
    ERR_CODE_KEYSTORE_MANAGER_FAILED_TO_COPY_PUBLIC_KEY("MSDKWLT", "12101", "Failed to copy public key"),
    ERR_CODE_KEYSTORE_MANAGER_PUBLIC_KEY_REPRESENTATION("MSDKWLT", "12102", "Failed to get public key representation : "),
    ERR_CODE_KEYSTORE_MANAGER_CANNOT_FIND_SECURE_KEY_BY_CONDITION("MSDKWLT", "12103", "Cannot find secure key by given conditions"),
    ERR_CODE_KEYSTORE_MANAGER_FAILED_TO_DELETE_SECURE_KEY("MSDKWLT", "12104", "Failed to delete secure key"),
    ERR_CODE_KEYSTORE_MANAGER_FAILED_TO_SIGN("MSDKWLT", "12200", "Signing failed : "),
    ERR_CODE_KEYSTORE_MANAGER_CANNOT_CREATE_ENCRYPTED_DATA("MSDKWLT", "12300", "Cannot create encrypted data : "),
    ERR_CODE_KEYSTORE_MANAGER_CANNOT_CREATE_DECRYPTED_DATA("MSDKWLT", "12301", "Cannot create decrypted data : "),
    ERR_CODE_KEYSTORE_MANAGER_NO_REGISTERED_BIO_AUTH_INFO("MSDKWLT", "12400", "No registered bio authentication information");

    companion object {
        @JvmStatic
        fun getEnumByCode(code: String): WalletCoreErrorCode {
            return entries.find { it.code == code }
                ?: throw IllegalArgumentException("Unknown Enum Code: $code")
        }
    }
}
