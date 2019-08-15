#ifndef binder_hpp
#define binder_hpp

#include <string>
#include <boost/regex.hpp>
#include <iostream>
#include <sstream>
#include <string.h>
#include <bitcoin/bitcoin/coinninja/wallet/mnemonic.hpp>

#include <android/log.h>
#include <jni.h>

extern "C" {

JNIEXPORT jobjectArray JNICALL
Java_app_coinninja_cn_libbitcoin_HDWallet_newWords(JNIEnv *env, jobject instance, jbyteArray entropy);
}
#endif /* bindings_hpp */
