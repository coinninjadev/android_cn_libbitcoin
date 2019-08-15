LOCAL_PATH := $(call my-dir)

ifneq ("$(wildcard $(PROJECT_DIR)/ndkBuild/$(TARGET_ARCH_ABI)/lib/libbitcoin.a)","")
    STATIC_LIBRARY_DIR := $(PROJECT_DIR)/ndkBuild/$(TARGET_ARCH_ABI)/lib/*.a
else
    STATIC_LIBRARY_DIR := $(PROJECT_DIR)/externaldeps/make/jniNDK/android_studio.a
endif

include $(CLEAR_VARS)
LOCAL_MODULE := liball
LOCAL_SRC_FILES := $(STATIC_LIBRARY_DIR)
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE    := cnlibbitcoin
LOCAL_C_INCLUDES += $(PROJECT_DIR)/ndkBuild/$(TARGET_ARCH_ABI)/include
LOCAL_SRC_FILES := \
	$(wildcard $(PROJECT_DIR)/src/main/cpp/*.cpp)
LOCAL_LDLIBS := -llog
LOCAL_CPP_FEATURES += \
	exceptions \
	rtti
LOCAL_STATIC_LIBRARIES := \
	liball
include $(BUILD_SHARED_LIBRARY)
