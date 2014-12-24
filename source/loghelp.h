/*****************************************************************************
 * loghelper.h
 *****************************************************************************
 *
 * copyright (c) 2014 taixin <yuyl@taixin.com>
 *
 */

#ifndef __UTIL_ANDROID__LOGHELP_H
#define __UTIL_ANDROID__LOGHELP_H

#include <android/log.h>

#ifdef __cplusplus
extern "C" {
#endif

#define TX_LOG_TAG "TXONVIF"

#define TX_LOG_UNKNOWN     ANDROID_LOG_UNKNOWN
#define TX_LOG_DEFAULT     ANDROID_LOG_DEFAULT

#define TX_LOG_VERBOSE     ANDROID_LOG_VERBOSE
#define TX_LOG_DEBUG       ANDROID_LOG_DEBUG
#define TX_LOG_INFO        ANDROID_LOG_INFO
#define TX_LOG_WARN        ANDROID_LOG_WARN
#define TX_LOG_ERROR       ANDROID_LOG_ERROR
#define TX_LOG_FATAL       ANDROID_LOG_FATAL
#define TX_LOG_SILENT      ANDROID_LOG_SILENT

#define VLOG(level, TAG, ...)    ((void)__android_log_vprint(level, TAG, __VA_ARGS__))
#define VLOGV(...)  VLOG(TX_LOG_VERBOSE,   TX_LOG_TAG, __VA_ARGS__)
#define VLOGD(...)  VLOG(TX_LOG_DEBUG,     TX_LOG_TAG, __VA_ARGS__)
#define VLOGI(...)  VLOG(TX_LOG_INFO,      TX_LOG_TAG, __VA_ARGS__)
#define VLOGW(...)  VLOG(TX_LOG_WARN,      TX_LOG_TAG, __VA_ARGS__)
#define VLOGE(...)  VLOG(TX_LOG_ERROR,     TX_LOG_TAG, __VA_ARGS__)

#define ALOG(level, TAG, ...)    ((void)__android_log_print(level, TAG, __VA_ARGS__))
#define ALOGV(...)  ALOG(TX_LOG_VERBOSE,   TX_LOG_TAG, __VA_ARGS__)
#define ALOGD(...)  ALOG(TX_LOG_DEBUG,     TX_LOG_TAG, __VA_ARGS__)
#define ALOGI(...)  ALOG(TX_LOG_INFO,      TX_LOG_TAG, __VA_ARGS__)
#define ALOGW(...)  ALOG(TX_LOG_WARN,      TX_LOG_TAG, __VA_ARGS__)
#define ALOGE(...)  ALOG(TX_LOG_ERROR,     TX_LOG_TAG, __VA_ARGS__)
#define LOG_ALWAYS_FATAL(...)   do { ALOGE(__VA_ARGS__); exit(1); } while (0)

#ifdef __cplusplus
}
#endif

#endif
