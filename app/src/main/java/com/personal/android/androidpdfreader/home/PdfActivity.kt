package com.personal.android.androidpdfreader.home

import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.personal.android.androidpdfreader.R
import com.personal.android.androidpdfreader.util.*
import kotlinx.android.synthetic.main.activity_pdf.*


class PdfActivity : AppCompatActivity(), PermissionCommonUtil.OnPermissionRequestListener {

    private lateinit var pdfReaderAdapter: PdfReaderAdapter
    private lateinit var viewModel: PdfViewModel
    private var pdfUri: Uri? = null
    private lateinit var title: String
    private lateinit var pdfPath: String
    private lateinit var permissionCommonUtil: PermissionCommonUtil
    private val readWritePermissionRequestCode=565
    private val permissions = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    companion object {
        fun newIntent(context: Context) = Intent(context, PdfActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdf)
        initPermission()
        checkReadWritePermission()
        getArguments()
        initViewModel()
        setAdapter()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this).get(PdfViewModel::class.java)
    }

    private fun initPermission(){
        permissionCommonUtil = PermissionCommonUtil(this,permissions,readWritePermissionRequestCode,this)
    }

    private fun getArguments(){
        intent?.let {
            if (it.action == Intent.ACTION_VIEW) {
                if (it.data != null) {
                    try {
                        pdfUri = it.data
                        FilePathUtil.getPath(this, it.data!!)?.let {
                            pdfPath = it
                        }
                        title = pdfPath.substringAfterLast("/")
                    } catch (e: Exception) {
                        pdfUri?.let {
                            title = it.path!!
                        }
                    }

                }
            }
        }
    }

    private fun setAdapter() {
        if (::viewModel.isInitialized && ::pdfPath.isInitialized && ::title.isInitialized) {
            toolbar.title = title
            viewModel.getPdfRenderer(pdfPath)
            viewModel.pdfRenderer?.let {
                takeActionForPdfRendererNotNull(it)
            } ?: run {
                takeActionForPdfRendererNull()
            }
        }
    }

    private fun takeActionForPdfRendererNotNull(pdfRenderer: PdfRenderer) {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = displayMetrics.widthPixels
        rvPdfView.visible()
        pdfWebView.gone()
        rvPdfView.layoutManager = LinearLayoutManager(this)
        pdfReaderAdapter = PdfReaderAdapter(pdfRenderer, width)
        rvPdfView.adapter = pdfReaderAdapter
    }

    private fun takeActionForPdfRendererNull() {
        rvPdfView.gone()
        pdfWebView.visible()
        showPdfInWebView()
    }

    private fun showPdfInWebView() {
        if (::pdfPath.isInitialized && pdfPath.isNotEmpty()) {
            pdfWebView.loadUrl(Constants.DEFAULT_GOOGLE_PDF_URL + pdfPath)
            setParametersToPdfWebView()
            attachWebViewClientToPdfWebView()
        }
    }

    private fun setParametersToPdfWebView() {
        pdfWebView.settings.setSupportZoom(true)
        pdfWebView.settings.allowFileAccess = true
        pdfWebView.settings.allowUniversalAccessFromFileURLs = true
        pdfWebView.settings.displayZoomControls = true
        pdfWebView.isHapticFeedbackEnabled = false
        pdfWebView.settings.javaScriptEnabled = true
    }

    private fun attachWebViewClientToPdfWebView() {
        pdfWebView.webViewClient = WebViewClient()
        pdfWebView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                if (view?.title == Constants.EMPTY_STRING)
                    view.reload()
            }

            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                super.onReceivedError(view, request, error)
            }
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionCommonUtil.onPermissionResult(requestCode, permissions, grantResults)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::viewModel.isInitialized)
            viewModel.pdfRenderer?.close()
    }

    private fun checkReadWritePermission() {
        if (permissionCommonUtil.isReadWritePermissionAvailable()) {
            permissionGranted()
        } else {
            permissionCommonUtil.requestReadWritePermission()
        }
    }

    override fun permissionGranted() {
        Toast.makeText(this,"Permission Granted",Toast.LENGTH_SHORT).show()
    }

    override fun permissionDenied() {
        Toast.makeText(this,"Please Grant Permission",Toast.LENGTH_SHORT).show()
    }

    override fun shouldShowRationalMessage() {
        Toast.makeText(this,"Please Allow Permission",Toast.LENGTH_SHORT).show()
    }

}