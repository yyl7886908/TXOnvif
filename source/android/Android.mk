# copyright (c) 2014 taixin <yuyl@taixin.cn>
# 
# This file is part of txonvif
# 


LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_CFLAGS += -std=c99

LOCAL_LDLIBS += -llog -landroid

LOCAL_C_INCLUDES += $(realpath $(LOCAL_PATH)/../onvif/include)
LOCAL_SRC_FILES += tx_onvif_jni.c

LOCAL_SHARED_LIBRARIES := txonvif

LOCAL_MODULE := txonvif-jni

include $(BUILD_SHARED_LIBRARY)
