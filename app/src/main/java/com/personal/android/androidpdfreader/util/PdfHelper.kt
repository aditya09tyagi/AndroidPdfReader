package com.personal.android.androidpdfreader.util

import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

suspend fun renderSinglePage(filePath: String, width: Int) = withContext(Dispatchers.IO) {
    PdfRenderer(ParcelFileDescriptor.open(File(filePath), ParcelFileDescriptor.MODE_READ_ONLY)).use { renderer ->
        renderer.openPage(0).renderAndClose(width)
    }
}