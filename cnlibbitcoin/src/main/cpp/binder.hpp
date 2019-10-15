#ifndef binder_hpp
#define binder_hpp

#include <string>
#include <boost/regex.hpp>
#include <iostream>
#include <sstream>
#include <string.h>
#include <bitcoin/bitcoin.hpp>
#include <bitcoin/bitcoin/coinninja/encryption/cipher_key_vendor.hpp>
#include <bitcoin/bitcoin/coinninja/wallet/base_coin.hpp>
#include <bitcoin/bitcoin/coinninja/wallet/data_signing.hpp>
#include <bitcoin/bitcoin/coinninja/wallet/derivation_path.hpp>
#include <bitcoin/bitcoin/coinninja/wallet/key_factory.hpp>
#include <bitcoin/bitcoin/coinninja/wallet/mnemonic.hpp>
#include <bitcoin/bitcoin/coinninja/address/address_helper.hpp>
#include <bitcoin/bitcoin/coinninja/address/base58check.hpp>
#include <bitcoin/bitcoin/coinninja/address/segwit_address.hpp>
#include <bitcoin/bitcoin/coinninja/address/usable_address.hpp>
#include <bitcoin/bitcoin/coinninja/address/meta_address.hpp>
#include <bitcoin/bitcoin/coinninja/transaction/transaction_data.hpp>
#include <bitcoin/bitcoin/coinninja/transaction/transaction_builder.hpp>
#include <bitcoin/bitcoin/coinninja/transaction/transaction_metadata.hpp>

#include <android/log.h>
#include <jni.h>

const char *META_ADDRESS = "app/coinninja/cn/libbitcoin/model/MetaAddress";
const char *ENCRYPTION_KEYS_CLASS = "app/coinninja/cn/libbitcoin/model/EncryptionKeys";
const char *DECRYPTION_KEYS_CLASS = "app/coinninja/cn/libbitcoin/model/DecryptionKeys";
const char *TRANSACTION_CLASS = "app/coinninja/cn/libbitcoin/model/Transaction";
const char *DERIVATION_PATH_CLASS = "app/coinninja/cn/libbitcoin/model/DerivationPath";
const char *DERIVATION_PATH_CLASS_ARG = "Lapp/coinninja/cn/libbitcoin/model/DerivationPath;";
const char *REPLACEABLE_OPTION_CLASS_ARG = "Lapp/coinninja/cn/libbitcoin/enum/ReplaceableOption;";
const char *UTXO_LIST_CLASS_ARG = "[Lapp/coinninja/cn/libbitcoin/model/UnspentTransactionOutput;";
const char *JAVA_STRING_CLASS = "java/lang/String";
const char *JAVA_STRING_CLASS_ARG = "Ljava/lang/String;";


extern "C" {

JNIEXPORT jobjectArray JNICALL
Java_app_coinninja_cn_libbitcoin_HDWallet_newWords(JNIEnv *env, jobject instance,
                                                   jbyteArray _entropy);

JNIEXPORT jstring JNICALL
Java_app_coinninja_cn_libbitcoin_HDWallet_sign(JNIEnv *env, jobject instance,
                                               jbyteArray _key, jint _network, jbyteArray data);

JNIEXPORT jbyteArray JNICALL
Java_app_coinninja_cn_libbitcoin_HDWallet_privateKeyFor(JNIEnv *env, jobject instance,
                                                        jobjectArray _words, jint network);

JNIEXPORT jstring JNICALL
Java_app_coinninja_cn_libbitcoin_HDWallet_getSigningKey(JNIEnv *env, jobject instance,
                                                        jbyteArray _key,
                                                        jint _network);

JNIEXPORT jstring JNICALL
Java_app_coinninja_cn_libbitcoin_HDWallet_getVerificationKey(JNIEnv *env, jobject instance,
                                                             jbyteArray _key, jint _network);

JNIEXPORT jobject JNICALL
Java_app_coinninja_cn_libbitcoin_HDWallet_getAddressFor(JNIEnv *env, jobject instance,
                                                        jbyteArray _key,
                                                        jint _network, jobject _path);


JNIEXPORT jobject JNICALL
Java_app_coinninja_cn_libbitcoin_HDWallet_encryptionKeys(JNIEnv *env, jobject instance,
                                                         jbyteArray _entropy,
                                                         jbyteArray _publicKey);

JNIEXPORT jobject JNICALL
Java_app_coinninja_cn_libbitcoin_HDWallet_decryptionKeys(JNIEnv *env, jobject instance,
                                                         jbyteArray _key, jint _network,
                                                         jobject _path, jbyteArray _publicKey);

JNIEXPORT jstring JNICALL
Java_app_coinninja_cn_libbitcoin_HDWallet_base58encode(JNIEnv *env, jobject instance,
                                                       jbyteArray _key, jint _network);

JNIEXPORT jobject JNICALL
Java_app_coinninja_cn_libbitcoin_HDWallet_transactionFrom(JNIEnv *env, jobject instance,
                                                          jbyteArray _key, jint _network,
                                                          jobject _transaction_data);

JNIEXPORT jboolean JNICALL
Java_app_coinninja_cn_libbitcoin_AddressUtil_isSegwit(JNIEnv *env, jobject instance,
                                                      jstring _address);


JNIEXPORT jboolean JNICALL
Java_app_coinninja_cn_libbitcoin_AddressUtil_isBase58(JNIEnv *env, jobject instance,
                                                      jstring _address);


JNIEXPORT jint JNICALL
Java_app_coinninja_cn_libbitcoin_AddressUtil_typeOfAddress(JNIEnv *env, jobject instance,
                                                           jint _purpose, jint _network,
                                                           jstring _address);

}

#endif /* bindings_hpp */
