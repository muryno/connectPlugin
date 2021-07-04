package com.native_communication.event

import androidx.multidex.MultiDexApplication
import com.native_communication.server.BridgeServer
import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import org.komputing.khex.extensions.toNoPrefixHexString
import org.walletconnect.Session
import org.walletconnect.impls.*
import org.walletconnect.nullOnThrow
import java.io.File
import java.util.*

class WallectConnectApplication : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        initMoshi()
        initClient()
        initBridge()
        initSessionStorage()
    }

    private fun initClient() {
        client = OkHttpClient.Builder().build()
    }

    private fun initMoshi() {
        moshi = Moshi.Builder().build()
    }


    private fun initBridge() {
        bridge = BridgeServer(moshi)
        bridge.start()
    }

    private fun initSessionStorage() {
        storage = FileWCSessionStore(File(cacheDir, "session_store.json").apply { createNewFile() }, moshi)
    }

    companion object {

        private lateinit var client: OkHttpClient
        private lateinit var moshi: Moshi
        private lateinit var bridge: BridgeServer
        private lateinit var storage: WCSessionStore
        lateinit var config: Session.Config
        lateinit var session: Session

        fun resetSession(bridgeServerUrl: String) {
            nullOnThrow { session }?.clearCallbacks()
            val key = ByteArray(32).also { Random().nextBytes(it) }.toNoPrefixHexString()
            config = Session.Config(UUID.randomUUID().toString(), bridgeServerUrl, key)
            session = WCSession(config,
                    MoshiPayloadAdapter(moshi),
                    storage,
                    OkHttpTransport.Builder(client, moshi),
                    Session.PeerMeta(name = "wallectConnect")
            )
            session.offer()
        }

        fun connectWithBridgeUrl(bridgeServerUrl: String) {
            nullOnThrow { session }?.clearCallbacks()

            config = Session.Config.fromWCUri(bridgeServerUrl)

            session = WCSession(config,
                MoshiPayloadAdapter(moshi),
                storage,
                OkHttpTransport.Builder(client, moshi),
                Session.PeerMeta(name = "wallectConnect")
            )
            session.offer()
        }


    }
}
