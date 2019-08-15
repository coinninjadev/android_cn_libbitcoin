#include "binder.hpp"


int *convertJints(JNIEnv *env, jintArray array) {
    return env->GetIntArrayElements(array, reinterpret_cast<jboolean *>(true));
}

uint8_t *as_c_byte_array(JNIEnv *env, jbyteArray array) {
    int len = env->GetArrayLength(array);
    uint8_t *buf = new uint8_t[len];
    env->GetByteArrayRegion(array, 0, len, reinterpret_cast<jbyte *>(buf));
    return buf;
}


jbyteArray as_byte_array(JNIEnv *env, uint8_t *buf, int length) {
    jbyteArray array = env->NewByteArray(length);
    env->SetByteArrayRegion(array, 0, length, reinterpret_cast<jbyte *>(buf));
    return array;
}

char *as_unsigned_char_array(JNIEnv *env, jbyteArray array) {
    int len = env->GetArrayLength(array);
    char *buf = new char[len];
    env->GetByteArrayRegion(array, 0, len, reinterpret_cast<jbyte *>(buf));
    return buf;
}

data_chunk bytes_to_data_chunk(uint8_t *bytes, int length) {
    data_chunk chunk;
    for (int i = 0; i < length; i++) {
        chunk.push_back(bytes[i]);
    }
    return chunk;
}


extern "C"

JNIEXPORT jobjectArray JNICALL
Java_app_coinninja_cn_libbitcoin_HDWallet_newWords(JNIEnv *env, jobject instance, jbyteArray entropy) {
    int length = env->GetArrayLength(entropy);
    uint8_t *entropyBytes = as_c_byte_array(env, entropy);
    data_chunk entropyChunk = bytes_to_data_chunk(entropyBytes, length);

    jobjectArray ret;
    wallet::word_list currentMNemonic = create_mnemonic(entropyChunk);

    ret = (jobjectArray) env->NewObjectArray(currentMNemonic.size(),
                                             env->FindClass("java/lang/String"),
                                             env->NewStringUTF(""));

    for (long i = 0; i < currentMNemonic.size(); i++) {
        std::string mn = currentMNemonic[i];
        jstring outString = env->NewStringUTF(mn.c_str());

        env->SetObjectArrayElement(ret, i, outString);
    }
    return (ret);
}
