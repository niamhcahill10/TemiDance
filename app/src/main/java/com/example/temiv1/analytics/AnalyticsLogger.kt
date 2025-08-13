package com.example.temiv1.analytics

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

object SessionIdProvider {
    val id: String by lazy {
        val ts = SimpleDateFormat("yyyyMMdd-HHmmss-SSS", Locale.US).format(Date())
        "$ts-${UUID.randomUUID().toString().take(8)}"
    }
}

object CsvLogger {
    private lateinit var appContext: Context
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val dateFmt = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)

    private var currentFile: File? = null
    private var wroteHeader = false

    private val lastPointByStream: MutableMap<String, Pair<String, Long>> = mutableMapOf()

    // Create csv file to append
    fun init(context: Context) {
        appContext = context.applicationContext
        val name = "temi_session_${SimpleDateFormat("yyyyMMdd-HHmmss", Locale.US).format(Date())}.csv"
        currentFile = File(appContext.filesDir, name)
        wroteHeader = false
    }

    // Track time taken to use the app
    fun logUseTime(stream: String, appPoint: String) {
        val now = System.currentTimeMillis()
        val last = lastPointByStream[stream]
        if (last != null) {
            val (from, startedAt) = last
            append(
                mapOf(
                    "timestampMs" to now,
                    "isoTime" to dateFmt.format(Date(now)),
                    "sessionId" to SessionIdProvider.id,
                    "stream" to stream,
                    "event" to "SPAN",
                    "fromPoint" to from,
                    "toPoint" to appPoint,
                    "elapsedMs" to (now - startedAt).toString()
                )
            )
        }
        lastPointByStream[stream] = appPoint to now
    }

    // Event logger for answers, selections, recovery clicks, and select or clear all clicks
    fun logEvent(stream: String, eventId: String, value: String, meta: String? = null) {
        val now = System.currentTimeMillis()
        append(
            mapOf(
                "timestampMs" to now,
                "isoTime" to dateFmt.format(Date(now)),
                "sessionId" to SessionIdProvider.id,
                "stream" to stream,
                "event" to "EVENT",
                "eventId" to eventId,
                "answer" to value,
                "meta" to (meta ?: "")
            )
        )
    }

    private fun append(fields: Map<String, Any?>) {
        scope.launch {
            val file = currentFile ?: return@launch
            if (!wroteHeader || file.length() == 0L) {
                writeLine(file, header())
                wroteHeader = true
            }
            writeLine(file, toCsv(fields))
        }
    }

    private fun header(): String = listOf(
        "timestampMs","isoTime","sessionId","stream","event",
        "eventId","answer","fromPoint","toPoint","elapsedMs","meta"
    ).joinToString(",")

    private fun toCsv(fields: Map<String, Any?>): String {
        fun q(s: Any?): String {
            val str = s?.toString() ?: ""
            return '"' + str.replace("\"", "\"\"") + '"'
        }
        return listOf(
            fields["timestampMs"],
            fields["isoTime"],
            fields["sessionId"],
            fields["stream"],
            fields["event"],
            fields["eventId"],
            fields["answer"],
            fields["fromPoint"],
            fields["toPoint"],
            fields["elapsedMs"],
            fields["reason"],
            fields["meta"],
        ).joinToString(",") { q(it) }
    }

    private fun writeLine(file: File, line: String) {
        FileOutputStream(file, /* append = */ true).use { fos ->
            BufferedWriter(OutputStreamWriter(fos)).use { out ->
                out.append(line)
                out.append('\n')
            }
        }
    }

    fun exportToDownloads(context: Context, fixedName: String = "temi_latest.csv"): Uri {
        val file = currentFile ?: error("Logger not initialized")

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val resolver = context.contentResolver
            val relativePath = Environment.DIRECTORY_DOWNLOADS + "/TemiLogs"

            // 1) Delete any existing file with same name/path
            val collection = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            val selection = "${MediaStore.MediaColumns.DISPLAY_NAME}=? AND ${MediaStore.MediaColumns.RELATIVE_PATH}=?"
            val args = arrayOf(fixedName, "$relativePath/")
            resolver.query(collection, arrayOf(MediaStore.MediaColumns._ID), selection, args, null)?.use { c ->
                val idIndex = c.getColumnIndexOrThrow(MediaStore.MediaColumns._ID)
                while (c.moveToNext()) {
                    val id = c.getLong(idIndex)
                    val uri = ContentUris.withAppendedId(collection, id)
                    resolver.delete(uri, null, null)
                }
            }

            // 2) Insert new row for event
            val values = ContentValues().apply {
                put(MediaStore.Downloads.DISPLAY_NAME, fixedName)
                put(MediaStore.Downloads.MIME_TYPE, "text/csv")
                put(MediaStore.MediaColumns.RELATIVE_PATH, relativePath)
                put(MediaStore.Downloads.IS_PENDING, 1)
            }
            val item = resolver.insert(collection, values) ?: error("Insert failed")

            // 3) Write file into downloads
            resolver.openOutputStream(item)?.use { os ->
                file.inputStream().use { it.copyTo(os) }
            }

            // 4) Mark complete
            values.clear(); values.put(MediaStore.Downloads.IS_PENDING, 0)
            resolver.update(item, values, null, null)
            item
        } else {
            // Pre-29: overwrite an internal file with the fixed name
            val dest = File(context.filesDir, fixedName)
            file.copyTo(dest, overwrite = true)
            Uri.fromFile(dest)
        }
    }


}
