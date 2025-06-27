package org.omnione.did.sdk.core

import android.content.Context
import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Test
import org.junit.runner.RunWith
import org.omnione.did.sdk.core.common.KeystoreManager
import org.omnione.did.sdk.core.keymanager.KeyManager
import org.omnione.did.sdk.core.keymanager.datamodel.DetailKeyInfo
import org.omnione.did.sdk.core.keymanager.datamodel.KeyGenWalletMethodType
import org.omnione.did.sdk.core.keymanager.datamodel.KeyInfo
import org.omnione.did.sdk.core.keymanager.datamodel.KeyStoreAccessMethod
import org.omnione.did.sdk.core.keymanager.datamodel.SecureKeyGenRequest
import org.omnione.did.sdk.core.keymanager.datamodel.StorageOption
import org.omnione.did.sdk.core.keymanager.datamodel.WalletKeyGenRequest
import org.omnione.did.sdk.core.keymanager.supportalgorithm.Secp256R1Manager
import org.omnione.did.sdk.core.storagemanager.StorageManager
import org.omnione.did.sdk.core.storagemanager.datamodel.FileExtension
import org.omnione.did.sdk.datamodel.common.enums.AlgorithmType
import org.omnione.did.sdk.datamodel.common.enums.VerifyAuthType
import org.omnione.did.sdk.utility.DataModels.DigestEnum
import org.omnione.did.sdk.utility.DataModels.MultibaseType
import org.omnione.did.sdk.utility.DigestUtils
import org.omnione.did.sdk.utility.MultibaseUtils

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */


@RunWith(AndroidJUnit4::class)
class KeyManagerTest {

    @Test
    @Throws(Exception::class)
    fun keyManager() {
        // assertArrayEquals(a,b) : 배열 a와 b가 일치함을 확인
        // assertEquals(a,b) : 객체 a와 b의 값이 같은지 확인
        // assertSame(a,b) : 객체 a와 b가 같은 객체임을 확인
        // assertTrue(a) : a가 참인지 확인
        // assertNotNull(a) : a 객체가 null이 아님을 확인

        val appContext: Context = InstrumentationRegistry.getInstrumentation().targetContext
        // assertEquals("com.opendid.walletcore.test", appContext.packageName)

        for (alias in KeystoreManager.getKeystoreAliasList()) {
            Log.d("KeyManagerTest", "Bio key 생성 전 alias : $alias")
        }

        // KeyManager 생성자
        val keyManager = KeyManager<DetailKeyInfo>("walletTest", appContext)
        if (keyManager.isAnyKeySaved()) keyManager.deleteAllKeys()

        // NONE KEY 생성
        var keyGenInfo = WalletKeyGenRequest().apply {
            id = "FREE"
            algorithmType = AlgorithmType.ALGORITHM_TYPE.SECP256R1
            storage = StorageOption.STORAGE_OPTION.WALLET
            methodType = KeyGenWalletMethodType()
        }
        Log.d("KeyManagerTest", "KeyGenInfo (FREE) : ${keyGenInfo.toJson()}")
        keyManager.generateKey(keyGenInfo)

        // pin Key 생성
        keyGenInfo = WalletKeyGenRequest().apply {
            id = "PIN"
            algorithmType = AlgorithmType.ALGORITHM_TYPE.SECP256R1
            storage = StorageOption.STORAGE_OPTION.WALLET
            methodType = KeyGenWalletMethodType(
                MultibaseUtils.encode(MultibaseType.MULTIBASE_TYPE.BASE_64, "111111".toByteArray())
            )
        }
        Log.d("KeyManagerTest", "KeyGenInfo (PIN) : ${keyGenInfo.toJson()}")
        keyManager.generateKey(keyGenInfo)

        val secp256R1Manager = Secp256R1Manager()
        val privateKey = MultibaseUtils.decode("f9e075a5e7a8ad6148a27ee2159a6a140c163084da7bf5e956fac2f5f36d81aa1")
        val publicKey = MultibaseUtils.decode("f023888e60a7bf9672b09fad449436939a1b9dff53f18bd9a2383a7d18e3e15c861")

        val privateKey2 = MultibaseUtils.decode("fbdf8c9e39d301233b5a262dd156f6602bc29f0cfcb346f7c5cf525c4f247207b")
        val publicKey2 = MultibaseUtils.decode("f035406dba5e8a29dc2d05b42c08f925b95d972786d95f91e86e7d2c0f51c6cef9b")

        Log.d("KeyManagerTest", "checkKeyPairMatch priv1 / pub1")
        secp256R1Manager.checkKeyPairMatch(privateKey, publicKey)
        Log.d("KeyManagerTest", "checkKeyPairMatch priv2 / pub2")
        secp256R1Manager.checkKeyPairMatch(privateKey2, publicKey2)

        // 지문키 등록
        val bioKeyRequest = SecureKeyGenRequest(
            "BIO",
            AlgorithmType.ALGORITHM_TYPE.SECP256R1,
            StorageOption.STORAGE_OPTION.KEYSTORE,
            KeyStoreAccessMethod.KEYSTORE_ACCESS_METHOD.BIOMETRY
        )
        keyManager.generateKey(bioKeyRequest)

        // key wallet 확인
        val storageManager = StorageManager<KeyInfo, DetailKeyInfo>(
            "walletTest", FileExtension.FILE_EXTENSION.KEY, false, appContext, DetailKeyInfo::class.java, KeyInfo::class.java
        )
        for (walletItem in storageManager.getAllItems()) {
            Log.i("KeyManagerTest", "Wallet Meta : ${walletItem.meta.toJson()}")
            Log.i("KeyManagerTest", "Wallet Item : ${walletItem.item.toJson()}")
        }

        for (alias in KeystoreManager.getKeystoreAliasList()) {
            Log.d("KeyManagerTest", "Bio key 생성 후 alias : $alias")
        }

        keyManager.changePin("PIN", "111111".toByteArray(), "222222".toByteArray())
        keyManager.changePin("PIN", "222222".toByteArray(), "111111".toByteArray())

        val ids = mutableListOf("PIN", "BIO")
        for (keyInfo in keyManager.getKeyInfos(ids)) {
            Log.d("KeyManagerTest", "getKeyInfos(ids) : ${keyInfo.toJson()}")
        }

        for (authType in VerifyAuthType.VERIFY_AUTH_TYPE.values()) {
            for (keyInfo in keyManager.getKeyInfos(authType)) {
                Log.i("KeyManagerTest", "getKeyInfos(verifyAuthType) $authType: ${keyInfo.toJson()}")
            }
        }

        /////////////// 서명, 검증/////////////////
        val plainData = "KeyManagerTest".toByteArray()
        val digest = DigestUtils.getDigest(plainData, DigestEnum.DIGEST_ENUM.SHA_256)
        val signature = keyManager.sign("PIN", "111111".toByteArray(), digest)

        Log.d("KeyManagerTest", "PIN 111111 sign Value : ${MultibaseUtils.encode(MultibaseType.MULTIBASE_TYPE.BASE_16, signature)}")

        var pubKey = ""
        for (keyInfo in keyManager.getKeyInfos(listOf("PIN"))) {
            pubKey = keyInfo.publicKey
            Log.d("KeyManagerTest", "PIN 111111 sign pubKey: $pubKey")
        }

        keyManager.verify(AlgorithmType.ALGORITHM_TYPE.SECP256R1, MultibaseUtils.decode(pubKey), digest, signature)

        for (alias in KeystoreManager.getKeystoreAliasList()) {
            Log.d("KeyManagerTest", "Bio key 서명 전 alias : $alias")
        }

        val bioSignature = keyManager.sign("BIO", null, "KeyManagerTest".toByteArray())
        Log.d("KeyManagerTest", "BIO sign Value : ${MultibaseUtils.encode(MultibaseType.MULTIBASE_TYPE.BASE_16, bioSignature)}")

        pubKey = ""
        for (keyInfo in keyManager.getKeyInfos(listOf("BIO"))) {
            pubKey = keyInfo.publicKey
            Log.d("KeyManagerTest", "BIO sign pubKey: $pubKey")
        }

        keyManager.verify(AlgorithmType.ALGORITHM_TYPE.SECP256R1, MultibaseUtils.decode(pubKey), "KeyManagerTest".toByteArray(), bioSignature)

        // PIN 값 삭제
        keyManager.deleteKeys(listOf("BIO"))

        // PIN, BIO로 가져오기
        for (keyInfo in keyManager.getKeyInfos(listOf("PIN", "BIO"))) {
            Log.d("KeyManagerTest", "deleteKeys(ids) 후 getKeyInfos(ids) : ${keyInfo.toJson()}")
        }

        for (alias in KeystoreManager.getKeystoreAliasList()) {
            Log.d("KeyManagerTest", "BIO 삭제 후 alias : $alias")
        }

        keyManager.deleteAllKeys()

        for (alias in KeystoreManager.getKeystoreAliasList()) {
            Log.d("KeyManagerTest", "전부삭제 후 alias : $alias")
        }
    }
}

