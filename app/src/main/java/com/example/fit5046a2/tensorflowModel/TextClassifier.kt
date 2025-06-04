package com.example.fit5046a2.tensorflowModel

import android.content.Context
import android.util.Log
import org.tensorflow.lite.Interpreter
import java.io.*
import java.nio.*
import java.nio.channels.FileChannel
import java.util.*

class TextClassifier(context: Context) {

    private val interpreter: Interpreter
    private val vocab: Map<String, Int>
    private val inputSize = 256

    init {
        try {
            interpreter = Interpreter(loadModelFile(context, "text_classification.tflite"))
            Log.d("TextClassifier", "Model loaded successfully.")
        } catch (e: Exception) {
            Log.e("TextClassifier", "Failed to load model: ${e.message}")
            throw RuntimeException("Failed to load model", e)
        }

        try {
            vocab = loadVocab(context, "vocab.txt")
            Log.d("TextClassifier", "Vocabulary loaded. Size: ${vocab.size}")
        } catch (e: Exception) {
            Log.e("TextClassifier", "Failed to load vocabulary: ${e.message}")
            throw RuntimeException("Failed to load vocabulary", e)
        }
    }

    private fun loadModelFile(context: Context, filename: String): MappedByteBuffer {
        val fileDescriptor = context.assets.openFd(filename)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    private fun loadVocab(context: Context, filename: String): Map<String, Int> {
        val vocabMap = mutableMapOf<String, Int>()
        val reader = BufferedReader(InputStreamReader(context.assets.open(filename)))
        var line: String?
        var index = 0
        while (reader.readLine().also { line = it } != null) {
            vocabMap[line!!.trim()] = index++
        }
        reader.close()
        return vocabMap
    }

    fun classify(text: String): FloatArray {
        Log.d("TextClassifier", "Classifying text: \"$text\"")
        return try {
            val input = tokenizeInputText(text)
            val output = Array(1) { FloatArray(2) } // 假设是二分类
            interpreter.run(input, output)
            Log.d("TextClassifier", "Classification result: ${output[0].joinToString()}")
            output[0]
        } catch (e: Exception) {
            Log.e("TextClassifier", "Classification failed: ${e.message}")
            floatArrayOf(0f, 0f)
        }
    }

    private fun tokenizeInputText(text: String): Array<FloatArray> {
        val tokens = FloatArray(inputSize)
        val words = text.lowercase(Locale.getDefault()).split(" ")
        var index = 0
        for (word in words) {
            if (index >= inputSize) break
            tokens[index++] = (vocab[word] ?: 0).toFloat()
        }
        return arrayOf(tokens)
    }
}
