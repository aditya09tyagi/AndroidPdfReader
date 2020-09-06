package com.personal.android.androidpdfreader.home

import android.graphics.pdf.PdfRenderer
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.personal.android.androidpdfreader.R

class PdfReaderAdapter(
    private val renderer: PdfRenderer,
    private val pageWidth: Int
) : RecyclerView.Adapter<PdfReaderViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PdfReaderViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.cell_pdf_page, parent, false)
        return PdfReaderViewHolder(view, renderer, pageWidth)
    }

    override fun getItemCount(): Int {
        return renderer.pageCount
    }

    override fun onBindViewHolder(holder: PdfReaderViewHolder, position: Int) {
        holder.setPdfPage()
    }
}