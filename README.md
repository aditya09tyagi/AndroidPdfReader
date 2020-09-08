# AndroidPdfReader

Android Pdf Reader to read PDFs without increasing app size. <br />

![Pdf View]()
<br />
<br />
<b>To add in your .xml file : </b>

```
        <com.personal.android.androidpdfreader.util.PinchToZoomRecyclerView
            android:id="@+id/rvPdfView"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:background="#EAEAEA" />
```

<b>In your PDFViewModel (kotlin/java) file add</b> <br />

```
var pdfRenderer: PdfRenderer? = null

    fun getPdfRenderer(filePath: String) {
        try {
            pdfRenderer = PdfRenderer(
                ParcelFileDescriptor.open(
                    File(filePath),
                    ParcelFileDescriptor.MODE_READ_ONLY
                )
            )
        } catch (exception: Exception) {
            throwExceptionForPdfRenderer(exception)
        }
    }
    
    private fun throwExceptionForPdfRenderer(exception: Exception) {
        val message: String = when (exception) {
            is IOException -> exception.localizedMessage
            is SecurityException -> exception.localizedMessage
            is FileNotFoundException -> exception.localizedMessage
            else -> exception.localizedMessage
        }
        Log.d("PDF Renderer exception", message)
    }
     
```
<p>
The PdfRenderer isn't meant to be a full blown PDF solution.If you need to cover more advanced cases, it probably won't be enough for you.
As an example, it doesn't support annotations and it has issues dealing with password protected and corrupted files.That's why the above mentioned
exceptions are thrown and in the case if any exception is thrown we use the alternative of showing the pdf in webView with a Google Drive pdf URL which 
enable us to rectify our issue upto some extent.
</p>


```
 private fun setAdapter() {
            viewModel.getPdfRenderer(pdfPath)                     //Path of the pdf you want to render
            viewModel.pdfRenderer?.let {
                val displayMetrics = DisplayMetrics()
                windowManager.defaultDisplay.getMetrics(displayMetrics)
                val width = displayMetrics.widthPixels
                rvPdfView.layoutManager = LinearLayoutManager(this)
                pdfReaderAdapter = PdfReaderAdapter(pdfRenderer, width)
                rvPdfView.adapter = pdfReaderAdapter                    //set your adapter here 
            } ?: run {
                  takeActionForPdfRendererNull()                  //if exception is thrown and renderer is null show it in web view(refer PdfActivity)
            }
        }
    }
```

Now coming to point where you set your view of the PDF there you can use [PhotoView library by chris banes](https://github.com/chrisbanes/PhotoView) for 
zoom in purpose but the issue that arises here is you'll be able to zoom only a single page instead of whole recycler view so instead of that you can use 
<b>[PinchToZoomRecyclerView](https://github.com/aditya09tyagi/AndroidPdfReader/blob/master/app/src/main/java/com/personal/android/androidpdfreader/util/PinchToZoomRecyclerView.kt)</b>
which enables you to perform zoom in ,zoom out ,double tap to zoom action on the whole recycler view just like you can do in any other PDF library. 


Through pdf renderer you can either opt. to render a single page or else you can use the following code where you create a bitmap for each and every page you render and then use that bitmap as a page of your pdf
```
fun PdfRenderer.Page.renderAndClose(width: Int) = use {
    val bitmap = createBitmap(width)
    render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
    bitmap
}

private fun PdfRenderer.Page.createBitmap(bitmapWidth: Int): Bitmap {
    val bitmap = Bitmap.createBitmap(
            bitmapWidth, (bitmapWidth.toFloat() / width * height).toInt(), Bitmap.Config.ARGB_8888
    )

    val canvas = Canvas(bitmap)
    canvas.drawColor(Color.WHITE)
    canvas.drawBitmap(bitmap, 0f, 0f, null)

    return bitmap
}
```

So for further information you can go through the project and you can get a better idea, 
basically this project is more like a pdf reader app you can use it to open pdf in you phone through this application .

Thanks and hope it helps everyone..!!
