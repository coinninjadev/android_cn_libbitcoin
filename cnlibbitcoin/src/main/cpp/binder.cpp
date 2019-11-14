#include "binder.hpp"



jint jint_from_field_name(JNIEnv *env, jobject obj, jclass clazz, const char *field_name) {
    jfieldID field = env->GetFieldID(clazz, field_name, "I");
    return env->GetIntField(obj, field);
}

uint8_t uint8_t_from_field_name(JNIEnv *env, jobject obj, jclass clazz, const char *field_name) {
    return (uint8_t) jint_from_field_name(env, obj, clazz, field_name);
}

uint32_t uint32_t_from_field_name(JNIEnv *env, jobject obj, jclass clazz, const char *field_name) {
    return (uint32_t) jint_from_field_name(env, obj, clazz, field_name);
}

jlong jlong_from_field_name(JNIEnv *env, jobject obj, jclass clazz, const char *field_name) {
    jfieldID field = env->GetFieldID(clazz, field_name, "J");
    return env->GetLongField(obj, field);
}

uint64_t uint64_t_from_field_name(JNIEnv *env, jobject obj, jclass clazz, const char *field_name) {
    return (uint64_t) jlong_from_field_name(env, obj, clazz, field_name);
}

jboolean jboolean_from_field_name(JNIEnv *env, jobject obj, jclass clazz, const char *field_name) {
    jfieldID field = env->GetFieldID(clazz, field_name, "Z");
    return env->GetBooleanField(obj, field);
}

bool boolean_from_field_name(JNIEnv *env, jobject obj, jclass clazz, const char *field_name) {
    return (bool) jboolean_from_field_name(env, obj, clazz, field_name);
}

jstring jstring_from_field_name(JNIEnv *env, jobject obj, jclass clazz, const char *field_name) {
    jfieldID field = env->GetFieldID(clazz, field_name, JAVA_STRING_CLASS_ARG);
    return static_cast<jstring>(env->GetObjectField(obj, field));
}

jobject jobject_from_class(JNIEnv *env, jobject obj, jclass clazz, const char *field_name,
                           const char *type) {
    jfieldID field = env->GetFieldID(clazz, field_name, type);
    return env->GetObjectField(obj, field);
}

std::string string_from_field_name(JNIEnv *env, jobject obj, jclass clazz, const char *field_name) {
    jstring _value = jstring_from_field_name(env, obj, clazz, field_name);
    const char *convertedValue = env->GetStringUTFChars(_value, nullptr);
    std::string value;
    value.assign(convertedValue);
    env->ReleaseStringUTFChars(_value, convertedValue);
    return value;
}


coinninja::wallet::derivation_path
path_from_jobject(JNIEnv *env, jobject _path) {
    jclass path_class = env->GetObjectClass(_path);
    coinninja::wallet::derivation_path path{
            uint32_t_from_field_name(env, _path, path_class, "purpose"),
            uint32_t_from_field_name(env, _path, path_class, "coin"),
            uint32_t_from_field_name(env, _path, path_class, "account"),
            uint32_t_from_field_name(env, _path, path_class, "chain"),
            uint32_t_from_field_name(env, _path, path_class, "index")};

    env->DeleteLocalRef(path_class);
    return path;
}

coinninja::wallet::derivation_path
derivation_path_from_field(JNIEnv *env, jobject obj, jclass clazz, const char *field_name) {
    jfieldID field = env->GetFieldID(clazz, field_name,
                                     "Lapp/coinninja/cn/libbitcoin/model/DerivationPath;");
    jobject _derivation_path = env->GetObjectField(obj, field);
    return path_from_jobject(env, _derivation_path);
}

uint64_t network_from(jint network) {
    return network == 0 ? bc::wallet::hd_private::mainnet : bc::wallet::hd_private::testnet;
}

coinninja::wallet::coin_derivation_purpose purpose_from(jint purpose) {
    switch (purpose) {
        case 32:
            return coinninja::wallet::coin_derivation_purpose::BIP32;
        case 39:
            return coinninja::wallet::coin_derivation_purpose::BIP39;
        case 44:
            return coinninja::wallet::coin_derivation_purpose::BIP44;
        case 49:
            return coinninja::wallet::coin_derivation_purpose::BIP49;
        case 84:
            return coinninja::wallet::coin_derivation_purpose::BIP84;
        default:
            return coinninja::wallet::coin_derivation_purpose::BIP84;
    }
}

coinninja::wallet::coin_derivation_coin cn_network_from(jint network) {
    using namespace bc::wallet;
    using namespace coinninja::wallet;
    return network_from(network) == hd_private::mainnet ? MainNet : TestNet;
}

uint8_t *as_c_byte_array(JNIEnv *env, jbyteArray array) {
    int len = env->GetArrayLength(array);
    uint8_t *buf = new uint8_t[len];
    env->GetByteArrayRegion(array, 0, len, reinterpret_cast<jbyte *>(buf));
    return buf;
}

jbyteArray as_jbyte_array(JNIEnv *env, const std::string &native) {
    jbyteArray byteArray = env->NewByteArray(native.length());
    env->SetByteArrayRegion(byteArray, 0, native.length(),
                            reinterpret_cast<const jbyte *>(native.c_str()));
    return byteArray;
}

jbyteArray as_jbyte_array(JNIEnv *env, bc::hash_digest digest) {
    jbyteArray array = env->NewByteArray(digest.size());
    env->SetByteArrayRegion(array, 0, digest.size(),
                            reinterpret_cast<const jbyte *>(digest.data()));
    return array;
}

jbyteArray as_jbyte_array(JNIEnv *env, data_chunk chunk) {
    jbyteArray array = env->NewByteArray(chunk.size());
    env->SetByteArrayRegion(array, 0, chunk.size(), reinterpret_cast<const jbyte *>(chunk.data()));
    return array;
}

data_chunk bytes_to_data_chunk(JNIEnv *env, jbyteArray _bytes) {
    int length = env->GetArrayLength(_bytes);
    uint8_t *bytes = as_c_byte_array(env, _bytes);
    data_chunk chunk;
    for (int i = 0; i < length; i++) {
        chunk.push_back(bytes[i]);
    }
    return chunk;
}

bc::wallet::hd_private privateFrom(JNIEnv *env, jbyteArray _key, jint _network) {
    return hd_private(bytes_to_data_chunk(env, _key), network_from(_network));
}

jstring as_jstring(JNIEnv *env, const char *value) {
    return env->NewStringUTF(value);
}

jstring as_jstring(JNIEnv *env, std::string value) {
    return as_jstring(env, value.c_str());
}

extern "C"
JNIEXPORT jstring JNICALL
Java_app_coinninja_cn_libbitcoin_HDWallet_base58encode(JNIEnv *env, jobject instance,
                                                       jbyteArray _key, jint _network) {
    return as_jstring(env, privateFrom(env, _key, _network).encoded());
}

extern "C"
JNIEXPORT jobjectArray JNICALL
Java_app_coinninja_cn_libbitcoin_HDWallet_newWords(JNIEnv *env, jobject instance,
                                                   jbyteArray _entropy) {
    data_chunk entropyChunk = bytes_to_data_chunk(env, _entropy);
    wallet::word_list currentMNemonic = wallet::create_mnemonic(entropyChunk);

    auto ret = (jobjectArray) env->NewObjectArray(currentMNemonic.size(),
                                                  env->FindClass(JAVA_STRING_CLASS),
                                                  env->NewStringUTF(""));

    for (long i = 0; i < currentMNemonic.size(); i++) {
        std::string mn = currentMNemonic[i];
        jstring outString = env->NewStringUTF(mn.c_str());

        env->SetObjectArrayElement(ret, i, outString);
    }
    return (ret);

}

extern "C"
JNIEXPORT jbyteArray JNICALL
Java_app_coinninja_cn_libbitcoin_HDWallet_privateKeyFor(JNIEnv *env, jobject instance,
                                                        jobjectArray _words,
                                                        jint _network) {
    std::vector<std::string> mnemonic;
    int size = env->GetArrayLength(_words);

    for (int i = 0; i < size; ++i) {
        auto string = (jstring) (env->GetObjectArrayElement(_words, i));
        const char *word = env->GetStringUTFChars(string, nullptr);
        mnemonic.emplace_back(word);

        env->ReleaseStringUTFChars(string, word);
        env->DeleteLocalRef(string);
    }

    return as_jbyte_array(env, to_chunk(wallet::decode_mnemonic(mnemonic)));
}


extern "C"
JNIEXPORT jstring JNICALL
Java_app_coinninja_cn_libbitcoin_HDWallet_getSigningKey(JNIEnv *env, jobject instance,
                                                        jbyteArray _key,
                                                        jint _network) {
    bc::wallet::hd_private master = privateFrom(env, _key, _network);
    return env->NewStringUTF(coinninja::wallet::key_factory::signing_key(master).encoded().c_str());
}

extern "C"
JNIEXPORT jstring JNICALL
Java_app_coinninja_cn_libbitcoin_HDWallet_getVerificationKey(JNIEnv *env, jobject instance,
                                                             jbyteArray _key, jint _network) {
    bc::wallet::hd_private master = privateFrom(env, _key, _network);
    return env->NewStringUTF(
            coinninja::wallet::key_factory::coinninja_verification_key_hex_string(master).c_str());
}

extern "C"
JNIEXPORT jobject JNICALL
Java_app_coinninja_cn_libbitcoin_HDWallet_getAddressFor(JNIEnv *env, jobject instance,
                                                        jbyteArray _key,
                                                        jint _network,
                                                        jobject _path) {

    bc::wallet::hd_private master = privateFrom(env, _key, _network);
    coinninja::wallet::derivation_path derivation_path = path_from_jobject(env, _path);
    coinninja::address::meta_address meta_address = coinninja::address::usable_address(master,
                                                                                       derivation_path).build_meta_address();

    jstring address = env->NewStringUTF(meta_address.get_address().c_str());
    jstring pubKey = env->NewStringUTF(meta_address.get_uncompressed_public_key().c_str());
    jclass dclazz = env->FindClass(DERIVATION_PATH_CLASS);
    jmethodID dconstructor = env->GetMethodID(dclazz, "<init>", "(IIIII)V");
    derivation_path = meta_address.get_derivation_path();
    jobject _d_path = env->NewObject(dclazz, dconstructor, derivation_path.get_purpose(),
                                     derivation_path.get_coin(),
                                     derivation_path.get_account(), derivation_path.get_change(),
                                     derivation_path.get_index());
    env->DeleteLocalRef(dclazz);

    jclass mclazz = env->FindClass(META_ADDRESS);
    std::stringstream construction_signature;
    construction_signature << "(" << JAVA_STRING_CLASS_ARG << JAVA_STRING_CLASS_ARG
                           << DERIVATION_PATH_CLASS_ARG << ")V";
    jmethodID mconstructor = env->GetMethodID(mclazz, "<init>",
                                              construction_signature.str().c_str());
    jobject _meta_address = env->NewObject(mclazz, mconstructor, address, pubKey, _d_path);
    env->DeleteLocalRef(address);
    env->DeleteLocalRef(pubKey);
    env->DeleteLocalRef(mclazz);
    return _meta_address;
}

extern "C"
JNIEXPORT jobject JNICALL
Java_app_coinninja_cn_libbitcoin_HDWallet_encryptionKeys(JNIEnv *env, jobject instance,
                                                         jbyteArray _entropy,
                                                         jbyteArray _publicKey) {
    using namespace coinninja::wallet;
    using namespace coinninja::encryption;
    data_chunk publicKey = bytes_to_data_chunk(env, _publicKey);
    data_chunk entropy = bytes_to_data_chunk(env, _entropy);

    encryption_cipher_keys keys = cipher_key_vendor::encryption_cipher_keys_for_uncompressed_public_key(publicKey, entropy);

    jclass clazz = env->FindClass(ENCRYPTION_KEYS_CLASS);
    jmethodID constructor = env->GetMethodID(clazz, "<init>", "([B[B[B)V");

    return env->NewObject(clazz, constructor,
                          as_jbyte_array(env, keys.get_encryption_key()),
                          as_jbyte_array(env, keys.get_hmac_key()),
                          as_jbyte_array(env, keys.get_associated_public_key())
    );
}

extern "C"
JNIEXPORT jobject JNICALL
Java_app_coinninja_cn_libbitcoin_HDWallet_encryptionKeysForM42(JNIEnv *env, jobject instance,
                                                               jbyteArray _key, jint _network,
                                                               jbyteArray _publicKey) {
    using namespace coinninja::wallet;
    using namespace coinninja::encryption;
    bc::wallet::hd_private master = privateFrom(env, _key, _network);
    data_chunk data = bytes_to_data_chunk(env, _publicKey);
    bc::wallet::hd_private signingKey = coinninja::wallet::key_factory::signing_key(master);

    encryption_cipher_keys keys = coinninja::encryption::cipher_key_vendor::encryption_cipher_keys_for_uncompressed_public_key(
            data, signingKey);

    jclass clazz = env->FindClass(ENCRYPTION_KEYS_CLASS);
    jmethodID constructor = env->GetMethodID(clazz, "<init>", "([B[B[B)V");

    return env->NewObject(clazz, constructor,
                          as_jbyte_array(env, keys.get_encryption_key()),
                          as_jbyte_array(env, keys.get_hmac_key()),
                          as_jbyte_array(env, keys.get_associated_public_key())
    );
}


extern "C"
JNIEXPORT jobject JNICALL
Java_app_coinninja_cn_libbitcoin_HDWallet_decryptionKeys(JNIEnv *env, jobject instance,
                                                         jbyteArray _key, jint _network,
                                                         jobject _path, jbyteArray _publicKey) {

    using namespace bc::wallet;
    using namespace coinninja::wallet;
    using namespace coinninja::encryption;
    data_chunk data = bytes_to_data_chunk(env, _publicKey);
    hd_private master = privateFrom(env, _key, _network);
    derivation_path path = path_from_jobject(env, _path);
    hd_private derivative_key{key_factory::index_private_key(master, path)};
    cipher_keys keys{
            coinninja::encryption::cipher_key_vendor::decryption_cipher_keys(derivative_key, data)};
    jclass mclazz = env->FindClass(DECRYPTION_KEYS_CLASS);
    jmethodID mconstructor = env->GetMethodID(mclazz, "<init>", "([B[B)V");
    return env->NewObject(mclazz, mconstructor,
                          as_jbyte_array(env, keys.get_encryption_key()),
                          as_jbyte_array(env, keys.get_hmac_key()));
}

extern "C"
JNIEXPORT jobject JNICALL
Java_app_coinninja_cn_libbitcoin_HDWallet_decryptionKeysForM42(JNIEnv *env, jobject instance,
                                                               jbyteArray _key, jint _network,
                                                               jbyteArray _publicKey) {
    using namespace bc::wallet;
    using namespace coinninja::wallet;
    using namespace coinninja::encryption;
    hd_private master = privateFrom(env, _key, _network);
    data_chunk data = bytes_to_data_chunk(env, _publicKey);
    bc::wallet::hd_private signingKey = coinninja::wallet::key_factory::signing_key(master);

    cipher_keys keys{coinninja::encryption::cipher_key_vendor::decryption_cipher_keys(signingKey, data)};

    jclass mclazz = env->FindClass(DECRYPTION_KEYS_CLASS);
    jmethodID mconstructor = env->GetMethodID(mclazz, "<init>", "([B[B)V");
    return env->NewObject(mclazz, mconstructor,
                          as_jbyte_array(env, keys.get_encryption_key()),
                          as_jbyte_array(env, keys.get_hmac_key()));
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_app_coinninja_cn_libbitcoin_AddressUtil_isBase58(JNIEnv *env, jobject instance,
                                                      jstring _address) {
    const char *address = env->GetStringUTFChars(_address, JNI_FALSE);
    bool isBase58 = coinninja::address::base58check::verify_base_58_check_encoding(address);
    env->ReleaseStringUTFChars(_address, address);
    return static_cast<jboolean>(isBase58);
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_app_coinninja_cn_libbitcoin_AddressUtil_isSegwit(JNIEnv *env, jobject instance,
                                                      jstring _address) {
    const char *address = env->GetStringUTFChars(_address, JNI_FALSE);
    using namespace coinninja::address::segwit_address;
    bool isBase58 = is_valid_p2wpkh_address(address) || is_valid_p2wsh_address(address);
    env->ReleaseStringUTFChars(_address, address);
    return static_cast<jboolean>(isBase58);
}

extern "C"
JNIEXPORT jint JNICALL
Java_app_coinninja_cn_libbitcoin_AddressUtil_typeOfAddress(JNIEnv *env, jobject instance,
                                                           jint _purpose, jint _network,
                                                           jstring _address) {
    using namespace coinninja::wallet;
    using namespace coinninja::address;
    std::string address = env->GetStringUTFChars(_address, JNI_FALSE);
    int address_type = address_helper(base_coin(purpose_from(_purpose), cn_network_from(_network)))
            .address_type_for_address(address);
    return address_type;
}

extern "C"
JNIEXPORT jstring JNICALL
Java_app_coinninja_cn_libbitcoin_HDWallet_sign(JNIEnv *env, jobject instance,
                                               jbyteArray _key, jint _network, jbyteArray _data) {
    data_chunk chunk = bytes_to_data_chunk(env, _data);
    bc::wallet::hd_private master = privateFrom(env, _key, _network);
    std::string signedData = coinninja::wallet::data_signing::signature_signing_data(chunk, master);
    return env->NewStringUTF(signedData.c_str());
}

coinninja::transaction::replaceability_option
replaceable_option_from_tx_data(JNIEnv *env, jobject obj, jclass clazz) {
    jobject _replaceable_obj = jobject_from_class(env, obj, clazz, "replaceableOption",
                                                  REPLACEABLE_OPTION_CLASS_ARG);
    jclass _replaceable_class = env->GetObjectClass(_replaceable_obj);
    using namespace coinninja::transaction;
    uint32_t replaceable_option = uint32_t_from_field_name(env, _replaceable_obj,
                                                           _replaceable_class, "id");
    env->DeleteLocalRef(_replaceable_class);
    env->DeleteLocalRef(_replaceable_obj);

    switch (replaceable_option) {
        case 0:
            return replaceability_option::must_be_rbf;
        case 1:
            return replaceability_option::must_not_be_rbf;
        default:
            return replaceability_option::allowed;
    }
}

coinninja::transaction::unspent_transaction_output utxo_from_jobject(JNIEnv *env, jobject &obj) {
    jclass utxo_class = env->GetObjectClass(obj);
    coinninja::transaction::unspent_transaction_output utxo{
            string_from_field_name(env, obj, utxo_class, "txid"),
            uint32_t_from_field_name(env, obj, utxo_class, "index"),
            uint64_t_from_field_name(env, obj, utxo_class, "amount"),
            path_from_jobject(env, jobject_from_class(env, obj, utxo_class, "path",
                                                      DERIVATION_PATH_CLASS_ARG)),
            boolean_from_field_name(env, obj, utxo_class, "replaceable")
    };
    env->DeleteLocalRef(utxo_class);
    return utxo;
}

std::vector<coinninja::transaction::unspent_transaction_output>
utxos_from_tx_data(JNIEnv *env, jobject obj, jclass clazz) {
    using namespace coinninja::transaction;
    std::vector<unspent_transaction_output> utxos;
    jobject utxo_objects = jobject_from_class(env, obj, clazz, "utxos", UTXO_LIST_CLASS_ARG);
    auto *arr = reinterpret_cast<jobjectArray *>(&utxo_objects);
    jsize size = env->GetArrayLength(*arr);
    for (int i = 0; i < size; i++) {
        jobject utxo_object = reinterpret_cast<jobject>(env->GetObjectArrayElement(*arr,
                                                                                   (jsize) i));
        coinninja::transaction::unspent_transaction_output utxo = utxo_from_jobject(env,
                                                                                    utxo_object);
        utxos.push_back(utxo);
    }

    return utxos;
}

coinninja::transaction::transaction_data
tx_data_from_jobject(JNIEnv *env, coinninja::wallet::coin_derivation_coin network,
                     jobject _transaction_data) {

    using namespace coinninja::transaction;
    using namespace coinninja::wallet;
    jclass tx_data_class = env->GetObjectClass(_transaction_data);
    uint64_t amount = uint64_t_from_field_name(env, _transaction_data, tx_data_class, "amount");
    uint64_t change_amount = uint64_t_from_field_name(env, _transaction_data, tx_data_class,
                                                      "changeAmount");
    uint64_t fee_amount = uint64_t_from_field_name(env, _transaction_data, tx_data_class,
                                                   "feeAmount");
    uint64_t block_height = uint64_t_from_field_name(env, _transaction_data, tx_data_class,
                                                     "blockHeight");
    derivation_path change_path = derivation_path_from_field(env, _transaction_data, tx_data_class,
                                                             "changePath");
    std::string payment_address = string_from_field_name(env, _transaction_data, tx_data_class,
                                                         "paymentAddress");
    coinninja::transaction::replaceability_option replaceable_option =
            replaceable_option_from_tx_data(env, _transaction_data, tx_data_class);
    std::vector<coinninja::transaction::unspent_transaction_output> utxos =
            utxos_from_tx_data(env, _transaction_data, tx_data_class);

    env->DeleteLocalRef(tx_data_class);

    if (utxos.size() == 0) {
        return {};
    }

    base_coin base = base_coin(utxos[0].path);
    return {payment_address, base, utxos, amount, fee_amount, change_amount, change_path,
            block_height,
            replaceable_option};
}

extern "C"
JNIEXPORT jobject JNICALL
Java_app_coinninja_cn_libbitcoin_HDWallet_transactionFrom(JNIEnv *env, jobject instance,
                                                          jbyteArray _key, jint _network,
                                                          jobject _transaction_data) {

    using namespace bc::wallet;
    using namespace coinninja::transaction;
    using namespace coinninja::wallet;
    hd_private master = privateFrom(env, _key, _network);
    transaction_data tx_data = tx_data_from_jobject(env, cn_network_from(_network),
                                                    _transaction_data);

    jclass clazz = env->FindClass(TRANSACTION_CLASS);
    jobject _transaction;
    if (tx_data.unspent_transaction_outputs.size() == 0) {
        jmethodID constructor = env->GetMethodID(clazz, "<init>", "()V");
        _transaction = env->NewObject(clazz, constructor);
    } else {
        base_coin base = base_coin(tx_data.unspent_transaction_outputs[0].path);
        transaction_metadata transaction = transaction_builder(master, base).generate_tx_metadata(
                tx_data);

        std::stringstream construct_signature;
        construct_signature << "(" << JAVA_STRING_CLASS_ARG << JAVA_STRING_CLASS_ARG << ")V";
        jmethodID constructor = env->GetMethodID(clazz, "<init>",
                                                 construct_signature.str().c_str());
        _transaction = env->NewObject(clazz, constructor,
                                      as_jstring(env, transaction.get_txid()),
                                      as_jstring(env, transaction.get_encoded_tx()));
    }

    env->DeleteLocalRef(clazz);
    return _transaction;

}
/*
__android_log_print(ANDROID_LOG_INFO, __FUNCTION__, "----- UTXO 1----- ");
__android_log_print(ANDROID_LOG_INFO, __FUNCTION__, "----- utxo txid:  %s", utxos[0].txid.c_str());
__android_log_print(ANDROID_LOG_INFO, __FUNCTION__, "----- utxo index:  %i", utxos[0].index);
__android_log_print(ANDROID_LOG_INFO, __FUNCTION__, "----- utxo amount:  %u", utxos[0].amount);
__android_log_print(ANDROID_LOG_INFO, __FUNCTION__, "----- utxo path purpose:  %i", utxos[0].path.get_purpose());
__android_log_print(ANDROID_LOG_INFO, __FUNCTION__, "----- utxo path coin:  %i", utxos[0].path.get_coin());
__android_log_print(ANDROID_LOG_INFO, __FUNCTION__, "----- utxo path account:  %i", utxos[0].path.get_account());
__android_log_print(ANDROID_LOG_INFO, __FUNCTION__, "----- utxo path chain:  %i", utxos[0].path.get_change());
__android_log_print(ANDROID_LOG_INFO, __FUNCTION__, "----- utxo path index:  %i", utxos[0].path.get_index());
__android_log_print(ANDROID_LOG_INFO, __FUNCTION__, "----- utxo replaceable:  %i", utxos[0].is_confirmed? 1 : 0);
__android_log_print(ANDROID_LOG_INFO, __FUNCTION__, "----- UTXO 2----- ");
__android_log_print(ANDROID_LOG_INFO, __FUNCTION__, "----- utxo txid:  %s", utxos[1].txid.c_str());
__android_log_print(ANDROID_LOG_INFO, __FUNCTION__, "----- utxo index:  %i", utxos[1].index);
__android_log_print(ANDROID_LOG_INFO, __FUNCTION__, "----- utxo amount:  %u", utxos[1].amount);
__android_log_print(ANDROID_LOG_INFO, __FUNCTION__, "----- utxo path purpose:  %i", utxos[1].path.get_purpose());
__android_log_print(ANDROID_LOG_INFO, __FUNCTION__, "----- utxo path coin:  %i", utxos[1].path.get_coin());
__android_log_print(ANDROID_LOG_INFO, __FUNCTION__, "----- utxo path account:  %i", utxos[1].path.get_account());
__android_log_print(ANDROID_LOG_INFO, __FUNCTION__, "----- utxo path chain:  %i", utxos[1].path.get_change());
__android_log_print(ANDROID_LOG_INFO, __FUNCTION__, "----- utxo path index:  %i", utxos[1].path.get_index());
__android_log_print(ANDROID_LOG_INFO, __FUNCTION__, "----- utxo replaceable:  %i", utxos[1].is_confirmed? 1 : 0);
*/
