package com.bunker.regitrationfb

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import kotlinx.android.synthetic.main.login_fragment.*
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


class LogInFragment : Fragment() {
    companion object {
        fun newInstance() = LogInFragment()
    }
    private var callbackManager: CallbackManager? = null
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.login_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        callbackManager = CallbackManager.Factory.create()
        printHashKey()
        loginButtonFB.setOnClickListener {
            getDataFb()
        }
    }

    override fun onActivityResult(
            requestCode: Int,
            resultCode: Int,
            data: Intent?
    ) {
        callbackManager!!.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun printHashKey() {
        try {
            val info = context?.packageName?.let { context?.packageManager?.getPackageInfo(it, PackageManager.GET_SIGNATURES) }
            for (signature in info!!.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val hashKey = String(Base64.encode(md.digest(), 0))
                Log.i("TAG", "printHashKey() Hash Key: $hashKey")
            }
        } catch (e: NoSuchAlgorithmException) {
            Log.e("TAG", "printHashKey()", e)
        } catch (e: Exception) {
            Log.e("TAG", "printHashKey()", e)
        }

    }
    fun getDataFb(){
        LoginManager.getInstance().registerCallback(callbackManager,
                object : FacebookCallback<LoginResult?> {
                    override fun onSuccess(loginResult: LoginResult?) {
                        val mGraphRequest = GraphRequest.newMeRequest(
                                loginResult?.accessToken) { objctFB, response ->

                            if(objctFB.has("id")){
                                Log.d("YURIITEST", objctFB.getString("firsName"))
                                Log.d("email", objctFB.getString("email"))
                            }
                        }
                        val parameters = Bundle()
                        parameters.putString("fields", "firsName,lastName, email")
                        mGraphRequest.parameters = parameters
                        mGraphRequest.executeAsync()
                    }

                    override fun onCancel() {
                        // App code
                    }

                    override fun onError(exception: FacebookException) {
                        // App code
                    }
                })
    }
}