package com.wilsonPinto.imageViewerDemo

abstract class ImageViewer {
    abstract var filePath: String
    abstract var fileName: String
    abstract var fileType: String
    abstract var fileSize: Int
    abstract var croppedFilePath: String
    abstract var isCropped: Boolean
    abstract var isLocal: Boolean

    fun resetFilePath() {
        if (croppedFilePath.isNotEmpty())
            filePath = croppedFilePath
    }

    fun setRemoteUrl(url: String){
        filePath = url
        isLocal = false
    }

    fun getFileUrl(): String {
        return if (croppedFilePath.isEmpty()) {
            if (isLocal)
                filePath
            else
                appendBaseUrl()
        } else
            croppedFilePath
    }

    private fun appendBaseUrl(): String {
        return AppConfig.API_BASE_URL + filePath
    }
}