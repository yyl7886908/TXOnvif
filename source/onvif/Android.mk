# copyright (c) 2014 taixin <yuyl@taixin.cn>
# 
# This file is part of txonvif
# 


LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_CFLAGS += -std=c99
LOCAL_CFLAGS += -D__ANDROID__
LOCAL_LDLIBS += -llog -landroid
#LOCAL_ALLOW_UNDEFINED_SYMBOLS := true

LOCAL_C_INCLUDE += $(realpath $(LOCAL_PATH)/include)

LOCAL_SRC_FILES += sha1.c
LOCAL_SRC_FILES += base64.c
LOCAL_SRC_FILES += duration.c
LOCAL_SRC_FILES += soapClient.c
LOCAL_SRC_FILES += stdsoap2.c
LOCAL_SRC_FILES += soapC.c

LOCAL_SRC_FILES += discovery.c
LOCAL_SRC_FILES += media.c
LOCAL_SRC_FILES += ptz.c
LOCAL_SRC_FILES += search.c
LOCAL_SRC_FILES += device.c
LOCAL_SRC_FILES += imaging.c
LOCAL_SRC_FILES += deviceio.c
LOCAL_SRC_FILES += recording.c

LOCAL_SRC_FILES += tx_onvif_sdk.c

LOCAL_MODULE := txonvif

include $(BUILD_SHARED_LIBRARY)

LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
include $(call all-subdir-makefiles)
