package com.app.graffiti.utils

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore


/**
 * [FilePath] : <p>
 *
 * </p>
 * @author Jeel Vankhede
 * @version 1.0.0
 * @since 28/12/17
 */

/**
 * Method for return file path of Gallery image/ Document / Video / Audio
 *
 * @param context
 * @param uri
 * @return path of the selected image file from gallery
 */
fun getPath(context: Context, uri: Uri): String? {
    /*val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT*/
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        when (DocumentsContract.isDocumentUri(context, uri)) {
            isExternalStorageDocument(uri) -> {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)) return "${Environment.getExternalStorageDirectory()}/${split[1]}" else return null
            }
            isDownloadsDocument(uri) -> {
                val id = DocumentsContract.getDocumentId(uri)
                val contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        java.lang.Long.valueOf(id)!!)

                return getDataColumn(context, contentUri, null, null)
            }
            isMediaDocument(uri) -> {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                if ("image".equals(type)) contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                else if ("video".equals(type)) contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                else if ("audio".equals(type)) contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                val selection = "_id=?"
                val selectionArgs = arrayOf(split[1])
                return getDataColumn(
                        context = context,
                        uri = contentUri,
                        selection = selection,
                        selectionArgs = selectionArgs
                )
            }
            else -> {
                return null
            }
        }
    } else {
        return if ("content".equals(uri.getScheme(), ignoreCase = true)) {
            if (isGooglePhotosUri(uri = uri)) uri.getLastPathSegment() else getDataColumn(
                    context,
                    uri,
                    null,
                    null
            )
        } else if ("file".equals(uri.getScheme(), ignoreCase = true)) {
            uri.getPath()
        } else null
    }
    /*if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
        if (isExternalStorageDocument(uri)) {
            val docId = DocumentsContract.getDocumentId(uri)
            val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val type = split[0]

            if ("primary".equals(type, ignoreCase = true)) {
                return "${Environment.getExternalStorageDirectory()}/${split[1]}"
                *//*return (Environment.getExternalStorageDirectory() + "/"
                        + split[1])*//*
            }
        } else if (isDownloadsDocument(uri)) {

            val id = DocumentsContract.getDocumentId(uri)
            val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"),
                    java.lang.Long.valueOf(id)!!)

            return getDataColumn(context, contentUri, null, null)
        } else if (isMediaDocument(uri)) {
            val docId = DocumentsContract.getDocumentId(uri)
            val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val type = split[0]

            var contentUri: Uri? = null
            if ("image" == type) {
                contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            } else if ("video" == type) {
                contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            } else if ("audio" == type) {
                contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            }

            val selection = "_id=?"
            val selectionArgs = arrayOf(split[1])

            return getDataColumn(context, contentUri, selection,
                    selectionArgs)
        }
    } else if ("content".equals(uri.getScheme(), ignoreCase = true)) {
        return if (isGooglePhotosUri(uri)) uri.getLastPathSegment() else getDataColumn(context, uri, null, null)
    } else if ("file".equals(uri.getScheme(), ignoreCase = true)) {
        return uri.getPath()
    }
    return null*/
}

/**
 * Get the value of the data column for this Uri. This is useful for
 * MediaStore Uris, and other file-based ContentProviders.
 *
 * @param context
 * The context.
 * @param uri
 * The Uri to query.
 * @param selection
 * (Optional) Filter used in the query.
 * @param selectionArgs
 * (Optional) Selection arguments used in the query.
 * @return The value of the _data column, which is typically a file path.
 */
fun getDataColumn(
        context: Context,
        uri: Uri?,
        selection: String?,
        selectionArgs: Array<String>?
): String? {
    var cursor: Cursor? = null
    val column = "_data"
    val projection = arrayOf(column)
    try {
        cursor = context.getContentResolver().query(uri, projection,
                selection, selectionArgs, null)
        if (cursor != null && cursor.moveToFirst()) {
            val index = cursor.getColumnIndexOrThrow(column)
            return cursor.getString(index)
        }
    } finally {
        if (cursor != null)
            cursor.close()
    }
    return null
}

/**
 * @param uri
 * The Uri to check.
 * @return Whether the Uri authority is ExternalStorageProvider.
 */
fun isExternalStorageDocument(uri: Uri): Boolean = "com.android.externalstorage.documents".equals(uri.authority)

/**
 * @param uri
 * The Uri to check.
 * @return Whether the Uri authority is DownloadsProvider.
 */
fun isDownloadsDocument(uri: Uri): Boolean = "com.android.providers.downloads.documents".equals(uri.authority)

/**
 * @param uri
 * The Uri to check.
 * @return Whether the Uri authority is MediaProvider.
 */
fun isMediaDocument(uri: Uri): Boolean = "com.android.providers.media.documents".equals(uri.authority)

/**
 * @param uri
 * The Uri to check.
 * @return Whether the Uri authority is Google Photos.
 */
fun isGooglePhotosUri(uri: Uri): Boolean = "com.google.android.apps.photos.content".equals(uri.authority)