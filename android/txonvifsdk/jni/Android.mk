# Copyright (C) taixin 2014--2099  
# 
# This is a file for txonvif
#

LOCAL_PATH := $(call my-dir)

TX_ONVIF_JNI_PATH := $(realpath $(LOCAL_PATH))
TX_ONVIF_PRJ_PATH := $(realpath $(TX_ONVIF_JNI_PATH)/..)
TX_ONVIF_ANDROID_PATH := $(realpath $(TX_ONVIF_PRJ_PATH)/..)
TX_ONVIF_SOURCE_PATH := $(realpath $(TX_ONVIF_ANDROID_PATH)/../source)
TX_ONVIF_INCLUDE_PATH := $(realpath $(TX_ONVIF_SOURCE_PATH)/include)

include $(call all-subdir-makefiles)
