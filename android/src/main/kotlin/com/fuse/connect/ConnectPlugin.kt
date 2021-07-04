package com.fuse.connect

import android.content.Intent
import android.net.Uri
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat.startActivity
import com.native_communication.event.WallectConnectApplication
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.walletconnect.Session
import org.walletconnect.nullOnThrow

/** ConnectPlugin */
class ConnectPlugin: FlutterPlugin, MethodCallHandler, Session.Callback, EventChannel.StreamHandler {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private lateinit var channel : MethodChannel


  ///Async
  var eventChannel: EventChannel? = null


  private var txRequest: Long? = null

  private var eventSink: EventChannel.EventSink? = null


  private val uiScope = CoroutineScope(Dispatchers.Main)


  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "connect")
    channel.setMethodCallHandler(this)

    eventChannel = EventChannel(flutterPluginBinding.binaryMessenger, "aconnect")
    eventChannel!!.setStreamHandler(this)
  }

  override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {

    when (call.method) {
      "connectToWcWithDeepLink" -> {
        //bridge url
        walletConnectToWC(call.arguments as String)
      }
      "connectFromWc" -> {
        //Wallect Connect Url
        connectFromWc(call.arguments as String)
      }
      "disconnectFromWc" -> {
        //disconnect Connect Url
        disconnectButton()
      }
      "mainTransfer" -> {
        //disconnect Connect Url
        transferToWallet(call.arguments as String)
      }
      "getPlatformVersion" ->{

        result.success("Android ${android.os.Build.VERSION.RELEASE}")
      }else -> {
        result.notImplemented()
      }
    }



  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }



  private   fun connectFromWc(wcUrl: String):String{
    initialSetup()
    WallectConnectApplication.resetSession(wcUrl)
    WallectConnectApplication.session.addCallback(this)
    return  WallectConnectApplication.config.toWCUri()
  }

  override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
    eventSink = events

  }

  override fun onCancel(arguments: Any?) {
    eventSink = null
  }


  override fun onStatus(status: Session.Status) {
    when(status) {
      Session.Status.Approved -> sessionApproved()
      Session.Status.Closed -> sessionClosed()
      Session.Status.Connected, Session.Status.Disconnected,
      is Session.Status.Error -> {

        eventSink?.success(mapOf(
          "status" to false,
          "message" to "Error with connection"
        ))
      }
    }
  }

  override fun onMethodCall(call: Session.MethodCall) {}


  private fun sessionApproved() {
    uiScope.launch {
      eventSink?.success(mapOf(
        "status" to true,
        "Connected" to WallectConnectApplication.session.approvedAccounts()
      ))

    }
  }

  private fun sessionClosed() {
    uiScope.launch {

      eventSink?.success(mapOf(
        "status" to true,
        "Disconnected" to "connection disconnected"
      ))
    }
  }


  private fun walletConnectToWC(str:String ){
    initialSetup()
    WallectConnectApplication.connectWithBridgeUrl(str)
    WallectConnectApplication.session.addCallback(this)

  }

  private fun disconnectButton() {
    WallectConnectApplication.session.kill()
    WallectConnectApplication.session.removeCallback(this)
  }

  ///transfer
  private fun transferToWallet(wallectAddress:String ) {

    val from = WallectConnectApplication.session.approvedAccounts()?.first() ?: return

    val txRequest = System.currentTimeMillis()
    WallectConnectApplication.session.performMethodCall(
      Session.MethodCall.SendTransaction(
        txRequest,
        from,
        wallectAddress,
        null,
        null,
        null,
        "0x5AF3107A4000",
        ""
      ),
      ::handleResponse
    )
    this.txRequest = txRequest
  }


  private fun initialSetup() {

    val session = nullOnThrow { WallectConnectApplication.session } ?: return
    session.addCallback(this)
    sessionApproved()

  }

  private fun handleResponse(resp: Session.MethodCall.Response) {

    if (resp.id == txRequest) {

      txRequest = null
      uiScope.launch {

        "Last response: " + ((resp.result as? String) ?: "Unknown response")

      }
    }
  }





}
